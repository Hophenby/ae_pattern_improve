package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.EncodedPatternWithAmountRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {
    /**
     * This mixin specifically targets rendering of items in the user interface to allow us to customize _only_ the UI
     * representation of an item, and none of the others (held items, in-world, etc.)
     */
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(value = "HEAD"),
            cancellable = true,
            order = 999
    )
    protected void renderGuiItem(@Nullable LivingEntity livingEntity, @Nullable Level level, ItemStack stack, int x,
                                 int y, int seed, int z, CallbackInfo ci) {
        var self = (GuiGraphics) (Object) this;

        if (EncodedPatternWithAmountRenderer.renderEncodedPatternWithAmount(self, livingEntity, level, stack, x, y, seed, z)) {
            ci.cancel();
        }
    }
}
