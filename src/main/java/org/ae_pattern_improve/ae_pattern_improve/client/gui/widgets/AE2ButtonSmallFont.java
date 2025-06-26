package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.ITooltip;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AE2ButtonSmallFont extends AE2Button implements ITooltip {
    private static final float SCALE_FACTOR = 0.5f;
    private static final float SCALE_FACTOR_INV = 2.0f;

    private final List<Component> tooltips = new ArrayList<>();

    public AE2ButtonSmallFont(Component component, OnPress onPress) {
        super(component, onPress);
    }

    @Override
    protected void renderButtonText(GuiGraphics pGuiGraphics, Font pFont, int pWidth, int pColor, int yOffset) {
        // Use a smaller font size for the button text
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, 1.0f);
        Component line = this.getMessage();
        int w = pFont.width(line);
        pGuiGraphics.drawString(pFont, line,
                (int) ((this.getX() + (float) (this.getWidth()) / 2 - w * SCALE_FACTOR / 2) * SCALE_FACTOR_INV),
                (int) ((this.getY() + (float) (this.getHeight()) / 2 - 2) * SCALE_FACTOR_INV), pColor, false);
        poseStack.popPose();
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }
    public void addTooltip(Component tooltip) {
        tooltips.add(tooltip);
    }

    @Override
    public List<Component> getTooltipMessage() {
        return tooltips;
    }
}
