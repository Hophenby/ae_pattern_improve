package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.client.ButtonUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermScreen.class)
public abstract class MixinPatternEncodingTermScreen {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PatternEncodingTermMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        AePatternImprove.LOGGER.debug("MixinPatternEncodingTermScreen: onInit called for {}", this.getClass().getName());
        ((MixinAEBaseScreen) this).ae_pattern_improve$addToLeftToolbar(ButtonUtils.createEncodingPreferenceScreenButton((PatternEncodingTermScreen<?>) (Object) this));
    }
}
