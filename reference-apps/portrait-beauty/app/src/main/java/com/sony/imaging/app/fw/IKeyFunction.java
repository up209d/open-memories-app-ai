package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public interface IKeyFunction {
    public static final String TYPE_EXEC = "EXEC";
    public static final String TYPE_NONE = "NONE";
    public static final String TYPE_SETTING = "SETTING";

    @Deprecated
    int getImageId();

    String getItemIdForMenu();

    String getType();

    boolean isValid();
}
