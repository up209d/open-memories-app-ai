package com.sony.mexi.webapi;

/* loaded from: classes.dex */
public enum Protocol {
    HTTP_POST("http", "http"),
    WEBSOCKET("ws", "websocket");

    public final String name;
    public final String scheme;

    Protocol(String scheme, String name) {
        this.scheme = scheme;
        this.name = name;
    }

    public static Protocol getProtocol(String scheme) {
        Protocol[] arr$ = values();
        for (Protocol protocol : arr$) {
            if (protocol.scheme.equals(scheme)) {
                return protocol;
            }
        }
        throw new IllegalArgumentException("undefined scheme :" + scheme);
    }
}
