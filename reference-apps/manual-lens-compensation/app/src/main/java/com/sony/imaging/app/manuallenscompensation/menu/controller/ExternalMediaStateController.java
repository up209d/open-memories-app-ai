package com.sony.imaging.app.manuallenscompensation.menu.controller;

import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ExternalMediaStateController extends AbstractController {
    private static final String CAUTION_ID = " Caution ID = ";
    public static final String DELETE_EXTERNAL_PROFILE = "DeleteExternalProfile";
    private static final String INH_ID_PB_MODIFY = "INH_FEATURE_COMMON_PB_MODIFY";
    private static final String MEMORY_CARD_EXISTANCE_STATE = " is external media Exist = ";
    private static final String MEMORY_CARD_LOCK_STATE = " Memory Card Locked = ";
    private static final String TAG = "ExternalMediaStateController";
    private static ExternalMediaStateController mInstance;
    private static ArrayList<String> sSupportedList;
    private int mCautionID = -1;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add(DELETE_EXTERNAL_PROFILE);
    }

    public static ExternalMediaStateController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static ExternalMediaStateController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new ExternalMediaStateController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return tag;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean isAvailable = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (getCautionID() == -1) {
            isAvailable = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isAvailable;
    }

    public void setCautionID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCautionID = -1;
        if (isMemoryCardNotExist()) {
            this.mCautionID = OCInfo.CAUTION_ID_DLAPP_NO_MEMORY_CARD_INSERTED;
        } else if (isMemoryCardLocked()) {
            this.mCautionID = OCInfo.CAUTION_ID_DLAPP_MEMORY_CARD_LOCKED;
        } else if (OCUtil.getInstance().getFileArray().size() == 0) {
            this.mCautionID = OCInfo.CAUTION_ID_DLAPP_NO_LENS_PROFILE;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + CAUTION_ID + this.mCautionID);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public int getCautionID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, AppLog.getMethodName() + "  " + this.mCautionID);
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCautionID;
    }

    private boolean isMemoryCardLocked() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isMemoryCardLocked = false;
        if (MediaNotificationManager.getInstance().isMounted()) {
            String[] media = AvindexStore.getExternalMediaIds();
            AvailableInfo.update();
            isMemoryCardLocked = AvailableInfo.isInhibition(INH_ID_PB_MODIFY, media[0]);
        }
        AppLog.exit(TAG, AppLog.getMethodName() + MEMORY_CARD_LOCK_STATE + isMemoryCardLocked);
        AppLog.exit(TAG, AppLog.getMethodName());
        return isMemoryCardLocked;
    }

    public boolean isMemoryCardNotExist() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isMemoryCardNotExist = false;
        if (MediaNotificationManager.getInstance().getMediaState() != 2) {
            isMemoryCardNotExist = true;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + MEMORY_CARD_EXISTANCE_STATE + isMemoryCardNotExist);
        AppLog.exit(TAG, AppLog.getMethodName());
        return isMemoryCardNotExist;
    }
}
