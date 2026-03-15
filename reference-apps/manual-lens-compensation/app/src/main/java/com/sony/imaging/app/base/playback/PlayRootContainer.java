package com.sony.imaging.app.base.playback;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.PseudoViewMode;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.fw.StateHandle;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.HDMIInfoWrapper;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.avio.Cec;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class PlayRootContainer extends PlayStateBase {
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String BUNDLE_KEY_CODE = "keyCode";
    public static final String BUNDLE_TOUCH = "BUNDLE_TOUCH";
    public static final String BUNDLE_TRANSITION_FROM = "from";
    private static final String[] DIAPLAY_MODE_TAGS = {DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE};
    public static final String ID_BROWSER = "ID_BROWSER";
    public static final String ID_CHANGING_OUTPUT_MODE = "ID_CHANGING_OUTPUT_MODE";
    public static final String ID_DELETE_SINGLE = "ID_DELETE_SINGLE";
    public static final String ID_PLAYZOOM = "ID_PLAYZOOM";
    public static final String ID_PSEUDOREC = "ID_PSEUDOREC";
    public static final String ID_PSEUDOREC_ZOOM = "ID_PSEUDOREC_ZOOM";
    public static final String ID_TRANSITING_TO_SHOOTING = "ID_TRANSITING_TO_SHOOTING";
    private static final String MSG_BUBNDLE_EXIST = "data is exist";
    private static final String MSG_INIT_PSEUDO_VIEW = "initializeViewMode(PSEUDO)";
    private static final String MSG_INIT_STILL_VIEW = "initializeViewMode(STILL_FOLDER)";
    private static final String MSG_POST_NOTIFICATION = " is received.";
    private static final String MSG_PREFIX_SET_ROOT = "setRoot ";
    private static final String MSG_TRANSITION_SHOOTING = "transition to Shooting";
    private static final String MSG_WARNING = "getContainer should not be called for PlayRootConteiner";
    public static final String PROP_ID_APP_ROOT = "PlayRoot";
    public static final String PROP_ID_PB_MODE = "PB_MODE";
    private static final int UI_PB_TYPE_SUPPORTED_VERSION = 2;
    protected NotificationListener mDisplayModeListener;
    protected NotificationListener mMediaListener;
    protected MediaNotificationManager mMediaNotifier;
    private StateHandle mHandle = null;
    private PB_MODE mPlaybackMode = PB_MODE.getInitialiMode();
    private Bundle mBundleAutoReview = null;
    protected String mWaitingApp = null;
    protected Bundle mWaitingData = null;

    public void changeApp(String appName) {
        changeApp(appName, null);
    }

    public void changeApp(String appName, Bundle bundle) {
        State state = getState(appName);
        if (PlaySubApp.class.isInstance(state) && !((PlaySubApp) state).is4kPbAvailable() && 1 != DisplayModeObserver.getInstance().get4kOutputStatus()) {
            DisplayModeObserver.getInstance().stop4kPlayback();
            this.mWaitingApp = appName;
            this.mWaitingData = bundle;
            appName = ID_CHANGING_OUTPUT_MODE;
        }
        if (this.mHandle == null) {
            this.mHandle = addChildState(appName, bundle);
        } else {
            replaceChildState(this.mHandle, appName, bundle);
        }
    }

    public void changeToShooting() {
        PTag.start(MSG_TRANSITION_SHOOTING);
        changeApp(ID_TRANSITING_TO_SHOOTING);
    }

    public void changeToShooting(int keyCode) {
        if (((BaseApp) getActivity()).isShootingSupported()) {
            PTag.start(MSG_TRANSITION_SHOOTING);
            Bundle b = new Bundle();
            b.putInt("keyCode", keyCode);
            b.putString(BUNDLE_TRANSITION_FROM, BaseApp.APP_PLAY);
            changeApp(ID_TRANSITING_TO_SHOOTING, b);
        }
    }

    public void openRecDisabledCaution() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
    }

    /* loaded from: classes.dex */
    public enum PB_MODE {
        SINGLE,
        INDEX;

        public static PB_MODE getInitialiMode() {
            if (2 > Environment.getVersionPfAPI()) {
                return SINGLE;
            }
            if (Environment.getVersionPfAPI() >= 2) {
                int type = ScalarProperties.getInt("ui.playback.type");
                return 1 == type ? INDEX : SINGLE;
            }
            return SINGLE;
        }
    }

    @Override // com.sony.imaging.app.fw.State
    public Object getData(String name) {
        return PROP_ID_PB_MODE.equals(name) ? this.mPlaybackMode : super.getData(name);
    }

    @Override // com.sony.imaging.app.fw.State
    public boolean setData(String name, Object o) {
        if (!PROP_ID_PB_MODE.equals(name)) {
            return super.setData(name, o);
        }
        this.mPlaybackMode = (PB_MODE) o;
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase
    protected PlaySubApp getContainer() {
        Log.w(getLogTag(), MSG_WARNING);
        return null;
    }

    @Override // com.sony.imaging.app.fw.ContainerState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getLogTag(), LogHelper.getScratchBuilder(MSG_PREFIX_SET_ROOT).append(setData(PROP_ID_APP_ROOT, this)).toString());
        ContentsManager mgr = ContentsManager.getInstance();
        mgr.initialize(getActivity());
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = getMediaMountListener();
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
        requestOneTouchPlay();
        if (this.data != null) {
            Log.d(getLogTag(), MSG_BUBNDLE_EXIST);
            this.mBundleAutoReview = (Bundle) this.data.getParcelable(AutoReviewState.STATE_NAME);
        }
        DisplayModeObserver.getInstance().showDialogOn4kOutputConnected(isDialog4kOutputConnectedShown());
        if (this.mDisplayModeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
        }
        this.mDisplayModeListener = getDisplayObserverListener();
        DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayModeListener);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mDisplayModeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
            this.mDisplayModeListener = null;
        }
        if (1 != DisplayModeObserver.getInstance().get4kOutputStatus()) {
            DisplayModeObserver.getInstance().stop4kPlayback();
        }
        DisplayModeObserver.getInstance().showDialogOn4kOutputConnected(false);
        this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        this.mMediaListener = null;
        this.mMediaNotifier = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        ContentsManager.getInstance().terminate();
        removeData(PROP_ID_APP_ROOT);
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase
    protected int getResumeKeyBeepPattern() {
        return 16;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 9;
    }

    @Override // com.sony.imaging.app.fw.State
    public HDMIInfoWrapper.INFO_TYPE getHDMIInfoType() {
        return HDMIInfoWrapper.INFO_TYPE.INFO_ON;
    }

    protected void requestOneTouchPlay() {
        Cec.issueMessage(1);
    }

    protected void initializeViewMode() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!isMemoryCardAvailable()) {
            Log.i(getLogTag(), MSG_INIT_PSEUDO_VIEW);
            mgr.setViewMode(PseudoViewMode.class);
        } else {
            Log.i(getLogTag(), MSG_INIT_STILL_VIEW);
            mgr.setViewMode(StillFolderViewMode.class);
        }
    }

    public boolean isMemoryCardAvailable() {
        int state = this.mMediaNotifier.getMediaState();
        return 2 == state;
    }

    public String getStartFunction() {
        if (this.mBundleAutoReview != null) {
            EventParcel userCodeAutoReview = (EventParcel) this.mBundleAutoReview.getParcelable(EventParcel.KEY_KEYCODE);
            EventParcel touchEventAutoReview = (EventParcel) this.mBundleAutoReview.getParcelable(EventParcel.KEY_TOUCH);
            if (touchEventAutoReview != null) {
                String action = touchEventAutoReview.mAction;
                if (action == null) {
                    return null;
                }
                if (isMemoryCardAvailable()) {
                    if (action.equals(EventParcel.TOUCH_UP)) {
                        return ID_PLAYZOOM;
                    }
                    getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.getInitialiMode());
                    return ID_BROWSER;
                }
                if (action.equals(EventParcel.TOUCH_UP)) {
                    return ID_PSEUDOREC_ZOOM;
                }
                return ID_PSEUDOREC;
            }
            if (userCodeAutoReview == null) {
                return null;
            }
            if (isMemoryCardAvailable()) {
                if (userCodeAutoReview.mKeyFunction != null) {
                    switch (userCodeAutoReview.mKeyFunction) {
                        case DispChange:
                            DisplayModeObserver.getInstance().toggleDisplayMode(1);
                            return ID_BROWSER;
                        case PbZoomPlus:
                            return ID_PLAYZOOM;
                        case PbZoomMinus:
                            getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.INDEX);
                            return ID_BROWSER;
                        case PlayIndex:
                            getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.INDEX);
                            return ID_BROWSER;
                        default:
                            getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.getInitialiMode());
                            return ID_BROWSER;
                    }
                }
                switch (userCodeAutoReview.mKeyEvent) {
                    case 103:
                        DisplayModeObserver.getInstance().toggleDisplayMode(1);
                        return ID_BROWSER;
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                        getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.INDEX);
                        return ID_BROWSER;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        return ID_PLAYZOOM;
                    default:
                        getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.getInitialiMode());
                        return ID_BROWSER;
                }
            }
            if (userCodeAutoReview.mKeyFunction != null) {
                switch (userCodeAutoReview.mKeyFunction) {
                    case DispChange:
                        DisplayModeObserver.getInstance().toggleDisplayMode(1);
                        return ID_PSEUDOREC;
                    case PbZoomPlus:
                        return ID_PSEUDOREC_ZOOM;
                    default:
                        return ID_PSEUDOREC;
                }
            }
            switch (userCodeAutoReview.mKeyEvent) {
                case 103:
                    DisplayModeObserver.getInstance().toggleDisplayMode(1);
                    return ID_PSEUDOREC;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    return ID_PSEUDOREC_ZOOM;
                default:
                    return ID_PSEUDOREC;
            }
        }
        EventParcel userCodeAutoReview2 = null;
        if (this.data != null) {
            Log.d(getLogTag(), MSG_BUBNDLE_EXIST);
            Bundle bundleEE = (Bundle) this.data.getParcelable(S1OffEEState.STATE_NAME);
            if (bundleEE != null) {
                this.data.remove(S1OffEEState.STATE_NAME);
                userCodeAutoReview2 = (EventParcel) bundleEE.getParcelable(EventParcel.KEY_KEYCODE);
            }
        }
        if (isMemoryCardAvailable()) {
            if (userCodeAutoReview2 != null && CustomizableFunction.PlayIndex.equals(userCodeAutoReview2.mKeyFunction)) {
                getRootContainer().setData(PROP_ID_PB_MODE, PB_MODE.INDEX);
            }
            return ID_BROWSER;
        }
        return ID_PSEUDOREC;
    }

    protected NotificationListener getMediaMountListener() {
        return new MediaMountEventListener();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MediaMountEventListener implements NotificationListener {
        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.i(PlayRootContainer.this.getLogTag(), LogHelper.getScratchBuilder(tag).append(PlayRootContainer.MSG_POST_NOTIFICATION).toString());
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                int state = PlayRootContainer.this.mMediaNotifier.getMediaState();
                int type = MediaNotificationManager.getInstance().getInsertedMediaType();
                if (2 == type || 1 == type) {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getExternalMediaIds());
                } else {
                    CautionUtilityClass.getInstance().setMedia(AvindexStore.getVirtualMediaIds());
                }
                if (state == 0) {
                    PlayRootContainer.this.onMediaChanged();
                    return;
                }
                if (state == 1) {
                    CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_PSEUDOREC_NOIMAGE);
                } else if (state == 3 || state == 4) {
                    PlayRootContainer.this.onMediaError();
                } else {
                    PlayRootContainer.this.onMediaChanged();
                }
            }
        }
    }

    public void onMediaChanged() {
        initializeViewMode();
        changeApp(getStartFunction(), this.mBundleAutoReview);
        this.mBundleAutoReview = null;
    }

    public void onMediaError() {
        changeToShooting();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DisplayModeListener implements NotificationListener {
        protected DisplayModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return PlayRootContainer.DIAPLAY_MODE_TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE.equals(tag) && 4 == RunStatus.getStatus()) {
                int status = DisplayModeObserver.getInstance().get4kOutputStatus();
                if (6 == status || 5 == status) {
                    PlayRootContainer.this.changeApp(PlayRootContainer.ID_CHANGING_OUTPUT_MODE);
                    return;
                }
                if (PlayRootContainer.this.mWaitingApp != null) {
                    PlayRootContainer.this.changeApp(PlayRootContainer.this.mWaitingApp, PlayRootContainer.this.mWaitingData);
                } else {
                    PlayRootContainer.this.changeApp(PlayRootContainer.this.getStartFunction());
                }
                PlayRootContainer.this.mWaitingApp = null;
                PlayRootContainer.this.mWaitingData = null;
            }
        }
    }

    protected NotificationListener getDisplayObserverListener() {
        return new DisplayModeListener();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (!CustomizableFunction.Unchanged.equals(func)) {
            return 0;
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
            case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
            case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
            case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
            case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                changeToShooting(code);
                return 1;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                if (Environment.isMovieAPISupported() && ((BaseApp) getActivity()).isMovieModeSupported()) {
                    changeToShooting(code);
                } else {
                    openRecDisabledCaution();
                }
                return 1;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                return -1;
            default:
                int result = super.onConvertedKeyDown(event, func);
                return result;
        }
    }

    protected boolean isDialog4kOutputConnectedShown() {
        return ((BaseApp) getActivity()).is4kPlaybackSupported();
    }
}
