package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import appeng.core.AppEng;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.IListBoxHost;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HoldAndDrag extends AbstractWidget implements ICompositeWidget, ITooltip {
    private static final Blitter TEXTURE = Blitter.texture(AppEng.makeId("textures/guis/states.png")).src(208, 224, 10, 10);

    private DragCallback onDragCallback;
    private boolean isDragging;
    private DropCallback onDropCallback;
    private final IListBoxHost host;

    public HoldAndDrag(int x, int y, int width, int height, IListBoxHost host) {
        super(x, y, width, height, Component.translatable("ae_pattern_improve.listbox.hold_and_drag"));
        this.host = host;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float partialTick) {
        // Render the hold and drag texture
        TEXTURE.dest(getX() + host.getGuiLeft(), getY() + host.getGuiTop())
                .blit(guiGraphics);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
    }

    @Override
    public void setPosition(Point position) {
        var x = position.getX();
        var y = position.getY();
        setX(x);
        setY(y);
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        //.move(host.getGuiLeft(), host.getGuiTop())
//        Ae_test.LOGGER.debug("HoldAndDrag: onDrag: mousePos: x = {}, y = {}, button: {}", mousePos.getX(), mousePos.getY(), button);
//        Ae_test.LOGGER.debug("HoldAndDrag: bounds: x = {}, y = {}, w = {}, h = {}, contains: {}",
//                this.getBounds().getX(),
//                this.getBounds().getY(),
//                this.getBounds().getWidth(),
//                this.getBounds().getHeight(),
//                this.getBounds().contains(mousePos.getX(), mousePos.getY()));
        if (button == 0 && mousePos.isIn(this.getBounds())) { // Left mouse button
            isDragging = true; // Set dragging state
            if (onDragCallback != null) {
                return onDragCallback.onDrag(mousePos);
            }
        }
        return ICompositeWidget.super.onMouseDown(mousePos, button);
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int button) {
        return ICompositeWidget.super.onMouseDrag(mousePos, button); // Return false to allow further processing
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        if (isDragging && onDropCallback != null) {
            onDropCallback.onDrop(mousePos);
            isDragging = false; // Reset dragging state
            return true; // Indicate that the drag was handled
        }
        return ICompositeWidget.super.onMouseUp(mousePos, button); // Return false to allow further processing
    }

    @Override
    public boolean wantsAllMouseUpEvents() {
        return true;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (onDragCallback != null && this.getBounds().contains((int) dragX, (int) dragY)) {
            isDragging = onDragCallback.onDrag(new Point((int) mouseX, (int) mouseY));
//            Ae_test.LOGGER.debug("HoldAndDrag: onDragCallback returned {}", isDragging);
        }
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }
    @Override
    public void onRelease(double mouseX, double mouseY) {
        isDragging = false; // Reset dragging state
        if (onDropCallback != null) onDropCallback.onDrop(new Point((int) mouseX, (int) mouseY));
        super.onRelease(mouseX, mouseY);
    }

    public void setOnDropCallback(DropCallback onDropCallback) {
        this.onDropCallback = onDropCallback;
    }

    public void setOnDragCallback(DragCallback onDragCallback) {
        this.onDragCallback = onDragCallback;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return List.of(Component.translatable("ae_pattern_improve.listbox.hold_and_drag.tooltip.title"),
                Component.translatable("ae_pattern_improve.listbox.hold_and_drag.tooltip"));
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX() + host.getGuiLeft(), getY() + host.getGuiTop(), getWidth(), getHeight());
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    @FunctionalInterface
    public interface DragCallback {
        boolean onDrag(Point mousePos);
    }
    @FunctionalInterface
    public interface DropCallback {
        void onDrop(Point mousePos);
    }
}
