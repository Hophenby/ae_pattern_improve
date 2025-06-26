package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.EncodingModePanel;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.items.ProcessingEncodingPanel;
import net.minecraft.client.gui.components.Button;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientConfig;
import org.ae_pattern_improve.ae_pattern_improve.client.ButtonUtils;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IMulableTermMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProcessingEncodingPanel.class)
public abstract class MixinProcessingEncodingPanel extends EncodingModePanel {
    public MixinProcessingEncodingPanel(PatternEncodingTermScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Unique
    private final Button[] ae_pattern_improve$multiplierButtons = new Button[6];

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {
        if (ClientConfig.isMultiplierButtonEnabled()) {
            int[] multipliers = {2, 3, 5};
            for (int i = 0; i < 6; i++) {
                boolean isDiv = i < 3;
                int multiplier = isDiv ? multipliers[i] : multipliers[i - 3];
                ae_pattern_improve$multiplierButtons[i] = ButtonUtils.getMultiplyButton((IMulableTermMenu) menu, multiplier, isDiv);
                widgets.add("multiplierButton" + (isDiv ? "Div" : "Mul") + multiplier, ae_pattern_improve$multiplierButtons[i]);
            }
        }
    }

    // This mixin is used to add custom functionality to the ProcessingEncodingPanel class.
    @Inject(method = "setVisible", at = @At("TAIL"))
    private void onSetVisible(boolean visible, CallbackInfo ci) {
        for (Button button : ae_pattern_improve$multiplierButtons) {
            if (button != null && ClientConfig.isMultiplierButtonEnabled()) {
                button.visible = visible;
            }
        }
    }

}
