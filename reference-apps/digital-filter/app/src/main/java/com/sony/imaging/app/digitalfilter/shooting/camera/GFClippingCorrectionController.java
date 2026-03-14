package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFClippingCorrectionController extends AbstractController {
    public static final String CLIPPING_CORRECTION = "clipping-correction";
    public static final String LOW = "clipping-correction-low";
    public static final String NORMAL = "clipping-correction-std";
    public static final String OFF = "clipping-correction-off";
    private static final String TAG = AppLog.getClassName();
    private static GFClippingCorrectionController mInstance;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(CLIPPING_CORRECTION);
        sSupportedList.add(OFF);
        sSupportedList.add(LOW);
        sSupportedList.add(NORMAL);
    }

    public static GFClippingCorrectionController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFClippingCorrectionController createInstance() {
        if (mInstance == null) {
            mInstance = new GFClippingCorrectionController();
        }
        return mInstance;
    }

    private GFClippingCorrectionController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(CLIPPING_CORRECTION)) {
            this.mGFBackUpKey.saveClippingCorrectionValue(value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mGFBackUpKey.getClippingCorrectionValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (isSupportedClippingCorrection()) {
            return sSupportedList;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isSupportedClippingCorrection() {
        return false;
    }
}
