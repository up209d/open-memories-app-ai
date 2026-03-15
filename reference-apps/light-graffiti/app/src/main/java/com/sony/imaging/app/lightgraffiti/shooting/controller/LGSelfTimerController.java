package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class LGSelfTimerController extends AbstractController {
    private static LGSelfTimerController mInstance;
    private boolean isOneTimeTimerRunning = false;
    private BackUpUtil mBackUpUtil;
    private static String TAG = LGSelfTimerController.class.getSimpleName();
    public static String DRIVE_MODE = DriveModeController.DRIVEMODE;
    public static String SELF_TIMER_10 = "SelfTimer10sec";
    public static String SELF_TIMER_2 = "SelfTimer2sec";
    public static String SELF_TIMER_OFF = "SelfTimerOff";
    public static String SELC_TIMER_EXPOSING_FINISH = "SelfTimerExposingFinish";

    public static LGSelfTimerController getInstance() {
        if (mInstance == null) {
            new LGSelfTimerController();
        }
        return mInstance;
    }

    private static void setController(LGSelfTimerController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGSelfTimerController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
    }

    public void setOneTimeTimer(String value) {
        if (value.equals(SELF_TIMER_2) || value.equals(SELF_TIMER_10)) {
            setValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER, value);
            this.isOneTimeTimerRunning = true;
        } else {
            Log.e(TAG, "Invalid Timer Setting!!");
        }
    }

    public void unsetOneTimeTimer() {
        if (this.isOneTimeTimerRunning) {
            setValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER, SELF_TIMER_OFF);
            this.isOneTimeTimerRunning = false;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag + " value=" + value);
        this.isOneTimeTimerRunning = false;
        this.mBackUpUtil.setPreference(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag);
        String value = this.mBackUpUtil.getPreferenceString(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER, SELF_TIMER_2);
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag);
        List<String> list = new ArrayList<>();
        list.add(SELF_TIMER_10);
        list.add(SELF_TIMER_2);
        list.add(SELF_TIMER_OFF);
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag);
        List<String> list = new ArrayList<>();
        list.add(SELF_TIMER_10);
        list.add(SELF_TIMER_2);
        list.add(SELF_TIMER_OFF);
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        Log.d(TAG, AppLog.getMethodName() + " : tag=" + tag);
        return true;
    }
}
