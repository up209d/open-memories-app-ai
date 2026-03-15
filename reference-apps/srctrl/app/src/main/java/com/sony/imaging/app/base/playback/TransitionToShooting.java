package com.sony.imaging.app.base.playback;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.playback.base.PlayStateBase;

/* loaded from: classes.dex */
public class TransitionToShooting extends PlayStateBase {
    private static final int DEFAULT_DELAY_TIME = 100;
    protected static int mDelayTime = 100;
    private Runnable mRunnable;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class TransitionRunnable implements Runnable {
        protected TransitionRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ((BaseApp) TransitionToShooting.this.getActivity()).changeApp("APP_SHOOTING", TransitionToShooting.this.data);
        }
    }

    protected Runnable getTransitionRunnable() {
        return new TransitionRunnable();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mRunnable = getTransitionRunnable();
        getHandler().postDelayed(this.mRunnable, mDelayTime);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mRunnable != null) {
            getHandler().removeCallbacks(this.mRunnable);
            this.mRunnable = null;
        }
        super.onPause();
    }
}
