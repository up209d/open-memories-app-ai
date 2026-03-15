package com.sony.imaging.app.srctrl.webapi.servlet;

import android.util.Log;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferData;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public class ContinuousResourceLoader extends FileTransfer {
    public static final String CONT_SHOOT_PATH = "/mnt/sdcard/";
    public static final String CONT_SHOOT_SIZE = "?size=";
    public static final String CONT_SHOOT_SIZE_ORIGIN = "Origin";
    public static final String CONT_SHOOT_SIZE_SCREENNAIL = "Scn";
    public static final String CONT_SHOOT_SIZE_THUMBNAIL = "Thumbnail";
    private static final long serialVersionUID = 1;
    private static final String TAG = ContinuousResourceLoader.class.getSimpleName();
    private static double mFileSize = 0.0d;
    private static ExecutorService mExecutor = null;
    private static ContinuousResourceLoader mInstance = null;

    protected ContinuousResourceLoader() {
        mInstance = this;
    }

    public static ContinuousResourceLoader getInstance() {
        if (mInstance == null) {
            new ContinuousResourceLoader();
        }
        return mInstance;
    }

    public void contTransferCancel(boolean isCancel) {
        mInstance.transferCancel(isCancel);
    }

    public void initialize() {
        mExecutor = mInstance.initial();
    }

    public void terminate() {
        mInstance.terminal(mExecutor);
        mExecutor = null;
    }

    protected synchronized InputStream getContentStream(String filePath, String size) {
        InputStream input;
        String reSize;
        Log.v(TAG, "getContentStream " + filePath + StringBuilderThreadLocal.SLASH + size);
        input = null;
        if ("Origin".equals(size)) {
            reSize = SRCtrlConstants.IMAGE_TYPE_ORGJPEG;
        } else if ("Scn".equals(size)) {
            reSize = "Scn";
        } else if ("Thumbnail".equals(size)) {
            reSize = "Thumbnail";
        } else {
            Log.e(TAG, "Invalid string " + size);
            reSize = null;
        }
        if (filePath != null && reSize != null) {
            mFileSize = PrepareTransferData.getInstance().beginResizeImage(filePath, reSize, false, null, false, false);
            if (0.0d < mFileSize) {
                input = PrepareTransferData.getInstance().getResizeImage();
            }
        }
        return input;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    ExecutorService getExecute() {
        return mExecutor;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    boolean enableTransfer() {
        return StateController.ServerStatus.IDLE.equals(StateController.getInstance().getServerStatus()) || StateController.ServerStatus.STILL_SAVING.equals(StateController.getInstance().getServerStatus());
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    InputStream getInputStream(HttpRequest req, HttpResponse res) {
        String reqUrl = req.getRequestURI();
        String filePath = CONT_SHOOT_PATH + reqUrl.substring(reqUrl.indexOf(SRCtrlConstants.SERVLET_ROOT_PATH_CONTINUOUS) + SRCtrlConstants.SERVLET_ROOT_PATH_CONTINUOUS.length(), reqUrl.indexOf("?size="));
        String size = reqUrl.substring(reqUrl.indexOf("?size=") + "?size=".length());
        InputStream in = getContentStream(filePath, size);
        if (in == null) {
            Log.v(TAG, "Inputstream for \"" + filePath + "\" is null.");
        } else {
            res.setContentLength((int) mFileSize);
            Log.v(TAG, "setContentLength = " + mFileSize + " bytes");
        }
        return in;
    }
}
