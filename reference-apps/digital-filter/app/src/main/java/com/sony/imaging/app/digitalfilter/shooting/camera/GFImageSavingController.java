package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFImageSavingController extends AbstractController {
    public static final String ALL = "gf-all-image";
    public static final String COMPOSIT = "gf-composit-image";
    public static final String SAVE = "gf-save-setting";
    private static final String TAG = AppLog.getClassName();
    private static GFImageSavingController mInstance = null;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(SAVE);
        sSupportedList.add(ALL);
        sSupportedList.add(COMPOSIT);
    }

    public static GFImageSavingController getInstance() {
        if (mInstance == null) {
            mInstance = createInstance();
        }
        return mInstance;
    }

    private static GFImageSavingController createInstance() {
        if (mInstance == null) {
            mInstance = new GFImageSavingController();
        }
        return mInstance;
    }

    private GFImageSavingController() {
        this.mBackupUtil = null;
        this.mBackupUtil = BackUpUtil.getInstance();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (tag.equals(SAVE)) {
            this.mBackupUtil.setPreference(SAVE, value);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return isSupportedImageSaving() ? this.mBackupUtil.getPreferenceString(SAVE, ALL) : this.mBackupUtil.getPreferenceString(SAVE, COMPOSIT);
    }

    public String getValue() {
        return getValue(SAVE);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (isSupportedImageSaving()) {
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

    public boolean isSupportedImageSaving() {
        return GFWhiteBalanceController.getInstance().isSupportedABGM();
    }
}
