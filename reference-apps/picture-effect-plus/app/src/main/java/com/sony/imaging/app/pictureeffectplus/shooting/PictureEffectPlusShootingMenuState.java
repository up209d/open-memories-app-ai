package com.sony.imaging.app.pictureeffectplus.shooting;

import android.os.Message;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusExposureModeController;

/* loaded from: classes.dex */
public class PictureEffectPlusShootingMenuState extends ShootingMenuState {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean ret = true;
        if (ModeDialDetector.hasModeDial()) {
            ret = PictureEffectPlusExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
        }
        AppLog.info(TAG, AppLog.getMethodName() + "canRemoveState return " + ret);
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return ID_LASTBASTIONLAYOUT;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 == msg.what) {
            AppLog.info(TAG, "ignore S2 On");
            return true;
        }
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getClassName());
        super.onDestroy();
    }
}
