package com.sony.imaging.app.soundphoto.playback.upload.util;

import android.app.Activity;
import android.net.Uri;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.snsdirect.util.DirectUploadManager;
import com.sony.imaging.app.snsdirect.util.MimeType;
import com.sony.imaging.app.soundphoto.util.AppLog;

/* loaded from: classes.dex */
public class UploadDataManager {
    private static final String TAG = "UploadDataManager";
    private static UploadDataManager mUploadDataManager;

    private UploadDataManager() {
    }

    public static UploadDataManager getInstance() {
        if (mUploadDataManager == null) {
            mUploadDataManager = new UploadDataManager();
        }
        AppLog.trace(TAG, "=========get Instance UploadDataManager ========");
        return mUploadDataManager;
    }

    public void uploadData(Activity activity, ContentsIdentifier id) {
        Uri uri = id.getContentUri();
        DirectUploadManager dManager = new DirectUploadManager();
        boolean isAvailable = dManager.isTypeSupported(activity.getApplicationContext(), MimeType.IMAGE_SPHOTO);
        if (isAvailable) {
            dManager.launch(activity, MimeType.IMAGE_SPHOTO, uri, null);
        } else {
            AppLog.trace(TAG, "Direct Upload which supports ACTION_SEND and input MimeType is not installed");
        }
    }

    public void uploadData(Activity activity, Object id) {
        DirectUploadManager dManager = new DirectUploadManager();
        boolean isAvailable = dManager.isTypeSupported(activity.getApplicationContext(), MimeType.IMAGE_SPHOTO);
        if (isAvailable) {
            dManager.launch(activity, MimeType.IMAGE_SPHOTO, null, null);
        } else {
            AppLog.trace(TAG, "Direct Upload which supports ACTION_SEND and input MimeType is not installed");
        }
    }
}
