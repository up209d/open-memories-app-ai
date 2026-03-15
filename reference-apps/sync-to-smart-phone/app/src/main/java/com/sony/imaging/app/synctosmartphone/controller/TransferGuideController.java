package com.sony.imaging.app.synctosmartphone.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TransferGuideController implements IController {
    public static final String GUIDE = "";
    private static final String TAG = TransferGuideController.class.getSimpleName();
    private static TransferGuideController mInstance = null;
    private String mGuide = "";

    public static TransferGuideController getInstance() {
        if (mInstance == null) {
            mInstance = new TransferGuideController();
        }
        return mInstance;
    }

    private static void setController(TransferGuideController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected TransferGuideController() {
        setController(this);
    }

    public void setValue(String value) {
        setValue(null, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        this.mGuide = value;
    }

    public String getValue() {
        return getValue(null);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        return this.mGuide;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        boolean isSupported;
        String version = ScalarProperties.getString("version.platform");
        int pfVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        int apiVersion = Integer.parseInt(version.substring(version.indexOf(".") + 1));
        int uiFastSuspendSupport = ScalarProperties.getInt("ui.fast.suspend.supported");
        if (pfVersion <= 2 && apiVersion < 6) {
            isSupported = true;
        } else if (uiFastSuspendSupport == 0) {
            isSupported = true;
        } else {
            isSupported = false;
        }
        if (isSupported) {
            List<String> supported = new ArrayList<>();
            supported.add("");
            return supported;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        availables.add("");
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        return 0;
    }
}
