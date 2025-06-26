package org.ae_pattern_improve.ae_pattern_improve.mixin_helpers;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.me.common.StackSizeRenderer;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.hooks.GuiGraphicsHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientConfig;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public final class EncodedPatternWithAmountRenderer {
    @SuppressWarnings("rawtypes")
    public static boolean renderEncodedPatternWithAmount(GuiGraphics guiGraphics, @Nullable LivingEntity livingEntity,
                                                         @Nullable Level level, ItemStack stack, int x, int y, int seed, int z) {

        if (GuiGraphicsHooks.onRenderGuiItem(guiGraphics, livingEntity, level, stack, x, y, seed, z)) {
            // If the GuiGraphicsHooks method returns true, it means we need to render the item with a custom amount.
            // If the feature is enabled in the config and the ItemStack is an EncodedPatternItem,
            // we will render the output item with its amount instead of the stack itself.
            if (ClientConfig.isDisplayAmountOnEncodedPattern() &&
                    stack.getItem() instanceof EncodedPatternItem encodedPattern) {
                var decoded = encodedPattern.decode(stack, level);
                if (decoded == null) {
//                    Ae_test.LOGGER.debug("Encoded pattern output is empty for stack: {}", GenericStack.unwrapItemStack(stack));
                    return true; // No output to render, skip further processing
                }
                var output = decoded.getPrimaryOutput();
//                Ae_test.LOGGER.debug("Encoded pattern output: {}", output);
                // Because the output item has already been rendered, we can skip rendering the stack itself.
                // only the amount text needs to be rendered.
                if (output != null) {
                    AEKeyRendering.drawInGui(
                            Minecraft.getInstance(),
                            guiGraphics,
                            x,
                            y, output.what());

                    if (output.amount() > 0) {
                        Font font = Minecraft.getInstance().font;
                        String amtText = output.what().formatAmount(output.amount(), AmountFormat.SLOT);
                        StackSizeRenderer.renderSizeLabel(guiGraphics, font, x, y, amtText, false);
                    }
                }
            }
            return true;
        }
        return false;
    }
    @Nullable
    public static ItemStack getItemStackWrappedWithAmount(ItemStack stack) {
        if (ClientConfig.isDisplayAmountOnEncodedPattern() &&
                stack.getItem() instanceof EncodedPatternItem<?> encodedPattern) {
            var decoded = encodedPattern.decode(stack, Minecraft.getInstance().level);
            if (decoded != null) {
                var output = decoded.getPrimaryOutput();
                if (output != null) {
                    return GenericStack.wrapInItemStack(output);
                }
            }
        }
        return null; // Return null if no output is found or feature is disabled in the config
    }
}
