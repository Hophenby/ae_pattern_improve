package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;
import appeng.core.AppEng;
import appeng.util.ConfigMenuInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientComparatorConfig;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.EntryComparators;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.IComparatorItem;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.IListBoxHost;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.slot.ClientFakeSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author merequester
 * @see com.almostreliable.merequester.client.widgets.RequestWidget#preInit(java.util.Map)
 */
@SuppressWarnings("unused")
public class AE2ListBoxItem implements IStyleless {
    private static final Blitter SLOT_BLITTER = Blitter.guiSprite(AePatternImprove.getRL("slot"));
    protected static final Blitter SRC_BG = Blitter.texture(AppEng.makeId("textures/elements/list_box.png"));
    protected static final Blitter UNSELECTED_BG = SRC_BG.copy().src(128, 0, 98, 24);
    protected static final Blitter SELECTING_BG = SRC_BG.copy().src(128, 24, 98, 24);
    protected static final Blitter SELECTED_BG = SRC_BG.copy().src(128, 48, 98, 24);
    protected final IListBoxHost host;
    protected final int index;

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    protected int x;
    protected int y;
    public static final int ITEM_HEIGHT = 25; // height of a single item in the list box
    protected final ScreenStyle style;
    protected final Map<String, AbstractWidget> subWidgets = new HashMap<>();

    public IComparatorItem.ConfigWritable<?> getComparatorItem() {
        return comparatorItem;
    }

    private final IComparatorItem.ConfigWritable<?> comparatorItem;
    /// HoldAndDrag widget for dragging items in the list box
    protected HoldAndDrag holdAndDrag;
    protected Button deleteButton;
    protected ClientFakeSlot slot;
    protected IRenderableTooltip tooltip;
    private boolean visible = true;
    private boolean selected = false;

    public AE2ListBoxItem(IListBoxHost host, int index, int x, int y, ScreenStyle style, IComparatorItem.ConfigWritable<?> comparatorItem) {
        this.host = host;
        this.index = index;
        this.x = x;
        this.y = y;
        this.style = style;
        this.comparatorItem = comparatorItem;
    }

    @Override
    public void preInit(Map<String, AbstractWidget> widgetContainer) {
        subWidgets.keySet().forEach(host::removeSubWidget);
        host.removeRenderableOnly(tooltip);
        subWidgets.forEach(widgetContainer::remove);
    }

    @Override
    public void postInit() {
        holdAndDrag = new HoldAndDrag(x + 162, y + 2, 8, 8, this.host);
        holdAndDrag.setOnDragCallback(mousePos -> {
            host.handleItemOnDrag(this);
            return true; // return true to indicate that the drag is handled
        });
        holdAndDrag.setOnDropCallback(host::handleItemOnDrop);
        host.addSubWidget("hold_and_drag_" + index, holdAndDrag, subWidgets);
        slot = ClientFakeSlot.create(this::slotChanged);
        slot.setPosition(x + 2, y + 2);
        slot.rawSet(comparatorItem instanceof EntryComparators<?> ?
                GenericStack.wrapInItemStack(((EntryComparators<?>) comparatorItem).getAEKey(), 1) :
                ItemStack.EMPTY);
        deleteButton = new IconButton(button -> host.deleteItem(comparatorItem)){
            @Override
            protected Icon getIcon() {
                return Icon.CONDENSER_OUTPUT_TRASH;
            }

            @Override
            public List<Component> getTooltipMessage() {
                return List.of(
                        Component.translatable("ae_pattern_improve.listbox.delete_button.tooltip"),
                        Component.translatable("ae_pattern_improve.listbox.delete_button.tooltip.description")
                );
            }
        };
        host.addSubWidget("delete_button_" + index, deleteButton, subWidgets);
        tooltip = host.addRenderableOnly(new IRenderableTooltip(){

            @Override
            public boolean isVisible() {
                return visible;
            }


            @Override
            public Rect2i getBounds() {
                return new Rect2i(x + 2 + host.getGuiLeft(), y + 2 + host.getGuiTop(), 16, 16);
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                var blitterMeta = ClientComparatorConfig.getRegisteredComparators().getOrDefault(comparatorItem.toConfigString(), null);
                if (blitterMeta != null && isVisible()) {
                    Blitter blitter = blitterMeta.getIcon();
                    if (blitter != null) {
                        blitter.dest(x + 2 + host.getGuiLeft(), y + 2 + host.getGuiTop())
                                .blit(guiGraphics);
                    }
                }
            }

            @Override
            public List<Component> getTooltipMessage() {
                return Optional.ofNullable(ClientComparatorConfig.getRegisteredComparators().getOrDefault(comparatorItem.toConfigString(), null)).stream()
                        .map(ClientComparatorConfig.ComparatorRegistry::tooltip)
                        .filter(Objects::nonNull)
                        .collect(() -> new ArrayList<>(List.of(
                                Component.translatable("ae_pattern_improve.listbox.base_tooltip")
                        )), ArrayList::add, ArrayList::addAll);
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

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {

        // debugging zone
//        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Index: " + index), x - 200, y + 2, 0x00FF00);
//        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(comparatorItem.toConfigString()), x - 200, y + 12, 0x00FF00);
    }
    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            Blitter blitter = selected ? SELECTED_BG : UNSELECTED_BG;
            blitter.dest(x + offsetX, y + offsetY).blit(guiGraphics);
            if (slot.isActive()) SLOT_BLITTER.dest(x + 2 + offsetX, y + 3 + offsetY).blit(guiGraphics);
        }
    }
    private void slotChanged(ConfigMenuInventory inv) {
        AEKey key = inv.getDelegate().getKey(0);
        if (key != null) {
//            Ae_test.LOGGER.debug("AE2ListBoxItem: slotChanged: {}", key);
            host.substituteItem(comparatorItem, EntryComparators.fromAEKey(key));
        }
    }

    protected boolean needsSlot(){
        return comparatorItem instanceof EntryComparators<?>;
    }

    public void setVisible(boolean visible) {
        holdAndDrag.visible = visible;
        slot.setActive(visible && needsSlot());
        deleteButton.visible = visible;
        this.visible = visible;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        slot.setPosition(x + 3, y + 4);
//        deleteButton.setPosition(x + 180, y + 2);
    }

    @Override
    public void updateBeforeRender() {
        host.addSlot(slot);
        deleteButton.setPosition(x + 76 + host.getGuiLeft(), y + 2 + host.getGuiTop());
        holdAndDrag.setPosition(x + 59, y + 3);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
