package org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae;

import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.AppEngSlot;
import com.glodblock.github.extendedae.client.gui.GuiPatternModifier;
import com.glodblock.github.extendedae.container.ContainerPatternModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.DynamicScrollBar;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IScrollableInvScreen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiPatternModifier.class)
public abstract class MixinGuiPatternModifier implements IScrollableInvScreen<ContainerPatternModifier, GuiPatternModifier> {
    @Unique
    private DynamicScrollBar ae_pattern_improve$scrollBar;
    @Unique
    private ContainerPatternModifier ae_pattern_improve$menu;
    @Unique
    @Override
    public @NotNull ContainerPatternModifier ae_pattern_improve$getMenu() {
        return ae_pattern_improve$menu;
    }

    @Override
    public GuiPatternModifier ae_pattern_improve$getSelf() {
        return (GuiPatternModifier) (Object) this;
    }

    @Override
    public boolean ae_pattern_improve$shouldAddScrollBar() {
        return ae_pattern_improve$menu.getSlots(SlotSemantics.ENCODED_PATTERN).size() > 27;
    }

    @Override
    public void ae_pattern_improve$setScrollBar(DynamicScrollBar scrollBar) {
        this.ae_pattern_improve$scrollBar = scrollBar;
    }

    @Override
    public DynamicScrollBar ae_pattern_improve$getScrollBar() {
        return ae_pattern_improve$scrollBar;
    }

    @Override
    public List<AppEngSlot> ae_pattern_improve$getScrollerSlots() {
        return ae_pattern_improve$getMenu().getSlots(SlotSemantics.ENCODED_PATTERN).stream()
                .filter(slot -> slot instanceof AppEngSlot)
                .map(slot -> (AppEngSlot) slot)
                .toList();
    }

    @Override
    public SlotSemantic ae_pattern_improve$getScrollableSlotSemantic() {
        return SlotSemantics.ENCODED_PATTERN;
    }

    @Override
    public int ae_pattern_improve$getEffectiveRowCount() {
        return 3;
    }

    @Override
    public int ae_pattern_improve$getBGHeight() {
        return 87;
    }

    @Override
    public String ae_pattern_improve$getStylesheetName() {
        return "patternModifier";
    }

    @Override
    public boolean ae_pattern_improve$isVisible() {
        var page = ae_pattern_improve$getMenu().getPage();
        return page == 0 || page == 1 || page == 2;
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void ae_pattern_improve$init(ContainerPatternModifier menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        ae_pattern_improve$menu = menu;
        // Initialize the scrollbar if needed
        ae_pattern_improve$initScrollBar();
    }
    @Inject(
            method = "updateBeforeRender",
            at = @At("TAIL")
    )
    protected void ae_pattern_improve$updateBeforeRender(CallbackInfo ci) {
        // Update the scrollbar position and visibility
        ae_pattern_improve$repositionSlots();
    }
}
