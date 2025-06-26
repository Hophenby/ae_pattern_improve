package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.emi;

import appeng.api.stacks.AEKey;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.GridInventoryEntry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternUtils;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;

public class EMIBatchEncodingTransferHandler<MENU extends PatternBatchEncodingTermMenu> implements StandardRecipeHandler<MENU> {

    public EMIBatchEncodingTransferHandler(Class<MENU> containerClass) {
        this.containerClass = containerClass;
    }

    @Override
    public List<Slot> getInputSources(MENU menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_INVENTORY));
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_HOTBAR));
        slots.addAll(menu.getSlots(SlotSemantics.CRAFTING_GRID));
        return slots;
    }
    private final Class<MENU> containerClass;

    @Override
    public List<Slot> getCraftingSlots(MENU handler) {
        return List.of();
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<MENU> context) {
        return true;
    }
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return true;
    }
    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<MENU> context) {
        return transferRecipe(recipe, context);
    }

    @Override
    public void render(EmiRecipe recipe, EmiCraftContext<MENU> context, List<Widget> widgets, GuiGraphics draw) {
        for (var widget : widgets) {
            if (widget instanceof SlotWidget slot && slot.getRecipe() == null) {

                Set<AEKey> craftableKeys = getCraftableKeys(context);

                if (isCraftable(craftableKeys, slot.getStack())) {
                    var poseStack = draw.pose();
                    poseStack.pushPose();
                    poseStack.translate(0, 0, 400);
                    var bounds = getInnerBounds(slot);
                    draw.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(),
                            BLUE_SLOT_HIGHLIGHT_COLOR);
                    poseStack.popPose();
                }
            }
        }
    }
    private Set<AEKey> getCraftableKeys(EmiCraftContext<MENU> context) {
        MENU menu = containerClass.cast(context.getScreenHandler());
        var repo = menu.getClientRepo();
        return repo != null ? repo.getAllEntries().stream()
                .filter(GridInventoryEntry::isCraftable)
                .map(GridInventoryEntry::getWhat)
                .collect(Collectors.toSet()) : Set.of();
    }
    private static Bounds getInnerBounds(SlotWidget slot) {
        var bounds = slot.getBounds();
        return new Bounds(
                bounds.x() + 1,
                bounds.y() + 1,
                bounds.width() - 2,
                bounds.height() - 2);
    }
    private static boolean isCraftable(Set<AEKey> craftableKeys, EmiIngredient ingredient) {
        return ingredient.getEmiStacks().stream().anyMatch(emiIngredient -> {
            var stack = EmiStackHelper.toGenericStack(emiIngredient);
            return stack != null && craftableKeys.contains(stack.what());
        });
    }
    protected boolean transferRecipe(EmiRecipe emiRecipe, EmiCraftContext<MENU> context) {
        if (!containerClass.isInstance(context.getScreenHandler())) {
            return false;
        }

        MENU menu = containerClass.cast(context.getScreenHandler());

        // EMI: Always encode the recipe as a processing recipe
        PatternUtils.encodeProcessingRecipe(menu,
                EmiStackHelper.ofInputs(emiRecipe),
                EmiStackHelper.ofOutputs(emiRecipe));
        Minecraft.getInstance().setScreen(context.getScreen());
        return true;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<MENU> context) {
        return TransferHelper.createEncodingTooltip(!getCraftableKeys(context).isEmpty(), false)
                .stream()
                .map(this::getTooltipText)
                .toList();
    }
    private ClientTooltipComponent getTooltipText(Component component) {
        return ClientTooltipComponent.create(component.getVisualOrderText());
    }
}
