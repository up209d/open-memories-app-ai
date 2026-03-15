package com.sony.imaging.app.base.playback.base.editor;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.contents.EditService;

/* loaded from: classes.dex */
public abstract class Executor extends PlayStateWithLayoutBase implements EditService.ExecutionListener {
    private static final String MSG_PREFIX_ON_PROGRESS = "onProgress ";

    /* loaded from: classes.dex */
    public interface IProgress {
        void onProgress(int i, int i2);
    }

    protected EditService getEditService() {
        return ((Editor) getContainer()).getEditService();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        EditService service = getEditService();
        service.registerListener(this);
        service.execute();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getEditService().unregisterListener(this);
        getEditService().cancel();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService.ExecutionListener
    public void onProgress(int current, int max) {
        Log.d(getLogTag(), LogHelper.getScratchBuilder(MSG_PREFIX_ON_PROGRESS).append(current).append(", ").append(max).toString());
        IProgress listener = (IProgress) getLayout(getResumeLayout());
        listener.onProgress(current, max);
    }
}
