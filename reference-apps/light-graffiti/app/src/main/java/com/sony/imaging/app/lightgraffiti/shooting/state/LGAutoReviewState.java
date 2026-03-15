package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.AppInfo;

/* loaded from: classes.dex */
public class LGAutoReviewState extends AutoReviewState {
    private LGAutoReviewStateLensStateListener mLensListener = new LGAutoReviewStateLensStateListener();

    /* loaded from: classes.dex */
    private class LGAutoReviewStateLensStateListener extends LGAbstractLensStateChangeListener {
        private LGAutoReviewStateLensStateListener() {
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener
        protected void onLensStateChanged() {
            Log.d("LGAutoReviewStateLensStateListener", "onLensStateChanged");
            LGUtility.getInstance().isLensAttachEventReady = true;
            LGAutoReviewState.this.setNextState("EE", null);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.AutoReviewState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        AppInfo.notifyAppInfo(getActivity().getApplicationContext(), getActivity().getPackageName(), getActivity().getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, BaseApp.PULLING_BACK_KEYS_FOR_SHOOTING, BaseApp.RESUME_KEYS_FOR_SHOOTING);
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
    }

    @Override // com.sony.imaging.app.base.shooting.AutoReviewState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
        super.onPause();
    }
}
