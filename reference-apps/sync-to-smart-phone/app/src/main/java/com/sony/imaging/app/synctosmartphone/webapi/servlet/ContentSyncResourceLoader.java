package com.sony.imaging.app.synctosmartphone.webapi.servlet;

import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncKikiLogUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.synctosmartphone.database.DeviceBufferInputStream;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.orb.servlet.MimeType;
import com.sony.mexi.orb.servlet.OrbDivisionTransfer;
import com.sony.scalar.hardware.DeviceBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class ContentSyncResourceLoader extends OrbDivisionTransfer {
    private static final String TAG = ContentSyncResourceLoader.class.getSimpleName();
    private static int contentsImageSize = 2097152;
    private static ThreadLocal<File> mThreadLocalContentSync = new ThreadLocal<File>() { // from class: com.sony.imaging.app.synctosmartphone.webapi.servlet.ContentSyncResourceLoader.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public synchronized File initialValue() {
            return null;
        }
    };
    private static final long serialVersionUID = 1;

    public String getRootPath() {
        return AutoSyncConstants.SERVLET_ROOT_PATH_CONTENTS;
    }

    protected synchronized InputStream getContentStream(String uri) {
        InputStream in;
        Log.v(TAG, "getContentStream: " + uri);
        in = null;
        File actualFile = mThreadLocalContentSync.get();
        if (actualFile != null) {
            String renameUrlPath = uri.replaceFirst(getRootPath(), "");
            String actualFileFromUri = Environment.getExternalStorageDirectory() + "/" + renameUrlPath;
            if (actualFileFromUri.equals(actualFile.toString())) {
                if (contentsImageSize == 0) {
                    try {
                        in = new FileInputStream(actualFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    DeviceBuffer dbuf = AutoSyncDataBaseUtil.getInstance().getScreennail();
                    if (dbuf != null) {
                        long fileSize = AutoSyncDataBaseUtil.getInstance().getFileSize();
                        in = new DeviceBufferInputStream(dbuf, (int) fileSize);
                    }
                }
            }
        }
        return in;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.servlet.OrbDivisionTransfer, javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Log.d(TAG, "doGet req.getRequestURI");
        String tmpUrl = req.getRequestURI();
        File tmpUrlFile = new File(tmpUrl);
        AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
        try {
            String url = tmpUrlFile.getCanonicalPath();
            if (url.toLowerCase().endsWith(".jpg")) {
                res.setContentType(MimeType.IMAGE_JPEG);
                Log.d(TAG, "doGet type " + MimeType.IMAGE_JPEG);
                try {
                    String renameUrlPath = url.replaceFirst(getRootPath(), "");
                    String actualFileName = Environment.getExternalStorageDirectory() + "/" + renameUrlPath;
                    Log.v(TAG, "File name: " + actualFileName);
                    int imageSize = SyncBackUpUtil.getInstance().getImageSize(2097152);
                    File actualFile = new File(actualFileName);
                    if (imageSize == 0 && 0 < actualFile.length()) {
                        Log.v(TAG, "File size from a memory card: " + actualFile.length());
                        setTotal((int) actualFile.length());
                        contentsImageSize = 0;
                    } else {
                        Log.v(TAG, "Screennail size : 2097152");
                        long length = dbUtil.getScreennailSize();
                        if (0 < length) {
                            setTotal((int) length);
                            Log.d(TAG, "!!! 2M length = " + length);
                            contentsImageSize = 2097152;
                        } else {
                            Log.e(TAG, "file length is 0.");
                            dbUtil.moveToNextReservationFile();
                            send404(res);
                            return;
                        }
                    }
                    mThreadLocalContentSync.set(actualFile);
                    super.doGet(req, res);
                    return;
                } finally {
                    Log.v(TAG, "Forget a thread-local variable.");
                    mThreadLocalContentSync.remove();
                }
            }
            Log.d(TAG, "doGet type unknown");
            send404(res);
        } catch (IOException e) {
            e.printStackTrace();
            send403(res);
        }
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

    @Override // com.sony.mexi.orb.servlet.OrbDivisionTransfer
    protected boolean doDivisionTransfer(HttpServletRequest req, HttpServletResponse res) {
        int count;
        try {
            OutputStream os = res.getOutputStream();
            String uri = getCanonicalUrl(req.getRequestURI());
            if (uri == null) {
                cancel();
                Log.v(TAG, "HttpServletRequest has a malformed URI.");
                return false;
            }
            InputStream in = getContentStream(uri);
            if (in == null) {
                Log.v(TAG, "Inputstream for \"" + uri + "\" is null.");
                cancel();
                return false;
            }
            boolean result = false;
            byte[] buff = new byte[102400];
            long totalCount = 0;
            long startTime = System.currentTimeMillis();
            while (true) {
                try {
                    count = in.read(buff);
                    if (-1 == count) {
                        break;
                    }
                    os.write(buff, 0, count);
                    totalCount += count;
                    os.flush();
                    res.flushBuffer();
                    Log.v(TAG, "wrote " + count + "/" + totalCount);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cancel();
                }
            }
            Log.v(TAG, "Wrote " + count + "/" + totalCount);
            result = true;
            SyncKikiLogUtil.logNumberOfTransfers();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (0 != elapsedTime) {
                Log.v(TAG, "TRANSFER SPEED: " + ((((8 * totalCount) * 1000) / 1024) / elapsedTime) + "[Kibps]");
            }
            try {
                in.close();
                return result;
            } catch (IOException e2) {
                e2.printStackTrace();
                return result;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            cancel();
            return false;
        } catch (IllegalStateException e3) {
            Log.v(TAG, "HttpServletResponse.getOutputStream() IllegalStateException.");
            cancel();
            return false;
        }
    }
}
