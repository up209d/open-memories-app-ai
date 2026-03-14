package com.sony.imaging.app.digitalfilter.shooting.trigger;

import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFSelfTimerController;
import com.sony.imaging.app.fw.CustomizableFunction;

/* loaded from: classes.dex */
public class GFCustomFunctionTable extends NormalFunctionTable {
    public GFCustomFunctionTable() {
        this.table.put(CustomizableFunction.PictureEffect, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FaceDetection, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DigitalZoom, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ScnSelection, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.CinemaTone, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, "drivemode", GFSelfTimerController.class));
    }
}
