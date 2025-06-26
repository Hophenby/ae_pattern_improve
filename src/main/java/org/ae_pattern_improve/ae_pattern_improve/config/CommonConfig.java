package org.ae_pattern_improve.ae_pattern_improve.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;

@EventBusSubscriber(modid = AePatternImprove.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue maxPatternSize = BUILDER
            .comment("""
                    Maximum pattern slots size of Extended Pattern Provider.\s
                    Only works when ExtendedAE is loaded.\s
                    """
            )
            .defineInRange("maxPatternSize", 36, 36, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue maxPatternModifierSize = BUILDER
            .comment("""
                    Maximum pattern slots size of Pattern Modifier.\s
                    Only works when ExtendedAE is loaded.\s
                    """
            )
            .defineInRange("maxPatternModifierSize", 27, 27, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue enablePatternModifierPulling = BUILDER
            .comment("""
                    Enable the Pattern Modifier pulling feature.\s
                    When enabled, the pattern modifier will pull patterns from the pattern provider when used with shift held.\s
                    This feature is only available when ExtendedAE is loaded.\s
                    """
            )
            .define("enablePatternModifierPulling", true);
    public static final ModConfigSpec.BooleanValue enableOversizedInterface = BUILDER
            .comment("""
                    Allow extremely large amounts of items being configured in the interface.\s
                    This will only take effects on ME Interfaces / ME Extended Interfaces.\s
                    """
            )
            .define("enableOversizedInterface", true);
    public static final ModConfigSpec.BooleanValue preserveInterfaceContents = BUILDER
            .comment("""
                    Preserve the contents of the InterfaceLogic when it is removed (destroyed or wrenched by player).\s
                    When enabled, the contents will try to be preserved in the ME Storage first, and then done its default behavior \s
                    (dropped on the ground or in the player's inventory) if the storage is not available or the storage is full.\s
                    It's recommended to enable this option if you use the oversized interface, \s
                    as you probably don't want to deal with too many dropped items or lose them if the number of item entities exceeds 1000.\s
                    """
            )
            .define("preserveInterfaceContents", true);
    public static final ModConfigSpec SPEC = BUILDER.build();


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
