package org.ae_pattern_improve.ae_pattern_improve.setup;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.WirelessBatchTermMenu;
import org.jetbrains.annotations.NotNull;

public class ItemsAndBlocksRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AePatternImprove.MODID);


    // Creates a new food item with the id "ae_pattern_improve:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<ItemWT> WIRELESS_BATCH_ENCODER = ITEMS.registerItem("wireless_batch_encoder", (properties) -> new ItemWT() {
        @Override
        public @NotNull MenuType<?> getMenuType(@NotNull ItemMenuHostLocator locator, @NotNull Player player) {
            return WirelessBatchTermMenu.TYPE;
        }
    });

}
