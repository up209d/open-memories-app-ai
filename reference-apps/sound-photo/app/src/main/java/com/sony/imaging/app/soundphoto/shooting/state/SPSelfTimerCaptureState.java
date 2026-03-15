package com.sony.imaging.app.soundphoto.shooting.state;

import com.sony.imaging.app.base.shooting.SelfTimerCaptureState;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationLayout;

/* loaded from: classes.dex */
public class SPSelfTimerCaptureState extends SelfTimerCaptureState {
    @Override // com.sony.imaging.app.base.shooting.SelfTimerCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        openLayout(SPAudioBufferAnimationLayout.TAG);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.SelfTimerCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(SPAudioBufferAnimationLayout.TAG);
        super.onPause();
    }
}
