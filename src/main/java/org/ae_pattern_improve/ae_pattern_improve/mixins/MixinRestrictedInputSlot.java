package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.menu.slot.RestrictedInputSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.EncodedPatternWithAmountRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RestrictedInputSlot.class)
@OnlyIn(Dist.CLIENT)
public abstract class MixinRestrictedInputSlot {
    // If a pattern is in a pattern provider, show the pattern output with its amount in the slot.
    @Final
    @Shadow
    private RestrictedInputSlot.PlacableItemType which;
    @Inject(method = "getDisplayStack", at = @At("HEAD"), cancellable = true)
    private void modifyPatternDisplayItem(CallbackInfoReturnable<ItemStack> cir){
        if (this.which == RestrictedInputSlot.PlacableItemType.PROVIDER_PATTERN){
            ItemStack is = ((RestrictedInputSlot) (Object) this).getItem();
            // If the stack is a pattern, show the output with its amount.
            ItemStack output = EncodedPatternWithAmountRenderer.getItemStackWrappedWithAmount(is);
            if (output != null && !output.isEmpty()) {
                cir.setReturnValue(output);
            }
        }
    }
}
