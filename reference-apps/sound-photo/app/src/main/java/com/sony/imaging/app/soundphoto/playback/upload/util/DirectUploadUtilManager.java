package com.sony.imaging.app.soundphoto.playback.upload.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.sony.imaging.app.snsdirect.util.DirectUploadManager;
import com.sony.imaging.app.snsdirect.util.MimeType;
import com.sony.imaging.app.snsdirect.util.MovieInfo;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class DirectUploadUtilManager {
    private static final String TAG = DirectUploadUtilManager.class.getSimpleName();
    private static DirectUploadUtilManager sManager = new DirectUploadUtilManager();
    private Context context;
    private DirectUploadManager dManager;
    private boolean isInitialized;
    private Boolean isInstalled;
    private Boolean isSupported;
    private DirectUploadInfoListener listener;
    private MimeType type;

    /* loaded from: classes.dex */
    public interface DirectUploadInfoListener {
        void onInstalledInfoObtainied(boolean z);

        void onSupportedInfoObtained(boolean z);
    }

    public static DirectUploadUtilManager getInstance() {
        return sManager;
    }

    public DirectUploadUtilManager() {
        clear();
    }

    public void clear() {
        this.isInstalled = null;
        this.isSupported = null;
        this.listener = null;
        this.context = null;
        this.type = null;
        this.isInitialized = false;
        this.dManager = null;
        Log.d(TAG, "Cleared");
    }

    public void initialize(Context context, MimeType type) {
        Log.d(TAG, "Start initializing");
        clear();
        this.dManager = new DirectUploadManager();
        this.context = context;
        this.type = type;
        this.isInitialized = true;
        Thread analyzingThread = new Thread("analyzingThread") { // from class: com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                DirectUploadUtilManager.this.isInstalled = Boolean.valueOf(DirectUploadUtilManager.this.dManager.isInstalled(DirectUploadUtilManager.this.context));
                Log.d(DirectUploadUtilManager.TAG, "isInstalled: " + DirectUploadUtilManager.this.isInstalled);
                if (DirectUploadUtilManager.this.listener != null) {
                    DirectUploadUtilManager.this.listener.onInstalledInfoObtainied(DirectUploadUtilManager.this.isInstalled.booleanValue());
                }
                DirectUploadUtilManager.this.isSupported = Boolean.valueOf(DirectUploadUtilManager.this.dManager.isTypeSupported(DirectUploadUtilManager.this.context, DirectUploadUtilManager.this.type));
                Log.d(DirectUploadUtilManager.TAG, "isSupported: " + DirectUploadUtilManager.this.isSupported);
                if (DirectUploadUtilManager.this.listener != null) {
                    DirectUploadUtilManager.this.listener.onSupportedInfoObtained(DirectUploadUtilManager.this.isSupported.booleanValue());
                }
            }
        };
        analyzingThread.start();
    }

    public Boolean isInstalled() {
        if (!this.isInitialized) {
            return null;
        }
        Log.d(TAG, "isInstalled Called: " + this.isInstalled);
        return this.isInstalled;
    }

    public Boolean isSupported() {
        if (!this.isInitialized) {
            return null;
        }
        Log.d(TAG, "isSupported Called: " + this.isSupported);
        return this.isSupported;
    }

    public boolean launch(Activity activity, Uri file, MovieInfo mInfo) {
        SPUtil.getInstance().printKikiLog(SPConstants.SOUND_PHOTO_DIRECT_UPLOAD_KIKILOG_ID);
        return this.dManager.launch(activity, this.type, file, mInfo);
    }

    public boolean launchMulti(Activity activity, ArrayList<Uri> fileList, MovieInfo mInfo) {
        SPUtil.getInstance().printKikiLog(SPConstants.SOUND_PHOTO_DIRECT_UPLOAD_KIKILOG_ID);
        return this.dManager.launchMulti(activity, this.type, fileList, mInfo);
    }

    public void registerInfoListener(DirectUploadInfoListener listener) {
        this.listener = listener;
    }
}
