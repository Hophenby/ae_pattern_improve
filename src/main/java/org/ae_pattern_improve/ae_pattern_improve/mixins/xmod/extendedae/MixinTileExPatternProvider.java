package org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae;

import appeng.helpers.patternprovider.PatternProviderLogic;
import com.glodblock.github.extendedae.common.tileentities.TileExPatternProvider;
import org.ae_pattern_improve.ae_pattern_improve.config.CommonConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileExPatternProvider.class)
public class MixinTileExPatternProvider {

    @Unique
    private TileExPatternProvider ae_pattern_improve$getSelf() {
        return (TileExPatternProvider) (Object) this;
    }
    @Inject(
            method = "createLogic",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyCreateLogic(CallbackInfoReturnable<PatternProviderLogic> cir) {
        // This method is intentionally left empty to prevent the original logic from executing.
        // The logic is handled in the XModUtils class.
        if (CommonConfig.maxPatternSize.get() > 36){
            cir.setReturnValue(new PatternProviderLogic(ae_pattern_improve$getSelf().getMainNode(), ae_pattern_improve$getSelf(), CommonConfig.maxPatternSize.get()));
        }
    }
}
