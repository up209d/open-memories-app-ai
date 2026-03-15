package com.sony.imaging.app.synctosmartphone.controller;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TransferImageSizeController implements IController {
    private static final String TRANSFER_IMAGE_SIZE_2M = "2M";
    private static final String TRANSFER_IMAGE_SIZE_ORIGINAL = "Original";
    private static final String TAG = TransferImageSizeController.class.getSimpleName();
    private static TransferImageSizeController mInstance = null;

    public static TransferImageSizeController getInstance() {
        if (mInstance == null) {
            mInstance = new TransferImageSizeController();
        }
        return mInstance;
    }

    private static void setController(TransferImageSizeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected TransferImageSizeController() {
        setController(this);
    }

    public void setValue(String value) {
        setValue(null, value);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (value.equals(TRANSFER_IMAGE_SIZE_ORIGINAL)) {
            SyncBackUpUtil.getInstance().setImageSize(0);
        } else {
            SyncBackUpUtil.getInstance().setImageSize(2097152);
        }
        Log.v(TAG, "setValue = " + value);
    }

    public String getValue() {
        return getValue(null);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String value;
        int imageSize = SyncBackUpUtil.getInstance().getImageSize(2097152);
        if (imageSize == 0) {
            value = TRANSFER_IMAGE_SIZE_ORIGINAL;
        } else {
            value = TRANSFER_IMAGE_SIZE_2M;
        }
        Log.d(TAG, "getValue = " + value);
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (1 != 0) {
            List<String> supported = new ArrayList<>();
            supported.add(TRANSFER_IMAGE_SIZE_2M);
            supported.add(TRANSFER_IMAGE_SIZE_ORIGINAL);
            return supported;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> availables = new ArrayList<>();
        availables.add(TRANSFER_IMAGE_SIZE_2M);
        availables.add(TRANSFER_IMAGE_SIZE_ORIGINAL);
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
