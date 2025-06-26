package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

public interface IRenderableTooltip extends Renderable, ITooltip, ICompositeWidget, GuiEventListener {

    @Override
    default void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
    }
    @Override
    default void setFocused(boolean b) {
    }

    @Override
    default boolean isFocused() {
        return false;
    }

    @Override
    default void setPosition(Point position) {

    }

    @Override
    default void setSize(int width, int height) {

    }
}
