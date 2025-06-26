package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt;

import appeng.api.storage.ITerminalHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;

public class WirelessBatchTermMenu extends PatternBatchEncodingTermMenu {
    public static final MenuType<WirelessBatchTermMenu> TYPE = MenuTypeBuilder
            .create(WirelessBatchTermMenu::new, HostWirelessBatchTerm.class)
            .build(AePatternImprove.getRL("wireless_batch_encoding_terminal"));
    private final HostWirelessBatchTerm host;
    private final ToolboxMenu toolboxMenu;
    public WirelessBatchTermMenu(int id, Inventory ip, HostWirelessBatchTerm host) {
        super(TYPE, id, ip, host, true);
        this.host = host;
        this.toolboxMenu = new ToolboxMenu(this);
        IUpgradeInventory upgrades = this.host.getUpgrades();

        for(int i = 0; i < upgrades.size(); ++i) {
            RestrictedInputSlot slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            this.addSlot(slot, SlotSemantics.UPGRADE);
        }

        this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY, this.host.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);

    }

    @Override
    public void broadcastChanges() {
        this.toolboxMenu.tick();
        super.broadcastChanges();
    }

    public boolean isWUT() {
        return this.host.getItemStack().getItem() instanceof ItemWUT;
    }

    public ITerminalHost getHost() {
        return this.host;
    }

    public ToolboxMenu getToolbox() {
        return this.toolboxMenu;
    }
}
