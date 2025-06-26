package org.ae_pattern_improve.ae_pattern_improve.events;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.ae_pattern_improve.ae_pattern_improve.config.CommonConfig;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.XModUtils;

import java.util.Objects;

@EventBusSubscriber(Dist.CLIENT)
public class TooltipEvent {
    @SubscribeEvent
    public static void onTooltipRender(RenderTooltipEvent.GatherComponents event) {
        var item = event.getItemStack();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());
        if (XModUtils.isModLoaded(XModUtils.EXTENDED_AE_MOD_ID)
                && CommonConfig.enablePatternModifierPulling.get()
                && Objects.equals(id, BuiltInRegistries.ITEM.getKey(EAESingletons.PATTERN_MODIFIER))) {
            var tooltipComponent1 = FormattedText.of(
                    Component.translatable("ae_pattern_improve.tooltip.extendedae.pattern_modifier.1").getString(), Style.EMPTY.withColor(0x00AABB)
            );
            var tooltipComponent2 = FormattedText.of(
                    Component.translatable("ae_pattern_improve.tooltip.extendedae.pattern_modifier.2").getString(), Style.EMPTY.withColor(0xAABB00)
            );
            event.getTooltipElements().add(1,
                    Either.left(tooltipComponent2)
            );
            event.getTooltipElements().add(1,
                    Either.left(tooltipComponent1)
            );
            event.setMaxWidth(Math.max(event.getMaxWidth(), Minecraft.getInstance().font.width(tooltipComponent1)));
            event.setMaxWidth(Math.max(event.getMaxWidth(), Minecraft.getInstance().font.width(tooltipComponent2)));
        }
    }
}
