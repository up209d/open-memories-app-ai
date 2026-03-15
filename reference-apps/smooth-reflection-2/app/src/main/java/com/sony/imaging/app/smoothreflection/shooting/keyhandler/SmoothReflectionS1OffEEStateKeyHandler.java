package com.sony.imaging.app.smoothreflection.shooting.keyhandler;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.smoothreflection.caution.SmoothReflectionInfo;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BatteryObserver;

/* loaded from: classes.dex */
public class SmoothReflectionS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final int BatteryPreEnd = 0;
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.info(this.TAG, e.toString());
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        if (ThemeController.CUSTOM.equals(selectedTheme)) {
            return super.incrementedApertureCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.info(this.TAG, e.toString());
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        if (ThemeController.CUSTOM.equals(selectedTheme)) {
            return super.decrementedApertureCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        if (BatteryObserver.getInt(BatteryObserver.TAG_LEVEL) == 0) {
            CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_NO_BATTERY_ON_SHOOTING);
            return -1;
        }
        ExecutorCreator executorCreator = ExecutorCreator.getInstance();
        if (!executorCreator.isAElockedOnAutoFocus()) {
            String behavior = null;
            String focusMode = AutoFocusModeController.getInstance().getValue();
            if ("af-s".equals(focusMode)) {
                behavior = "af_woaf";
            } else if ("af-c".equals(focusMode)) {
                behavior = "afc_woaf";
            }
            executorCreator.getSequence().autoFocus(null, behavior);
        } else {
            executorCreator.getSequence().autoFocus(null);
        }
        return super.pushedIRShutterNotCheckDrivemodeKey();
    }
}
