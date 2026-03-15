package com.sony.mexi.orb.service;

/* loaded from: classes.dex */
public interface EventEmitter {

    /* loaded from: classes.dex */
    public enum Event {
        CLOSE
    }

    void emit(Event event, String str);
}
