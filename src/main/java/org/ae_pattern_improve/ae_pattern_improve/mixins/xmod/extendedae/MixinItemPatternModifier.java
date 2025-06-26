package org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import com.glodblock.github.extendedae.common.items.tools.ItemPatternModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.ae_pattern_improve.ae_pattern_improve.config.CommonConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPatternModifier.class)
public abstract class MixinItemPatternModifier {
    @Shadow
    protected abstract PatternProviderLogic findPatternProvider(Level world, BlockPos pos, Vec3 clicked);
    @Inject(
            method = "onItemUseFirst",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void ae_pattern_improve$pullFromPatternProvider(ItemStack stack, UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        var world = context.getLevel();
        var pp = findPatternProvider(world, context.getClickedPos(), context.getClickLocation());
        var player = context.getPlayer();
        boolean didSomething = false;

        if (!world.isClientSide()
                && CommonConfig.enablePatternModifierPulling.get() // Config option to enable pulling patterns from pattern provider
                && player != null
                && InteractionUtil.isInAlternateUseMode(player) // Shift-clicking means we want to transfer patterns from the pattern modifier to the pattern provider
                && pp != null) {
            var selfInv = ae_pattern_improve$getSelf().getMenuHost(player, MenuLocators.forStack(stack), null).getInventoryByName("patternInv");
            var ppInv = pp.getPatternInv();
            for (int slot = 0; slot < ppInv.size(); slot ++) {
                var pattern = ppInv.getStackInSlot(slot);
                if (!pattern.isEmpty()) {
                    var overflow = selfInv.addItems(pattern);
                    if (overflow.isEmpty()) {
                        ppInv.setItemDirect(slot, ItemStack.EMPTY);
                        didSomething = true;
                    }
                }
            }
            cir.setReturnValue(didSomething ? InteractionResult.SUCCESS : InteractionResult.PASS);
        }
    }
    @Unique
    private ItemPatternModifier ae_pattern_improve$getSelf() {
        return (ItemPatternModifier) (Object) this;
    }
}
