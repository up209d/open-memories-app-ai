package com.sony.imaging.app.bracketpro;

import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.CustomizableFunction;

/* loaded from: classes.dex */
public class BMNormalFunctionTable extends NormalFunctionTable {
    public BMNormalFunctionTable() {
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.PictureEffect, new FunctionInfo(-1, null, null));
    }
}
