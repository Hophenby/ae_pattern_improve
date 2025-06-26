package org.ae_pattern_improve.ae_pattern_improve.setup;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.common.command.AeTestCommand;

@EventBusSubscriber(modid = AePatternImprove.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommandsRegistry {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        var ctx = event.getBuildContext();
        AeTestCommand.register(dispatcher, ctx);
    }
}
