package org.ae_pattern_improve.ae_pattern_improve.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;

@EventBusSubscriber(modid = AePatternImprove.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent dataEvent) {
            var pack = dataEvent.getGenerator().getVanillaPack(true);
            var file = dataEvent.getExistingFileHelper();
            var lookup = dataEvent.getLookupProvider();
            var blockTags = pack.addProvider(p -> new BlockTags(p, lookup, file));
            pack.addProvider(p -> new VanillaAndAERecipeProvider(p, lookup));
        }

}
