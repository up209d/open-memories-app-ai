package com.sony.imaging.app.doubleexposure.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.shooting.layout.ThemeGuideOnShootingLayout;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class DoubleExposureS1OffEEState extends S1OffEEState {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().getFocusMode();
        super.onResume();
        openShootingGuideLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.S1OffEEState
    public NotificationListener getListener() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (ThemeSelectionController.SOFTFILTER.equals(selectedTheme) && DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen)) {
            return null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.getListener();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (msg.what == 1111) {
            closeLayout(StateBase.DEFAULT_LAYOUT);
            closeLayout(StateBase.S1OFF_LAYOUT);
            closeShootingGuideLayout();
            openLayout(StateBase.DEFAULT_LAYOUT);
            openLayout(StateBase.S1OFF_LAYOUT);
            openShootingGuideLayout();
        }
        return super.handleMessage(msg);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        closeShootingGuideLayout();
        super.onDestroy();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void openShootingGuideLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!DoubleExposureUtil.getInstance().isGuideClosed()) {
            openLayout(ThemeGuideOnShootingLayout.ID_THEMEGUIDEONSHOOTINGLAYOUT);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void closeShootingGuideLayout() {
        Layout layout = getLayout(ThemeGuideOnShootingLayout.ID_THEMEGUIDEONSHOOTINGLAYOUT);
        if (layout != null && layout.getView() != null) {
            DoubleExposureUtil.getInstance().setGuideClosed();
            closeLayout(ThemeGuideOnShootingLayout.ID_THEMEGUIDEONSHOOTINGLAYOUT);
        }
    }
}
