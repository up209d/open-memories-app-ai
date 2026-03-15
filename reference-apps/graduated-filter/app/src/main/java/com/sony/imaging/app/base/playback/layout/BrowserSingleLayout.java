package com.sony.imaging.app.base.playback.layout;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class BrowserSingleLayout extends SingleLayoutBase implements I4kPlaybackTrigger, ISinglePlaybackTrigger {
    private static final int DISP_MODE_NUM = 3;
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
    private static final String INH_ID_FILE_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final SparseIntArray LAYOUT_LIST_FINDER;
    private static final SparseIntArray LAYOUT_LIST_HDMI;
    private static final SparseIntArray LAYOUT_LIST_PANEL = new SparseIntArray(3);
    private static final String MSG_AUTOREVIEW_SK2_CAUTION = "AutoReview SK2 Caution";
    private static final String MSG_AUTO_REVIEW_TOUCH = "AutoReviewTouch ";
    private static final String MSG_DELAY_TRANSITION_TO_DELETE = "Delay to transition to delete";
    private static final String MSG_DIRECTION_LEFT = " Left";
    private static final String MSG_DIRECTION_RIGHT = " Right";
    private static final String MSG_TRANSITON_TO_DELETE = "Transition to delete by AutoReview SK2";
    private static final String MSG_TRANSITON_TO_ZOOM = "Into playzoom from BrowserSingle. Start";
    private static final String[] TAGS_OBSERVE_DISPLAY;
    protected I4kPlaybackTriggerFunction m4kPbTrigger;
    protected NotificationListener mDisplayChangedListener;
    protected ISinglePlaybackTriggerFunction mSinglePbTrigger;
    protected boolean mTransitionToDeletion = false;

    @Override // com.sony.imaging.app.base.playback.layout.I4kPlaybackTrigger
    public void set4kPlaybackFunction(I4kPlaybackTriggerFunction trigger) {
        this.m4kPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.ISinglePlaybackTrigger
    public void setFunction(ISinglePlaybackTriggerFunction keyFunction) {
        this.mSinglePbTrigger = keyFunction;
    }

    static {
        LAYOUT_LIST_PANEL.put(1, R.layout.pb_layout_cmn_singlepb_image_info);
        LAYOUT_LIST_PANEL.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        LAYOUT_LIST_PANEL.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        LAYOUT_LIST_FINDER = new SparseIntArray(3);
        LAYOUT_LIST_FINDER.put(1, R.layout.pb_layout_cmn_singlepb_image_info);
        LAYOUT_LIST_FINDER.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        LAYOUT_LIST_FINDER.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        LAYOUT_LIST_HDMI = new SparseIntArray(3);
        LAYOUT_LIST_HDMI.put(1, R.layout.pb_layout_cmn_singlepb_image_info);
        LAYOUT_LIST_HDMI.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        LAYOUT_LIST_HDMI.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON = new int[]{R.id.pb_parts_cmn_singlepb_metadata, R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_bottom_date, R.id.pb_parts_cmn_layout_play_autoframing};
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
        IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM = new int[]{R.id.pb_parts_cmn_singlepb_histo_metadata, R.id.pb_parts_cmn_singlepb_histo, R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_bottom_date, R.id.pb_parts_cmn_layout_play_autoframing};
        IMAGE_ERR_LAYOUT_RESOURCE_ID = new int[]{R.id.pb_parts_cmn_singlepb_image_err};
        TAGS_OBSERVE_DISPLAY = new String[]{DisplayModeObserver.TAG_DEVICE_CHANGE};
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (device == 0) {
            int id = LAYOUT_LIST_PANEL.get(displayMode);
            return id;
        }
        if (device == 1) {
            int id2 = LAYOUT_LIST_FINDER.get(displayMode);
            return id2;
        }
        if (device != 2) {
            return -1;
        }
        int id3 = LAYOUT_LIST_HDMI.get(displayMode);
        return id3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mTransitionToDeletion = false;
        super.onResume();
        if (this.data != null) {
            EventParcel userCodeAutoReview = (EventParcel) this.data.getParcelable(EventParcel.KEY_KEYCODE);
            EventParcel touchEventAutoReview = (EventParcel) this.data.getParcelable(EventParcel.KEY_TOUCH);
            if (touchEventAutoReview != null) {
                String action = touchEventAutoReview.mAction;
                if (action.equals(EventParcel.FLICK)) {
                    if (touchEventAutoReview.mVelocityX > 0.0f) {
                        Log.v(MSG_AUTO_REVIEW_TOUCH, LogHelper.getScratchBuilder(action).append(MSG_DIRECTION_RIGHT).toString());
                        previousContents();
                    } else if (touchEventAutoReview.mVelocityX < 0.0f) {
                        Log.v(MSG_AUTO_REVIEW_TOUCH, LogHelper.getScratchBuilder(action).append(MSG_DIRECTION_LEFT).toString());
                        nextContents();
                    }
                } else if (action.equals(EventParcel.SCROLL)) {
                    if (touchEventAutoReview.mDistanceX > 0.0f) {
                        Log.v(MSG_AUTO_REVIEW_TOUCH, LogHelper.getScratchBuilder(action).append(MSG_DIRECTION_RIGHT).toString());
                        previousContents();
                    } else if (touchEventAutoReview.mDistanceX < 0.0f) {
                        Log.v(MSG_AUTO_REVIEW_TOUCH, LogHelper.getScratchBuilder(action).append(MSG_DIRECTION_LEFT).toString());
                        nextContents();
                    }
                }
                this.data.putParcelable(EventParcel.KEY_TOUCH, null);
            } else if (userCodeAutoReview != null) {
                if (userCodeAutoReview.mKeyFunction != null) {
                    switch (userCodeAutoReview.mKeyFunction) {
                        case MainPrev:
                        case SubPrev:
                            previousContents();
                            break;
                        case MainNext:
                        case SubNext:
                            nextContents();
                            break;
                        case Delete:
                            ContentsManager mgr = ContentsManager.getInstance();
                            AvailableInfo.update();
                            if (AvailableInfo.isFactor(INH_ID_FILE_WRITING)) {
                                Log.i(this.TAG, MSG_AUTOREVIEW_SK2_CAUTION);
                                CautionUtilityClass.getInstance().requestTrigger(1410);
                                break;
                            } else if (mgr.isInitialQueryDone()) {
                                Log.i(this.TAG, MSG_TRANSITON_TO_DELETE);
                                transitionDeleteThis();
                                break;
                            } else {
                                Log.i(this.TAG, MSG_DELAY_TRANSITION_TO_DELETE);
                                this.mTransitionToDeletion = true;
                                break;
                            }
                    }
                } else {
                    switch (userCodeAutoReview.mKeyEvent) {
                        case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                        case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                        case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                        case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                            previousContents();
                            break;
                        case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                        case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                        case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                        case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                            nextContents();
                            break;
                        case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                        case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                            ContentsManager mgr2 = ContentsManager.getInstance();
                            AvailableInfo.update();
                            if (AvailableInfo.isFactor(INH_ID_FILE_WRITING)) {
                                Log.i(this.TAG, MSG_AUTOREVIEW_SK2_CAUTION);
                                CautionUtilityClass.getInstance().requestTrigger(1410);
                                break;
                            } else if (mgr2.isInitialQueryDone()) {
                                Log.i(this.TAG, MSG_TRANSITON_TO_DELETE);
                                transitionDeleteThis();
                                break;
                            } else {
                                Log.i(this.TAG, MSG_DELAY_TRANSITION_TO_DELETE);
                                this.mTransitionToDeletion = true;
                                break;
                            }
                    }
                }
                this.data.putParcelable(EventParcel.KEY_KEYCODE, null);
            }
        }
        setFooterGuideData(getFooterGuideDataResId());
        registerDisplayChangedListener();
    }

    protected FooterGuideDataResId getFooterGuideDataResId() {
        if (Environment.getVersionOfHW() == 1) {
            FooterGuideDataResId resId = new FooterGuideDataResId(getActivity(), 0, android.R.string.heavy_weight_notification_detail);
            return resId;
        }
        if (!DisplayModeObserver.getInstance().is4kDeviceAvailable()) {
            return null;
        }
        int status = DisplayModeObserver.getInstance().get4kOutputStatus();
        if (1 != status) {
            return null;
        }
        FooterGuideDataResId resId2 = new FooterGuideDataResId(getActivity(), android.R.string.kg_too_many_failed_password_attempts_dialog_message);
        return resId2;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        int status = DisplayModeObserver.getInstance().get4kOutputStatus();
        if (6 == status || 5 == status) {
            this.mMuteOptimizedImageViewOnPause = false;
        }
        unregisterDisplayChangedListener();
        super.onPause();
        this.mTransitionToDeletion = false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        if (this.mTransitionToDeletion) {
            this.mTransitionToDeletion = false;
            Log.i(this.TAG, MSG_TRANSITON_TO_DELETE);
            transitionDeleteThis();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 14;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        if (this.mTransitionToDeletion) {
            return -1;
        }
        thinZoomKey();
        return transitionZoom(null) ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return Environment.getVersionOfHW() == 1 ? pushedPbZoomFuncPlus() : super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        if (this.mTransitionToDeletion) {
            return -1;
        }
        ContentsManager mgr = ContentsManager.getInstance();
        return (mgr.isInitialQueryDone() && transitionDeleteThis()) ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (((BaseApp) getActivity()).is4kPlaybackSupported() && DisplayModeObserver.getInstance().is4kDeviceAvailable()) {
            openStart4kDialog();
            return 1;
        }
        if (((BaseApp) getActivity()).canVolumeAdjustment() && AudioVolumeController.getInstance().getSupportedRange(AudioVolumeController.TAG_AUDIO_VOLUME) != null && this.mSinglePbTrigger != null) {
            return this.mSinglePbTrigger.openVolumeAdjustment() ? 1 : -1;
        }
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
    public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
        if (this.mTransitionToDeletion) {
            return false;
        }
        PTag.start(MSG_TRANSITON_TO_ZOOM);
        if (isReleasedInside) {
            int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
            if (displayMode == 5) {
                transitionZoom(null);
            } else {
                OptimizedImageView imgView = getOptimizedImageView();
                Rect viewRect = new Rect(0, 0, imgView.getWidth(), imgView.getHeight());
                EventParcel touch = new EventParcel(null, e, null, isReleasedInside, 0.0f, 0.0f, 0.0f, 0.0f, viewRect);
                transitionZoom(touch);
            }
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_OPTION_ON);
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean previousContents() {
        if (this.mTransitionToDeletion) {
            return false;
        }
        return super.previousContents();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean nextContents() {
        if (this.mTransitionToDeletion) {
            return false;
        }
        return super.nextContents();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageOkLayoutResourceId() {
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        switch (displayMode) {
            case 1:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
            case 2:
            case 4:
            default:
                return null;
            case 3:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
            case 5:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageErrLayoutResourceId() {
        return IMAGE_ERR_LAYOUT_RESOURCE_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DisplayChangedListener implements NotificationListener {
        protected DisplayChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return BrowserSingleLayout.TAGS_OBSERVE_DISPLAY;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            BrowserSingleLayout.this.setFooterGuideData(BrowserSingleLayout.this.getFooterGuideDataResId());
        }
    }

    protected void registerDisplayChangedListener() {
        if (((BaseApp) getActivity()).is4kPlaybackSupported()) {
            this.mDisplayChangedListener = new DisplayChangedListener();
            DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayChangedListener);
        }
    }

    protected void unregisterDisplayChangedListener() {
        if (this.mDisplayChangedListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayChangedListener);
            this.mDisplayChangedListener = null;
        }
    }

    protected void openStart4kDialog() {
        if (this.m4kPbTrigger != null) {
            this.m4kPbTrigger.open4kStartingDialog();
        }
    }
}
