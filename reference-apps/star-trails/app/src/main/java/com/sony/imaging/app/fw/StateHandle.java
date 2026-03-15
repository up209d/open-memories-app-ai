package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public class StateHandle {
    int layer;
    State state;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StateHandle(State f) {
        this.layer = -2;
        this.state = f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StateHandle(int layer, State f) {
        this.layer = layer;
        this.state = f;
    }

    public boolean isAlive() {
        return -1 != this.layer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void expire() {
        this.layer = -1;
    }
}
