package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.jei;

import appeng.api.stacks.AEKey;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.me.common.GridInventoryEntry;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternUtils;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.GenericEntryStackHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.transfer.AbstractTransferHandler;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JEIBatchEncodingTransferHandler<MENU extends PatternBatchEncodingTermMenu>
        extends AbstractTransferHandler
        implements IUniversalRecipeTransferHandler<MENU> {

    private final MenuType<MENU> menuType;
    private final Class<MENU> menuClass;

    public JEIBatchEncodingTransferHandler(MenuType<MENU> menuType,
                                           Class<MENU> menuClass) {
        this.menuType = menuType;
        this.menuClass = menuClass;
    }
    @Override
    public @NotNull Class<? extends MENU> getContainerClass() {
        return menuClass;
    }

    @Override
    public @NotNull Optional<MenuType<MENU>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(@NotNull MENU menu, @NotNull Object recipeBase, @NotNull IRecipeSlotsView slotsView, @NotNull Player player,
                                                         boolean maxTransfer, boolean doTransfer) {

        if (doTransfer) {
            PatternUtils.encodeProcessingRecipe(menu,
                    GenericEntryStackHelper.ofInputs(slotsView),
                    GenericEntryStackHelper.ofOutputs(slotsView));
        } else {
            var craftableSlots = findCraftableSlots(menu, slotsView);
            //draw a blue overlay on the slots that can be crafted as
            return new IRecipeTransferError(){

                @Override
                public @NotNull Type getType() {
                    return Type.COSMETIC;
                }

                @Override
                public int getButtonHighlightColor() {
                    return 0;
                }

                @Override
                public void showError(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, @NotNull IRecipeSlotsView recipeSlotsView,
                                      int recipeX, int recipeY) {
                    var poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.translate(recipeX, recipeY, 0);

                    for (IRecipeSlotView slotView : craftableSlots) {
                        slotView.drawHighlight(guiGraphics, TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR);
                    }

                    poseStack.popPose();
                }

                @Override
                public void getTooltip(@NotNull ITooltipBuilder tooltip) {
                    tooltip.addAll(TransferHelper.createEncodingTooltip(!craftableSlots.isEmpty(), true));
                }

                @Override
                public int getMissingCountHint() {
                    return IRecipeTransferError.super.getMissingCountHint();
                }
            };
        }

        return null;
    }

    private List<IRecipeSlotView> findCraftableSlots(MENU menu, @NotNull IRecipeSlotsView slotsView) {
        var repo = menu.getClientRepo();
        Set<AEKey> craftableKeys = repo != null ? repo.getAllEntries().stream()
                .filter(GridInventoryEntry::isCraftable)
                .map(GridInventoryEntry::getWhat)
                .collect(Collectors.toSet()) : Set.of();

        return slotsView.getSlotViews(RecipeIngredientRole.INPUT).stream()
                .filter(slotView -> slotView.getAllIngredients().anyMatch(ingredient -> {
                    var stack = GenericEntryStackHelper.ingredientToStack(ingredient);
                    return stack != null && craftableKeys.contains(stack.what());
                }))
                .toList();
    }
}
