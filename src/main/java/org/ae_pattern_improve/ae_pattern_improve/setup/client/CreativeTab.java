package org.ae_pattern_improve.ae_pattern_improve.setup.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.setup.AEPartsRegistry;
import org.ae_pattern_improve.ae_pattern_improve.setup.ItemsAndBlocksRegistry;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AePatternImprove.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB_GENERAL = CREATIVE_MODE_TABS.register("ae_pattern_improve_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ae_pattern_improve"))
            .icon(AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL::toStack)
            .displayItems((params, output) -> {
                output.accept(AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL.toStack());
                output.accept(ItemsAndBlocksRegistry.WIRELESS_BATCH_ENCODER.toStack());
            })
            .build());

}
