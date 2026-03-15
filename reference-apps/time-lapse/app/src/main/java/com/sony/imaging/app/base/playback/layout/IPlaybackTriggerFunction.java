package com.sony.imaging.app.base.playback.layout;

import com.sony.imaging.app.base.common.EventParcel;

/* loaded from: classes.dex */
public interface IPlaybackTriggerFunction {
    boolean transitionBrowser();

    boolean transitionDeleteThis();

    boolean transitionIndexPb();

    boolean transitionShooting(EventParcel eventParcel);

    boolean transitionSinglePb();

    boolean transitionZoom(EventParcel eventParcel);
}
