package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.api.stacks.AEKey;
import appeng.util.ConfigInventory;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IOversizedInv;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConfigInventory.class)
public abstract class MixinConfigInventory implements IOversizedInv {
    @Unique
    private boolean ae_pattern_improve$oversizedInv = false;
    @Unique
    @Override
    public void ae_pattern_improve$setOversizedInv(boolean oversized) {
        this.ae_pattern_improve$oversizedInv = oversized;
    }

    @Unique
    @Override
    public boolean ae_pattern_improve$isOversizedInv() {
        return ae_pattern_improve$oversizedInv;
    }
    @Inject(
            method = "getMaxAmount",
            at = @At("TAIL"),
            cancellable = true)
    private void setMaxAmount(AEKey key, CallbackInfoReturnable<Long> cir) {
        if (ae_pattern_improve$isOversizedInv()) {
            cir.setReturnValue((long) Integer.MAX_VALUE);
        }
    }
}
