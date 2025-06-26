package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.WirelessBatchTermMenu;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(PatternBatchEncodingTermMenu.TYPE,
                new EMIBatchEncodingTransferHandler<>(PatternBatchEncodingTermMenu.class));
        registry.addRecipeHandler(WirelessBatchTermMenu.TYPE,
                new EMIBatchEncodingTransferHandler<>(WirelessBatchTermMenu.class));
    }
}
