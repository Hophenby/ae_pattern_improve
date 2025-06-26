package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.style.WidgetStyle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ScreenStyle.class)
public class MixinScreenStyle {
    @Unique
    private static final Map<String, ScreenStyle> ae_pattern_improve$missingStyles = new HashMap<>();
    static {
        // Initialize the map with some default styles if needed
        ae_pattern_improve$missingStyles.put("multiplierButton", StyleManager.loadStyleDoc("/screens/terminals/modify_pattern_encoding_terminal.json"));
        ae_pattern_improve$missingStyles.put("patternProvider", StyleManager.loadStyleDoc("/screens/modify_pattern_provider.json"));
        ae_pattern_improve$missingStyles.put("patternModifier", StyleManager.loadStyleDoc("/screens/modify_pattern_modifier.json"));
    }
//    @Unique
//    private static final ScreenStyle STYLE = StyleManager.loadStyleDoc("/screens/terminals/modify_pattern_encoding_terminal.json");
    @Final
    @Shadow
    private Map<String, WidgetStyle> widgets;
    @Inject(method = "getWidget", at = @At("HEAD"), cancellable = true)
    private void onGetWidget(String id, CallbackInfoReturnable<WidgetStyle> cir) {
        for (String key : ae_pattern_improve$missingStyles.keySet()) {
            if (id.contains(key) && widgets.get(id) == null) {
                // If the style is missing, return the custom style
                cir.setReturnValue(ae_pattern_improve$missingStyles.get(key).getWidget(id));
                return;
            }
        }
    }
}
