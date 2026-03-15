package com.sony.imaging.app.util;

import android.content.Context;
import android.content.Intent;

/* loaded from: classes.dex */
public class AppInfo {
    private static final String ACTION_NOTIFY_APP_INFO = "com.android.server.DAConnectionManagerService.AppInfoReceive";
    public static final String CEC_CONTENTS_MENU = "CEC_CONTENTS_MENU";
    public static final String CEC_LINK_MENU = "CEC_LINK_MENU";
    public static final String CEC_SETUP_MENU = "CEC_SETUP_MENU";
    private static final String EXTRA_CLASS_NAME = "class_name";
    private static final String EXTRA_LARGE_CATEGORY = "large_category";
    private static final String EXTRA_PACKAGE_NAME = "package_name";
    private static final String EXTRA_PULLING_BACK_KEY = "pullingback_key";
    private static final String EXTRA_RESUME_KEY = "resume_key";
    private static final String EXTRA_SMALL_CATEGORY = "small_category";
    public static final String KEY_ACCESSORY_APO = "KEY_ACCESSORY_APO";
    public static final String KEY_DEDICATED_APO = "KEY_DEDICATED_APO";
    public static final String KEY_LENS_APO = "KEY_LENS_APO";
    public static final String KEY_MEDIA_INOUT_APO = "KEY_MEDIA_INOUT_APO";
    public static final String KEY_MENU = "KEY_MENU";
    public static final String KEY_MENU_APO = "KEY_MENU_APO";
    public static final String KEY_MODE_DIAL = "KEY_MODE_DIAL";
    public static final String KEY_MOVREC = "KEY_MOVREC";
    public static final String KEY_MOVREC_APO = "KEY_MOVREC_APO";
    public static final String KEY_MOVREC_PON = "KEY_MOVREC_PON";
    public static final String KEY_PLAY = "KEY_PLAY";
    public static final String KEY_PLAY_APO = "KEY_PLAY_APO";
    public static final String KEY_PLAY_PON = "KEY_PLAY_PON";
    public static final String KEY_POWER_APO = "KEY_POWER_APO";
    public static final String KEY_POWER_SLIDE_PON = "KEY_POWER_SLIDE_PON";
    public static final String KEY_RELEASE_APO = "KEY_RELEASE_APO";
    public static final String KEY_S1_1 = "KEY_S1_1";
    public static final String KEY_S1_2 = "KEY_S1_2";
    public static final String KEY_S2 = "KEY_S2";
    public static final String KEY_SK1 = "KEY_MENU";
    public static final String KEY_SK1_APO = "KEY_MENU_APO";
    public static final String KEY_USB_CONNECT = "KEY_USB_CONNECT";
    public static final String LARGE_CATEGORY_PB = "CATEGORY_PB";
    public static final String LARGE_CATEGORY_REC = "CATEGORY_REC";
    public static final String SMALL_CATEGORY_INDEX = "INDEX";
    public static final String SMALL_CATEGORY_MOVIE = "MOVIE";
    public static final String SMALL_CATEGORY_SINGLE = "SINGLE";
    public static final String SMALL_CATEGORY_STILL = "STILL";

    public static void notifyAppInfo(Context context, String pkgName, String className, String largeCategory, String smallCategory, String[] pullingBackKeys, String[] resumeKeys) {
        Intent intent = new Intent(ACTION_NOTIFY_APP_INFO);
        intent.putExtra(EXTRA_LARGE_CATEGORY, largeCategory);
        intent.putExtra(EXTRA_SMALL_CATEGORY, smallCategory);
        intent.putExtra(EXTRA_CLASS_NAME, className);
        intent.putExtra(EXTRA_PACKAGE_NAME, pkgName);
        intent.putExtra(EXTRA_PULLING_BACK_KEY, pullingBackKeys);
        intent.putExtra(EXTRA_RESUME_KEY, resumeKeys);
        context.sendBroadcast(intent);
    }
}
