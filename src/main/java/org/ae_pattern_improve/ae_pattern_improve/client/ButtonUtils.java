package org.ae_pattern_improve.ae_pattern_improve.client;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.AE2ListBoxSubScreen;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.AE2ButtonSmallFont;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IMulableTermMenu;

import java.util.List;

public final class ButtonUtils {
    private static final Blitter ENCODING_PREFERENCE_BLITTER =
            Blitter.guiSprite(AePatternImprove.getRL("encoding_preference"));
    public static <MENU extends AEBaseMenu,
            SCREEN extends AEBaseScreen<MENU>> Button createEncodingPreferenceScreenButton(
                    SCREEN screen) {
        return new IconButton(
                act -> screen.switchToScreen(
                        new AE2ListBoxSubScreen<MENU, AEBaseScreen<MENU>>(screen)
                )){
            @Override
            protected Icon getIcon() {
                return null;
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partial);

                if (this.visible) {
                    ENCODING_PREFERENCE_BLITTER
                            .dest(getX(), getY() + 1 + (isHovered() ? 1 : 0)).zOffset(3).blit(guiGraphics);
                }
            }

            @Override
            public List<Component> getTooltipMessage() {
                return List.of(
                        Component.translatable("ae_pattern_improve.listbox.encoding_preference.tooltip"),
                        Component.translatable("ae_pattern_improve.listbox.encoding_preference.tooltip.description")
                );
            }
        };
    }
    public static <MENU extends IMulableTermMenu> Button getMultiplyButton(MENU menu, int multiplier, boolean isDiv) {
        var btn = new AE2ButtonSmallFont(getDisplayNumber(multiplier, isDiv),
                b -> menu.mulPatternEncoding(multiplier, isDiv));
        btn.setSize(10, 8);
        btn.addTooltip(getDisplayNumber(multiplier, isDiv));
        if (!isDiv) {
            btn.addTooltip(Component.translatable("gui.ae_pattern_improve.pattern_modifier.mul.desc", multiplier));
        } else {
            btn.addTooltip(Component.translatable("gui.ae_pattern_improve.pattern_modifier.div.desc", multiplier));
        }
        return btn;
    }
    private static Component getDisplayNumber(int number, boolean isDiv) {
        return isDiv ? Component.literal("÷" + number) :  Component.literal("x" + number);
    }
}
