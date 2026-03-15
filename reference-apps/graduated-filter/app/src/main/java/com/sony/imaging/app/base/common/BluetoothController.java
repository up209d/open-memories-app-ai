package com.sony.imaging.app.base.common;

import com.sony.imaging.app.base.common.widget.BluetoothNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class BluetoothController implements IController {
    private static BluetoothController sInstance = new BluetoothController();
    private String TAG = BluetoothController.class.getSimpleName();

    public static BluetoothController getInstance() {
        return sInstance;
    }

    public boolean isDeviceSupportedBluetooth() {
        if (!Environment.isBluetoothAPISupported() || 1 != ScalarProperties.getInt("device.bluetooth.supported")) {
            return false;
        }
        return true;
    }

    public int getValueInt(String tag) {
        int ValueInt = BluetoothNotificationManager.getInstance().getBluetoothInfo(tag);
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
