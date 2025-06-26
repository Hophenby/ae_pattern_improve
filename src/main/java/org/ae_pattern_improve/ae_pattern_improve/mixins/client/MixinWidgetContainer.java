package org.ae_pattern_improve.ae_pattern_improve.mixins.client;


import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.WidgetContainer;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(WidgetContainer.class)
public interface MixinWidgetContainer {

    @Accessor(value = "widgets", remap = false)
    Map<String, AbstractWidget> getWidgets();

    @Accessor(value = "compositeWidgets", remap = false)
    Map<String, ICompositeWidget> getCompositeWidgets();
}

