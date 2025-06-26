package org.ae_pattern_improve.ae_pattern_improve.mixin_helpers;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.style.Blitter;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import org.ae_pattern_improve.ae_pattern_improve.client.BlitterGroup;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.DynamicScrollBar;
import org.ae_pattern_improve.ae_pattern_improve.mixins.MixinSlot;
import org.ae_pattern_improve.ae_pattern_improve.mixins.client.MixinAEBaseScreen;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IScrollableInvScreen<MENU extends AEBaseMenu, SCREEN extends AEBaseScreen<MENU>> {
    BlitterGroup ae_pattern_improve$SCROLLBAR_BG = BlitterGroup.createFromSplittingBlitter(
            Blitter.texture("guis/pattern_provider_scrollbar.png").src(0, 0, 21, 103),
            17, 18
    );
    int SLOT_HEIGHT = 18;
    int SLOT_NUMBER_PER_ROW = 9;
    int BG_WIDTH = 21;

    @NotNull
    MENU ae_pattern_improve$getMenu();
    SCREEN ae_pattern_improve$getSelf();

    boolean ae_pattern_improve$shouldAddScrollBar();
    void ae_pattern_improve$setScrollBar(DynamicScrollBar scrollBar);
    DynamicScrollBar ae_pattern_improve$getScrollBar();

    List<AppEngSlot> ae_pattern_improve$getScrollerSlots();
    SlotSemantic ae_pattern_improve$getScrollableSlotSemantic();
    int ae_pattern_improve$getEffectiveRowCount();

    int ae_pattern_improve$getBGHeight();
    String ae_pattern_improve$getStylesheetName();
    default boolean ae_pattern_improve$isVisible() {
        return true;
    }

    default void ae_pattern_improve$initScrollBar() {
        if (ae_pattern_improve$shouldAddScrollBar()) {
            DynamicScrollBar ae_pattern_improve$scrollBar = new DynamicScrollBar(DynamicScrollBar.BIG){
                @Override
                public boolean isVisible() {
                    return ae_pattern_improve$isVisible();
                }
            }.setHeight(SLOT_HEIGHT * ae_pattern_improve$getEffectiveRowCount());
            ae_pattern_improve$scrollBar.setRange(0, (int) Math.ceil(
                    (ae_pattern_improve$getMenu().getSlots(ae_pattern_improve$getScrollableSlotSemantic()).size()
                            - (ae_pattern_improve$getEffectiveRowCount() * SLOT_NUMBER_PER_ROW)
                    ) / (float) SLOT_NUMBER_PER_ROW), 1);
            ((MixinAEBaseScreen) this).ae_pattern_improve$getWidgets().add(ae_pattern_improve$getStylesheetName() + "Scrollbar", ae_pattern_improve$scrollBar);
            ae_pattern_improve$setScrollBar(ae_pattern_improve$scrollBar);
            ((MixinAEBaseScreen) this).ae_pattern_improve$getWidgets().add(ae_pattern_improve$getStylesheetName() + "ScrollbarBackground",
                    new ICompositeWidget() {
                        private int x = 0;
                        private int y = 0;
                        @Override
                        public void setPosition(Point position) {
                            x = position.getX();
                            y = position.getY();
                        }

                        @Override
                        public void setSize(int width, int height) {}

                        @Override
                        public Rect2i getBounds() {
                            return new Rect2i(x, y, BG_WIDTH, ae_pattern_improve$getBGHeight());
                        }

                        @Override
                        public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
                            ICompositeWidget.super.drawBackgroundLayer(guiGraphics, bounds, mouse);
                            ae_pattern_improve$SCROLLBAR_BG.copy().dest(x + ae_pattern_improve$getSelf().getGuiLeft() , y + ae_pattern_improve$getSelf().getGuiTop())
                                    .vBlitFixedHeight(guiGraphics, ae_pattern_improve$getBGHeight());
                        }

                        @Override
                        public boolean isVisible() {
                            return ae_pattern_improve$isVisible();
                        }
                    });
        }
    }
    default void ae_pattern_improve$repositionSlots(){
        if (ae_pattern_improve$getScrollBar() != null && ae_pattern_improve$isVisible()) {
            ae_pattern_improve$getSelf()
                    .repositionSlots(ae_pattern_improve$getScrollableSlotSemantic());

            for (int i = 0; i < ae_pattern_improve$getScrollerSlots().size(); i++) {
                var slot = ae_pattern_improve$getScrollerSlots().get(i);
                var effectiveRow = (i / SLOT_NUMBER_PER_ROW) - ae_pattern_improve$getScrollBar().getCurrentScroll();

                slot.setActive(effectiveRow >= 0 && effectiveRow < ae_pattern_improve$getEffectiveRowCount());
                ((MixinSlot) slot).setY(slot.y - ae_pattern_improve$getScrollBar().getCurrentScroll() * SLOT_HEIGHT);
            }
        }
    }
}
