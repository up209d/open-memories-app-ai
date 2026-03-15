package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public interface ICustomKey {
    public static final String CATEGORY_FOCUS_SETTING = "Focus Setting";
    public static final String CATEGORY_INDEX = "Index";
    public static final String CATEGORY_MENU = "Menu";
    public static final String CATEGORY_MOVIE_A = "Movie-A";
    public static final String CATEGORY_MOVIE_M = "Movie-M";
    public static final String CATEGORY_MOVIE_OTHER = "Movie-Other";
    public static final String CATEGORY_MOVIE_P = "Movie-P";
    public static final String CATEGORY_MOVIE_S = "Movie-S";
    public static final String CATEGORY_NONE = "None";
    public static final String CATEGORY_SHOOTING_A = "Shooting-A";
    public static final String CATEGORY_SHOOTING_M = "Shooting-M";
    public static final String CATEGORY_SHOOTING_OTHER = "Shooting-Other";
    public static final String CATEGORY_SHOOTING_P = "Shooting-P";
    public static final String CATEGORY_SHOOTING_S = "Shooting-S";
    public static final String CATEGORY_SINGLE = "Single";

    @Deprecated
    IKeyFunction getAssigned();

    IKeyFunction getAssigned(String str);

    IKeyFunction getFunctionCode(String str);

    @Deprecated
    void setAssigned(IKeyFunction iKeyFunction);
}
