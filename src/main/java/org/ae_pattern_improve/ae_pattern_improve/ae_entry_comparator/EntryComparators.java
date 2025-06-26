package org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.menu.me.common.GridInventoryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public abstract class EntryComparators<TYPE> implements IComparatorItem.ConfigWritable<Boolean> {
    private final static AEKeyType AE_ITEMS = AEKeyType.items();
    private final static AEKeyType AE_FLUIDS = AEKeyType.fluids();
    protected ResourceLocation id;
    abstract Registry<TYPE> getRegistry();
    abstract AEKeyType getAEType();
    // check if the TYPE item is registered and represents a valid item or fluid
    abstract boolean checkValidity(TYPE item);
    @Override
    public Boolean getComparableValue(GridInventoryEntry entry) {
//        if (entry.getWhat() != null) {
//            Ae_test.LOGGER.debug("Comparing entry type: {} with id: {}", entry.getWhat().getType(), getAEType());
//        }
//        Ae_test.LOGGER.debug("Comparing entry: {} == {}", entry.getWhat(), id);
        return entry.getWhat() != null
                && entry.getWhat().getType() == getAEType()
                && entry.getWhat().getId().equals(id);
    }
    @Override
    public boolean parseFromConfig(String config) {
        if (config == null || config.isEmpty()) {
            return false;
        }
        var itemId = ResourceLocation.parse(config);
        var cfgItem = getRegistry().get(itemId);
        if (!checkValidity(cfgItem)) {
            return false;
        }
        this.id = itemId;
        return true;
    }
    abstract public AEKey getAEKey();
    public static EntryComparators<?> fromAEKey(AEKey key) {
        EntryComparators<?> comparator;
        if (key.getType() == AE_ITEMS) {
            comparator = new ItemComparator();
        } else if (key.getType() == AE_FLUIDS) {
            comparator = new FluidComparator();
        } else {
            throw new IllegalArgumentException("Unknown AEKey type: " + key.getType());
        }
        if (!comparator.parseFromConfig(key.getId().toString())) {
            throw new IllegalArgumentException("Invalid AEKey id: " + key.getId());
        }
        return comparator;
    }
    public static class ItemComparator extends EntryComparators<Item>{

        @Override
        Registry<Item> getRegistry() {
            return BuiltInRegistries.ITEM;
        }

        @Override
        AEKeyType getAEType() {
            return AE_ITEMS;
        }

        @Override
        boolean checkValidity(Item item) {
            return !item.getDefaultInstance().isEmpty();
        }

        @Override
        public String toConfigString() {
            return "item:" + id.toString();
        }

        @Override
        public AEKey getAEKey() {
            return AEItemKey.of(
                    Objects.requireNonNull(getRegistry().get(id),
                            "Item with id " + id + " is not registered!"));
        }
    }
    public static class FluidComparator extends EntryComparators<Fluid>{

        @Override
        Registry<Fluid> getRegistry() {
            return BuiltInRegistries.FLUID;
        }

        @Override
        AEKeyType getAEType() {
            return AE_FLUIDS;
        }

        @Override
        boolean checkValidity(Fluid fluid) {
            return !fluid.defaultFluidState().isEmpty();
        }

        @Override
        public String toConfigString() {
            return "fluid:" + id.toString();
        }

        @Override
        public AEKey getAEKey() {
            return AEFluidKey.of(
                    Objects.requireNonNull(getRegistry().get(id),
                            "Fluid with id " + id + " is not registered!"));
        }
    }

}
