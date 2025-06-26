package org.ae_pattern_improve.ae_pattern_improve.setup;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import org.ae_pattern_improve.ae_pattern_improve.common.part.PatternBatchEncodingTermPart;

import java.util.function.Function;

public class AEPartsRegistry {
    public static final DeferredItem<PartItem<PatternBatchEncodingTermPart>> BATCH_PATTERN_ENCODING_TERMINAL = createPart(
            "pattern_batch_encoding_terminal",
            PatternBatchEncodingTermPart.class,
            PatternBatchEncodingTermPart::new);

    private static <T extends IPart> DeferredItem<PartItem<T>> createPart(
            String id,
            Class<T> partClass,
            Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return ItemsAndBlocksRegistry.ITEMS.registerItem(
                id,
                props -> new PartItem<>(props, partClass, factory));
    }
    // Used to control in which order static constructors are called
    public static void init() {
    }
}
