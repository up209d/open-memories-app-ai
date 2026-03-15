package com.sony.imaging.app.srctrl.webapi.servlet;

import android.util.Log;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferData;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.util.Postview;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/* loaded from: classes.dex */
public class SingleResourceLoader extends FileTransfer {
    public static final String SINGLE_SHOOT_SIZE = "?size=";
    public static final String SINGLE_SHOOT_SIZE_ORIGIN = "Origin";
    public static final String SINGLE_SHOOT_SIZE_SCREENNAIL = "Scn";
    private static final String TAG = SingleResourceLoader.class.getSimpleName();
    private static ArrayList<Postview> postviews = new ArrayList<>();
    private static ExecutorService mExecutor = null;
    private static double mFileSize = 0.0d;
    private static SingleResourceLoader mInstance = null;

    public static SingleResourceLoader getCurrentInstance() {
        return mInstance;
    }

    protected SingleResourceLoader() {
        mInstance = this;
    }

    public static SingleResourceLoader getInstance() {
        if (mInstance == null) {
            new SingleResourceLoader();
        }
        return mInstance;
    }

    public void contTransferCancel(boolean isCancel) {
        mInstance.transferCancel(isCancel);
    }

    public static synchronized void addPicture(byte[] picture, String url) {
        synchronized (SingleResourceLoader.class) {
            synchronized (postviews) {
                postviews.clear();
                postviews.add(new Postview(picture, url));
            }
        }
    }

    public static synchronized void initData() {
        synchronized (SingleResourceLoader.class) {
            synchronized (postviews) {
                postviews.clear();
            }
        }
    }

    public void initialize() {
        mExecutor = mInstance.initial();
    }

    public void terminate() {
        mInstance.terminal(mExecutor);
        mExecutor = null;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    ExecutorService getExecute() {
        return mExecutor;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    boolean enableTransfer() {
        return true;
    }

    @Override // com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer
    InputStream getInputStream(HttpRequest req, HttpResponse res) {
        String reSize;
        InputStream in = null;
        String reqUrl = req.getRequestURI();
        if (reqUrl != null) {
            if (reqUrl.indexOf(SRCtrlConstants.SERVLET_ROOT_PATH_POSTVIEW_ON_MEMORY) != -1) {
                String renamedUrl = reqUrl.replaceFirst(SRCtrlConstants.SERVLET_ROOT_PATH_POSTVIEW_ON_MEMORY, "");
                String renamedUrl2 = "/mnt/sdcard" + renamedUrl.substring(0, renamedUrl.indexOf("?size="));
                String size = reqUrl.substring(reqUrl.indexOf("?size=") + "?size=".length());
                if ("Origin".equals(size)) {
                    reSize = SRCtrlConstants.IMAGE_TYPE_ORGJPEG;
                } else if ("Scn".equals(size)) {
                    reSize = "Scn";
                } else {
                    Log.e(TAG, "Invalid string " + size);
                    reSize = null;
                }
                if (reSize != null) {
                    Log.v(TAG, "beginResizeImage");
                    mFileSize = PrepareTransferData.getInstance().beginResizeImage(renamedUrl2, reSize, false, null, false, false);
                    if (0.0d < mFileSize) {
                        Log.v(TAG, "getResizeImage");
                        in = PrepareTransferData.getInstance().getResizeImage();
                        if (in != null) {
                            res.setContentLength((int) mFileSize);
                            Log.v(TAG, "setContentLength = " + mFileSize + " bytes");
                        }
                    } else {
                        Log.e(TAG, "Invalid size " + mFileSize);
                    }
                }
            } else if (reqUrl.indexOf(SRCtrlConstants.SERVLET_ROOT_PATH_POSTVIEW) != -1) {
                String renamedUrl3 = reqUrl.replaceFirst(SRCtrlConstants.SERVLET_ROOT_PATH_POSTVIEW, "");
                synchronized (postviews) {
                    boolean processed = false;
                    Log.v(TAG, "postviews size = " + postviews.size() + " renamedUrl = " + renamedUrl3);
                    Iterator i$ = postviews.iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        Postview postview = i$.next();
                        Log.v(TAG, postview.getUrl());
                        if (postview.getUrl().equals(renamedUrl3)) {
                            int len = postview.getPicture().length;
                            res.setContentLength(len);
                            processed = true;
                            Log.v(TAG, "File size on memory: " + len);
                            in = new ByteArrayInputStream(postview.getPicture());
                            break;
                        }
                    }
                    if (!processed) {
                        Log.e(TAG, "No postview data...");
                    }
                }
            } else {
                Log.e(TAG, "Invalid path " + reqUrl);
            }
        }
        return in;
    }
}
