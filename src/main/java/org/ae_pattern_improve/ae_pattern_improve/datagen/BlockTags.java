package org.ae_pattern_improve.ae_pattern_improve.datagen;

import appeng.datagen.providers.tags.BlockTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(packOutput, registries, existingFileHelper);
    }
}
