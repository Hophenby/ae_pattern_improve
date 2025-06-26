package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.helpers.InventoryAction;
import appeng.menu.AEBaseMenu;
import appeng.menu.slot.FakeSlot;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.slot.ClientFakeSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AEBaseMenu.class)
public interface AccessorAEBaseMenu {
    @Mixin(AEBaseMenu.class)
    abstract class MixinAEBaseMenu {
        @Inject(
                method = "handleFakeSlotAction",
                at = @At("HEAD"),
                cancellable = true
        )
        private void handleClientFakeSlotAction(FakeSlot fakeSlot, InventoryAction action, CallbackInfo ci) {
            if (fakeSlot instanceof ClientFakeSlot cfs) {
                // Prevent the server from processing the fake slot action
                switch (action) {
                    case PICKUP_OR_SET_DOWN -> {
                        var hand = ((AEBaseMenu) (Object) this).getCarried();
                        if (!hand.isEmpty()) {
                            cfs.set(hand);
                        }
                    }
                    case EMPTY_ITEM -> {
                    }
                    default -> {
                    }
                }
                ci.cancel();
            }
        }
    }
}