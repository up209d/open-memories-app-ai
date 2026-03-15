package com.sony.imaging.app.startrails.playback;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.PseudoViewMode;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.startrails.common.STDisplayModeObserver;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.database.StarTrailsBO;
import com.sony.imaging.app.startrails.playback.controller.PlayBackController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class STPlayRootContainer extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    private static final String PRE_TAKE_PICTURE = "PRETAKE_SHOOTING";
    public static final String ID_DELETE_MULTIPLE_LAYOUT = null;
    public static boolean isSTListView = false;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public String getStartFunction() {
        String ret;
        STDisplayModeObserver.getInstance().setStartTrailsPlayBackDisplayMode();
        STUtility.getInstance().setMenuBoot(false);
        AppLog.enter(this.TAG, AppLog.getMethodName());
        isSTListView = true;
        if (this.data != null && this.data.getBoolean(PRE_TAKE_PICTURE)) {
            MediaNotificationManager.getInstance().hold(true);
            PlayBackController.getInstance().setBufferPBState((byte) 0);
            isSTListView = false;
            ret = PlayRootContainer.ID_PSEUDOREC;
        } else if (isMemoryCardAvailable()) {
            DataBaseOperations.getInstance().refreshDatabase();
            CameraNotificationManager.getInstance().requestNotify(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE);
            ret = PlayRootContainer.ID_BROWSER;
        } else {
            PlayBackController.getInstance().setBufferPBState((byte) 1);
            isSTListView = false;
            ret = PlayRootContainer.ID_PSEUDOREC;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public NotificationListener getMediaMountListener() {
        if (this.data == null || !this.data.getBoolean(PRE_TAKE_PICTURE)) {
            return super.getMediaMountListener();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return new LocalMediaMountEventListener();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        MediaNotificationManager.getInstance().hold(false);
        STUtility.getInstance().setFromPlayback(true);
        STUtility.getInstance().setPlayBackKeyPressed(false);
        if (this.data != null) {
            this.data.remove(PRE_TAKE_PICTURE);
        }
        AppLog.enter(this.TAG, AppLog.getMethodName());
        CameraNotificationManager.getInstance().requestNotify(STConstants.PLAY_ROOT_CONTAINER_REMOVED);
        PlayBackController.getInstance().setDefaultPBState();
        STUtility.getInstance().setIsLauncherBoot(false);
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:6:0x0019. Please report as an issue. */
    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (516 == code) {
            STUtility.getInstance().setFromPlayback(true);
        }
        if (!CustomizableFunction.Unchanged.equals(func)) {
            return 0;
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                changeToShooting(AppRoot.USER_KEYCODE.DIAL1_LEFT);
                return 1;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                STUtility.getInstance().setEVDialRotated(true);
            default:
                int result = super.onConvertedKeyDown(event, func);
                return result;
        }
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ArrayList<StarTrailsBO> tlBOList = DataBaseOperations.getInstance().getStartrailsBOList();
        return (tlBOList == null || tlBOList.size() > 0) ? ApoWrapper.APO_TYPE.NONE : ApoWrapper.APO_TYPE.SPECIAL;
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected void initializeViewMode() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!isMemoryCardAvailable() || (this.data != null && this.data.getBoolean(PRE_TAKE_PICTURE))) {
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
                int state = STPlayRootContainer.this.mMediaNotifier.getMediaState();
                CautionUtilityClass.getInstance().executeTerminate();
                int type = MediaNotificationManager.getInstance().getInsertedMediaType();
                if (2 == type || 1 == type) {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getExternalMediaIds());
                } else {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getVirtualMediaIds());
                }
                if (state == 0) {
                    STPlayRootContainer.this.onMediaChanged();
                    return;
                }
                if (state == 1) {
                    CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_PSEUDOREC_NOIMAGE);
                } else if (state == 3 || state == 4) {
                    STPlayRootContainer.this.onMediaError();
                } else {
                    STPlayRootContainer.this.onMediaChanged();
                }
            }
        }
    }
}
