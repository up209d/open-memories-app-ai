package com.sony.imaging.app.doubleexposure.shooting.state;

import android.database.Cursor;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.doubleexposure.caution.DoubleExposureInfo;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class DoubleExposureShootingState extends ShootingState {
    private final String TAG = AppLog.getClassName();
    private DESA mDESA = null;
    private MediaMountNotificationListener mMediaMountNotificationListener = null;
    private DoubleExposureUtil mDoubleExposureUtil = null;
    private int mCautionID = 0;

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        if (DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus()) {
            this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        }
        if (DoubleExposureConstant.SECOND_SHOOTING.equalsIgnoreCase(this.mDoubleExposureUtil.getCurrentShootingScreen())) {
            String currentMenuSelection = this.mDoubleExposureUtil.getCurrentMenuSelectionScreen();
            if (currentMenuSelection == null && !isFirstImageExist()) {
                this.mCautionID = DoubleExposureInfo.CAUTION_ID_DLAPP_FIRST_IMAGE_NOT_FOUND;
                displayCaution(this.mCautionID);
            } else {
                this.mDESA = DESA.getInstance();
                this.mDESA.setPackageName(AppContext.getAppContext().getPackageName());
                this.mDESA.intialize();
                this.mDESA.setSFRMode(1);
                this.mDESA.startLiveViewEffect();
            }
        }
        super.onResume();
        if (this.mDoubleExposureUtil.IsTurnedEVDialInPlayback()) {
            this.mDoubleExposureUtil.updateExposureCompensation();
            this.mDoubleExposureUtil.setTurnedEVDialInPlayback(false);
        }
        if (this.mMediaMountNotificationListener == null) {
            this.mMediaMountNotificationListener = new MediaMountNotificationListener();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mMediaMountNotificationListener);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mCautionID != 0) {
            CautionUtilityClass.getInstance().disapperTrigger(this.mCautionID);
            CautionUtilityClass.getInstance().setDispatchKeyEvent(this.mCautionID, null);
        } else {
            int cautionID = 0;
            try {
                cautionID = CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId;
            } catch (IndexOutOfBoundsException e) {
                AppLog.error(this.TAG, "Error is :" + e.toString());
            } catch (NullPointerException e2) {
                AppLog.error(this.TAG, "Error is :" + e2.toString());
            }
            if (131220 == cautionID) {
                CautionUtilityClass.getInstance().disapperTrigger(cautionID);
            }
        }
        MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaMountNotificationListener);
        this.mDoubleExposureUtil.setCameraSettingValues();
        if (this.mDESA != null) {
            this.mDESA.terminate();
            this.mDESA = null;
        }
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCautionID = 0;
        this.mDoubleExposureUtil = null;
        this.mMediaMountNotificationListener = null;
        if (this.mDESA != null) {
            this.mDESA.terminate();
            this.mDESA = null;
        }
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void displayCaution(final int cautionId) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.doubleexposure.shooting.state.DoubleExposureShootingState.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int retValue;
                AppLog.enter(DoubleExposureShootingState.this.TAG, AppLog.getMethodName());
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        if (131228 == cautionId) {
                            DoubleExposureShootingState.this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
                            DoubleExposureShootingState.this.getHandler().sendEmptyMessage(DoubleExposureConstant.SEND_MESSAGE_TO_EE_STATE);
                        }
                        retValue = 1;
                        break;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        if (131228 == cautionId) {
                            DoubleExposureShootingState.this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
                            DoubleExposureShootingState.this.getHandler().sendEmptyMessage(DoubleExposureConstant.SEND_MESSAGE_TO_EE_STATE);
                            retValue = 1;
                            break;
                        } else {
                            retValue = 0;
                            break;
                        }
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        if (131228 == cautionId) {
                            DoubleExposureShootingState.this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
                            DoubleExposureShootingState.this.getHandler().sendEmptyMessage(DoubleExposureConstant.SEND_MESSAGE_TO_EE_STATE);
                        }
                        retValue = 0;
                        break;
                    default:
                        retValue = -1;
                        break;
                }
                AppLog.exit(DoubleExposureShootingState.this.TAG, AppLog.getMethodName());
                return retValue;
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                int retValue;
                AppLog.enter(DoubleExposureShootingState.this.TAG, AppLog.getMethodName());
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                        retValue = 0;
                        break;
                    default:
                        retValue = -1;
                        break;
                }
                AppLog.exit(DoubleExposureShootingState.this.TAG, AppLog.getMethodName());
                return retValue;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private boolean isFirstImageExist() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bRetValue = false;
        int firstImageFileNumber = this.mDoubleExposureUtil.getFirstImageFileNumber();
        int firstImageFolderNumber = this.mDoubleExposureUtil.getFirstImageFolderNumber();
        AppLog.info(this.TAG, "First Image File Number: = " + firstImageFileNumber);
        AppLog.info(this.TAG, "First Image Folder Number: = " + firstImageFolderNumber);
        String[] projection = {"_id", "dcf_file_number", "dcf_folder_number", "exist_jpeg"};
        String selection = "dcf_file_number=" + firstImageFileNumber + " AND dcf_folder_number" + MovieFormatController.Settings.EQUAL + firstImageFolderNumber + " AND exist_jpeg=1";
        AppLog.info(this.TAG, "selection : " + selection);
        Cursor cursor = AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]), projection, selection, null, null);
        if (cursor != null && 1 == cursor.getCount()) {
            bRetValue = true;
            cursor.close();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bRetValue;
    }

    /* loaded from: classes.dex */
    private class MediaMountNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaMountNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.enter(DoubleExposureShootingState.this.TAG, AppLog.getMethodName());
            int state = MediaNotificationManager.getInstance().getMediaState();
            if (DoubleExposureConstant.SECOND_SHOOTING.equalsIgnoreCase(DoubleExposureShootingState.this.mDoubleExposureUtil.getCurrentShootingScreen()) && state == 0) {
                ExecutorCreator creator = ExecutorCreator.getInstance();
                String currentMedia = creator.getSequence().getRecordingMedia();
                String[] ids = ExecutorCreator.getMediaId();
                if (currentMedia.equals(ids[0])) {
                    DoubleExposureShootingState.this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
                    if (DoubleExposureShootingState.this.mDESA != null) {
                        DoubleExposureShootingState.this.mDESA.terminate();
                        DoubleExposureShootingState.this.mDESA = null;
                    }
                    DoubleExposureShootingState.this.getHandler().sendEmptyMessage(DoubleExposureConstant.SEND_MESSAGE_TO_EE_STATE);
                }
            }
            AppLog.exit(DoubleExposureShootingState.this.TAG, AppLog.getMethodName());
        }
    }
}
