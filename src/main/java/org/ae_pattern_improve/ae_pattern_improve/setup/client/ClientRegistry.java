package org.ae_pattern_improve.ae_pattern_improve.setup.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.PatternBatchEncodingTermScreen;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.ae_pattern_improve.ae_pattern_improve.setup.AEPartsRegistry;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.WirelessBatchTermMenu;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.WirelessBatchTermScreen;

@EventBusSubscriber(value = Dist.CLIENT, modid = AePatternImprove.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistry {
    @SubscribeEvent
    public static void registerColorHandler(RegisterColorHandlersEvent.Item event) {
        event.register(tint(new StaticItemColor(AEColor.TRANSPARENT)), AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL);
    }
    private static ItemColor tint(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }
    @SubscribeEvent
    @SuppressWarnings("all")
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        InitScreens.<PatternBatchEncodingTermMenu, PatternBatchEncodingTermScreen<PatternBatchEncodingTermMenu>>register(
                event,
                PatternBatchEncodingTermMenu.TYPE,
                PatternBatchEncodingTermScreen::new,
                "/screens/terminals/pattern_batch_encoding_terminal.json"
        );
        InitScreens.<WirelessBatchTermMenu, WirelessBatchTermScreen>register(
                event,
                WirelessBatchTermMenu.TYPE,
                WirelessBatchTermScreen::new,
                "/screens/terminals/wireless_batch_encoding_terminal.json"
        );
    }
}
