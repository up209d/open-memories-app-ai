package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.imaging.app.lightgraffiti.util.LGSAReviewFilter;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class LGEasyReviewState extends StateBase {
    private static final String TAG = LGEasyReviewState.class.getSimpleName();
    private static LGSAReviewFilter sLGSAReviewFilter = null;
    private LGEasyReviewStateLensStateListener mLensListener;
    private NotificationListener mediaStatusChangedListener;

    public LGEasyReviewState() {
        this.mLensListener = new LGEasyReviewStateLensStateListener();
        this.mediaStatusChangedListener = new MediaNotificationListener();
    }

    /* loaded from: classes.dex */
    private class LGEasyReviewStateLensStateListener extends LGAbstractLensStateChangeListener {
        private LGEasyReviewStateLensStateListener() {
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener
        protected void onLensStateChanged() {
            Log.d("LGEasyReviewStateLensStateListener", "onLensStateChanged");
            LGStateHolder.getInstance().setLensProblemFlag(true);
            LGEasyReviewState.this.onCloseReview();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        openLayout("LGEasyReviewLayout");
        LGPreviewEffect.getInstance().stopPreviewEffect();
        FaceDetectionController.getInstance().setFaceFrameRendering(false);
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
        initialzeSA();
        sLGSAReviewFilter.startLiveViewEffect(LGStateHolder.getInstance().getDb_review());
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        releaseSA();
        closeLayout("LGEasyReviewLayout");
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        int runStatus = RunStatus.getStatus();
        if (2 != runStatus) {
            LGPreviewEffect.getInstance().startPreviewEffect();
        }
        super.onDestroy();
    }

    private void initialzeSA() {
        sLGSAReviewFilter = LGSAReviewFilter.getInstance();
        sLGSAReviewFilter.intialize();
        sLGSAReviewFilter.setWeightMeanValue(0);
    }

    public void releaseSA() {
        if (sLGSAReviewFilter != null) {
            sLGSAReviewFilter.terminate();
            sLGSAReviewFilter = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NONE;
        return type;
    }

    public void onCloseReview() {
        setNextState(S1OffEEState.STATE_NAME, null);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "Pushed Menu key.");
        onCloseReview();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "Pushed S2 key.");
        onCloseReview();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "Pushed Delete key.");
        onCloseReview();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "Pushed Menu key.");
        onCloseReview();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (scanCode == 513 || scanCode == 595) {
            onCloseReview();
        }
        if (0 != 0) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_SHOOTING_P;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Bundle bundle = new Bundle();
        bundle.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_PB);
        setNextState("EE", bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRShutterKeyPushed(KeyEvent event) {
        Log.d(TAG, "onIRShutterKeyPushed.");
        return pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onIR2SecKeyPushed(KeyEvent event) {
        Log.d(TAG, "onIR2SecKeyPushed.");
        return pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRRecKeyPushed(KeyEvent event) {
        Log.d(TAG, "onIRRecKeyPushed.");
        return super.pushedMovieRecKey();
    }

    /* loaded from: classes.dex */
    private class MediaNotificationListener implements NotificationListener {
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
            if (tag.equalsIgnoreCase(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                int state = MediaNotificationManager.getInstance().getMediaState();
                if (state == 0) {
                    LGEasyReviewState.this.closeLayout("LGEasyReviewLayout");
                    LGEasyReviewState.this.onCloseReview();
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d(TAG, "attachedLens");
        LGStateHolder.getInstance().setLensProblemFlag(true);
        onCloseReview();
        return super.attachedLens();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d(TAG, "detachedLens");
        LGStateHolder.getInstance().setLensProblemFlag(true);
        onCloseReview();
        return super.detachedLens();
    }
}
