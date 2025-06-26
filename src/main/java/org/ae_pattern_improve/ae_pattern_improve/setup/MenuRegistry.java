package org.ae_pattern_improve.ae_pattern_improve.setup;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.WirelessBatchTermMenu;

public class MenuRegistry {
    @SuppressWarnings("all")
    public static void init(RegisterEvent event) {
        // Initialize the menu registry
        if (event.getRegistryKey() == Registries.MENU) {
            Registry.register(event.getRegistry(Registries.MENU), AePatternImprove.getRL("pattern_batch_encoding_terminal"), PatternBatchEncodingTermMenu.TYPE);
            Registry.register(event.getRegistry(Registries.MENU), AePatternImprove.getRL("wireless_batch_encoding_terminal"), WirelessBatchTermMenu.TYPE);
        }
    }
}
