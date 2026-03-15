package com.sony.imaging.app.base.common;

/* loaded from: classes.dex */
public class StringBuilderThreadLocal extends ThreadLocal<StringBuilder> {
    public static final String COMMA = ", ";
    public static final String EQUAL = "length = ";
    public static final String MSG_ID = "id = ";
    public static final String MSG_LENGTH = "length = ";
    public static final String MSG_MODE = "mode = ";
    public static final String MSG_STATUS = "status = ";
    public static final String MSG_TAG = "tag = ";
    public static final String MSG_TYPE = "type = ";
    public static final String ROUND_BRACKET_CLOSE = " ) ";
    public static final String ROUND_BRACKET_OPEN = " ( ";
    public static final String SLASH = " / ";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.lang.ThreadLocal
    public StringBuilder initialValue() {
        return new StringBuilder();
    }
}
