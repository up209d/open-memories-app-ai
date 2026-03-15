package com.sony.imaging.app.base.menu;

import java.util.List;

/* loaded from: classes.dex */
public interface IController {
    public static final int CAUTION_INDEX_CAUTION_ID_DLAPP_INVALID_MOVIE_MODE = -1;
    public static final int CAUTION_INDEX_CAUTION_ID_DLAPP_INVALID_STILL_MODE = -2;

    /* loaded from: classes.dex */
    public static class NotSupportedException extends Exception {
        private static final long serialVersionUID = 7828004784172946520L;
    }

    List<String> getAvailableValue(String str);

    int getCautionIndex(String str);

    Object getDetailValue();

    List<String> getSupportedValue(String str);

    String getValue(String str) throws NotSupportedException;

    boolean isAvailable(String str);

    void setDetailValue(Object obj);

    void setValue(String str, String str2) throws IllegalArgumentException;
}
