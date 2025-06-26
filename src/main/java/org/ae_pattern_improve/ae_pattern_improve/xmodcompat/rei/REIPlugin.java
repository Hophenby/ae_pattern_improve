package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.rei;

import appeng.integration.modules.itemlists.CompatLayerHelper;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return AePatternImprove.MODID;
    }
    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
        registry.register(
                new REIBatchEncodingTransferHandler<>(PatternBatchEncodingTermMenu.class)
        );
    }
}
