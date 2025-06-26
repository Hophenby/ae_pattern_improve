package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.rei;

import appeng.api.stacks.AEKey;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.integration.modules.rei.GenericEntryStackHelper;
import appeng.menu.me.common.GridInventoryEntry;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.entry.EntryStack;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternUtils;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;

public class REIBatchEncodingTransferHandler<T extends PatternBatchEncodingTermMenu> implements TransferHandler {
    private final Class<T> containerClass;

    public REIBatchEncodingTransferHandler(Class<T> containerClass) {
        this.containerClass = containerClass;
    }


    @Override
    @SuppressWarnings("all")
    public Result handle(Context context) {
        if (!containerClass.isInstance(context.getMenu())) {
            return Result.createNotApplicable();
        }

        var recipeDisplay = context.getDisplay();

        T menu = containerClass.cast(context.getMenu());

        boolean doTransfer = context.isActuallyCrafting();

        if (doTransfer) {
            PatternUtils.encodeProcessingRecipe(menu,
                    GenericEntryStackHelper.ofInputs(recipeDisplay),
                    GenericEntryStackHelper.ofOutputs(recipeDisplay));
        } else {
            var repo = menu.getClientRepo();
            Set<AEKey> craftableKeys = repo != null ? repo.getAllEntries().stream()
                    .filter(GridInventoryEntry::isCraftable)
                    .map(GridInventoryEntry::getWhat)
                    .collect(Collectors.toSet()) : Set.of();

            var anyCraftable = recipeDisplay.getInputEntries().stream().anyMatch(ing -> isCraftable(craftableKeys, ing));
            var tooltip = TransferHelper.createEncodingTooltip(anyCraftable, true);
            return Result.createSuccessful()
                    .blocksFurtherHandling()
                    .overrideTooltipRenderer((point, sink) -> sink.accept(Tooltip.create(tooltip)))
                    .renderer((guiGraphics, mouseX, mouseY, delta, widgets, bounds, display) -> {
                        for (Widget widget : widgets) {
                            if (widget instanceof Slot slot && slot.getNoticeMark() == Slot.INPUT) {
                                if (isCraftable(craftableKeys, slot.getEntries())) {
                                    var poseStack = guiGraphics.pose();
                                    poseStack.pushPose();
                                    poseStack.translate(0, 0, 400);
                                    Rectangle innerBounds = slot.getInnerBounds();
                                    guiGraphics.fill(innerBounds.x, innerBounds.y, innerBounds.getMaxX(),
                                            innerBounds.getMaxY(), BLUE_SLOT_HIGHLIGHT_COLOR);
                                    poseStack.popPose();
                                }
                            }
                        }});
        }

        return Result.createSuccessful().blocksFurtherHandling();
    }
    private static boolean isCraftable(Set<AEKey> craftableKeys, List<EntryStack<?>> ingredient) {
        return ingredient.stream().anyMatch(entryStack -> {
            var stack = GenericEntryStackHelper.ingredientToStack(entryStack);
            return stack != null && craftableKeys.contains(stack.what());
        });
    }
}
