package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.slot.FakeSlot;
import appeng.parts.encoding.EncodingMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternUtils;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IMulableTermMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import appeng.menu.me.items.PatternEncodingTermMenu;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public abstract class MixinPatternEncodingTermMenu extends AEBaseMenu implements IMulableTermMenu {
    public MixinPatternEncodingTermMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }
    @Unique
    private static String ae_pattern_improve$getMulPatternEncodingActionName(int multiplier, boolean divMode) {
        if (divMode) {
            return "patternEncodingDivide" + multiplier;
        } else {
            return "patternEncodingMultiply" + multiplier;
        }
    }
    @Shadow
    public EncodingMode mode;
    @Final
    @Shadow
    private FakeSlot[] processingInputSlots;
    @Final
    @Shadow
    private FakeSlot[] processingOutputSlots;
    @Unique
    private Runnable ae_pattern_improve$getMulPatternEncodingAction(int multiplier, boolean divMode) {
        return () -> {
            if (isClientSide()) {
                sendClientAction(ae_pattern_improve$getMulPatternEncodingActionName(multiplier, divMode));
            } else {
                if (mode != EncodingMode.PROCESSING) {
                    return;
                }
                PatternUtils.mulPatternEncodingArea(
                        processingInputSlots,
                        processingOutputSlots,
                        multiplier,
                        divMode
                );
            }
        };
    }
    @Override
    public void mulPatternEncoding(int multiplier, boolean divMode) {
        // This method is called from the client side to trigger the action
        ae_pattern_improve$getMulPatternEncodingAction(multiplier, divMode).run();
    }
    @Inject(method = "<init>*", at = @At("TAIL"))
    private void ae_pattern_improve$init(MenuType<?> menuType, int id, Inventory ip, IPatternTerminalMenuHost host,
                              boolean bindInventory, CallbackInfo ci) {
        // Add the multiply/divide actions to the menu
        registerClientAction(
                ae_pattern_improve$getMulPatternEncodingActionName(2, false),
                ae_pattern_improve$getMulPatternEncodingAction(2, false)
        );
        registerClientAction(
                ae_pattern_improve$getMulPatternEncodingActionName(2, true),
                ae_pattern_improve$getMulPatternEncodingAction(2, true)
        );
        registerClientAction(
                ae_pattern_improve$getMulPatternEncodingActionName(3, false),
                ae_pattern_improve$getMulPatternEncodingAction(3, false)
        );
        registerClientAction(
                ae_pattern_improve$getMulPatternEncodingActionName(3, true),
                ae_pattern_improve$getMulPatternEncodingAction(3, true)
        );
        registerClientAction(
                ae_pattern_improve$getMulPatternEncodingActionName(5, false),
                ae_pattern_improve$getMulPatternEncodingAction(5, false)
        );
        registerClientAction(
                ae_pattern_improve$getMulPatternEncodingActionName(5, true),
                ae_pattern_improve$getMulPatternEncodingAction(5, true)
        );
        //Ae_test.LOGGER.debug("PatternEncodingTermMenu mixin initialized with custom actions for multiplying/dividing patterns.");
    }
}
