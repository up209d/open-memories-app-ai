package com.sony.imaging.app.soundphoto.playback.audiotrack.controller;

import java.util.EventListener;

/* loaded from: classes.dex */
public interface AudioTrackControlListener extends EventListener {
    void finishedUpdateStatus();

    void notifiedPlayTime(int i);
}
