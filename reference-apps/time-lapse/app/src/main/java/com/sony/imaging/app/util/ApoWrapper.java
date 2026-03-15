package com.sony.imaging.app.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* loaded from: classes.dex */
public class ApoWrapper {
    private static final String ACTION_NAME = "com.android.server.DAConnectionManagerService.apo";
    private static final String DEV_TYPE_NO_APO = "APO/NO";
    private static final String DEV_TYPE_SPECIAL = "APO/SPECIAL";
    private static final String EXTRA_NAME = "apo_info";
    private static final String LOG_APO_TYPE = "setApoType : ";
    private static final String LOG_APO_TYPE_FIXED = "ApoType is fixed: ";
    private static final String LOG_FIX_APO_TYPE = "fixApoType : ";
    private static final String TAG = "ApoWrapper";
    private Context mContext = null;
    private static ApoWrapper mInstance = new ApoWrapper();
    private static NotificationListener mBatteryObserverListener = new NotificationListener() { // from class: com.sony.imaging.app.util.ApoWrapper.1
        private String[] TAGS = {BatteryObserver.TAG_PLUGGED};

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ApoWrapper.update();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    };
    private static final String DEV_TYPE_NORMAL = "APO/NORMAL";
    private static String mLatestType = DEV_TYPE_NORMAL;
    private static APO_TYPE mFixedType = APO_TYPE.UNKNOWN;

    /* loaded from: classes.dex */
    public enum APO_TYPE {
        UNKNOWN,
        NONE,
        NORMAL,
        SPECIAL
    }

    private ApoWrapper() {
    }

    public static boolean isNoApoWhenAcPlugged() {
        return false;
    }

    public static void initialize(Context context) {
        mInstance.mContext = context;
        if (isNoApoWhenAcPlugged()) {
            BatteryObserver.setNotificationListener(mBatteryObserverListener);
        }
        if (!APO_TYPE.UNKNOWN.equals(mFixedType)) {
            postIntent(toDevValue(mFixedType));
        }
    }

    public static void terminate() {
        if (isNoApoWhenAcPlugged()) {
            BatteryObserver.removeNotificationListener(mBatteryObserverListener);
        }
    }

    public static void fixApoType(APO_TYPE type) {
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        Log.d(TAG, builder.replace(0, builder.length(), LOG_FIX_APO_TYPE).append(type).toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        mFixedType = type;
        if (APO_TYPE.UNKNOWN.equals(type)) {
            update();
        } else {
            postIntent(toDevValue(type));
        }
    }

    public static boolean isApoTypeFixed() {
        return !APO_TYPE.UNKNOWN.equals(mFixedType);
    }

    public static void clearFixedApoType() {
        fixApoType(APO_TYPE.UNKNOWN);
    }

    public static boolean setApoType(APO_TYPE type) {
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        Log.d(TAG, builder.replace(0, builder.length(), LOG_APO_TYPE).append(type).toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        String dev_value = toDevValue(type);
        mLatestType = dev_value;
        update();
        return APO_TYPE.UNKNOWN != type;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void update() {
        if (!isApoTypeFixed()) {
            if (isNoApoWhenAcPlugged()) {
                int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
                if (plugged == 1) {
                    postIntent(DEV_TYPE_NO_APO);
                    return;
                } else {
                    if (mLatestType != null) {
                        postIntent(mLatestType);
                        return;
                    }
                    return;
                }
            }
            if (mLatestType != null) {
                postIntent(mLatestType);
                return;
            }
            return;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        Log.i(TAG, builder.replace(0, builder.length(), LOG_APO_TYPE_FIXED).append(mFixedType).toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    private static void postIntent(String value) {
        Intent intent = new Intent();
        intent.setAction(ACTION_NAME);
        intent.putExtra(EXTRA_NAME, value);
        mInstance.mContext.sendBroadcast(intent);
    }

    private static String toDevValue(APO_TYPE type) {
        switch (type) {
            case NONE:
                return DEV_TYPE_NO_APO;
            case NORMAL:
                return DEV_TYPE_NORMAL;
            case SPECIAL:
                return DEV_TYPE_SPECIAL;
            default:
                return null;
        }
    }
}
