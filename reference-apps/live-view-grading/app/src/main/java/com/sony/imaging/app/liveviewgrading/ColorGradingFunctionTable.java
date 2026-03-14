package com.sony.imaging.app.liveviewgrading;

import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.CustomizableFunction;

/* loaded from: classes.dex */
public class ColorGradingFunctionTable extends NormalFunctionTable {
    public ColorGradingFunctionTable() {
        this.table.put(CustomizableFunction.PictureEffect, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.CreativeStyle, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageQuality, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageSize, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageAspect, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashCompensation, new FunctionInfo(-1, null, null));
    }
}
