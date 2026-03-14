package com.sony.imaging.app.portraitbeauty.common;

import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.CustomizableFunction;

/* loaded from: classes.dex */
public class PortraitBeautyFunctionTable extends NormalFunctionTable {
    public PortraitBeautyFunctionTable() {
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ScnSelection, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FaceDetection, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.PictureEffect, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DigitalZoom, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ExposureMode, new FunctionInfo(-1, null, null));
    }
}
