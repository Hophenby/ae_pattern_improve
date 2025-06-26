package org.ae_pattern_improve.ae_pattern_improve.common.menu;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class TestMenu extends AEBaseMenu {
    public static final MenuType<TestMenu> TYPE = MenuTypeBuilder
            .create(TestMenu::new, ItemMenuHost.class)
            .build("ae_pattern_improve_menu");
    private final ConfigInventory templateInventory = ConfigInventory.storage(125).build();
    private final AppEngSlot[] templateSlots = new AppEngSlot[125];
    public TestMenu(MenuType<?> menuType, int id, Inventory playerInventory, ItemMenuHost host) {
        super(menuType, id, playerInventory, host);
        var menuInv = templateInventory.createMenuWrapper();
        for (int i = 0; i < templateSlots.length; i++) {
            templateSlots[i] = new AppEngSlot(menuInv, i);
            addSlot(templateSlots[i]);
        }
    }
}
