package com.sony.imaging.app.smoothreflection.shooting.state;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.smoothreflection.SmoothReflectionApp;
import com.sony.imaging.app.smoothreflection.caution.SmoothReflectionInfo;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothReflectionEEState extends EEState {
    private static final int BatteryPreEnd = 0;
    private final String TAG = AppLog.getClassName();
    private final String NEXT_STATE_MENU = ICustomKey.CATEGORY_MENU;
    private final String DATA_TAG_ITEMID = MenuState.ITEM_ID;
    private String mItemId = null;
    private Bundle mBundle = null;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.data != null) {
            this.mItemId = this.data.getString(MenuState.ITEM_ID);
            this.data = null;
        }
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        CameraNotificationManager.OnFocusInfo obj;
        int cautionId = 0;
        boolean ret = false;
        if (518 == msg.what) {
            AvailableInfo.update();
            if (BatteryObserver.getInt(BatteryObserver.TAG_LEVEL) == 0) {
                CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_NO_BATTERY_ON_SHOOTING);
                return false;
            }
            if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") || AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
                cautionId = SmoothReflectionInfo.CAUTION_ID_DLAPP_STEADY_SHOT;
                CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_STEADY_SHOT);
            } else {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String focusMode = ((Camera.Parameters) p.first).getFocusMode();
                if (sensorType == 1 && "auto".equals(focusMode) && ((obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS)) == null || obj.status != 1)) {
                    cautionId = 1;
                }
            }
        } else if ((552 == msg.what || 553 == msg.what) && BatteryObserver.getInt(BatteryObserver.TAG_LEVEL) == 0) {
            CautionUtilityClass.getInstance().requestTrigger(SmoothReflectionInfo.CAUTION_ID_DLAPP_NO_BATTERY_ON_SHOOTING);
            return false;
        }
        Log.d("MSG Check", "MSGCheck = " + msg.what);
        if (cautionId == 0) {
            ret = super.handleMessage(msg);
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mItemId = null;
        this.mBundle = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String state;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        SmoothReflectionUtil smoothPhotographyUtil = SmoothReflectionUtil.getInstance();
        String currentMenuSelectionScreen = smoothPhotographyUtil.getCurrentMenuSelectionScreen();
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            if (SmoothReflectionApp.sBootFactor != 0) {
                smoothPhotographyUtil.setCurrentMenuSelectionScreen(null);
            }
            this.mItemId = null;
            state = ICustomKey.CATEGORY_MENU;
        } else if (SmoothReflectionApp.sBootFactor == 0) {
            if (this.mItemId != null || (currentMenuSelectionScreen != null && currentMenuSelectionScreen.equalsIgnoreCase(SmoothReflectionConstants.THEME_SELECTION))) {
                this.mItemId = "ApplicationTop";
                this.mBundle = new Bundle();
                this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
                smoothPhotographyUtil.setCurrentMenuSelectionScreen(null);
                this.mItemId = null;
                AppLog.exit(this.TAG, AppLog.getMethodName());
                state = ICustomKey.CATEGORY_MENU;
            } else {
                SmoothReflectionApp.sBootFactor = -1;
                AppLog.info(this.TAG, "EE screen opened ");
                return super.getNextChildState();
            }
        } else {
            AppLog.exit(this.TAG, AppLog.getMethodName());
            state = super.getNextChildState();
            if (state.equalsIgnoreCase(S1OffEEState.STATE_NAME)) {
                ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
            }
        }
        return state;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mBundle;
    }
}
