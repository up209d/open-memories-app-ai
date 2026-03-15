package com.sony.imaging.app.base.shooting;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class AutoReviewState extends StateBase implements CameraEx.PreviewStartListener {
    public static final String LIMITED_CONTSHOOTING_LAYOUT = "LimitedContShootingLayout";
    private static final String LOG_S2_ON = "handling S2 on";
    private static final String LOG_STR_APPROOT_NULL = "appRoot is null";
    protected static final String NEXT_CAPTURE_STATE = "Capture";
    protected static final String NEXT_EE_STATE = "EE";
    private static final String PTAG_DISPLAYED_EE_SCREEN = "Displayed EE screen from AutoReview";
    public static final String STATE_NAME = "AutoReview";
    private static final String TAG = "AutoReviewState";
    protected BaseShootingExecutor mExecutor;
    private TouchArea.OnTouchAreaListener mOnTouchAreaListener = new TouchArea.OnTouchAreaListener() { // from class: com.sony.imaging.app.base.shooting.AutoReviewState.1
        private Rect r;

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchDown(MotionEvent e) {
            return false;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
            DisplayModeObserver dispObserver = DisplayModeObserver.getInstance();
            int displayMode = dispObserver.getActiveDispMode(1);
            if (displayMode == 5) {
                e = null;
            }
            DisplayManager.VideoRect v = (DisplayManager.VideoRect) dispObserver.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            this.r = new Rect(v.pxLeft, v.pxTop, v.pxRight, v.pxBottom);
            AutoReviewState.this.moveToPlayback(new EventParcel(EventParcel.TOUCH_UP, e, e, isReleasedInside, -1.0f, -1.0f, -1.0f, -1.0f, this.r));
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_OPTION_ON);
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onFlick(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            DisplayManager.VideoRect v = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            this.r = new Rect(v.pxLeft, v.pxTop, v.pxRight, v.pxBottom);
            AutoReviewState.this.moveToPlayback(new EventParcel(EventParcel.SCROLL, e1, e2, false, -1.0f, -1.0f, distanceX, distanceY, this.r));
            return false;
        }
    };

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mExecutor = ExecutorCreator.getInstance().getSequence();
        ShootingExecutor.setPreviewStartListener(this);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (enableCancelAutoPictureReview()) {
            this.mExecutor.cancelAutoPictureReview();
        }
        ShootingExecutor.setPreviewStartListener(null);
        super.onPause();
    }

    protected boolean enableCancelAutoPictureReview() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveToPlayback(EventParcel event) {
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_TOUCH, event);
        b.putParcelable(STATE_NAME, data);
        Activity appRoot = getActivity();
        if (appRoot != null) {
            ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        } else {
            Log.e(TAG, LOG_STR_APPROOT_NULL);
        }
    }

    public void onStart(CameraEx camex) {
        PTag.end(PTAG_DISPLAYED_EE_SCREEN);
        setNextState(NEXT_EE_STATE, null);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 15;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 != msg.what) {
            return false;
        }
        Log.i(TAG, LOG_S2_ON);
        setNextState(NEXT_CAPTURE_STATE, getBundle());
        return true;
    }

    protected Bundle getBundle() {
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
