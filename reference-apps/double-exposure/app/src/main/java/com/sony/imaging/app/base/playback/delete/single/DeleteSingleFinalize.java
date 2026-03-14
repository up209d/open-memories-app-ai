package com.sony.imaging.app.base.playback.delete.single;

import android.util.Log;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;

/* loaded from: classes.dex */
public class DeleteSingleFinalize extends PlayStateWithLayoutBase {
    private static final String MSG_DELAY = "Done delay";
    protected static int sEndProcessDelayInMSec = 200;
    private Runnable mWaitRunnable = new Runnable() { // from class: com.sony.imaging.app.base.playback.delete.single.DeleteSingleFinalize.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(DeleteSingleFinalize.this.TAG, DeleteSingleFinalize.MSG_DELAY);
            DeleteSingleFinalize.this.onWaitDone();
        }
    };

    protected void onWaitDone() {
        PlayRootContainer root = getRootContainer();
        root.changeApp(PlayRootContainer.ID_BROWSER);
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        getHandler().postDelayed(this.mWaitRunnable, sEndProcessDelayInMSec);
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getHandler().removeCallbacks(this.mWaitRunnable);
        super.onPause();
    }
}
