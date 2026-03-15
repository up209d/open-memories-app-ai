package com.sony.imaging.app.base.common;

import com.sony.imaging.app.base.common.widget.GpsNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class GpsController implements IController {
    private static GpsController sInstance = new GpsController();
    private String TAG = GpsController.class.getSimpleName();

    public static GpsController getInstance() {
        return sInstance;
    }

    public boolean isDeviceSupportedGps() {
        if (!Environment.isReceiveGpsData()) {
            return false;
        }
        if (1 != ScalarProperties.getInt("device.gps.supported") && 2 != ScalarProperties.getInt("device.gps.supported")) {
            return false;
        }
        return true;
    }

    public int getValueInt(String tag) {
        int ValueInt = GpsNotificationManager.getInstance().getGpsInfo(tag);
        return ValueInt;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        return 0;
    }
}
