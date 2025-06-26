package org.ae_pattern_improve.ae_pattern_improve.client.gui;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.widgets.AE2Button;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientComparatorConfig;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.IComparatorItem;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.AE2ListBoxItem;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.DynamicScrollBar;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.ExtendableButtonGroup;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.slot.ClientFakeSlot;
import org.ae_pattern_improve.ae_pattern_improve.mixins.client.MixinWidgetContainer;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class AE2ListBoxSubScreen<
        MENU extends AEBaseMenu,
        SCREEN extends AEBaseScreen<MENU>> extends AESubScreen<MENU, SCREEN> implements IListBoxHost {
    protected static final int GUI_WIDTH = 195;
    private static final int GUI_PADDING_X = 8;
    private static final int GUI_PADDING_Y = 6;
    protected static final int GUI_HEADER_HEIGHT = 19;
    protected static final int GUI_FOOTER_HEIGHT = 100;

    private static final int TEXT_MARGIN_X = 2;
    private static final int TEXT_MAX_WIDTH = 156;
    private final ExtendableButtonGroup addNewItem;

    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;

    protected boolean refreshList;

    /// The amount of rows that are shown in the list box
    protected int showingRowAmount = 3;

    /// The items that are currently shown in the list box
    protected final ArrayList<AE2ListBoxItem> currentShowing = new ArrayList<>();
    private final DynamicScrollBar scrollbar;

    protected @Nullable AE2ListBoxItem selectedItem = null;

    private final List<IComparatorItem.ConfigWritable<?>> comparatorItems = new ArrayList<>();

    public AE2ListBoxSubScreen(SCREEN parent) {
        super(parent, "/screens/list_box.json");
        Button returnToParentButton = new AE2Button(
                x + GUI_PADDING_X + 20,
                y + h - GUI_FOOTER_HEIGHT + GUI_PADDING_Y,
                50, 20,
                Component.translatable("gui.ae_pattern_improve.return_to_parent"),
                button -> {
                    menu.slots.removeIf(ClientFakeSlot.class::isInstance);
                    returnToParent();
                }
        );
        widgets.add("returnToParent", returnToParentButton);
        Button resetToDefaultButton = new AE2Button(
                x + GUI_PADDING_X + 80,
                y + h - GUI_FOOTER_HEIGHT + GUI_PADDING_Y,
                50, 20,
                Component.translatable("ae_pattern_improve.listbox.reset_to_default"),
                button -> {
                    preInitWidgets();
                    ClientComparatorConfig.resetToDefault();
                    pullFromConfig();
                    postInitWidgets();
                }
        );
        widgets.add("resetToDefault", resetToDefaultButton);
        scrollbar = new DynamicScrollBar(DynamicScrollBar.SMALL);
        widgets.add("scrollbar", scrollbar);
        addNewItem = new ExtendableButtonGroup(this);
        widgets.add("addNewItem", addNewItem);
        pullFromConfig();
    }


    @Override
    public void addSubWidget(String id, AbstractWidget widget, Map<String, AbstractWidget> subWidgets) {
        if (widget.isFocused()) widget.setFocused(false);
        widget.setX(widget.getX() + leftPos);
        widget.setY(widget.getY() + topPos);
        subWidgets.put(id, widget);
        ((MixinWidgetContainer) widgets).getWidgets().put(id, widget);
        if (widget instanceof ICompositeWidget compositeWidget) {
            ((MixinWidgetContainer) widgets).getCompositeWidgets().put(id, compositeWidget);
        }
        addRenderableWidget(widget);
    }
    @Override
    public void removeSubWidget(String widgetId) {
        AbstractWidget widget = ((MixinWidgetContainer) widgets).getWidgets().remove(widgetId);
        ICompositeWidget widget1 = ((MixinWidgetContainer) widgets).getCompositeWidgets().remove(widgetId);
        if (widget != null) {
            removeWidget(widget);
        }
    }


    public void reorderItem(@Nullable IComparatorItem.ConfigWritable<?> item, int newIndex) {
        preInitWidgets();
        int currentIndex = comparatorItems.indexOf(item);
        if (currentIndex >= 0 && newIndex >= 0 && newIndex < comparatorItems.size()) {
            // Move the item to the new index
            comparatorItems.remove(currentIndex);
            comparatorItems.add(newIndex, item);
            saveConfig();
        }
        postInitWidgets();
    }

    @Override
    public void deleteItem(IComparatorItem.ConfigWritable<?> ae2ListBoxItem) {
        preInitWidgets();
        comparatorItems.remove(ae2ListBoxItem);
        saveConfig();
        postInitWidgets();
    }

    @Override
    public void substituteItem(IComparatorItem.ConfigWritable<?> targetItem, IComparatorItem.ConfigWritable<?> with) {
        preInitWidgets();
        int index = comparatorItems.indexOf(targetItem);
        if (index >= 0) {
            comparatorItems.set(index, with);
        } else {
            comparatorItems.add(with);
        }
        saveConfig();
        postInitWidgets();
    }

    @Override
    public void addItem(IComparatorItem.ConfigWritable<?> item) {
        preInitWidgets();
        comparatorItems.add(item);
        saveConfig();
        postInitWidgets();
    }

    @Override
    public void addSlot(ClientFakeSlot slot) {
        menu.slots.add(slot);
    }


    @Override
    @MustBeInvokedByOverriders
    protected void init() {
        imageHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + getShowingRowAmount() * AE2ListBoxItem.ITEM_HEIGHT;

        preInitWidgets();
        super.init();
        postInitWidgets();

    }
    @Override
    public void preInitWidgets() {
        // remove widgets to skip the style-sheet check in method appeng.client.gui.WidgetContainer.populateScreen
        saveConfig();
        currentShowing.forEach(item -> item.preInit(((MixinWidgetContainer) widgets).getWidgets()));
        addNewItem.preInit(((MixinWidgetContainer) widgets).getWidgets());
        menu.slots.removeIf(ClientFakeSlot.class::isInstance);
    }


    @Override
    public void postInitWidgets() {
        // clear old widgets because init() is recalled when the terminal resizes
        currentShowing.clear();
        // re-add the widgets to the screen
        currentShowing.addAll(createListItems());
        addNewItem.postInit();
        resetScrollbar();
    }

    @Override
    public void requireExtendableButtonsUpdate() {
        addNewItem.preInit(((MixinWidgetContainer) widgets).getWidgets());
        addNewItem.postInit();
    }

    private List<AE2ListBoxItem> createListItems() {
        List<AE2ListBoxItem> items = new ArrayList<>();
        pullFromConfig();
        for (int i = 0; i < comparatorItems.size(); i++) {
            int itemX = x + GUI_PADDING_X;
            int itemY = y + GUI_HEADER_HEIGHT + (i - scrollbar.getCurrentScroll()) * AE2ListBoxItem.ITEM_HEIGHT;
            var item = new AE2ListBoxItem(
                    this,
                    i,
                    itemX,
                    itemY,
                    style,
                    comparatorItems.get(i)
            );
            item.postInit();
            item.setVisible(i >= scrollbar.getCurrentScroll() && i < scrollbar.getCurrentScroll() + getShowingRowAmount());
            items.add(item);
        }
        return items;
    }
    private void saveConfig() {
        // Update the config with the current comparator items
        ClientComparatorConfig.saveConfig(comparatorItems);
    }
    private void pullFromConfig() {
        // Load the comparator items from the config
        comparatorItems.clear();
        comparatorItems.addAll(ClientComparatorConfig.getComparatorList());
    }

    private void resetScrollbar() {
        scrollbar.setHeight(getShowingRowAmount() * AE2ListBoxItem.ITEM_HEIGHT + 1);
        scrollbar.setRange(0, Mth.clamp(currentShowing.size() - getShowingRowAmount(), 0, Integer.MAX_VALUE), 1);
    }
    public int getShowingRowAmount() {
        return showingRowAmount;
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        addNewItem.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        // Draw the list items according to the scroll level
        for (var item: currentShowing) {
            item.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        }
        if (selectedItem != null) {
            // *Float* the selected item to the top of the list
            var pose = guiGraphics.pose();
            pose.pushPose();
//            pose.scale(0.75f, 0.75f, 1.0f);
            selectedItem.setPosition(
                    selectedItem.getX() + offsetX,
                    Mth.clamp((mouseY) - 5,
                            y + GUI_HEADER_HEIGHT + this.topPos,
                            y + GUI_HEADER_HEIGHT + this.topPos + (getShowingRowAmount() - 1) * AE2ListBoxItem.ITEM_HEIGHT)
            );
            selectedItem.setVisible(true);
            selectedItem.drawBG(guiGraphics, -offsetX, -offsetY, mouseX, mouseY, 0);
            selectedItem.drawFG(guiGraphics, 0, 0, mouseX, mouseY);
            pose.popPose();
        }
    }

    @Override
    public void updateBeforeRender() {
        menu.slots.removeIf(ClientFakeSlot.class::isInstance);
        super.updateBeforeRender();
        addNewItem.updateBeforeRender();
        for (int i = 0; i < currentShowing.size(); i++) {
            int itemX = x + GUI_PADDING_X;
            int itemY = y + GUI_HEADER_HEIGHT + (i - scrollbar.getCurrentScroll()) * AE2ListBoxItem.ITEM_HEIGHT;
            var item = currentShowing.get(i);
            item.setPosition(itemX, itemY);
            item.setVisible(i >= scrollbar.getCurrentScroll() && i < scrollbar.getCurrentScroll() + getShowingRowAmount());
            item.updateBeforeRender();
        }
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
        addNewItem.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
        for (var item: currentShowing) {
            item.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
        }
    }

    private int getItemOrder(int itemY) {

        int itemIndex = (itemY - GUI_HEADER_HEIGHT + scrollbar.getCurrentScroll() * AE2ListBoxItem.ITEM_HEIGHT) / AE2ListBoxItem.ITEM_HEIGHT;
//        Ae_test.LOGGER.debug("getItemOrder: itemY = {}, itemIndex = {}, screen.y = {}", itemY, itemIndex, this.topPos);
//        return Mth.clamp(itemIndex, 0, currentShowing.size() - 1);
        return Mth.clamp(itemIndex, scrollbar.getCurrentScroll(), scrollbar.getCurrentScroll() + getShowingRowAmount() - 1);
    }
    @Override
    public void handleItemOnDrop(Point mousePosition) {
        if (selectedItem != null) {
            int itemIndex = getItemOrder(mousePosition.getY());
            this.reorderItem(selectedItem.getComparatorItem(), itemIndex);
            selectedItem.setSelected(false);
            selectedItem = null;
        }
    }
    @Override
    public void handleItemOnDrag(AE2ListBoxItem item){
        if (selectedItem == null) {
            selectedItem = item;
            selectedItem.setSelected(true);
        }
    }

    @Override
    public <RENDERABLE extends Renderable> @NotNull RENDERABLE addRenderableOnly(@NotNull RENDERABLE renderable) {
        return super.addRenderableOnly(renderable);
    }

    @Override
    public void removeRenderableOnly(GuiEventListener renderable) {
        removeWidget(renderable);
    }

    @Override
    protected @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack stack) {
        var tooltips = super.getTooltipFromContainerItem(stack);
        tooltips = new ArrayList<>(tooltips);
        tooltips.add(Component.translatable("ae_pattern_improve.listbox.specific_entry").withColor(0x888888));// gray
        return tooltips;
    }
}
