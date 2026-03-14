package com.sony.imaging.app.portraitbeauty.playback;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.ImageEditor;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.portraitbeauty.playback.layout.IEachPlaybackTriggerFunction;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class PortraitBeautyReview extends BrowserSingle {
    private static int AUTOREVIEW_TIME = PortraitBeautyConstants.FIVE_SECOND_MILLIS;
    public static final String ID_REVIEW = "ID_REVIEW";
    public static final String ID_REVIEW_PB = "ID_REVIEW_STATE";
    public static final String ID_REVIEW_PLAYBACK_LAYOUT = "REVIEW_PB";
    IEachPlaybackTriggerFunction mEachPbTrigger;
    private int mFaceNum;
    private Handler mHandler = null;
    Runnable mTimerRunnable = null;
    FinishListener mListener = null;
    private int mcautionID = 0;
    private ImageAnalyzer.AnalyzedFace[] mFaces = null;
    private String[] TAGS = {PortraitBeautyConstants.FINISHTAG};

    public void setEachPbTrigger(IEachPlaybackTriggerFunction trigger) {
        this.mEachPbTrigger = trigger;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        if (this.mListener == null) {
            this.mListener = new FinishListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
            return ID_REVIEW_PLAYBACK_LAYOUT;
        }
        return ID_REVIEW_PLAYBACK_LAYOUT;
    }

    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.contents.ContentsManager.QueryCallback
    public void onCallback(boolean result) {
        int delayTimeMilli = AUTOREVIEW_TIME;
        if (this.mHandler == null) {
            this.mHandler = getHandler();
        }
        this.mTimerRunnable = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyReview.1
            @Override // java.lang.Runnable
            public void run() {
                PortraitBeautyReview.this.transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
            }
        };
        if (this.mHandler != null) {
            this.mHandler.postDelayed(this.mTimerRunnable, delayTimeMilli);
        }
    }

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        }
        deInitialize();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        if (this.mcautionID != 0) {
            CautionUtilityClass.getInstance().disapperTrigger(this.mcautionID);
            this.mcautionID = 0;
        }
        this.mFaces = null;
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (!(DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus())) {
            if (!MediaNotificationManager.getInstance().isMounted()) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_CARD_ON_PLAYBACK;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                return 1;
            }
            if (!PortraitBeautyUtil.getInstance().isSpaceAvailableInMemoryCard()) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                return 1;
            }
            if (isUnsupportedCaution()) {
                return 1;
            }
            this.mcautionID = 0;
            deInitialize();
            while (!AvindexStore.waitLoadMediaComplete(AvindexStore.getExternalMediaIds()[0])) {
                AppLog.info(this.TAG, "Laxmikant Load media is not complete");
            }
            String[] mId = AvindexStore.getExternalMediaIds();
            AvindexStore.Images.waitAndUpdateDatabase(ContentsManager.getInstance().getContentResolver(), mId[0]);
            setNextState(PortraitBeautyCatchLightState.ID_CATCH_LIGHT_PB, null);
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        deInitialize();
        return 0;
    }

    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new FinishListener();
        }
        return this.mListener;
    }

    /* loaded from: classes.dex */
    protected class FinishListener implements NotificationListener {
        protected FinishListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (PortraitBeautyConstants.FINISHTAG.equalsIgnoreCase(tag)) {
                PortraitBeautyReview.this.deInitialize();
                PortraitBeautyReview.this.transitionSinglePb();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return PortraitBeautyReview.this.TAGS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deInitialize() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mTimerRunnable);
        }
        this.mHandler = null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onFocusModeDialTurned(KeyEvent event) {
        deInitialize();
        transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        deInitialize();
        transitionSinglePb();
        return 1;
    }

    private boolean isUnsupportedCaution() {
        boolean result = false;
        ContentsManager contentManager = ContentsManager.getInstance();
        ContentsIdentifier contentsIdentifier = contentManager.getContentsId();
        ImageEditor.releaseOptImage(CatchLightPlayBackLayout.sSelectedOptimizedImage);
        try {
            OptimizedImage optImage = contentManager.getOptimizedImageWithoutCache(contentsIdentifier, 1);
            if (optImage == null || (optImage != null && !optImage.isValid())) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
            }
            String imageAspectRatio = PortraitBeautyUtil.getInstance().getAspectRatio(optImage.getWidth(), optImage.getHeight());
            ImageAnalyzer imgAnalyzer1 = new ImageAnalyzer();
            Log.d(this.TAG, "fineFaces in");
            this.mFaces = new ImageAnalyzer.AnalyzedFace[8];
            this.mFaceNum = imgAnalyzer1.findFaces(optImage, this.mFaces);
            imgAnalyzer1.release();
            if (optImage != null) {
                optImage.release();
            }
            if (this.mFaces != null) {
                int len = this.mFaces.length;
                for (int i = 0; i < len; i++) {
                    this.mFaces[i] = null;
                }
            }
            if (imageAspectRatio == null) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                result = true;
            } else if (this.mFaceNum == 0) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_FACE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                result = true;
            }
            return result;
        } catch (Exception e) {
            return true;
        }
    }
}
