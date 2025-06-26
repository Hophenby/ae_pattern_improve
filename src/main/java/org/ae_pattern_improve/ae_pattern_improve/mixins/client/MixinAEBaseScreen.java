package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.SlotPosition;
import net.minecraft.client.gui.components.Button;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IMoveSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AEBaseScreen.class)
public interface MixinAEBaseScreen {
    @Invoker(value = "addToLeftToolbar")
    <B extends Button> B ae_pattern_improve$addToLeftToolbar(B button);
    @Accessor("widgets")
    WidgetContainer ae_pattern_improve$getWidgets();
    @Mixin(AEBaseScreen.class)
    abstract class MixinAEBaseScreenInjector {
        @Inject(method = "getSlotPosition", at = @At("TAIL"), cancellable = true)
        private void moveSlotPosition(SlotPosition position, int semanticIndex, CallbackInfoReturnable<Point> cir) {
            if ((Object) this instanceof IMoveSlot moveSlot) {
                Point point = cir.getReturnValue();
                cir.setReturnValue(new Point(point.getX() + moveSlot.getSlotXShift(), point.getY() + moveSlot.getSlotYShift()));
            }
        }
    }
}