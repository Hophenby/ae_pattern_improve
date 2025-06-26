package org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae;

import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.common.me.itemhost.HostPatternModifier;
import org.ae_pattern_improve.ae_pattern_improve.config.CommonConfig;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.exae.ExAEReflect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HostPatternModifier.class)
public abstract class MixinHostPatternModifier {

    // inject point directly after the constructor of super class HostPatternModifier
    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/glodblock/github/extendedae/common/me/itemhost/HostPatternModifier;getItemStack()Lnet/minecraft/world/item/ItemStack;")
    )
    private void ae_pattern_improve$initPatternInv(CallbackInfo ci) {
        // This is a workaround for the issue where the HostPatternModifier does not have its patternInv initialized.
        // We want to initialize it so that it can be used later.
        // This is done by calling the setPatternInv method in ExAEReflect.
        // This is a hack, but it works.
        ExAEReflect.setPatternModifierInv(ae_pattern_improve$getSelf(), new AppEngInternalInventory(ae_pattern_improve$getSelf(), CommonConfig.maxPatternModifierSize.get()));
    }
    @Unique
    private HostPatternModifier ae_pattern_improve$getSelf() {
        return (HostPatternModifier) (Object) this;
    }
}
