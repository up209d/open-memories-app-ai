package com.sony.mexi.webapi;

/* loaded from: classes.dex */
public enum Status {
    OK(0, "OK"),
    ANY(1, "Any"),
    TIMEOUT(2, "Timeout"),
    ILLEGAL_ARGUMENT(3, "Illegal Argument"),
    ILLEGAL_REQUEST(5, "Illegal Request"),
    ILLEGAL_RESPONSE(6, "Illegal Response"),
    ILLEGAL_STATE(7, "Illegal State"),
    NO_SUCH_METHOD(12, "No Such Method"),
    UNSUPPORTED_VERSION(14, "Unsupported Version"),
    UNSUPPORTED_OPERATION(15, "Unsupported Operation"),
    TRANSPORT_DISCONNECTED(16, "Transport Disconnected"),
    CLIENT_INTERNAL_ERROR(17, "Client Internal Error"),
    RESPONSE_TIMEOUT(18, "Response Timeout"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    LONG_POLLING_END_WITHOUT_ANY_CHANGE(40000, "Long Polling End Without Any Change"),
    NOTIFICATION_REQUESTS_EXCEED_CAPACITY(40001, "Notification Requests Exceed Capacity"),
    ENCRYPTION_FAILED(40002, "Encryption Failed"),
    REQUEST_DUPLICATED(40003, "Request Duplicated");

    private final int intVal;
    private final String message;

    Status(int val, String message) {
        this.intVal = val;
        this.message = message;
    }

    public int toInt() {
        return this.intVal;
    }

    public String toMessage() {
        return this.message;
    }

    public static Status valueOf(int val) {
        Status[] arr$ = values();
        for (Status e : arr$) {
            if (e.toInt() == val) {
                return e;
            }
        }
        return ANY;
    }
}
