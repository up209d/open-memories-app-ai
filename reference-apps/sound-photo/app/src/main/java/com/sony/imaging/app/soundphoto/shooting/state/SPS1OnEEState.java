package com.sony.imaging.app.soundphoto.shooting.state;

import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationLayout;

/* loaded from: classes.dex */
public class SPS1OnEEState extends S1OnEEState {
    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(SPAudioBufferAnimationLayout.TAG);
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(SPAudioBufferAnimationLayout.TAG);
        super.onPause();
    }
}
