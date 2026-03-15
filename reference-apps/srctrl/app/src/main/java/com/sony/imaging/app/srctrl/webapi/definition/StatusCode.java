package com.sony.imaging.app.srctrl.webapi.definition;

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
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    PAYMENT_REQUIRED(402),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    NOT_ACCEPTABLE(406),
    PROXY_AUTHENTICATION_REQUIRED(407),
    REQUEST_TIMEOUT(408),
    CONFLICT(409),
    GONE(410),
    LENGTH_REQUIRED(411),
    PRECONDITION_FAILED(412),
    REQUEST_ENTITY_TOO_LARGE(413),
    REQUEST_URI_TOO_LONG(414),
    UNSUPPORTED_MEDIA_TYPE(415),
    REQUESTED_RANGE_NOT_SATISFIABLE(416),
    EXPECTATION_FAILED(417),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504),
    HTTP_VERSION_NOT_SUPPORTED(505),
    FAILED_SHOOTING(40400),
    NOT_READY(40401),
    ALREADY_RUNNING_POLLING(40402),
    STILL_CAPTURING_NOT_FINISHED(40403),
    CONTENT_IS_PROTECTED(41000),
    CONTENT_DOES_NOT_EXIST(41001),
    STRAGE_IS_NOT_CONNECTED(41002),
    SOME_CONTENT_COULD_NOT_BE_DELETED(41003);

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
