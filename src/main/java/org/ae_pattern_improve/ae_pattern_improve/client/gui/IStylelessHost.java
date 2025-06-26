package org.ae_pattern_improve.ae_pattern_improve.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.Map;

/**
 * An interface for gui hosts that manage IStyleless widgets, which do not use the style-sheet.
 *
 */
public interface IStylelessHost {
    void addSubWidget(String id, AbstractWidget widget, Map<String, AbstractWidget> subWidgets);

    void removeSubWidget(String widgetId);
    /**
     * Pre-initializes widgets that do not use the style-sheet.
     */
    void preInitWidgets();


    void postInitWidgets();

    void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY);

    void updateBeforeRender();

    void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks);

    <RENDERABLE extends Renderable> RENDERABLE addRenderableOnly(RENDERABLE renderable);

    void removeRenderableOnly(GuiEventListener renderable);

    int getGuiTop();

    int getGuiLeft();
}
