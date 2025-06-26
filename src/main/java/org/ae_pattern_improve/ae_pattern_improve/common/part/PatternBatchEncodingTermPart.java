package org.ae_pattern_improve.ae_pattern_improve.common.part;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.IConfigManager;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts.IPatternBatchTermLogicHost;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts.IPatternBatchTermMenuHost;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternBatchEncodingLogic;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;

import java.util.List;

public class PatternBatchEncodingTermPart extends AbstractTerminalPart
        implements IPatternBatchTermLogicHost, IPatternBatchTermMenuHost {
    @PartModels
    public static final ResourceLocation MODEL_OFF = AePatternImprove.getRL(
            "part/pattern_batch_encoding_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = AePatternImprove.getRL(
            "part/pattern_batch_encoding_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private final PatternBatchEncodingLogic logic = new PatternBatchEncodingLogic(this);

    private final IConfigManager configManager = IConfigManager.builder(() -> this.getHost().markForSave())
            .build();
    public PatternBatchEncodingTermPart(IPartItem<?> partItem) {
        super(partItem);
    }
    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeToNBT(tag, registries);
        configManager.writeToNBT(tag, registries);
        logic.writeToNBT(tag, registries);
    }

    @Override
    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        super.readFromNBT(tag, registries);
        configManager.readFromNBT(tag, registries);
        logic.readFromNBT(tag, registries);
    }

    @Override
    public PatternBatchEncodingLogic getLogic() {
        return logic;
    }

    @Override
    public void markForSave() {
        getHost().markForSave();
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return PatternBatchEncodingTermMenu.TYPE;
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        for (var is : this.logic.getBlankPatternInv()) {
            drops.add(is);
        }
        for (var is : this.logic.getEncodedPatternInv()) {
            drops.add(is);
        }
    }
}
