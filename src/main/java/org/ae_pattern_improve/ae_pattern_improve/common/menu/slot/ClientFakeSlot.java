package org.ae_pattern_improve.ae_pattern_improve.common.menu.slot;

import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.slot.FakeSlot;
import appeng.util.ConfigMenuInventory;
import net.minecraft.world.item.ItemStack;
import org.ae_pattern_improve.ae_pattern_improve.mixins.MixinSlot;

import java.util.function.Consumer;

public class ClientFakeSlot extends FakeSlot {
    private final Callback callback;
    private ClientFakeSlot(Callback callback) {
        super(callback.inv, 0);
        this.callback = callback;
        this.setHideAmount(true);
    }
    public static ClientFakeSlot create(Consumer<ConfigMenuInventory> slotChangedCallback) {
        var innerCallback = new Callback();
        innerCallback.inv = new ConfigMenuInventory(
                new GenericStackInv(innerCallback::slotChanged,
                        GenericStackInv.Mode.CONFIG_TYPES,
                        1
                )
        );
        innerCallback.slotChangedCallback = slotChangedCallback;
        return new ClientFakeSlot(innerCallback);
    }
    public void setPosition(int x, int y) {
        ((MixinSlot) this).setX(x);
        ((MixinSlot) this).setY(y);
    }
    public void rawSet(ItemStack stack) {
        callback.doTrigger = false;
        this.set(stack);
        callback.doTrigger = true;
    }

    @Override
    public void setFilterTo(ItemStack itemStack) {
        // skip package distributing to the server
        // handle this filter directly in the client
        this.set(itemStack);
    }

    private static class Callback{
        private ConfigMenuInventory inv;
        private Consumer<ConfigMenuInventory> slotChangedCallback;
        private boolean doTrigger = true;
        public void slotChanged() {
            if (inv != null && slotChangedCallback != null && doTrigger) {
                slotChangedCallback.accept(inv);
            }
        }
    }

}
