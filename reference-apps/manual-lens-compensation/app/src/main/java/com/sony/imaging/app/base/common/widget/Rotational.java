package com.sony.imaging.app.base.common.widget;

/* loaded from: classes.dex */
public interface Rotational {
    void init();

    boolean isValidValue();

    void removeRotationFinishListener();

    void setRotationFinishListener(RotationFinishListener rotationFinishListener);

    void start();

    void stop();
}
