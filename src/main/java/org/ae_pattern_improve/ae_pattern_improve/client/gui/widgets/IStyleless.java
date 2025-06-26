package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.client.gui.AEBaseScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;

import java.util.Map;
import java.util.function.Consumer;

/** An interface functioning as a javadoc container for widgets that do not use the style-sheet
 * * <p>
 *     <li> Classes implementing this interface means that they contain widgets that do not use the style-sheet.
 *     <li> Override the {@link IStyleless#preInit(Map)} methods to remove widgets from the widget container to get rid of the style-sheet check
 *               in {@link appeng.client.gui.WidgetContainer#populateScreen(Consumer, Rect2i, AEBaseScreen)}.
 *     <li> You can also use this to manage widgets that appear dynamically or conditionally.
 *     <li> Override the {@link IStyleless#postInit()} method to initialize the styleless widgets for this item.
 *  </p>
 */
public interface IStyleless {

    /// remove widgets to skip the style-sheet check in method {@link appeng.client.gui.WidgetContainer#populateScreen(Consumer, Rect2i, AEBaseScreen)}
    void preInit(Map<String, AbstractWidget> widgetContainer);
    /**
     * Implement this method to initialize the styleless widgets for this item.
     */
    void postInit();

    default void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {

    }

    default void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {

    }

    default void updateBeforeRender() {
    }
}
