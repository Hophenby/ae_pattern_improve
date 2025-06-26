package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.client.gui.me.patternaccess.PatternSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.EncodedPatternWithAmountRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(PatternSlot.class)
public abstract class MixinPatternSlot {
    // If a pattern is to be shown in the pattern access terminal, show the pattern output with its amount in the slot.
    // This is done by the client, so the server does not need to send the output amount.
    @Inject(method = "getDisplayStack", at = @At("HEAD"), cancellable = true)
    private void modifyPatternDisplayItem(CallbackInfoReturnable<ItemStack> cir){
        ItemStack is = ((PatternSlot) (Object) this).getItem();
        // If the stack is a pattern, show the output with its amount.
        ItemStack output = EncodedPatternWithAmountRenderer.getItemStackWrappedWithAmount(is);
        if (output != null && !output.isEmpty()) {
            cir.setReturnValue(output);
        }
    }
}
