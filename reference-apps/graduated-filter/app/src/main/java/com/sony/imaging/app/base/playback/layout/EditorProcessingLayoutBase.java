package com.sony.imaging.app.base.playback.layout;

import android.widget.ProgressBar;
import com.sony.imaging.app.base.playback.base.editor.Executor;

/* loaded from: classes.dex */
public abstract class EditorProcessingLayoutBase extends PlayLayoutBase implements Executor.IProgress {
    protected abstract int getProgressBarResource();

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ProgressBar progressBar = (ProgressBar) getView().findViewById(getProgressBarResource());
        if (progressBar != null) {
            progressBar.setProgress(0);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Executor.IProgress
    public void onProgress(int current, int max) {
        ProgressBar progressBar = (ProgressBar) getView().findViewById(getProgressBarResource());
        if (progressBar != null) {
            progressBar.setProgress((progressBar.getMax() * current) / max);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS1Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }
}
