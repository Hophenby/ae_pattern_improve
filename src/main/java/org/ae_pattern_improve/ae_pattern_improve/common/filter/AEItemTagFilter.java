package org.ae_pattern_improve.ae_pattern_improve.common.filter;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public class AEItemTagFilter extends AEKeyTagFilter<Item, AEItemTagFilter>{
    public AEItemTagFilter(GenericStack stack) {
        super(stack);
    }


    @Override
    protected GenericStack createResultStack(Item item, long amount) {
        var key = AEItemKey.of(item.getDefaultInstance());
        return new GenericStack(key, amount);
    }

    @Override
    public Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public List<TagKey<Item>> getFilterableTags(GenericStack keySource) {
        var item = getRegistry().getOptional(keySource.what().getId()).orElse(null);
        if (item != null) {
            return item.getDefaultInstance().getTags().toList();
        }
        return List.of();
    }
}
