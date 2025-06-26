package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt;

import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternBatchEncodingLogic;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts.IPatternBatchTermLogicHost;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts.IPatternBatchTermMenuHost;
import org.ae_pattern_improve.ae_pattern_improve.setup.DataComponentRegistry;

import java.util.function.BiConsumer;

public class HostWirelessBatchTerm extends WTMenuHost implements IPatternBatchTermMenuHost, IPatternBatchTermLogicHost {
    private final PatternBatchEncodingLogic logic = new PatternBatchEncodingLogic(this);
    public static final String ID4WT = "pattern_batch_encoding";

    public HostWirelessBatchTerm(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.logic.readFromNBT(this.getItemStack().getOrDefault(DataComponentRegistry.PATTERN_BATCH_ENCODING.get(), new CompoundTag()), player.registryAccess());
    }

    @Override
    public PatternBatchEncodingLogic getLogic() {
        return new PatternBatchEncodingLogic(this);
    }

    @Override
    public Level getLevel() {
        return getPlayer().level();
    }

    @Override
    public void markForSave() {
        CompoundTag tag = this.getItemStack().getOrDefault(DataComponentRegistry.PATTERN_BATCH_ENCODING.get(), new CompoundTag());
        this.logic.writeToNBT(tag, this.getPlayer().registryAccess());
        this.getItemStack().set(DataComponentRegistry.PATTERN_BATCH_ENCODING.get(), tag);
    }

    @Override
    public int getInstalledUpgrades(ItemLike upgradeCard) {
        return super.getInstalledUpgrades(upgradeCard);
    }

    @Override
    public boolean isUpgradedWith(ItemLike upgradeCard) {
        return super.isUpgradedWith(upgradeCard);
    }
}
