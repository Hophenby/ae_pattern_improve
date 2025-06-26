package org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts;

import net.minecraft.world.level.Level;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternBatchEncodingLogic;

public interface IPatternBatchTermLogicHost {
    PatternBatchEncodingLogic getLogic();
    Level getLevel();
    void markForSave();
}
