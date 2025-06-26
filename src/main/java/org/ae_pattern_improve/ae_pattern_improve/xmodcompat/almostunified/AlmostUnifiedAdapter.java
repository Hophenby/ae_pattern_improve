package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.almostunified;

import appeng.api.stacks.AEKeyType;
import appeng.menu.me.common.GridInventoryEntry;
import com.almostreliable.unified.api.AlmostUnified;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.XModUtils;

public class AlmostUnifiedAdapter {
    private static boolean isLoaded() {
        // For Forge:
        return XModUtils.isModLoaded(XModUtils.ALMOST_UNIFIED_MOD_ID);
    }

    public static boolean isAUPreferredItem(Item item) {
        if (!isLoaded()) {
            return false; // AU is not loaded, so no preferred items.
        }
        var relaventItem = Adapter.getTagTargetItem(
                Adapter.getRelevantItemTag(item) // Get the tag for the item
        );
        // Check if the item is preferred by Almost Unified.
        return relaventItem != null && relaventItem.equals(item);
    }
    public static boolean isAUPreferredItem(GridInventoryEntry entry) {
        var aeKey = entry.getWhat();
        // since almost unified only checks types of items, we should filter out anything that is not an item
        if (aeKey == null || aeKey.getType() != AEKeyType.items()) {
            return false; // Not an item, so not preferred.
        }
        var item = BuiltInRegistries.ITEM.get(aeKey.getId());
        return isAUPreferredItem(item);
    }

    private static class Adapter {
        public static Item getTagTargetItem(TagKey<Item> tag) {
            return AlmostUnified.INSTANCE.getTagTargetItem(tag);
        }
        public static TagKey<Item> getRelevantItemTag(ItemLike item) {
            return AlmostUnified.INSTANCE.getRelevantItemTag(item);
        }
    }
}