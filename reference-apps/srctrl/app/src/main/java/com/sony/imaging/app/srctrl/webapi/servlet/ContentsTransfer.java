package com.sony.imaging.app.srctrl.webapi.servlet;

import android.util.Log;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferData;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlPlaybackUtil;
import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public class ContentsTransfer extends FileTransfer {
    private static final long serialVersionUID = 1;
    private static final String TAG = ContentsTransfer.class.getSimpleName();
    private static long mFileSize = 0;
    private static ExecutorService mExecutor = null;
    private static volatile boolean transferFlg = false;
    private static ContentsTransfer mInstance = null;

    protected ContentsTransfer() {
        mInstance = this;
    }

    public static ContentsTransfer getInstance() {
        if (mInstance == null) {
            new ContentsTransfer();
        }
        return mInstance;
    }

    public void initialize() {
        transferFlg = true;
        mExecutor = mInstance.initial();
    }

    public void terminate() {
        transferFlg = false;
        mInstance.terminal(mExecutor);
        mExecutor = null;
    }

    private String getCanonicalUrl(String tmpUrl) {
        File tmpUrlFile = new File(tmpUrl);
        try {
            String url = tmpUrlFile.getCanonicalPath();
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected synchronized InputStream getContentStream(String url) {
        InputStream resizeImage;
        String decUrl = SRCtrlPlaybackUtil.urlDecode(url);
        if (decUrl == null) {
            Log.v(TAG, "It fails to decode content URL.");
            resizeImage = null;
        } else {
            Log.v(TAG, "getContentStream:encUrl:" + url);
            Log.v(TAG, "getContentStream:decUrl:" + decUrl);
            PrepareTransferData transData = PrepareTransferData.getInstance();
            String imageType = SRCtrlPlaybackUtil.getContentType(url);
            String filePath = transData.getFilePath(imageType, decUrl);
            if (SRCtrlConstants.IMAGE_TYPE_SMALL.equals(imageType)) {
                mFileSize = PrepareTransferData.getInstance().beginResizeVgaImage(filePath, decUrl);
            } else {
                mFileSize = PrepareTransferData.getInstance().beginResizeImage(filePath, imageType, true, decUrl, true, true);
            }
            resizeImage = PrepareTransferData.getInstance().getResizeImage();
        }
        return resizeImage;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    ExecutorService getExecute() {
        return mExecutor;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    boolean enableTransfer() {
        return transferFlg;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    InputStream getInputStream(HttpRequest req, HttpResponse res) {
        InputStream in = null;
        String canonicalUrl = getCanonicalUrl(req.getRequestURI());
        if (canonicalUrl == null) {
            Log.v(TAG, "HttpServletRequest has a malformed URI.");
        } else {
            in = getContentStream(canonicalUrl);
            if (in == null) {
                Log.v(TAG, "Inputstream for \"" + canonicalUrl + "\" is null.");
            } else {
                res.setHeader(HttpDefs.HEADER_CONTENT_LENGTH, Long.toString(mFileSize));
                Log.v(TAG, "setContentLength = " + mFileSize + " bytes");
            }
        }
        return in;
    }
}
