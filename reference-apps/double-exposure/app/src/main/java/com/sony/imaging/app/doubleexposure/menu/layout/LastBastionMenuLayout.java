package com.sony.imaging.app.doubleexposure.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureModeController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LastBastionMenuLayout extends BaseMenuLayout {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return new View(getActivity());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if ("Manual".equals(selectedTheme)) {
            AppLog.info(this.TAG, "Display Unsupported Exposure Mode Caution");
            DoubleExposureModeController emc = DoubleExposureModeController.getInstance();
            CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
            CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
        } else {
            AppLog.info(this.TAG, "No caution for other than Manual Theme");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if ("Manual".equalsIgnoreCase(selectedTheme)) {
            DoubleExposureModeController emc = DoubleExposureModeController.getInstance();
            CautionUtilityClass.getInstance().disapperTrigger(emc.getCautionId());
            CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        } else {
            AppLog.info(this.TAG, "No caution for other than Manual Theme");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    protected IkeyDispatchEach getKeyHandler() {
        AppLog.exit(this.TAG, AppLog.getMethodName());
        IkeyDispatchEach ikeyDispatchEach = new IkeyDispatchEach(null, 0 == true ? 1 : 0) { // from class: com.sony.imaging.app.doubleexposure.menu.layout.LastBastionMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedPlayBackKey() {
                AppLog.enter(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                CautionUtilityClass.getInstance().executeTerminate();
                AppLog.exit(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMovieRecKey() {
                AppLog.enter(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                AppLog.exit(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedModeDial() {
                AppLog.enter(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                if (ModeDialDetector.getModeDialPosition() != -1) {
                    CautionUtilityClass.getInstance().executeTerminate();
                    super.turnedModeDial();
                }
                AppLog.exit(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMenuKey() {
                AppLog.enter(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                LastBastionMenuLayout.this.getActivity().finish();
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                AppLog.exit(LastBastionMenuLayout.this.TAG, AppLog.getMethodName());
                return 1;
            }
        };
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return ikeyDispatchEach;
    }
}
