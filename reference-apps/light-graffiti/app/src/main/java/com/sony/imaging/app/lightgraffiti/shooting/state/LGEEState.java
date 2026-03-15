package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.lightgraffiti.LightGraffitiApp;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGAppTopController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGMenuDataInitializer;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class LGEEState extends EEState implements NotificationListener {
    private static final String PTAG_RELEASE_LAG_END = "start shutter lag";
    private static final String TAG = LGEEState.class.getSimpleName();
    private static final String[] TAGS = {CameraNotificationManager.DONE_AUTO_FOCUS, CameraNotificationManager.SHUTTER_SPEED};
    private Bundle mBundle = null;
    private boolean mShutterRelease = false;
    protected NotificationListener mediaStatusChangedListener = new MediaNotificationListener();

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        this.mShutterRelease = false;
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            LGPreviewEffect.getInstance().startPreviewEffect();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        String stage = LGStateHolder.getInstance().getShootingStage();
        LGMenuDataInitializer.initMenuData(stage);
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        if (LGDiscardLightTrailDialogueState.showDiscardCaution) {
            Log.d(TAG, "getNextChildState TRANSIT_FROM_3RD_BY_PB");
            return "DiscardDialogue";
        }
        if (LightGraffitiApp.openMenuOnEE) {
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, "ApplicationTop");
            LightGraffitiApp.openMenuOnEE = false;
            return ICustomKey.CATEGORY_MENU;
        }
        if (this.data != null && this.data.getString(LGConstants.TRANSIT_FROM_WHERE) != null) {
            String transitFromWhere = this.data.getString(LGConstants.TRANSIT_FROM_WHERE);
            if (transitFromWhere.equals(LGConstants.TRANSIT_FROM_3RD_BY_AUTO_REVIEW) || transitFromWhere.equals(LGConstants.TRANSIT_FROM_3RD_BY_PB)) {
                this.mBundle = new Bundle();
                this.mBundle.putString(LGConstants.TRANSIT_FROM_WHERE, transitFromWhere);
                return "DiscardDialogue";
            }
            return super.getNextChildState();
        }
        LGAppTopController.getInstance().setShootingScreenOpened(true);
        return super.getNextChildState();
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        return this.mBundle;
    }

    /* loaded from: classes.dex */
    private static class MediaNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int caution_ID = 0;
            try {
                Log.d(LGEEState.TAG, "onNotify:getcaution_id");
                caution_ID = CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId;
            } catch (IndexOutOfBoundsException ex) {
                Log.i(LGEEState.TAG, ex.getMessage());
            } catch (NullPointerException e) {
                Log.i(LGEEState.TAG, "NullPointerException");
            }
            Log.d(LGEEState.TAG, "onNotify:getcaution_id=" + caution_ID);
            switch (caution_ID) {
                case LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING /* 132005 */:
                    Log.e(LGEEState.TAG, "LGEEState:onNotify:CAUTION Route = " + caution_ID);
                    int state = MediaNotificationManager.getInstance().getMediaState();
                    if (2 == state) {
                        LGEEState.disapearDisplayingCaution(LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
                        return;
                    }
                    return;
                case LGInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL /* 132006 */:
                    Log.e(LGEEState.TAG, "LGEEState:onNotify:CAUTION Route = " + caution_ID);
                    LGEEState.disapearDisplayingCaution(caution_ID);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void disapearDisplayingCaution(int caution_ID) {
        CautionUtilityClass.getInstance().disapperTrigger(caution_ID);
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        Log.e(TAG, "handleMessage Called");
        if (518 == msg.what) {
            int sCautionId = LGUtility.getInstance().getCautionId();
            AppLog.trace(TAG, "handleMessage() sCautionId=" + sCautionId);
            if (sCautionId != 0) {
                CautionUtilityClass.getInstance().requestTrigger(sCautionId);
            } else {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String focusMode = ((Camera.Parameters) p.first).getFocusMode();
                if (sensorType == 1 && "auto".equals(focusMode)) {
                    try {
                        if (LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                            CameraNotificationManager.OnFocusInfo obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
                            if (obj == null || obj.status != 1) {
                                Log.e(TAG, "Auto focus is not done completely.");
                                sCautionId = 1;
                            }
                        } else {
                            Log.d(TAG, "Selftimer Shutter sequence. Imitate Remote Shutter.");
                            if (!this.mShutterRelease) {
                                this.mShutterRelease = true;
                                Log.d(TAG, "Release shutter in EEState.");
                                LGStateHolder.getInstance().setIsStartFocusing(false);
                                LGStateHolder.getInstance().setIsRemoteShutter(true);
                            } else {
                                this.mShutterRelease = false;
                                Log.d(TAG, "Already released!");
                                sCautionId = 1;
                            }
                        }
                    } catch (IController.NotSupportedException e) {
                        Log.e(TAG, "Selftimer parameter NotSupportedException in LGEEState.");
                        e.printStackTrace();
                    }
                }
            }
            if (sCautionId != 0) {
                return false;
            }
            boolean ret = super.handleMessage(msg);
            return ret;
        }
        AppLog.trace(TAG, "handleMessage() do not handled");
        boolean ret2 = super.handleMessage(msg);
        return ret2;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        Log.d(TAG, "onNotify() : tag=" + tag);
        if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S2_ON);
            if (1 == status.status) {
                Log.d(TAG, "onNotify DONE_AUTO_FOCUS");
                int sensorType = FocusAreaController.getInstance().getSensorType();
                Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
                String focusMode = ((Camera.Parameters) p.first).getFocusMode();
                if (sensorType == 1 && "auto".equals(focusMode)) {
                    Log.d(TAG, "Only GPMA support.");
                    CameraNotificationManager.OnFocusInfo obj = (CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
                    if (!this.mShutterRelease && obj != null && obj.status == 1) {
                        this.mShutterRelease = true;
                        Log.d(TAG, "inquireKey(USER_KEYCODE.S2_ON)");
                        ExecutorCreator executor = ExecutorCreator.getInstance();
                        PTag.start(PTAG_RELEASE_LAG_END);
                        executor.getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
                        return;
                    }
                    Log.d(TAG, "Already released!");
                    return;
                }
                return;
            }
            return;
        }
        if (CameraNotificationManager.SHUTTER_SPEED.equals(tag) && LGUtility.getInstance().isShutterSpeedUninitialized()) {
            try {
                String paintDuration = LGAppTopController.getInstance().getValue(LGAppTopController.DURATION_SELECTION);
                String shutterSpeed = LGAppTopController.convertToShutterSpeedFromPaintDuration(paintDuration);
                LGUtility.getInstance().setRecommanedSS(shutterSpeed);
            } catch (IController.NotSupportedException e) {
                AppLog.error(TAG, "NotSupportedException LGAppTopController.getValue()");
                e.printStackTrace();
            }
            LGUtility.getInstance().setShutterSpeedUninitialized(false);
        }
    }
}
