package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface MixinAbstractContainerScreen {
    // expose the findSlot method to allow for custom slot finding
    @Invoker("findSlot")
    Slot ae_pattern_improve$findSlot(double mouseX, double mouseY);
}
