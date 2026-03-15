package com.sony.imaging.app.lightshaft.shooting;

import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.CustomizableFunction;

/* loaded from: classes.dex */
public class LSNormalFunctionTable extends NormalFunctionTable {
    public LSNormalFunctionTable() {
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageSize, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DroHdr, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DigitalZoom, new FunctionInfo(-1, null, null));
    }
}
