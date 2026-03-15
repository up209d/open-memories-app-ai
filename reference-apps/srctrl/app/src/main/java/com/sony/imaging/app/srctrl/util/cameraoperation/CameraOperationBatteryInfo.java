package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationBatteryInfo {
    private static final String ADDITIONAL_STATUS_BATTERY_NEAR_END = "batteryNearEnd";
    private static final String ADDITIONAL_STATUS_CHARGING = "charging";
    private static final int BATTERY_LEVEL_0 = 0;
    private static final int BATTERY_LEVEL_1 = 1;
    private static final int BATTERY_LEVEL_2 = 2;
    private static final int BATTERY_LEVEL_3 = 3;
    private static final int BATTERY_LEVEL_4 = 4;
    private static final String EMPTY_STRING = "";
    private static final int ENABLE_GET_BATTERY_THRESHOLD_LIST = 2;
    private static final String ID_EXTERNAL_BATTERY_1 = "externalBattery1";
    private static final String ID_EXTERNAL_BATTERY_2 = "externalBattery2";
    private static final String ID_NO_BATTERY = "noBattery";
    private static final int INVALID_VALUE = -1;
    private static final int PREEND_LEVEL = 1;
    private static final String STATUS_ACTIVE = "active";
    private static final int STATUS_BATTERY_NEAR_END = 0;
    private static final String STATUS_INACTIVE = "inactive";
    private static final String STATUS_INVALID = "unknown";
    private static final String STR_ADDITIONAL_STATUS = ", additionalStatus=";
    private static final String STR_BATTERY_ID = "batteryID=";
    private static final String STR_DESCRIPTION = ", description=";
    private static final String STR_LEVEL_DENOM = ", levelDenominator=";
    private static final String STR_LEVEL_NUMER = ", levelNumer=";
    private static final String STR_STATUS = ", status=";
    private static final String TAG = CameraOperationBatteryInfo.class.getSimpleName();
    private static int[] batteryList = null;
    private static StringBuilder builder = new StringBuilder();
    private static WeakReference<NotificationListener> s_BatteryNotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_BatteryNotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationBatteryInfo.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{BatteryObserver.TAG_LEVEL, BatteryObserver.TAG_PLUGGED, BatteryObserver.TAG_STATUS, BatteryObserver.TAG_EXTRA_PRESENT};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    int plugged = BatteryObserver.getInt(BatteryObserver.TAG_PLUGGED);
                    int value = BatteryObserver.getInt(BatteryObserver.TAG_LEVEL);
                    int status = BatteryObserver.getInt(BatteryObserver.TAG_STATUS);
                    boolean isPresent = BatteryObserver.getBoolean(BatteryObserver.TAG_EXTRA_PRESENT);
                    int level = CameraOperationBatteryInfo.checkBatteryLevel(value);
                    String batteryID_api = CameraOperationBatteryInfo.ID_NO_BATTERY;
                    String status_api = "unknown";
                    String additionalStatus_api = "";
                    int levelNumer_api = -1;
                    int levelDenominator_api = -1;
                    if (isPresent) {
                        if (plugged == 1) {
                            batteryID_api = CameraOperationBatteryInfo.ID_EXTERNAL_BATTERY_1;
                        } else if (plugged == 2) {
                            if (status == 2) {
                                batteryID_api = CameraOperationBatteryInfo.ID_EXTERNAL_BATTERY_1;
                                status_api = CameraOperationBatteryInfo.STATUS_INACTIVE;
                                additionalStatus_api = CameraOperationBatteryInfo.ADDITIONAL_STATUS_CHARGING;
                            } else if (status == 3) {
                                batteryID_api = CameraOperationBatteryInfo.ID_EXTERNAL_BATTERY_1;
                                status_api = "active";
                                additionalStatus_api = CameraOperationBatteryInfo.ADDITIONAL_STATUS_CHARGING;
                                levelNumer_api = level;
                                levelDenominator_api = 4;
                            }
                        } else {
                            batteryID_api = CameraOperationBatteryInfo.ID_EXTERNAL_BATTERY_1;
                            status_api = "active";
                            levelNumer_api = level;
                            levelDenominator_api = 4;
                        }
                    }
                    boolean toBeNotified = ParamsGenerator.updateBatteryInfoParams(batteryID_api, status_api, additionalStatus_api, levelNumer_api, levelDenominator_api, "");
                    if (toBeNotified) {
                        ServerEventHandler.getInstance().onServerStatusChanged();
                        Log.v(CameraOperationBatteryInfo.TAG, CameraOperationBatteryInfo.builder.replace(0, CameraOperationBatteryInfo.builder.length(), CameraOperationBatteryInfo.STR_BATTERY_ID).append(batteryID_api).append(CameraOperationBatteryInfo.STR_STATUS).append(status_api).append(CameraOperationBatteryInfo.STR_ADDITIONAL_STATUS).append(additionalStatus_api).append(CameraOperationBatteryInfo.STR_LEVEL_NUMER).append(levelNumer_api).append(CameraOperationBatteryInfo.STR_LEVEL_DENOM).append(levelDenominator_api).append(CameraOperationBatteryInfo.STR_DESCRIPTION).append("").toString());
                    }
                }
            };
            s_BatteryNotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public static void setNotificationListener() {
        BatteryObserver.setNotificationListener(getNotificationListener());
    }

    public static void removeNotificationListener() {
        BatteryObserver.removeNotificationListener(getNotificationListener());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int checkBatteryLevel(int value) {
        int level;
        if (batteryList == null) {
            if (Environment.getVersionPfAPI() >= 2) {
                batteryList = ScalarProperties.getIntArray("ui.battery.threshold.list");
            } else {
                batteryList = new int[3];
                batteryList[0] = 80;
                batteryList[1] = 50;
                batteryList[2] = 20;
            }
        }
        if (1 > value) {
            level = 0;
        } else if (batteryList[2] >= value) {
            level = 1;
        } else if (batteryList[1] >= value) {
            level = 2;
        } else if (batteryList[0] >= value) {
            level = 3;
        } else {
            level = 4;
        }
        Log.i(TAG, "value:" + Integer.toString(value) + " Battery Level:" + Integer.toString(level));
        return level;
    }
}
