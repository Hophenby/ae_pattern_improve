package org.ae_pattern_improve.ae_pattern_improve.setup;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.ae_pattern_improve.ae_pattern_improve.AePatternImprove.MODID;

public class DataComponentRegistry {

    private static final Consumer<DataComponentType.Builder<CompoundTag>> COMPOUND_TAG_CODECS = builder -> builder
            .persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG);
    // In another class
// The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final Supplier<DataComponentType<CompoundTag>> PATTERN_BATCH_ENCODING = register(
            "pattern_encoding_preference", COMPOUND_TAG_CODECS
    );
    public static <T> Supplier<DataComponentType<T>> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {

        return REGISTRAR.registerComponentType(
                name,
                builder -> {
                    customizer.accept(builder);
                    return builder;
                }
        );
    }
}
