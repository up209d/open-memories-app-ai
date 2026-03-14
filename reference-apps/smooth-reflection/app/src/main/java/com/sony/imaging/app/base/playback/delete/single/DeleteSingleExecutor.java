package com.sony.imaging.app.base.playback.delete.single;

import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.playback.base.editor.Executor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;

/* loaded from: classes.dex */
public class DeleteSingleExecutor extends Executor {
    private static final String MSG_DELAY = "Done delay";
    protected int sEndProcessDelayInMSec = 100;
    private Runnable sDelayedEndProcess = new Runnable() { // from class: com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(DeleteSingleExecutor.this.TAG, DeleteSingleExecutor.MSG_DELAY);
            DeleteSingleExecutor.this.executeEndProcess();
        }
    };

    protected void executeEndProcess() {
        setNextState(DeleteSingle.ID_FINALIZER, null);
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Executor, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 1);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Executor, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Handler handler = getHandler();
        handler.removeCallbacks(this.sDelayedEndProcess);
        super.onPause();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 0);
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService.ExecutionListener
    public void onEnd(int status) {
        if (status == 0) {
            Handler handler = getHandler();
            handler.postDelayed(this.sDelayedEndProcess, this.sEndProcessDelayInMSec);
        } else {
            executeEndProcess();
        }
    }
}
