package com.sony.imaging.app.doubleexposure.playback.state;

import android.os.Message;
import android.view.KeyEvent;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.DatabaseUtil;

/* loaded from: classes.dex */
public class PreparingPlayState extends PlayStateBase {
    public static final String ID_PREPAIRINGLAYOUT = "ID_PREPAIRINGLAYOUT";
    public static final int TRANSIT_TO_SHOOTING = 102;
    private final String TAG = AppLog.getClassName();
    private DoubleExposureUtil mDoubleExposureUtil = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        openLayout(ID_PREPAIRINGLAYOUT, this.data);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.SECOND_SHOOTING);
        if (getLayout(ID_PREPAIRINGLAYOUT).getView() != null) {
            getLayout(ID_PREPAIRINGLAYOUT).closeLayout();
        }
        this.mDoubleExposureUtil = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        switch (msg.what) {
            case TRANSIT_TO_SHOOTING /* 102 */:
                returnToShootingState();
                break;
        }
        return super.handleMessage(msg);
    }

    private int returnToShootingState() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus()) {
            this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        } else {
            String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
            if ("Manual".equals(selectedTheme)) {
                this.mDoubleExposureUtil.setCurrentMenuSelectionScreen(DoubleExposureConstant.MODE_SELECTION);
            }
            this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.SECOND_SHOOTING);
        }
        transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }
}
