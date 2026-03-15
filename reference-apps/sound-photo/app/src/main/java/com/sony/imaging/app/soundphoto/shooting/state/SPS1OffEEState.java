package com.sony.imaging.app.soundphoto.shooting.state;

import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationLayout;

/* loaded from: classes.dex */
public class SPS1OffEEState extends S1OffEEState {
    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(SPAudioBufferAnimationLayout.TAG);
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(SPAudioBufferAnimationLayout.TAG);
        super.onPause();
    }
}
