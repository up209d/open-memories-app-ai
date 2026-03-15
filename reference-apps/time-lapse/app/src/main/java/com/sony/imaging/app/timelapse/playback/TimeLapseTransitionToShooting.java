package com.sony.imaging.app.timelapse.playback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.playback.TransitionToShooting;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;

/* loaded from: classes.dex */
public class TimeLapseTransitionToShooting extends TransitionToShooting {
    @Override // com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDelayTime = IntervalRecExecutor.INTVL_REC_INITIALIZED;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
