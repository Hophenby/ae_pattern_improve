package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.PatternBatchEncodingTermScreen;
import org.jetbrains.annotations.NotNull;

public class WirelessBatchTermScreen extends PatternBatchEncodingTermScreen<WirelessBatchTermMenu> implements IUniversalTerminalCapable {
    private final ScrollingUpgradesPanel upgradesPanel;
    public WirelessBatchTermScreen(WirelessBatchTermMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        if (getMenu().isWUT()){
            this.addToLeftToolbar(this.cycleTerminalButton());
        }
        this.upgradesPanel = this.addUpgradePanel(this.widgets, this.getMenu());
        if (this.getMenu().getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, this.getMenu().getToolbox().getName()));
        }
    }

    @Override
    public @NotNull WTMenuHost getHost() {
        return (WTMenuHost) this.getMenu().getHost();
    }

    @Override
    public void init() {
        super.init();
        this.upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }
}
