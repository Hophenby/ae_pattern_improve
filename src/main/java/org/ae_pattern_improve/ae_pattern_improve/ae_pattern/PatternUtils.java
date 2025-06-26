package org.ae_pattern_improve.ae_pattern_improve.ae_pattern;

import appeng.api.stacks.GenericStack;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.slot.FakeSlot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.ae_pattern_improve.ae_pattern_improve.mixins.MixinEncodingHelper;

import java.util.List;

import static appeng.integration.modules.itemlists.EncodingHelper.getIngredientPriorities;

public class PatternUtils {
    public static void encodeProcessingRecipe(PatternBatchEncodingTermMenu menu, List<List<GenericStack>> genericIngredients,
                                              List<GenericStack> genericResults) {

        // Note that this runs on the client and getClientRepo() is guaranteed to be available there.
        var ingredientPriorities = getIngredientPriorities(menu, MixinEncodingHelper.entryComparator());

        MixinEncodingHelper.encodeBestMatchingStacksIntoSlots(
                genericIngredients,
                ingredientPriorities,
                menu.getProcessingInputSlots());
        MixinEncodingHelper.encodeBestMatchingStacksIntoSlots(
                // For the outputs, it's only one possible item per slot
                genericResults.stream().map(List::of).toList(),
                ingredientPriorities,
                menu.getProcessingOutputSlots());
    }
    public static void mulPatternEncodingArea(final FakeSlot[] processingInputSlots,
                                              final FakeSlot[] processingOutputSlots,
                                              int multiplier,
                                              boolean divMode) {
        var inputs = new GenericStack[processingInputSlots.length];
        boolean valid = true;
        for (int slot = 0; slot < processingInputSlots.length; slot++) {
            inputs[slot] = GenericStack.fromItemStack(processingInputSlots[slot].getItem());
            if (inputs[slot] == null) {
                // At least one input must be set, but it doesn't matter which one
                continue;
            }
            // if the item count exceeds the Integer.MAX_VALUE, or the count becomes floating point, we cannot encode it
            if (divMode ? (inputs[slot].amount() % multiplier != 0) : (inputs[slot].amount() > Integer.MAX_VALUE / multiplier)) {
                valid = false;
                //Ae_test.LOGGER.warn("Invalid input stack in pattern encoding: " + inputs[slot] + " with multiplier " + multiplier + " and divMode " + divMode);
            }
        }
        if (!valid) {
            return;
        }

        var outputs = new GenericStack[processingOutputSlots.length];
        for (int slot = 0; slot < processingOutputSlots.length; slot++) {
            outputs[slot] = GenericStack.fromItemStack(processingOutputSlots[slot].getItem());
            if (outputs[slot] != null) {
                // if the item count exceeds the Integer.MAX_VALUE, or the count becomes floating point, we cannot encode it
                if (divMode ? (outputs[slot].amount() % multiplier != 0) : (outputs[slot].amount() > Integer.MAX_VALUE / multiplier)) {
                    // Ae_test.LOGGER.warn("Invalid output stack in pattern encoding: " + outputs[slot] + " with multiplier " + multiplier + " and divMode " + divMode);
                    return;
                }
            }
        }
        // If we are in divMode, we need to divide the inputs by the multiplier
        for (int slot = 0; slot < processingInputSlots.length; slot++) {
            if (inputs[slot] != null) {
                var stack = new GenericStack(inputs[slot].what(), divMode ? (int) (inputs[slot].amount() / multiplier) : (inputs[slot].amount() * multiplier));
                ServerboundPacket message = new InventoryActionPacket(
                        InventoryAction.SET_FILTER, processingInputSlots[slot].index, GenericStack.wrapInItemStack(stack));
                PacketDistributor.sendToServer(message);
            }
        }
        for (int slot = 0; slot < processingOutputSlots.length; slot++) {
            if (outputs[slot] != null) {
                var stack = new GenericStack(outputs[slot].what(), divMode ? (int) (outputs[slot].amount() / multiplier) : (outputs[slot].amount() * multiplier));
                ServerboundPacket message = new InventoryActionPacket(
                        InventoryAction.SET_FILTER, processingOutputSlots[slot].index, GenericStack.wrapInItemStack(stack));
                PacketDistributor.sendToServer(message);
            }
        }
    }
}
