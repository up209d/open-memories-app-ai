package com.sony.imaging.app.srctrl.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.ProgressLayout;
import com.sony.imaging.app.srctrl.util.SRCtrlNotificationManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ProgressLayoutEx extends ProgressLayout {
    private SRCtrlBulbShootEndListener mBulbShootEndListener = null;
    private final String[] tags = {SRCtrlNotificationManager.REMOTE_BULB_SHOOT_END};

    @Override // com.sony.imaging.app.base.shooting.layout.ProgressLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mBulbShootEndListener == null) {
            this.mBulbShootEndListener = new SRCtrlBulbShootEndListener();
        }
        SRCtrlNotificationManager.getInstance().setNotificationListener(this.mBulbShootEndListener);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ProgressLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        SRCtrlNotificationManager.getInstance().removeNotificationListener(this.mBulbShootEndListener);
        if (this.mBulbShootEndListener != null) {
            this.mBulbShootEndListener = null;
        }
    }

    /* loaded from: classes.dex */
    public class SRCtrlBulbShootEndListener implements NotificationListener {
        public SRCtrlBulbShootEndListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return ProgressLayoutEx.this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ProgressLayoutEx.this.getView().setVisibility(0);
            ProgressLayoutEx.this.handler.postDelayed(ProgressLayoutEx.this.mUnmuteRunnable, 300L);
        }
    }
}
