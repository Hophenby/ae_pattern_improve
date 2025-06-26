package org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts;

import appeng.api.storage.ITerminalHost;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternBatchEncodingLogic;

public interface IPatternBatchTermMenuHost extends ITerminalHost {
    PatternBatchEncodingLogic getLogic();
}
