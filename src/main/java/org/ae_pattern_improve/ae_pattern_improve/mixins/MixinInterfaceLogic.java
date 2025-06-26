package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.util.ConfigInventory;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.ItemStack;
import org.ae_pattern_improve.ae_pattern_improve.config.CommonConfig;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IOversizedInv;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InterfaceLogic.class)
public abstract class MixinInterfaceLogic {
    @Nullable
    @Shadow
    private MEStorage networkStorage;
    @Final
    @Shadow
    private ConfigInventory storage;
    @Shadow
    @Final
    protected InterfaceLogicHost host;
    @ModifyExpressionValue(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/util/ConfigInventory$Builder;build()Lappeng/util/ConfigInventory;"
            )
    )
    private ConfigInventory ae_pattern_improve$modifySlotSize(ConfigInventory original) {
        if (original instanceof IOversizedInv) {
            ((IOversizedInv) original).ae_pattern_improve$setOversizedInv(CommonConfig.enableOversizedInterface.get());
        }
        return original;
    }
    @Inject(
            method = "addDrops",
            at = @At("HEAD")
    )
    private void ae_pattern_improve$tryPreserveContents(List<ItemStack> drops, CallbackInfo ci){
        // This is a workaround for the issue where the InterfaceLogic drops its contents when it is removed from the grid.
        // We want to preserve the contents of the InterfaceLogic when it is removed, so we can restore them later.
        if (this.networkStorage == null || this.storage == null || this.host == null || !CommonConfig.preserveInterfaceContents.get()) {
            return; // No storage to preserve or config disabled
        }
        for (int i = 0; i < this.storage.size(); i++) {
            var stack = storage.getStack(i);

            if (stack != null) {
                stack = new GenericStack(stack.what(),
                        stack.amount() - networkStorage.insert(stack.what(), stack.amount(), Actionable.MODULATE, IActionSource.ofMachine((InterfaceLogic) (Object) this)));
                if (stack.amount() == 0) {
                    storage.setStack(i, null);
                } else {
                    storage.setStack(i, stack);
                }
            }
        }
    }
}

