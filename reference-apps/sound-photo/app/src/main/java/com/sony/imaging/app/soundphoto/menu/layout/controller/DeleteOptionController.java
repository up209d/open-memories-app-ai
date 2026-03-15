package com.sony.imaging.app.soundphoto.menu.layout.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DeleteOptionController extends AbstractController {
    public static final String DELETE_IMAGE = "delete_image";
    public static final String DELETE_ONLY_AUDIO = "delete_only_audio";
    public static final String DETELE_SELECTOR = "ApplicationTop";
    private static final String TAG = "AutoPlayBackStatusController";
    private static DeleteOptionController mInstance;
    private static boolean sDeleteImageOnlySelected = true;
    private static ArrayList<String> sSupportedList;
    private BackUpUtil mBackupUtil;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(DETELE_SELECTOR);
        sSupportedList.add(DELETE_IMAGE);
        sSupportedList.add(DELETE_ONLY_AUDIO);
    }

    public static DeleteOptionController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        sDeleteImageOnlySelected = BackUpUtil.getInstance().getPreferenceBoolean(SPBackUpKey.DELETE_STATUS_KEY, false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static DeleteOptionController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new DeleteOptionController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private DeleteOptionController() {
        this.mBackupUtil = null;
        if (this.mBackupUtil == null) {
            this.mBackupUtil = BackUpUtil.getInstance();
        }
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        if (DETELE_SELECTOR.equals(tag)) {
            if (DELETE_IMAGE.equals(value)) {
                sDeleteImageOnlySelected = true;
            } else {
                sDeleteImageOnlySelected = false;
            }
            this.mBackupUtil.setPreference(SPBackUpKey.DELETE_STATUS_KEY, Boolean.valueOf(sDeleteImageOnlySelected));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String value = DELETE_ONLY_AUDIO;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (DETELE_SELECTOR.equals(tag)) {
            sDeleteImageOnlySelected = this.mBackupUtil.getPreferenceBoolean(SPBackUpKey.DELETE_STATUS_KEY, false);
            if (sDeleteImageOnlySelected) {
                value = DELETE_IMAGE;
            } else {
                value = DELETE_ONLY_AUDIO;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        if (DatabaseUtil.checkMediaStatus() == DatabaseUtil.MediaStatus.READ_ONLY) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    public boolean isDeleteImageSelectd() {
        return sDeleteImageOnlySelected;
    }
}
