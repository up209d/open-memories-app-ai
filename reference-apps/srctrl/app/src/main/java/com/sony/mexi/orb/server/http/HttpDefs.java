package com.sony.mexi.orb.server.http;

import java.io.IOException;

/* loaded from: classes.dex */
public final class HttpDefs {
    public static final String CONTENT_TYPE_APP_JSON = "application/json";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String HEADER_ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ORIGIN = "origin";
    public static final String METHOD_CONNECT = "CONNECT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_TRACE = "TRACE";
    public static final IOException STREAM_CLOSED_EXCEPTION = new IOException("Stream Closed");
    public static final String UTF_8 = "UTF-8";
    public static final String VALUE_CHARSET = "charset";
    public static final String VALUE_CLOSE = "close";
    public static final String VALUE_TRUE = "true";

    private HttpDefs() {
    }
}
