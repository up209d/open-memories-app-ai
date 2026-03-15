package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class DevelopmentStateExBase extends DevelopmentState {
    protected static final String CURRENT_STATE_NAME = "DevelopmentEx";
    private static final String TAG = DevelopmentStateExBase.class.getSimpleName();
    public static CameraEx.ReviewInfo s_ReviewInfo;
    private CameraNotificationManager mCamNtfy = CameraNotificationManager.getInstance();
    private NotificationListener mReviewInfoListener = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExBase.1
        private String[] TAGS = {CameraNotificationManager.PICTURE_REVIEW_INFO};

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            CameraEx.ReviewInfo reviewInfo = (CameraEx.ReviewInfo) DevelopmentStateExBase.this.mCamNtfy.getValue(CameraNotificationManager.PICTURE_REVIEW_INFO);
            if (DevelopmentStateExBase.s_ReviewInfo == null) {
                DevelopmentStateExBase.s_ReviewInfo = reviewInfo;
            } else if (reviewInfo.hist == null) {
                DevelopmentStateExBase.s_ReviewInfo.photo = reviewInfo.photo;
            } else {
                DevelopmentStateExBase.s_ReviewInfo.hist = reviewInfo.hist;
            }
        }
    };

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        s_ReviewInfo = null;
        this.mCamNtfy.setNotificationListener(this.mReviewInfoListener);
        Log.v(TAG, "Set ReviewInfoListener");
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mCamNtfy.removeNotificationListener(this.mReviewInfoListener);
        Log.v(TAG, "Remove ReviewInfoListener");
    }
}
