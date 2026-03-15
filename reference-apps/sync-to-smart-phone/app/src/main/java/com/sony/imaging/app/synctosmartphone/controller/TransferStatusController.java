package com.sony.imaging.app.synctosmartphone.controller;

import com.sony.imaging.app.base.menu.IController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TransferStatusController implements IController {
    public static final String TRANSFER_STATUS = "－";
    private String mTranseferStatus = TRANSFER_STATUS;
    private static final String TAG = TransferStatusController.class.getSimpleName();
    private static TransferStatusController mInstance = null;

    public static TransferStatusController getInstance() {
        if (mInstance == null) {
            mInstance = new TransferStatusController();
        }
        return mInstance;
    }

    private static void setController(TransferStatusController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected TransferStatusController() {
        setController(this);
    }

    public void setValue(String value) {
        setValue(null, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        this.mTranseferStatus = value;
    }

    public String getValue() {
        return getValue(null);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        return this.mTranseferStatus;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (1 != 0) {
            List<String> supported = new ArrayList<>();
            supported.add(TRANSFER_STATUS);
            return supported;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        availables.add(TRANSFER_STATUS);
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
