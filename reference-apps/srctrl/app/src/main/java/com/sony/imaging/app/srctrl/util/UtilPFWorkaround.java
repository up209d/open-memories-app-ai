package com.sony.imaging.app.srctrl.util;

import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class UtilPFWorkaround {
    public static final String UI_MODEL_NAME_DMH = "DSLR-15-01";
    public static final String UI_MODEL_NAME_DMHH = "DSLR-15-04";
    public static final String UI_MODEL_NAME_FV = "DSC-15-01";
    public static final String UI_MODEL_NAME_GC = "DSLR-15-02";
    public static final String UI_MODEL_NAME_ICV2 = "DSLR-15-03";
    public static final String UI_MODEL_NAME_KV = "DSC-15-03";
    public static final String UI_MODEL_NAME_ZV = "DSC-15-02";
    static String TAG = "UtilPFWorkaround";
    public static final String PF_VERSION = ScalarProperties.getString("version.platform");
    public static final int PF_MAJOR_VERSION = Integer.parseInt(PF_VERSION.substring(0, PF_VERSION.indexOf(StringBuilderThreadLocal.PERIOD)));
    public static final int PF_API_VERSION = Integer.parseInt(PF_VERSION.substring(0, PF_VERSION.indexOf(StringBuilderThreadLocal.PERIOD)));
    public static final String PROP_MODEL_NAME = "model.name";
    public static final String MODEL_NAME = ScalarProperties.getString(PROP_MODEL_NAME);
    public static final String UI_MODEL_NAME = ScalarProperties.getString("ui.model.mame");

    public static boolean isDeviceNameModifyNeeded() {
        return 9 >= Environment.getVersionPfAPI();
    }
}
