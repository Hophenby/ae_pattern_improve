package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.menu.slot.AppEngSlot;
import com.glodblock.github.extendedae.container.ContainerExPatternProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.DynamicScrollBar;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IScrollableInvScreen;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.XModUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PatternProviderScreen.class)
public abstract class MixinPatternProviderScreen implements IScrollableInvScreen<PatternProviderMenu, PatternProviderScreen<PatternProviderMenu>> {
    @Unique
    DynamicScrollBar ae_pattern_improve$scrollBar;
    @Unique
    private PatternProviderMenu ae_pattern_improve$menu;

    @Unique
    @Override
    @SuppressWarnings("unchecked")
    public PatternProviderScreen<PatternProviderMenu> ae_pattern_improve$getSelf() {
        return (PatternProviderScreen<PatternProviderMenu>) (Object) this;
    }
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onInit(PatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        ae_pattern_improve$menu = menu;
        // Initialize the scrollbar if needed
        ae_pattern_improve$initScrollBar();
    }
    @Unique
    @Override
    public boolean ae_pattern_improve$shouldAddScrollBar() {
        return ae_pattern_improve$menu.getSlots(SlotSemantics.ENCODED_PATTERN).size() > 36
                && XModUtils.isModLoaded(XModUtils.EXTENDED_AE_MOD_ID)
                && ContainerExPatternProvider.class.isAssignableFrom(ae_pattern_improve$menu.getClass());
    }
    @Inject(
            method = "updateBeforeRender",
            at = @At("TAIL")
    )
    protected void ae_pattern_improve$updateBeforeRender(CallbackInfo ci) {
        // Update the scrollbar position and visibility
        ae_pattern_improve$repositionSlots();
    }
    @Unique
    @Override
    public List<AppEngSlot> ae_pattern_improve$getScrollerSlots() {
        return ae_pattern_improve$menu.getSlots(SlotSemantics.ENCODED_PATTERN).stream()
                .map(AppEngSlot.class::cast)
                .toList();
    }
    @Unique
    @Override
    public SlotSemantic ae_pattern_improve$getScrollableSlotSemantic() {
        return SlotSemantics.ENCODED_PATTERN;
    }
    @Unique
    @Override
    public @NotNull PatternProviderMenu ae_pattern_improve$getMenu() {
        return ae_pattern_improve$menu;
    }
    @Unique
    @Override
    public void ae_pattern_improve$setScrollBar(DynamicScrollBar scrollBar) {
        this.ae_pattern_improve$scrollBar = scrollBar;
    }
    @Unique
    @Override
    public DynamicScrollBar ae_pattern_improve$getScrollBar() {
        return ae_pattern_improve$scrollBar;
    }
    @Unique
    @Override
    public int ae_pattern_improve$getEffectiveRowCount() {
        return 4;
    }
    @Unique
    @Override
    public int ae_pattern_improve$getBGHeight() {
        return 105;
    }
    @Unique
    @Override
    public String ae_pattern_improve$getStylesheetName() {
        return "patternProvider";
    }
}
