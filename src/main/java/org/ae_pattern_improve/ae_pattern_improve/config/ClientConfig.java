package org.ae_pattern_improve.ae_pattern_improve.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = AePatternImprove.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue ENABLE_MULTIPLIER_BUTTON = BUILDER
            .comment("""
                    Enable or disable the multiplier button in pattern encoding terminals. \s
                    Multiplier buttons allow you to multiply/divide processing pattern's input and \s
                    output amounts by {2, 3, 5} by clicking the corresponding button. \s
                    """)
            .define("enableMultiplierButton", true);
    private static final ModConfigSpec.BooleanValue DISPLAY_AMOUNT_ON_ENCODED_PATTERN =
            BUILDER.comment("Display the amount of output items in encoded patterns while shift hold or in pattern access terminal/pattern provider.")
                    .define("displayAmountOnEncodedPattern", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isMultiplierButtonEnabled() {
        return ENABLE_MULTIPLIER_BUTTON.get();
    }
    public static boolean isDisplayAmountOnEncodedPattern() {
        return DISPLAY_AMOUNT_ON_ENCODED_PATTERN.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
