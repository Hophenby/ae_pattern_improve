package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface MixinScreen {
    @Invoker(value = "addRenderableOnly")
    <RENDERABLE extends net.minecraft.client.gui.components.Renderable> RENDERABLE ae_pattern_improve$addRenderableWidget(RENDERABLE renderable);

    @Invoker(value = "removeWidget")
    void ae_pattern_improve$removeWidget(net.minecraft.client.gui.components.events.GuiEventListener widget);
}
