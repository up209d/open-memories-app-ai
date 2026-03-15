package com.sony.imaging.app.soundphoto.playback.state;

/* loaded from: classes.dex */
public interface MenuTransitionTrigger {
    boolean transition4KDisplay();

    boolean transitionCancel();

    boolean transitionDeleteMultiple();

    boolean transitionDeleteSound();

    boolean transitionToVolumeSetting();

    boolean transitionUploadMultiple();
}
