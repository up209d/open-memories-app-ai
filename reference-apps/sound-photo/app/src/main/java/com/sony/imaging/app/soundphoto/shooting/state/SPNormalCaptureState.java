package com.sony.imaging.app.soundphoto.shooting.state;

import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationLayout;
import com.sony.imaging.app.soundphoto.util.AppLog;

/* loaded from: classes.dex */
public class SPNormalCaptureState extends NormalCaptureState {
    private static final String TAG = "SPNormalCaptureState";

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AppLog.info(TAG, "onResume()");
        openLayout(SPAudioBufferAnimationLayout.TAG);
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.info(TAG, "onPause()");
        closeLayout(SPAudioBufferAnimationLayout.TAG);
        super.onPause();
    }
}
