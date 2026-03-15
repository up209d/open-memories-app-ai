package com.sony.imaging.app.synctosmartphone.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TransferStartNumController implements IController {
    private static final String TRANSFER_IMAGE_NUM_1 = "1";
    private static final String TRANSFER_IMAGE_NUM_10 = "10";
    private static final String TRANSFER_IMAGE_NUM_20 = "20";
    private static final String TRANSFER_IMAGE_NUM_5 = "5";
    private static final String TAG = TransferStartNumController.class.getSimpleName();
    private static TransferStartNumController mInstance = null;

    public static TransferStartNumController getInstance() {
        if (mInstance == null) {
            mInstance = new TransferStartNumController();
        }
        return mInstance;
    }

    private static void setController(TransferStartNumController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected TransferStartNumController() {
        setController(this);
    }

    public void setValue(String value) {
        setValue(null, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (value.equals(TRANSFER_IMAGE_NUM_1)) {
            SyncBackUpUtil.getInstance().setImageStartNum(1);
        } else if (value.equals(TRANSFER_IMAGE_NUM_5)) {
            SyncBackUpUtil.getInstance().setImageStartNum(5);
        } else if (value.equals(TRANSFER_IMAGE_NUM_10)) {
            SyncBackUpUtil.getInstance().setImageStartNum(10);
        } else {
            SyncBackUpUtil.getInstance().setImageStartNum(20);
        }
        Log.v(TAG, "setValue = " + value);
    }

    public String getValue() {
        return getValue(null);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String value;
        int imageNum = SyncBackUpUtil.getInstance().getImageStartNum(1);
        if (1 == imageNum) {
            value = TRANSFER_IMAGE_NUM_1;
        } else if (5 == imageNum) {
            value = TRANSFER_IMAGE_NUM_5;
        } else if (10 == imageNum) {
            value = TRANSFER_IMAGE_NUM_10;
        } else {
            value = TRANSFER_IMAGE_NUM_20;
        }
        Log.d(TAG, "getValue = " + value);
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (1 != 0) {
            List<String> supported = new ArrayList<>();
            supported.add(TRANSFER_IMAGE_NUM_1);
            supported.add(TRANSFER_IMAGE_NUM_5);
            supported.add(TRANSFER_IMAGE_NUM_10);
            supported.add(TRANSFER_IMAGE_NUM_20);
            return supported;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        availables.add(TRANSFER_IMAGE_NUM_1);
        availables.add(TRANSFER_IMAGE_NUM_5);
        availables.add(TRANSFER_IMAGE_NUM_10);
        availables.add(TRANSFER_IMAGE_NUM_20);
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
