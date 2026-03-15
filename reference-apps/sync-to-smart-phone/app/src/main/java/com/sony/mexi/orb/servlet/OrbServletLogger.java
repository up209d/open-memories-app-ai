package com.sony.mexi.orb.servlet;

import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class OrbServletLogger implements Serializable {
    private static final boolean DEBUG = false;
    private static final long serialVersionUID = 1;

    public abstract void log(String str);

    public void debug(String msg) {
    }
}
