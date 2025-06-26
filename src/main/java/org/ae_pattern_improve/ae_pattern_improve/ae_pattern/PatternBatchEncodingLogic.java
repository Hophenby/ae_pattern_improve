package org.ae_pattern_improve.ae_pattern_improve.ae_pattern;

import appeng.api.inventories.InternalInventory;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts.IPatternBatchTermLogicHost;

import static appeng.crafting.pattern.AEProcessingPattern.MAX_INPUT_SLOTS;
import static appeng.crafting.pattern.AEProcessingPattern.MAX_OUTPUT_SLOTS;

public class PatternBatchEncodingLogic implements InternalInventoryHost {

    private final IPatternBatchTermLogicHost host;
    private final ConfigInventory encodedInputInv = ConfigInventory.configStacks(MAX_INPUT_SLOTS)
            .changeListener(this::onEncodedInputChanged).allowOverstacking(true).build();
    private final ConfigInventory encodedOutputInv = ConfigInventory.configStacks(MAX_OUTPUT_SLOTS)
            .changeListener(this::onEncodedOutputChanged).allowOverstacking(true).build();
    private final ConfigInventory srcMaterialInv = ConfigInventory.configStacks(9)
            .changeListener(this::onEncodedInputChanged).allowOverstacking(true).build();
    private final AppEngInternalInventory blankPatternInv = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory encodedPatternInv = new AppEngInternalInventory(this, 9);

    private boolean isLoading = false;
    public PatternBatchEncodingLogic(IPatternBatchTermLogicHost host) {
        this.host = host;
    }
    private void onEncodedInputChanged() {
        saveChanges();
    }
    public ConfigInventory getSrcMaterialInv() {
        return srcMaterialInv;
    }
    /**
     * Inventory of size 1, which contains the blank patterns for encoding.
     * modified to allow oversized patterns.
     */
    public InternalInventory getBlankPatternInv() {
        return blankPatternInv;
    }
    public void saveChanges() {
        // Do not re-save while we're loading since it could overwrite the NBT with incomplete data
        if (!isLoading) {
            host.markForSave();
        }
    }

    private void onEncodedOutputChanged() {
        saveChanges();
    }

    /**
     * Inventory of size 1, which will receive the encoded pattern and can be used to place an already-encoded pattern
     * for re-encoding.
     */
    public InternalInventory getEncodedPatternInv() {
        return encodedPatternInv;
    }


    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        isLoading = true;
        try {
            blankPatternInv.readFromNBT(data, "blankPattern", registries);
            encodedPatternInv.readFromNBT(data, "encodedPattern", registries);

            encodedInputInv.readFromChildTag(data, "encodedInputs", registries);
            encodedOutputInv.readFromChildTag(data, "encodedOutputs", registries);
            srcMaterialInv.readFromChildTag(data, "srcMaterials", registries);
        } finally {
            isLoading = false;
        }
    }

    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        blankPatternInv.writeToNBT(data, "blankPattern", registries);
        encodedPatternInv.writeToNBT(data, "encodedPattern", registries);

        encodedInputInv.writeToChildTag(data, "encodedInputs", registries);
        encodedOutputInv.writeToChildTag(data, "encodedOutputs", registries);
        srcMaterialInv.writeToChildTag(data, "srcMaterials", registries);
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        saveChanges();
    }

    @Override
    public boolean isClientSide() {
        return host.getLevel().isClientSide();
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        saveChanges();
    }

    public ConfigInventory getEncodedInputInv() {
        return encodedInputInv;
    }

    public ConfigInventory getEncodedOutputInv() {
        return encodedOutputInv;
    }

}
