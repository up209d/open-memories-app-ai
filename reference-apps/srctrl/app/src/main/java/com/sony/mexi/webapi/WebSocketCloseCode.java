package com.sony.mexi.webapi;

import com.sony.scalar.lib.ssdpdevice.SsdpDevice;

/* loaded from: classes.dex */
public enum WebSocketCloseCode {
    UNDEFINED(-1, "Undefined"),
    NORMAL_CLOSURE(SsdpDevice.RETRY_INTERVAL, "Normal Closure"),
    GOING_AWAY(1001, "Going Away"),
    PROTOCOL_ERROR(1002, "Protocol error"),
    UNSUPPORTED_DATA(1003, "Unsupported Data"),
    NO_STATUS_RECEIVED(1005, "No Status Rcvd"),
    ABNORMAL_CLOSURE(1006, "Abnormal Closure"),
    INVALID_FRAME_PAYLOAD_DATA(1007, "Invalid frame payload data"),
    POLICY_VIOLATION(1008, "Policy Violation"),
    MESSAGE_TOO_BIG(1009, "Message Too Big"),
    MANDATORY_EXTENSION(1010, "Mandatory Ext."),
    INTERNAL_SERVER_ERROR(1011, "Internal Server Error"),
    TLS_HANDSHAKE(1015, "TLS handshake");

    private final int intVal;
    private final String message;

    WebSocketCloseCode(int val, String message) {
        this.intVal = val;
        this.message = message;
    }

    public int toInt() {
        return this.intVal;
    }

    public String toMessage() {
        return this.message;
    }

    public static WebSocketCloseCode fromNumber(int val) {
        WebSocketCloseCode[] arr$ = values();
        for (WebSocketCloseCode e : arr$) {
            if (e.toInt() == val) {
                return e;
            }
        }
        return UNDEFINED;
    }
}
