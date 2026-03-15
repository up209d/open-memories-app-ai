package com.sony.imaging.app.startrails.shooting.trigger.custom;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.startrails.base.menu.controller.STCreativeStyleController;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureCompensationController;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.base.menu.controller.STISOSensitivityController;
import com.sony.imaging.app.startrails.base.menu.controller.STPictureEffectController;
import com.sony.imaging.app.startrails.base.menu.controller.STWhiteBalanceController;
import com.sony.imaging.app.startrails.menu.controller.STSelfTimerMenuController;

/* loaded from: classes.dex */
public class STNormalFunctionTable extends NormalFunctionTable {
    public STNormalFunctionTable() {
        this.table.put(CustomizableFunction.PictureEffect, new FunctionInfo(17306771, PictureEffectController.PICTUREEFFECT, STPictureEffectController.class));
        this.table.put(CustomizableFunction.CreativeStyle, new FunctionInfo(17306741, CreativeStyleController.CREATIVESTYLE, STCreativeStyleController.class));
        this.table.put(CustomizableFunction.WhiteBalance, new FunctionInfo(17306790, WhiteBalanceController.WHITEBALANCE, STWhiteBalanceController.class));
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, "drivemode", STSelfTimerMenuController.class));
        this.table.put(CustomizableFunction.IsoSensitivity, new FunctionInfo(-1, ISOSensitivityController.MENU_ITEM_ID_ISO, STISOSensitivityController.class));
        this.table.put(CustomizableFunction.ExposureMode, new FunctionInfo(-1, ExposureModeController.EXPOSURE_MODE, STExposureModeController.class));
        this.table.put(CustomizableFunction.ExposureCompensation, new FunctionInfo(-1, "ExposureCompensation", STExposureCompensationController.class));
        this.table.put(CustomizableFunction.FocusMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.AutoFocusMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FocusArea, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageQuality, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageSize, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ImageAspect, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DroHdr, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashCompensation, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.MeteringMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DispChange, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ShutterSpeedDecrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ShutterSpeedIncrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FaceDetection, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ApertureIncrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ApertureDecrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.IsoSensitivityDecrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.IsoSensitivityIncrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DigitalZoom, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.NoAssign, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.Invalid, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.Unknown, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.CinemaTone, new FunctionInfo(-1, null, null));
    }
}
