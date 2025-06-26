package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.core.AEConfig;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.AE2ListBoxSubScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MEStorageScreen.class)
public abstract class MixinMEStorageScreen {
    @Unique
    private final AEConfig aeConfig = AEConfig.instance();
    @Shadow
    protected abstract void reinitalize();
    @Shadow
    protected abstract void setSearchText(String searchText);
    @Shadow
    @Final
    private AETextField searchField;
    @Inject(
            method = "onReturnFromSubScreen",
            at = @At("TAIL")
    )

    protected <SCREEN extends AEBaseScreen<?>> void onReturnFromSubScreen(AESubScreen<?, SCREEN> subScreen, CallbackInfo ci) {
        // Fix slot misalignment when returning from the terminal settings screen
        if (subScreen instanceof AE2ListBoxSubScreen<?,SCREEN>) {
            this.reinitalize();
            if (!aeConfig.isUseExternalSearch()) {
                setSearchText(searchField.getValue());
            }
        }
    }
}
