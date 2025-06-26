package org.ae_pattern_improve.ae_pattern_improve.client;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.TextureTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;

@OnlyIn(Dist.CLIENT)
public final class BlitterGroup {
    private final Blitter head;
    private final Blitter tail;
    private final Blitter middleSegment;
    private int r = 255;
    private int g = 255;
    private int b = 255;
    private int a = 255;
    private Rect2i destRect = new Rect2i(0, 0, 0, 0);
    private boolean blending = true;
    private TextureTransform transform = TextureTransform.NONE;
    private int zOffset;

    BlitterGroup(Blitter head, Blitter tail, Blitter middleSegment) {
        this.head = head;
        this.tail = tail;
        this.middleSegment = middleSegment;
    }
    public static BlitterGroup createFromSplittingBlitter(Blitter blitter, int headHeight, int tailHeight) {
        var srcX = blitter.getSrcX();
        var srcY = blitter.getSrcY();
        var srcWidth = blitter.getSrcWidth();
        var srcHeight = blitter.getSrcHeight();
        return new BlitterGroup(
                blitter.copy().src(srcX, srcY, srcWidth, headHeight),
                blitter.copy().src(srcX, srcY + srcHeight - tailHeight, srcWidth, tailHeight),
                blitter.copy().src(srcX, srcY + headHeight, srcWidth, 1)
        );
    }
    public static BlitterGroup createFromGuiSprite(ResourceLocation sprite, int headHeight, int tailHeight){
        return createFromSplittingBlitter(
                Blitter.guiSprite(sprite),
                headHeight,
                tailHeight
        );
    }
    public BlitterGroup dest(int x, int y){
        this.destRect = new Rect2i(x, y, 0, 0);
        return this;
    }
    public void vBlit(GuiGraphics gui, int segments){
        if (segments <= 0) {
            return;
        }
        vBlit(gui, head);
        if (segments > 1) {
            for (int i = 0; i < segments - 2; i++) {
                vBlit(gui, middleSegment);
            }
        }
        vBlit(gui, tail);
    }
    public void vBlitFixedHeight(GuiGraphics gui, int fixedHeight){
        if (fixedHeight <= head.getSrcHeight() + tail.getSrcHeight()) {
            AePatternImprove.LOGGER.warn("Fixed height {} is too small for the blitter group with head height {} and tail height {}",
                    fixedHeight, head.getSrcHeight(), tail.getSrcHeight());
            return;
        }
        int segments = fixedHeight - head.getSrcHeight() - tail.getSrcHeight();
        vBlit(gui, segments);
    }
    private void vBlit(GuiGraphics gui, Blitter blitter){
        blitter.dest(destRect).color(r, g, b, a).blending(blending).transform(transform).zOffset(zOffset).blit(gui);
        destRect = new Rect2i(destRect.getX(), destRect.getY() + blitter.getSrcHeight(), destRect.getWidth(), 0);
    }

    public BlitterGroup copy() {
        var bg = new BlitterGroup(
                head.copy(),
                tail.copy(),
                middleSegment.copy()
        );
        bg.r = this.r;
        bg.g = this.g;
        bg.b = this.b;
        bg.a = this.a;
        bg.destRect = this.destRect;
        bg.blending = this.blending;
        bg.transform = this.transform;
        bg.zOffset = this.zOffset;
        return bg;
    }
}
