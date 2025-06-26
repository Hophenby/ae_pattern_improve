package org.ae_pattern_improve.ae_pattern_improve.client.gui;

import appeng.client.Point;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.IComparatorItem;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.AE2ListBoxItem;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.slot.ClientFakeSlot;

public interface IListBoxHost extends IStylelessHost {

    void addItem(IComparatorItem.ConfigWritable<?> item);

    void requireExtendableButtonsUpdate();

    void deleteItem(IComparatorItem.ConfigWritable<?> ae2ListBoxItem);

    void substituteItem(IComparatorItem.ConfigWritable<?> targetItem, IComparatorItem.ConfigWritable<?> with);

    void addSlot(ClientFakeSlot slot);

    void handleItemOnDrop(Point mousePosition);

    void handleItemOnDrag(AE2ListBoxItem item);
}
