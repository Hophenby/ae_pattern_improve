package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.api.stacks.GenericStack;
import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientComparatorConfig;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.EntryComparators;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.IComparatorItem;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.IListBoxHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExtendableButtonGroup implements ICompositeWidget, IStyleless {
    private static final Blitter PLUS_BLITTER = Blitter.guiSprite(AePatternImprove.getRL("plus_icon"));
    private static final Blitter PLUS_BLITTER_ACTIVATED = Blitter.guiSprite(AePatternImprove.getRL("plus_icon_activated"));
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private boolean extended = false;
    private final IListBoxHost host;
    private final Map<String, AbstractWidget> buttons = new HashMap<>();
    private IRenderableTooltip tooltipPlaceholder;

    public ExtendableButtonGroup(IListBoxHost host) {
        this.host = host;
    }

    @Override
    public void setPosition(Point position) {
        x = position.getX();
        y = position.getY();
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        if (button == 0 && mousePos.isIn(getBounds())) {
            extended = !extended;
            host.requireExtendableButtonsUpdate();
            return true; // Indicate that the mouse down event was handled
        }
        return ICompositeWidget.super.onMouseDown(mousePos, button);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        IStyleless.super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var blitter = extended ? PLUS_BLITTER_ACTIVATED : PLUS_BLITTER;
        blitter.dest(x + width - 18, y + 3).blit(guiGraphics);
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x, y, width, height);
    }

    @Override
    public void updateBeforeRender() {
        ICompositeWidget.super.updateBeforeRender();
    }

    private void initButtons() {
        // Initialize buttons here
        // This method should be called when the group is extended or collapsed
        buttons.clear();
        if (extended) {
            var buttonEntries = ClientComparatorConfig.getRegisteredComparators().entrySet();
            buttonEntries.forEach(entry -> {
                var comparator = entry.getValue();
                {
                    Blitter icon = Optional.ofNullable(comparator.getIcon()).orElse(Blitter.guiSprite(AePatternImprove.getRL("slot")));
                    Component tooltip = Optional.ofNullable(comparator.tooltip()).orElse(Component.translatable("ae_pattern_improve.listbox.comparator.tooltip", entry.getKey()));
                    IComparatorItem.ConfigWritable<?> item = comparator.supplier().get();
                    Rect2i buttonBounds = new Rect2i(x - buttons.size() * 20 + 2, y + height + 3, 16, 16);
                    ExtButton button;
                    if (item instanceof EntryComparators<?> entryComparator){
                        var config = entryComparator.getAEKey();
                        button = new ExtButton(
                                buttonBounds,
                                icon,
                                List.of(tooltip, Component.literal(config.getId().toString())),
                                btn -> host.addItem(item),
                                new GenericStack(config, 0)
                        );
                    } else {
                        button = new ExtButton(
                                buttonBounds,
                                icon,
                                List.of(Component.literal(entry.getKey()), tooltip),
                                btn -> host.addItem(item)
                        );
                    }
                    button.visible = true;
                    String name = entry.getKey();
                    host.addSubWidget("addComparatorButton_" + name, button, buttons);
                }
            });
        }
    }

    @Override
    public void preInit(Map<String, AbstractWidget> widgetContainer) {
        buttons.keySet().forEach(host::removeSubWidget);
        host.removeRenderableOnly(tooltipPlaceholder);
        buttons.forEach(widgetContainer::remove);
    }

    @Override
    public void postInit() {
        initButtons();
        tooltipPlaceholder = host.addRenderableOnly(new IRenderableTooltip() {
            @Override
            public Rect2i getBounds() {
                return new Rect2i(x + host.getGuiLeft(), y + host.getGuiTop(), width, height);
            }

            @Override
            public List<Component> getTooltipMessage() {
                return List.of(
                        Component.translatable("ae_pattern_improve.listbox.extendable_button_group.tooltip"),
                        Component.translatable("ae_pattern_improve.listbox.extendable_button_group.tooltip.description")
                );
            }

            @Override
            public Rect2i getTooltipArea() {
                return getBounds();
            }

            @Override
            public boolean isTooltipAreaVisible() {
                return isVisible();
            }
        });
    }

    public static class ExtButton extends AE2Button implements ITooltip {
        private final Blitter icon;
        private final @Nullable GenericStack displayStack;
        private final List<Component> tooltips;
        public ExtButton(Rect2i rect, Blitter icon, List<Component> tooltips, OnPress onPress) {
            this(rect, icon, tooltips, onPress, null);
        }
        public ExtButton(Rect2i rect, Blitter icon, List<Component> tooltips, OnPress onPress, @Nullable GenericStack displayStack) {
            super(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), tooltips.getFirst(), onPress);
            this.icon = icon;
            this.displayStack = displayStack;
            this.tooltips = tooltips;
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            //super.renderWidget(graphics, mouseX, mouseY, delta);
            icon.dest(getX(), getY()).blit(graphics);
            if (displayStack != null) {
                graphics.renderItem(GenericStack.wrapInItemStack(displayStack), getX() + 1, getY() + 1);
            }
        }

        @Override
        public List<Component> getTooltipMessage() {
            return List.copyOf(tooltips);
        }

        @Override
        public Rect2i getTooltipArea() {
            return new Rect2i(getX(), getY(), getWidth(), getHeight());
        }

        @Override
        public boolean isTooltipAreaVisible() {
            return visible;
        }
    }
}
