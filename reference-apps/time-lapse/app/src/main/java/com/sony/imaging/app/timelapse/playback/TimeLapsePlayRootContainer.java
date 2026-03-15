package com.sony.imaging.app.timelapse.playback;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.PseudoViewMode;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseOperations;
import com.sony.imaging.app.timelapse.databaseutil.TimeLapseBO;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TimeLapsePlayRootContainer extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    public static final String ID_DELETE_MULTIPLE_LAYOUT = "ID_DELETE_MULTIPLE_LAYOUT";
    private static final int INTEGER_CONSTANT_ZERO = 0;
    private static final String TESTSHOT = "TESTSHOT";
    public static boolean isTimeLapseListView = false;
    public static boolean isTimeLapseAngleShift = false;
    public static boolean isLayoutOpened = false;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public String getStartFunction() {
        isTimeLapseListView = true;
        if (this.data != null && this.data.getBoolean(TESTSHOT)) {
            AppLog.info(this.TAG, "getStartFunction: TESTSHOT");
            MediaNotificationManager.getInstance().hold(true);
            PlayBackController.getInstance().setBufferPBState((byte) 0);
            isTimeLapseListView = false;
            isTimeLapseAngleShift = false;
            return PlayRootContainer.ID_PSEUDOREC;
        }
        if (isMemoryCardAvailable()) {
            AppLog.info(this.TAG, "getStartFunction: MemoryCardAvailable");
            Bundle bundleEE = null;
            EventParcel userCode = null;
            if (this.data != null) {
                bundleEE = (Bundle) this.data.getParcelable(S1OffEEState.STATE_NAME);
            }
            if (bundleEE != null) {
                this.data.remove(S1OffEEState.STATE_NAME);
                userCode = (EventParcel) bundleEE.getParcelable(EventParcel.KEY_KEYCODE);
            }
            DataBaseOperations.getInstance().refreshDatabase();
            CameraNotificationManager.getInstance().requestNotify(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE);
            if (userCode != null && CustomizableFunction.PlayIndex.equals(userCode.mKeyFunction)) {
                getRootContainer().setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.INDEX);
            }
            isLayoutOpened = true;
            return PlayRootContainer.ID_BROWSER;
        }
        isTimeLapseListView = false;
        isTimeLapseAngleShift = false;
        isLayoutOpened = false;
        return PlayRootContainer.ID_PSEUDOREC;
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected NotificationListener getMediaMountListener() {
        if (this.data == null || !this.data.getBoolean(TESTSHOT)) {
            return new MediaMountEventListener();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return new LocalMediaMountEventListener();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.info(this.TAG, "onResume");
        isLayoutOpened = false;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.info(this.TAG, "onPause");
        isLayoutOpened = false;
        MediaNotificationManager.getInstance().hold(false);
        if (this.data != null) {
            this.data.remove(TESTSHOT);
        }
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.PLAY_ROOT_CONTAINER_REMOVED);
        PlayBackController.getInstance().setDefaultPBState();
        System.gc();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (event.getScanCode() == 620) {
            TimeLapseConstants.slastStateExposureCompansasion = 0;
            TimeLapseConstants.isEVDial = true;
        } else if (event.getScanCode() == 516) {
            TLCommonUtil.getInstance().setS2_ONFromPlayBack(true);
        }
        return super.onConvertedKeyDown(event, func);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ArrayList<TimeLapseBO> tlBOList = DataBaseOperations.getInstance().getTimeLapseBOList();
        return (tlBOList == null || tlBOList.size() > 0) ? ApoWrapper.APO_TYPE.NONE : ApoWrapper.APO_TYPE.SPECIAL;
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected void initializeViewMode() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!isMemoryCardAvailable() || (this.data != null && this.data.getBoolean(TESTSHOT))) {
            mgr.setViewMode(PseudoViewMode.class);
        } else {
            mgr.setViewMode(StillFolderViewMode.class);
        }
    }

    /* loaded from: classes.dex */
    class LocalMediaMountEventListener implements NotificationListener {
        LocalMediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                int state = TimeLapsePlayRootContainer.this.mMediaNotifier.getMediaState();
                CautionUtilityClass.getInstance().executeTerminate();
                int type = MediaNotificationManager.getInstance().getInsertedMediaType();
                if (2 == type || 1 == type) {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getExternalMediaIds());
                } else {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getVirtualMediaIds());
                }
                if (state == 0) {
                    TimeLapsePlayRootContainer.this.onMediaChanged();
                    return;
                }
                if (state == 1) {
                    CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_PSEUDOREC_NOIMAGE);
                } else if (state == 3 || state == 4) {
                    TimeLapsePlayRootContainer.this.onMediaError();
                } else {
                    TimeLapsePlayRootContainer.this.onMediaChanged();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class MediaMountEventListener implements NotificationListener {
        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                int state = TimeLapsePlayRootContainer.this.mMediaNotifier.getMediaState();
                int type = MediaNotificationManager.getInstance().getInsertedMediaType();
                if (2 == type || 1 == type) {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getExternalMediaIds());
                } else {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getVirtualMediaIds());
                }
                if (state == 0) {
                    TimeLapsePlayRootContainer.this.onMediaChanged();
                    return;
                }
                if (state == 1) {
                    CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_PSEUDOREC_NOIMAGE);
                    return;
                }
                if (state == 3 || state == 4) {
                    TimeLapsePlayRootContainer.this.onMediaError();
                } else if (!TimeLapsePlayRootContainer.isLayoutOpened) {
                    TimeLapsePlayRootContainer.this.onMediaChanged();
                }
            }
        }
    }
}
