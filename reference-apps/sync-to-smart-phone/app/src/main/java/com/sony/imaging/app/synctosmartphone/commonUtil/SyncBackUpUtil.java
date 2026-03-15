package com.sony.imaging.app.synctosmartphone.commonUtil;

import android.content.Context;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SyncBackUpUtil {
    private static final String TAG = SyncBackUpUtil.class.getSimpleName();
    private static SyncBackUpUtil mInstance = null;

    public static SyncBackUpUtil getInstance() {
        if (mInstance == null) {
            mInstance = new SyncBackUpUtil();
        }
        return mInstance;
    }

    public void Init(Context context) {
        BackUpUtil.getInstance().Init(context);
    }

    public void finishSettings() {
        BackUpUtil.getInstance().finishSettings(false);
    }

    public void setStartUTC(long value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_START_UTC, Long.valueOf(value));
        Log.v(TAG, "setStartUTC = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setRegister(boolean flg) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_REGISTER, Boolean.valueOf(flg));
        Log.v(TAG, "setRegister = " + flg + LogHelper.MSG_COLON + ret);
    }

    public void setAppFirstTime(boolean flg) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_APP_FIRST_TIME, Boolean.valueOf(flg));
        Log.v(TAG, "setAppFirstTime = " + flg + LogHelper.MSG_COLON + ret);
    }

    public void setImageSize(int value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_IMAGE_SIZE, Integer.valueOf(value));
        Log.v(TAG, "setImageSize = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setImageStartNum(int value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_IMAGE_START_NUM, Integer.valueOf(value));
        Log.v(TAG, "setImageStartNum = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setSmartphoneInfo(String value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_SMARTPHONE_INFO, value);
        Log.v(TAG, "setSmartphoneInfo = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setSmartphone(String value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_SMARTPHONE, value);
        Log.v(TAG, "setSmartphone = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setSyncDate(long value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_SYNC_DATE, Long.valueOf(value));
        Log.v(TAG, "setSyncDate = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setSyncError(int value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_SYNC_ERROR, Integer.valueOf(value));
        if (value != 0) {
            setKeyDisplayErrorAppStart(true);
        }
        Log.v(TAG, "setSyncError = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setSyncErrorTotal(int value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_SYNC_ERROR_TOTAL, Integer.valueOf(value));
        Log.v(TAG, "setSyncErrorTotal = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setSyncErrorSent(int value) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_SYNC_ERROR_SENT, Integer.valueOf(value));
        Log.v(TAG, "setSyncErrorSent = " + value + LogHelper.MSG_COLON + ret);
    }

    public void setKeyDisplayErrorAppStart(boolean flg) {
        boolean ret = BackUpUtil.getInstance().setPreference(BackUpKey.KEY_DISPLAY_ERROR_APP_START, Boolean.valueOf(flg));
        Log.v(TAG, "setKeyDisplayErrorAppStart = " + flg + LogHelper.MSG_COLON + ret);
    }

    public long getStartUTC(long def) {
        long ret = BackUpUtil.getInstance().getPreferenceLong(BackUpKey.KEY_START_UTC, def);
        Log.v(TAG, "getSetUTC = " + ret);
        return ret;
    }

    public boolean getRegister(boolean flg) {
        boolean ret = BackUpUtil.getInstance().getPreferenceBoolean(BackUpKey.KEY_REGISTER, flg);
        Log.v(TAG, "getRegister = " + ret);
        return ret;
    }

    public boolean getAppFirstTime(boolean flg) {
        boolean ret = BackUpUtil.getInstance().getPreferenceBoolean(BackUpKey.KEY_APP_FIRST_TIME, flg);
        Log.v(TAG, "getAppFirstTime = " + ret);
        return ret;
    }

    public int getImageSize(int value) {
        int ret = BackUpUtil.getInstance().getPreferenceInt(BackUpKey.KEY_IMAGE_SIZE, value);
        Log.v(TAG, "getImageSize = " + ret);
        return ret;
    }

    public int getImageStartNum(int value) {
        int ret = BackUpUtil.getInstance().getPreferenceInt(BackUpKey.KEY_IMAGE_START_NUM, value);
        Log.v(TAG, "getImageStartNum = " + ret);
        return ret;
    }

    public String getSmartphoneInfo(String value) {
        String ret = BackUpUtil.getInstance().getPreferenceString(BackUpKey.KEY_SMARTPHONE_INFO, value);
        Log.v(TAG, "getSmartphoneInfo = " + ret);
        return ret;
    }

    public String getSmartphone(String value) {
        String ret = BackUpUtil.getInstance().getPreferenceString(BackUpKey.KEY_SMARTPHONE, value);
        Log.v(TAG, "getSmartphone = " + ret);
        return ret;
    }

    public long getSyncDate(long value) {
        long ret = BackUpUtil.getInstance().getPreferenceLong(BackUpKey.KEY_SYNC_DATE, value);
        Log.v(TAG, "getSyncDate = " + ret);
        return ret;
    }

    public int getSyncError(int value) {
        int ret = BackUpUtil.getInstance().getPreferenceInt(BackUpKey.KEY_SYNC_ERROR, value);
        Log.v(TAG, "getSyncError = " + ret);
        return ret;
    }

    public int getSyncErrorTotal(int value) {
        int ret = BackUpUtil.getInstance().getPreferenceInt(BackUpKey.KEY_SYNC_ERROR_TOTAL, value);
        Log.v(TAG, "getSyncErrorTotal = " + ret);
        return ret;
    }

    public int getSyncErrorSent(int value) {
        int ret = BackUpUtil.getInstance().getPreferenceInt(BackUpKey.KEY_SYNC_ERROR_SENT, value);
        Log.v(TAG, "getSyncErrorSent = " + ret);
        return ret;
    }

    public boolean getKeyDisplayErrorAppStart(boolean flg) {
        boolean ret = BackUpUtil.getInstance().getPreferenceBoolean(BackUpKey.KEY_DISPLAY_ERROR_APP_START, flg);
        Log.v(TAG, "getKeyDisplayErrorAppStart = " + flg + LogHelper.MSG_COLON + ret);
        return ret;
    }
}
