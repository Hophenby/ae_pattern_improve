package org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator;

import appeng.menu.me.common.GridInventoryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public interface IComparatorItem<COMPARABLE extends Comparable<COMPARABLE>> {

    // Main comparator method for GridInventoryEntry
    COMPARABLE getComparableValue(GridInventoryEntry entry);

    // Method to parse configuration settings, if any error occurs during parsing, it should return false.
    default boolean parseFromConfig(@Nullable String config) {
        return true;
    }
    default Comparator<GridInventoryEntry> getComparator() {
        return Comparator.comparing(this::getComparableValue);
    }
    static <COMPARABLE extends Comparable<COMPARABLE>> ConfigWritable<COMPARABLE> wrapSimpleWritable(String name,
                                                                                                     IComparatorItem<COMPARABLE> method) {
        return new ConfigWritable<>() {
            @Override
            public String toConfigString() {
                return name;
            }
            @Override
            public COMPARABLE getComparableValue(GridInventoryEntry entry) {
                return method.getComparableValue(entry);
            }
        };
    }
    interface ConfigWritable<COMPARABLE extends Comparable<COMPARABLE>> extends IComparatorItem<COMPARABLE> {
        String toConfigString();
    }
}
