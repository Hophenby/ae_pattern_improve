package org.ae_pattern_improve.ae_pattern_improve.datagen;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import org.ae_pattern_improve.ae_pattern_improve.setup.AEPartsRegistry;
import org.ae_pattern_improve.ae_pattern_improve.setup.ItemsAndBlocksRegistry;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.XModUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.HostWirelessBatchTerm.ID4WT;

public class VanillaAndAERecipeProvider extends RecipeProvider {

    public VanillaAndAERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // Wireless Batch Encoder Recipe
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, ItemsAndBlocksRegistry.WIRELESS_BATCH_ENCODER.get())
                .pattern("A")
                .pattern("B")
                .pattern("C")
                .define('A', AEItems.WIRELESS_RECEIVER)
                .define('B', AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL)
                .define('C', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy("has_item", has(AEItems.WIRELESS_RECEIVER))
                .save(recipeOutput, ItemsAndBlocksRegistry.WIRELESS_BATCH_ENCODER.getId());

        // Pattern Batch Encoding Terminal Recipe
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL.get())
                .requires(AEItems.CELL_COMPONENT_1K)
                .requires(AEParts.CRAFTING_TERMINAL)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("has_item", has(AEParts.CRAFTING_TERMINAL))
                .save(recipeOutput, AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL.getId());

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL.get())
                .requires(AEItems.CELL_COMPONENT_1K)
                .requires(AEParts.PATTERN_ENCODING_TERMINAL)
                .unlockedBy("has_item", has(AEParts.PATTERN_ENCODING_TERMINAL))
                .save(recipeOutput, AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL.getId().withSuffix("2"));

        recipeOutput.withConditions(XModUtils.modRecipeCondition(XModUtils.AE2WTLIB_MOD_ID)).accept(
                AEPartsRegistry.BATCH_PATTERN_ENCODING_TERMINAL.getId().withSuffix("_for_upgrade"),
                new Upgrade(
                        Ingredient.of(ItemsAndBlocksRegistry.WIRELESS_BATCH_ENCODER.get()),
                        WTDefinition.of(ID4WT)
                ),
                null
        );
    }
}
