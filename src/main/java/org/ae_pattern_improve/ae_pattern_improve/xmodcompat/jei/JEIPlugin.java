package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return AePatternImprove.getRL("jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IModPlugin.super.registerRecipeTransferHandlers(registration);

        // Universal handler for processing to try and handle all IRecipe
        registration.addUniversalRecipeTransferHandler(new JEIBatchEncodingTransferHandler<>(
                PatternBatchEncodingTermMenu.TYPE,
                PatternBatchEncodingTermMenu.class
        ));
    }
}
