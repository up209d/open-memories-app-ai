package com.sony.mexi.json.mouse;

/* loaded from: classes.dex */
public interface Phrase {
    char charAt(int i);

    void errClear();

    String errMsg();

    Object get();

    boolean isA(String str);

    boolean isEmpty();

    void put(Object obj);

    String text();
}
