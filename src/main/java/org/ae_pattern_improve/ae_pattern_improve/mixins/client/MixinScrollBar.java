package org.ae_pattern_improve.ae_pattern_improve.mixins.client;

import appeng.client.gui.widgets.Scrollbar;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IScrollbarAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Scrollbar.class)
public abstract class MixinScrollBar implements IScrollbarAccessor {
    @Shadow
    private int minScroll;

    @Shadow
    private int maxScroll;

    @Shadow
    private int pageSize;

    @Shadow
    private int height;

    @Shadow
    protected abstract int getRange();

    @Override
    public int ae_pattern_improve$getRange() {
        return getRange();
    }

    @Override
    public int ae_pattern_improve$getMin() {
        return minScroll;
    }

    @Override
    public int ae_pattern_improve$getMax() {
        return maxScroll;
    }

    @Override
    public int ae_pattern_improve$getPageSize() {
        return pageSize;
    }

    @Override
    public int ae_pattern_improve$getHeight() {
        return height;
    }
}
