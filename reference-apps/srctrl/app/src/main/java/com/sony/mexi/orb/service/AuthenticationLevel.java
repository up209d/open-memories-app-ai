package com.sony.mexi.orb.service;

/* loaded from: classes.dex */
public enum AuthenticationLevel {
    NONE("none"),
    GENERIC("generic"),
    PRIVATE("private");

    private String mLevel;

    AuthenticationLevel(String level) {
        this.mLevel = level;
    }

    public String value() {
        return this.mLevel;
    }
}
