package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFVerticalLinkController extends AbstractController {
    public static final String BOUNDARY_MODE = "vertical-link";
    public static final String MANUAL = "vertical-link-manual";
    private static final String TAG = AppLog.getClassName();
    public static final String VERTICAL = "vertical-link-on";
    private static GFVerticalLinkController mInstance;
    private static ArrayList<String> sSupportedList;
    private GFBackUpKey mGFBackUpKey;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(BOUNDARY_MODE);
        sSupportedList.add(MANUAL);
        sSupportedList.add(VERTICAL);
    }

    public static GFVerticalLinkController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFVerticalLinkController createInstance() {
        if (mInstance == null) {
            mInstance = new GFVerticalLinkController();
        }
        return mInstance;
    }

    private GFVerticalLinkController() {
        this.mGFBackUpKey = null;
        this.mGFBackUpKey = GFBackUpKey.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(BOUNDARY_MODE)) {
            this.mGFBackUpKey.saveVerticalLinkValue(value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mGFBackUpKey.getVerticalLinkValue();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (hasDigitalLevel()) {
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

    public boolean hasDigitalLevel() {
        boolean isSupporeted = ScalarProperties.getInt("device.digital.level") != 0;
        return isSupporeted && GyroscopeObserver.getInstance().hasDigitalLevel();
    }
}
