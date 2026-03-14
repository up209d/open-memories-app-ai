package com.sony.imaging.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.HashMap;
import java.util.Set;

/* loaded from: classes.dex */
public class BackUpUtil {
    private static final String TAG = "BackUpUtil";
    private static BackUpUtil mBackUpUtil = new BackUpUtil();
    private Context mContext;
    private int mDefaultValues;
    private HashMap<String, Object> mHashMap;
    private SharedPreferences mRevertable;
    private SharedPreferences mSp;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum MODEL {
        INT,
        STRING,
        BOOLEAN,
        LONG,
        FLOAT,
        UNKNOWN
    }

    public static BackUpUtil getInstance() {
        return mBackUpUtil;
    }

    private BackUpUtil() {
    }

    public void Init(Context context) {
        this.mContext = context;
        this.mRevertable = PreferenceManager.getDefaultSharedPreferences(context);
        this.mHashMap = new HashMap<>();
        this.mSp = context.getSharedPreferences("unrevertable", 0);
    }

    public void setDefaultValues(int resId) {
        if (resId != 0) {
            PreferenceManager.setDefaultValues(this.mContext, resId, true);
            this.mDefaultValues = resId;
        }
    }

    public void reset() {
        this.mHashMap.clear();
        SharedPreferences.Editor e = this.mRevertable.edit();
        e.clear();
        e.commit();
        setDefaultValues(this.mDefaultValues);
    }

    public void resetAll() {
        reset();
        this.mHashMap.clear();
        SharedPreferences.Editor e = this.mSp.edit();
        e.clear();
        e.commit();
    }

    public void removePreference(String key) {
        if (this.mHashMap.containsKey(key)) {
            this.mHashMap.remove(key);
        }
        if (this.mRevertable.contains(key)) {
            this.mRevertable.edit().remove(key).commit();
        }
        if (this.mSp.contains(key)) {
            this.mSp.edit().remove(key).commit();
        }
    }

    public boolean setPreference(String key, Object value) {
        MODEL input = checkModel(value);
        if (input == MODEL.UNKNOWN) {
            Log.e(TAG, "set preference value is unknown model");
            return false;
        }
        this.mHashMap.put(key, value);
        return true;
    }

    public int getPreferenceInt(String key, int def) {
        int ret;
        if (this.mHashMap.containsKey(key)) {
            Object ob = this.mHashMap.get(key);
            if (checkModel(ob) != MODEL.INT) {
                return def;
            }
            return ((Integer) ob).intValue();
        }
        if (this.mRevertable.contains(key)) {
            try {
                ret = this.mRevertable.getInt(key, def);
            } catch (ClassCastException e) {
                ret = Integer.parseInt(this.mRevertable.getString(key, String.valueOf(def)));
            }
        } else {
            ret = this.mSp.getInt(key, def);
        }
        return ret;
    }

    public String getPreferenceString(String key, String def) {
        if (this.mHashMap.containsKey(key)) {
            Object ob = this.mHashMap.get(key);
            if (checkModel(ob) != MODEL.STRING) {
                return def;
            }
            return (String) ob;
        }
        String ret = this.mRevertable.contains(key) ? this.mRevertable.getString(key, def) : this.mSp.getString(key, def);
        return ret;
    }

    public boolean getPreferenceBoolean(String key, boolean def) {
        if (this.mHashMap.containsKey(key)) {
            Object ob = this.mHashMap.get(key);
            if (checkModel(ob) != MODEL.BOOLEAN) {
                return def;
            }
            return ((Boolean) ob).booleanValue();
        }
        boolean ret = this.mRevertable.contains(key) ? this.mRevertable.getBoolean(key, def) : this.mSp.getBoolean(key, def);
        return ret;
    }

    public long getPreferenceLong(String key, long def) {
        if (this.mHashMap.containsKey(key)) {
            Object ob = this.mHashMap.get(key);
            if (checkModel(ob) != MODEL.LONG) {
                return def;
            }
            return ((Long) ob).longValue();
        }
        long ret = this.mRevertable.contains(key) ? this.mRevertable.getLong(key, def) : this.mSp.getLong(key, def);
        return ret;
    }

    public float getPreferenceFloat(String key, float def) {
        if (this.mHashMap.containsKey(key)) {
            Object ob = this.mHashMap.get(key);
            if (checkModel(ob) != MODEL.FLOAT) {
                return def;
            }
            return ((Float) ob).floatValue();
        }
        float ret = this.mRevertable.contains(key) ? this.mRevertable.getFloat(key, def) : this.mSp.getFloat(key, def);
        return ret;
    }

    private MODEL checkModel(Object item) {
        if (item instanceof Integer) {
            MODEL model = MODEL.INT;
            return model;
        }
        if (item instanceof String) {
            MODEL model2 = MODEL.STRING;
            return model2;
        }
        if (item instanceof Boolean) {
            MODEL model3 = MODEL.BOOLEAN;
            return model3;
        }
        if (item instanceof Long) {
            MODEL model4 = MODEL.LONG;
            return model4;
        }
        if (item instanceof Float) {
            MODEL model5 = MODEL.FLOAT;
            return model5;
        }
        MODEL model6 = MODEL.UNKNOWN;
        return model6;
    }

    public boolean finishSettings(boolean asyncAct) {
        if (!asyncAct) {
            return preferenceWrite();
        }
        new Thread(new PreferenceWriteThread()).start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean preferenceWrite() {
        if (this.mHashMap == null) {
            return false;
        }
        Set<String> set = this.mHashMap.keySet();
        SharedPreferences.Editor editRevertable = this.mRevertable.edit();
        SharedPreferences.Editor editNormal = this.mSp.edit();
        for (String key : set) {
            Object item = this.mHashMap.get(key);
            switch (checkModel(item)) {
                case INT:
                    if (this.mRevertable.contains(key)) {
                        editRevertable.putString(key, String.valueOf(item));
                        break;
                    } else {
                        editNormal.putInt(key, ((Integer) item).intValue());
                        break;
                    }
                case STRING:
                    if (this.mRevertable.contains(key)) {
                        editRevertable.putString(key, (String) item);
                        break;
                    } else {
                        editNormal.putString(key, (String) item);
                        break;
                    }
                case BOOLEAN:
                    if (this.mRevertable.contains(key)) {
                        editRevertable.putBoolean(key, ((Boolean) item).booleanValue());
                        break;
                    } else {
                        editNormal.putBoolean(key, ((Boolean) item).booleanValue());
                        break;
                    }
                case LONG:
                    if (this.mRevertable.contains(key)) {
                        editRevertable.putLong(key, ((Long) item).longValue());
                        break;
                    } else {
                        editNormal.putLong(key, ((Long) item).longValue());
                        break;
                    }
                case FLOAT:
                    if (this.mRevertable.contains(key)) {
                        editRevertable.putFloat(key, ((Float) item).floatValue());
                        break;
                    } else {
                        editNormal.putFloat(key, ((Float) item).floatValue());
                        break;
                    }
                case UNKNOWN:
                    Log.v(TAG, "Error Invalid PreferenceModel");
                    break;
            }
        }
        boolean ret = editRevertable.commit();
        boolean ret2 = ret & editNormal.commit();
        this.mHashMap.clear();
        this.mHashMap = null;
        return ret2;
    }

    /* loaded from: classes.dex */
    class PreferenceWriteThread implements Runnable {
        PreferenceWriteThread() {
        }

        @Override // java.lang.Runnable
        public void run() {
            BackUpUtil.this.preferenceWrite();
        }
    }
}
