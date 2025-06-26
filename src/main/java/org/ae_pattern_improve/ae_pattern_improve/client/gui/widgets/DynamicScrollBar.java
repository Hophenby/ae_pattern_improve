package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.widgets.Scrollbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.ae_pattern_improve.ae_pattern_improve.client.BlitterGroup;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IScrollbarAccessor;

public class DynamicScrollBar extends Scrollbar {
    // automatically adjust the scrollbar size based on the content
    private final DynamicStyle style;
    private int segments = 0;

    public DynamicScrollBar(DynamicStyle style) {
        this.style = style;
    }
    @Override
    public Rect2i getBounds() {
        return new Rect2i(getDisplayX(), getDisplayY(), style.handleWidth(), ((IScrollbarAccessor) this).ae_pattern_improve$getHeight());
    }

    @Override
    public void setRange(int min, int max, int pageSize) {
        super.setRange(min, max, pageSize);
        resetSegments();
//        Ae_test.LOGGER.debug("Scrollbar segments: {}", segments);
    }
    private void resetSegments() {
        this.segments = ((IScrollbarAccessor) this).ae_pattern_improve$getHeight()
                / ((IScrollbarAccessor) this).ae_pattern_improve$getPageSize()
                / (((IScrollbarAccessor) this).ae_pattern_improve$getRange() + 1);
    }

    @Override
    public DynamicScrollBar setHeight(int v) {
        super.setHeight(v);
        resetSegments();
        return this;
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        // Draw the track (nice for debugging)
//         guiGraphics.fill( displayX, displayY, this.displayX + width, this.displayY +
//         height, 0xffff0000);

        int yOffset;
        BlitterGroup image;
        if (((IScrollbarAccessor) this).ae_pattern_improve$getRange() == 0) {
            yOffset = 0;
            image = style.disabledBlitter();
        } else {
            yOffset = getHandleYOffset(segments);
            image = style.enabledBlitter();
        }
        image = image.copy();
        image.dest(this.getDisplayX(), this.getDisplayY() + yOffset).vBlit(guiGraphics, segments);
    }
    private int getDisplayX() {
        return super.getBounds().getX();
    }
    private int getDisplayY() {
        return super.getBounds().getY();
    }

    private int getHandleYOffset(int segments) {
        if (((IScrollbarAccessor) this).ae_pattern_improve$getRange() == 0) {
            return 0;
        }
        int availableHeight = ((IScrollbarAccessor) this).ae_pattern_improve$getHeight() - style.handleHeight(segments);
        return (this.getCurrentScroll() - (((IScrollbarAccessor) this).ae_pattern_improve$getMin())) * availableHeight / ((IScrollbarAccessor) this).ae_pattern_improve$getRange();
    }
    public static final DynamicStyle SMALL = DynamicStyle.ofCommonScrollbarStyle(Scrollbar.SMALL);
    public static final DynamicStyle BIG = DynamicStyle.ofCommonScrollbarStyle(Scrollbar.BIG);
    @SuppressWarnings("resource")
    public record DynamicStyle(ResourceLocation disabled,
                                ResourceLocation enabled,
                               int headerHeight, int footerHeight){
        public int handleWidth() {
            var minecraft = Minecraft.getInstance();
            return minecraft.getGuiSprites().getSprite(disabled).contents().width();
        }
        public int handleHeight(int segments) {
            return headerHeight + segments + footerHeight;
        }
        public BlitterGroup disabledBlitter() {
            return BlitterGroup.createFromGuiSprite(disabled, headerHeight, footerHeight);
        }
        public BlitterGroup enabledBlitter() {
            return BlitterGroup.createFromGuiSprite(enabled, headerHeight, footerHeight);
        }
        public static DynamicStyle ofCommonScrollbarStyle(Scrollbar.Style style) {
            return new DynamicStyle(
                    style.disabledSprite(),
                    style.enabledSprite(),
                    2, 4
            );
        }
    }


}
