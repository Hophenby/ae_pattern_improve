package org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.widgets.Scrollbar;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IScrollbarAccessor;
import org.jetbrains.annotations.Nullable;

public class MirrorScrollBar extends Scrollbar {
    private MirrorScrollBar other;
    private boolean recursive = false;

    public MirrorScrollBar() {
        super();
    }
    public MirrorScrollBar(Style style) {
        super(style);
    }

    private void setOther(MirrorScrollBar other) {
        if (this.other != null) {
            throw new IllegalStateException("Other scrollbar is already set!");
        }
        this.other = other;
        other.other = this;
    }
    public MirrorScrollBar getOrCreateOther(@Nullable Style fallbackStyle) {
        if (other == null) {
            other = fallbackStyle == null ? new MirrorScrollBar() : new MirrorScrollBar(fallbackStyle);
            other.setOther(this);
            onChanged();
        }
        return other;
    }
    private void onChanged() {
        if (recursive) {
            return; // Prevent infinite recursion
        }
        if (other != null && other.other == this && !other.recursive) {
            recursive = true;
            other.setRange(((IScrollbarAccessor) this).ae_pattern_improve$getMin(),
                    ((IScrollbarAccessor) this).ae_pattern_improve$getMax(),
                    ((IScrollbarAccessor) this).ae_pattern_improve$getPageSize());
            other.setCurrentScroll(this.getCurrentScroll());
        }
        recursive = false;
    }

    @Override
    public void setCurrentScroll(int currentScroll) {
        super.setCurrentScroll(currentScroll);
        onChanged();
    }

    @Override
    public void setRange(int min, int max, int pageSize) {
        super.setRange(min, max, pageSize);
        onChanged();
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        var success = super.onMouseUp(mousePos, button);
        onChanged();
        return success;
    }
    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        var success = super.onMouseDown(mousePos, button);
        onChanged();
        return success;
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int button) {
        var success = super.onMouseDrag(mousePos, button);
        onChanged();
        return success;
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        var success = super.onMouseWheel(mousePos, delta);
        onChanged();
        return success;
    }
}
