package org.ae_pattern_improve.ae_pattern_improve.mixin_helpers;

public interface IMoveSlot {
    default int getSlotXShift(){
        return 0;
    }
    default int getSlotYShift(){
        return 0;
    }
}
