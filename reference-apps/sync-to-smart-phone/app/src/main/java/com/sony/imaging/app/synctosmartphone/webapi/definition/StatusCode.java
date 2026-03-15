package com.sony.imaging.app.synctosmartphone.webapi.definition;

import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public enum StatusCode {
    OK(0),
    ANY(1),
    TIMEOUT(2),
    ILLEGAL_ARGUMENT(3),
    ILLEGAL_DATA_FORMAT(4),
    ILLEGAL_REQUEST(5),
    ILLEGAL_RESPONSE(6),
    ILLEGAL_STATE(7),
    ILLEGAL_TYPE(8),
    INDEX_OUT_OF_BOUNDS(9),
    NO_SUCH_ELEMENT(10),
    NO_SUCH_FIELD(11),
    NO_SUCH_METHOD(12),
    NULL_POINTER(13),
    UNSUPPORTED_VERSION(14),
    UNSUPPORTED_OPERATION(15),
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST),
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED),
    PAYMENT_REQUIRED(HttpServletResponse.SC_PAYMENT_REQUIRED),
    FORBIDDEN(HttpServletResponse.SC_FORBIDDEN),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND),
    METHOD_NOT_ALLOWED(HttpServletResponse.SC_METHOD_NOT_ALLOWED),
    NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE),
    PROXY_AUTHENTICATION_REQUIRED(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED),
    REQUEST_TIMEOUT(HttpServletResponse.SC_REQUEST_TIMEOUT),
    CONFLICT(HttpServletResponse.SC_CONFLICT),
    GONE(HttpServletResponse.SC_GONE),
    LENGTH_REQUIRED(HttpServletResponse.SC_LENGTH_REQUIRED),
    PRECONDITION_FAILED(HttpServletResponse.SC_PRECONDITION_FAILED),
    REQUEST_ENTITY_TOO_LARGE(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE),
    REQUEST_URI_TOO_LONG(HttpServletResponse.SC_REQUEST_URI_TOO_LONG),
    UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE),
    REQUESTED_RANGE_NOT_SATISFIABLE(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE),
    EXPECTATION_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED),
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    NOT_IMPLEMENTED(HttpServletResponse.SC_NOT_IMPLEMENTED),
    BAD_GATEWAY(HttpServletResponse.SC_BAD_GATEWAY),
    SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT(HttpServletResponse.SC_GATEWAY_TIMEOUT),
    HTTP_VERSION_NOT_SUPPORTED(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED),
    CLIENT_NOT_PAIRED(42600),
    NOT_UNDER_PAIRING_MODE(42601);

    private int intVal;

    StatusCode(int val) {
        this.intVal = val;
    }

    public int toInt() {
        return this.intVal;
    }

    public static StatusCode valueOf(int val) {
        StatusCode[] arr$ = values();
        for (StatusCode e : arr$) {
            if (e.toInt() == val) {
                return e;
            }
        }
        return null;
    }
}
