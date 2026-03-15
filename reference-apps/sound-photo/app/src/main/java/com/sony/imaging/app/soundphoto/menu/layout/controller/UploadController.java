package com.sony.imaging.app.soundphoto.menu.layout.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager;
import com.sony.imaging.app.soundphoto.util.AppLog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class UploadController extends AbstractController implements DirectUploadUtilManager.DirectUploadInfoListener {
    private static final String TAG = "AutoPlayBackStatusController";
    public static final String UPLOAD_SELECTOR = "ApplicationSettings";
    private static UploadController mInstance;
    private static ArrayList<String> sSupportedList;
    private boolean isSupported = false;
    private boolean isInstalled = false;

    static {
        if (sSupportedList == null) {
            sSupportedList = new ArrayList<>();
        }
        sSupportedList.add("ApplicationSettings");
    }

    public static UploadController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = createInstance();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private static UploadController createInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new UploadController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    private UploadController() {
        getSupportedValue(TAG);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "setValue  tag = " + tag + "  value =  " + value);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        Boolean isSupportedUpload = DirectUploadUtilManager.getInstance().isSupported();
        Boolean isInstalledUpload = DirectUploadUtilManager.getInstance().isInstalled();
        if (isSupportedUpload == null) {
            DirectUploadUtilManager.getInstance().registerInfoListener(this);
        } else {
            this.isSupported = isSupportedUpload.booleanValue();
            this.isInstalled = isInstalledUpload.booleanValue();
        }
        return this.isSupported && this.isInstalled;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return sSupportedList;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager.DirectUploadInfoListener
    public void onInstalledInfoObtainied(boolean isInstalled) {
        this.isInstalled = isInstalled;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager.DirectUploadInfoListener
    public void onSupportedInfoObtained(boolean isSupported) {
        this.isSupported = isSupported;
    }
}
