package com.sony.imaging.app.timelapse.shooting;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseCreativeStyleController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapsePictureEffectController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseWhiteBalanceController;

/* loaded from: classes.dex */
public class TimelapseFunctionTable extends NormalFunctionTable {
    public TimelapseFunctionTable() {
        this.table.put(CustomizableFunction.PictureEffect, new FunctionInfo(17306771, "PictureEffect", TimelapsePictureEffectController.class));
        this.table.put(CustomizableFunction.CreativeStyle, new FunctionInfo(17306741, CreativeStyleController.CREATIVESTYLE, TimelapseCreativeStyleController.class));
        this.table.put(CustomizableFunction.WhiteBalance, new FunctionInfo(17306790, WhiteBalanceController.WHITEBALANCE, TimelapseWhiteBalanceController.class));
        this.table.put(CustomizableFunction.ExposureMode, new FunctionInfo(-1, ExposureModeController.EXPOSURE_MODE, ExposureModeController.class));
        this.table.put(CustomizableFunction.FocusMode, new FunctionInfo(-1, FocusModeController.TAG_FOCUS_MODE, FocusModeController.class));
        this.table.put(CustomizableFunction.AutoFocusMode, new FunctionInfo(-1, FocusModeController.TAG_FOCUS_MODE, FocusModeController.class));
        this.table.put(CustomizableFunction.FocusArea, new FunctionInfo(-1, FocusAreaController.TAG_FOCUS_AREA, FocusAreaController.class));
        this.table.put(CustomizableFunction.DriveMode, new FunctionInfo(-1, "drivemode", DriveModeController.class));
        this.table.put(CustomizableFunction.IsoSensitivity, new FunctionInfo(-1, ISOSensitivityController.MENU_ITEM_ID_ISO, ISOSensitivityController.class));
        this.table.put(CustomizableFunction.ImageQuality, new FunctionInfo(-1, TimeLapseConstants.API_NAME, PictureQualityController.class));
        this.table.put(CustomizableFunction.ImageSize, new FunctionInfo(-1, PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, PictureSizeController.class));
        this.table.put(CustomizableFunction.ImageAspect, new FunctionInfo(-1, PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, PictureSizeController.class));
        this.table.put(CustomizableFunction.DroHdr, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.AelHold, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.AelToggle, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.AfMfHold, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FlashCompensation, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.MeteringMode, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.AfMfToggle, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DispChange, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ExposureCompensation, new FunctionInfo(-1, "ExposureCompensation", ExposureCompensationController.class));
        this.table.put(CustomizableFunction.ShutterSpeedDecrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ShutterSpeedIncrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.FaceDetection, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ApertureIncrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.ApertureDecrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.IsoSensitivityDecrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.IsoSensitivityIncrement, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.MfAssist, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.DigitalZoom, new FunctionInfo(-1, DigitalZoomController.DIGITAL_ZOOM, DigitalZoomController.class));
        this.table.put(CustomizableFunction.NoAssign, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.Invalid, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.Unknown, new FunctionInfo(-1, null, null));
        this.table.put(CustomizableFunction.CinemaTone, new FunctionInfo(-1, null, null));
    }
}
