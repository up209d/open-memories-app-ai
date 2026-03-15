package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGMenuDataInitializer;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class LGS1OffEEState extends S1OffEEState implements NotificationListener, LGStateHolder.ValueChangedListener {
    private static final String HALF_SHUTTER_STATE = "S1OnEE";
    private LGS1OffEEStateLensStateListener mLensListener = new LGS1OffEEStateLensStateListener();
    private static final String TAG = LGS1OffEEState.class.getSimpleName();
    public static String LAYOUT_EE_GUIDE_EASY = "ID_LGLAYOUTEEGUIDEEASY";

    /* loaded from: classes.dex */
    private class LGS1OffEEStateLensStateListener extends LGAbstractLensStateChangeListener {
        private LGS1OffEEStateLensStateListener() {
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener
        protected void onLensStateChanged() {
            Log.d("LGS1OffEEStateLensStateListener", "onLensStateChanged");
            if (LGStateHolder.getInstance().isShootingStage3rd()) {
                Log.d("LGS1OffEEStateLensStateListener", "LGS1OffEEState detected the lens problem. App will ");
                LGS1OffEEState.this.setNextState("LensProblem", null);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(TAG, "onPause()");
        closeLayout(LAYOUT_EE_GUIDE_EASY);
        closeLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        LGStateHolder.getInstance().removeValueChangedListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
        LGShootingState.startFirstTimeLensCare();
        if (LGLensProblemCautionRelayState.isLensProblemState) {
            LGLensProblemCautionRelayState.isLensProblemState = false;
        }
        if (LGStateHolder.getInstance().isLendsProblem()) {
            LGStateHolder.getInstance().setLensProblemFlag(false);
            setNextState("LensProblem", null);
            return;
        }
        FaceDetectionController.getInstance().setFaceFrameRendering(true);
        AppInfo.notifyAppInfo(getActivity().getApplicationContext(), getActivity().getPackageName(), getActivity().getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, BaseApp.PULLING_BACK_KEYS_FOR_SHOOTING, BaseApp.RESUME_KEYS_FOR_SHOOTING);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        Log.d(TAG, "releasedS1Key. setIsStartFocusing(false)");
        LGStateHolder.getInstance().setIsStartFocusing(false);
        openLayout(LAYOUT_EE_GUIDE_EASY);
        String stage = LGStateHolder.getInstance().getShootingStage();
        boolean isFirstOpen = LGUtility.getInstance().isFirstTimeOpen();
        if (stage.equals(LGStateHolder.SHOOTING_1ST) && isFirstOpen) {
            openLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_1ST_INTRODUCTION_LAYOUT_OPEN_KEY, true);
        } else if (LGStateHolder.getInstance().isShootingStage3rd() && isFirstOpen) {
            openLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_3RD_INTRODUCTION_LAYOUT_OPEN_KEY, true);
        }
        LGStateHolder.getInstance().setValueChangedListener(this);
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == status.status) {
            closeLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
            LGStateHolder.getInstance().setShootingEnable(true);
            ExecutorCreator executorCreator = ExecutorCreator.getInstance();
            if (!executorCreator.isAElockedOnAutoFocus()) {
                String behavior = null;
                String focusMode = AutoFocusModeController.getInstance().getValue();
                if ("af-s".equals(focusMode)) {
                    behavior = "af_woaf";
                } else if ("af-c".equals(focusMode)) {
                    behavior = "af_woaf";
                }
                executorCreator.getSequence().autoFocus(null, behavior);
            } else {
                executorCreator.getSequence().autoFocus(null);
            }
            setNextState(HALF_SHUTTER_STATE, null);
        }
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder.ValueChangedListener
    public void onValueChanged(String tag) {
        if (tag.equals(LGStateHolder.SHOOTING_STAGE)) {
            String stage = LGStateHolder.getInstance().getShootingStage();
            LGMenuDataInitializer.initMenuData(stage);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{LGConstants.SHOOTING_GUIDE_DETAIL_OPEN_EVENT};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (LGConstants.SHOOTING_GUIDE_DETAIL_OPEN_EVENT.equals(tag)) {
            String stage = LGStateHolder.getInstance().getShootingStage();
            if (!LGStateHolder.SHOOTING_3RD_AFTER_SHOOT.equals(stage)) {
                openLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
                return;
            } else {
                Log.e(TAG, AppLog.getMethodName() + " Unexpected! tag=" + tag + " stage=" + stage);
                return;
            }
        }
        Log.e(TAG, AppLog.getMethodName() + " Unexpected! tag=" + tag);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NONE;
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (LGStateHolder.SHOOTING_1ST.equals(stage)) {
            ApoWrapper.APO_TYPE type2 = ApoWrapper.APO_TYPE.NORMAL;
            return type2;
        }
        return type;
    }
}
