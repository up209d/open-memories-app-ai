package com.sony.imaging.app.smoothreflection.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionExposureModeController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LastBastionMenuLayout extends BaseMenuLayout {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return new View(getActivity());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, e.toString());
        }
        if (ThemeController.CUSTOM.equals(selectedTheme)) {
            SmoothReflectionExposureModeController exposureModeController = SmoothReflectionExposureModeController.getInstance();
            CautionUtilityClass.getInstance().setDispatchKeyEvent(exposureModeController.getCautionId(), getKeyHandler());
            CautionUtilityClass.getInstance().requestTrigger(exposureModeController.getCautionId());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.info(TAG, e.toString());
        }
        if (ThemeController.CUSTOM.equalsIgnoreCase(selectedTheme)) {
            SmoothReflectionExposureModeController emc = SmoothReflectionExposureModeController.getInstance();
            CautionUtilityClass.getInstance().disapperTrigger(emc.getCautionId());
            CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        }
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected IkeyDispatchEach getKeyHandler() {
        return new IkeyDispatchEach(null, 0 == true ? 1 : 0) { // from class: com.sony.imaging.app.smoothreflection.menu.layout.LastBastionMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedPlayBackKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMovieRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedModeDial() {
                if (ModeDialDetector.getModeDialPosition() != -1) {
                    CautionUtilityClass.getInstance().executeTerminate();
                    super.turnedModeDial();
                    return 1;
                }
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMenuKey() {
                LastBastionMenuLayout.this.getActivity().finish();
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                return 1;
            }
        };
    }
}
