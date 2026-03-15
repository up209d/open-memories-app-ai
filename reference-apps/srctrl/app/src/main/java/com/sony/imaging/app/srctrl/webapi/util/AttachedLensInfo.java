package com.sony.imaging.app.srctrl.webapi.util;

import android.util.Log;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.Formatter;

/* loaded from: classes.dex */
public abstract class AttachedLensInfo {
    private static final String ID_ATTACHED_LENSINFO_ID = "ID_ATTACHED_LENSINFO_ID";
    private static final String ID_ATTACHED_LENSINFO_NAME = "ID_ATTACHED_LENSINFO_NAME";
    private static final String ID_ATTACHED_LENSINFO_VERSION = "ID_ATTACHED_LENSINFO_VERSION";
    private static final long invalidLensId = 4294967295L;
    private static final int invalidLensVersion = -1;
    private static final String TAG = AttachedLensInfo.class.getSimpleName();
    private static String lensName = null;
    private static String lensId = null;
    private static String lensVersion = null;

    public static void load() {
        lensName = BackUpUtil.getInstance().getPreferenceString(ID_ATTACHED_LENSINFO_NAME, null);
        lensId = BackUpUtil.getInstance().getPreferenceString(ID_ATTACHED_LENSINFO_ID, null);
        lensVersion = BackUpUtil.getInstance().getPreferenceString(ID_ATTACHED_LENSINFO_VERSION, null);
        Log.v(TAG, "LensName:" + lensName);
        Log.v(TAG, "LensID  :" + lensId);
        Log.v(TAG, "LensVersion  :" + lensVersion);
    }

    public static void save() {
        if (lensName != null) {
            BackUpUtil.getInstance().setPreference(ID_ATTACHED_LENSINFO_NAME, lensName);
        }
        if (lensId != null) {
            BackUpUtil.getInstance().setPreference(ID_ATTACHED_LENSINFO_ID, lensId);
        }
        if (lensVersion != null) {
            BackUpUtil.getInstance().setPreference(ID_ATTACHED_LENSINFO_VERSION, lensVersion);
        }
    }

    public static String getLensName() {
        return lensName;
    }

    public static String getLensID() {
        return lensId;
    }

    public static String getLensVersion() {
        return lensVersion;
    }

    public static void setLensInfo(String name) {
        setLensInfo(name, invalidLensId, -1);
    }

    public static void setLensInfo(String name, long id, int version) {
        if (name == null || name.length() == 0) {
            Log.v(TAG, "Invalid Lens Name");
            return;
        }
        String lensIdHexString = convertLensId(id);
        String lensVersionString = convertLensVersion(version);
        lensName = name;
        lensId = lensIdHexString;
        lensVersion = lensVersionString;
        Log.v(TAG, "set Lens\u3000Name : " + lensName);
        Log.v(TAG, "set Lens\u3000ID : " + lensId);
        Log.v(TAG, "set Lens Version : " + lensVersion);
    }

    private static String convertLensId(long id) {
        if (id != invalidLensId) {
            Formatter formatter = new Formatter();
            String lensIdHexString = formatter.format("%08X", Long.valueOf(id)).toString();
            return lensIdHexString;
        }
        Log.v(TAG, "Invalid Lens ID");
        return null;
    }

    private static String convertLensVersion(int version) {
        if (version != -1) {
            Formatter formatter = new Formatter();
            String lensVersionString = formatter.format("%1$02d%2$02d", Integer.valueOf((65280 & version) >>> 8), Integer.valueOf(version & BatteryIcon.BATTERY_STATUS_CHARGING)).toString();
            return lensVersionString;
        }
        Log.v(TAG, "Invalid Lens Version");
        return null;
    }
}
