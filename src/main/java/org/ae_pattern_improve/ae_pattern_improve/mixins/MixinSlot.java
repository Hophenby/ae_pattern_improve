package org.ae_pattern_improve.ae_pattern_improve.mixins;


import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface MixinSlot {

    @Accessor("x")
    void setX(int x);

    @Accessor("y")
    void setY(int y);
}