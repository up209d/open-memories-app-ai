package com.sony.imaging.app.srctrl.webapi.servlet;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.util.Postview;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.service.http.GenericHttpRequestHandler;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class PostviewResourceLoader extends GenericHttpRequestHandler {
    private static final long serialVersionUID = 1;
    private static final String TAG = PostviewResourceLoader.class.getSimpleName();
    private static ArrayList<Postview> postviews = new ArrayList<>();
    private static ThreadLocal<File> mThreadLocalPostview = new ThreadLocal<File>() { // from class: com.sony.imaging.app.srctrl.webapi.servlet.PostviewResourceLoader.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public synchronized File initialValue() {
            return null;
        }
    };

    public static synchronized void addPicture(byte[] picture, String url) {
        synchronized (PostviewResourceLoader.class) {
            synchronized (postviews) {
                postviews.add(new Postview(picture, url));
            }
        }
    }

    public static synchronized void initData() {
        synchronized (PostviewResourceLoader.class) {
            synchronized (postviews) {
                postviews.clear();
            }
        }
    }

    protected synchronized InputStream getContentStream(String url) {
        InputStream inputStream;
        Log.v(TAG, "getContentStream: " + url);
        File postviewFile = mThreadLocalPostview.get();
        if (postviewFile != null) {
            inputStream = getContentStreamOnMemoryCard(postviewFile);
        } else {
            String renamedUrl = url.replaceFirst(SRCtrlConstants.SERVLET_ROOT_PATH_POSTVIEW, "");
            inputStream = null;
            synchronized (postviews) {
                Iterator i$ = postviews.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    Postview postview = i$.next();
                    if (postview.getUrl().equals(renamedUrl)) {
                        ByteArrayInputStream input = new ByteArrayInputStream(postview.getPicture());
                        inputStream = input;
                        break;
                    }
                }
            }
        }
        return inputStream;
    }

    private InputStream getContentStreamOnMemoryCard(File actualFile) {
        try {
            InputStream in = new FileInputStream(actualFile);
            return in;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0162 A[Catch: all -> 0x0240, TRY_LEAVE, TryCatch #0 {all -> 0x0240, blocks: (B:2:0x0000, B:5:0x000f, B:6:0x0013, B:8:0x001b, B:10:0x0039, B:16:0x0050, B:20:0x00a5, B:22:0x00ae, B:24:0x00bf, B:26:0x00d5, B:28:0x00eb, B:29:0x0154, B:31:0x0162, B:34:0x0251, B:36:0x025b, B:39:0x028e, B:42:0x017a, B:46:0x019c, B:50:0x01b9, B:51:0x01c3, B:72:0x023f, B:75:0x0086, B:54:0x01c6, B:55:0x01cc, B:57:0x01d2, B:60:0x01e6, B:62:0x0214, B:63:0x0228, B:66:0x023a), top: B:1:0x0000, inners: #1, #2, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0251 A[Catch: all -> 0x0240, TRY_ENTER, TryCatch #0 {all -> 0x0240, blocks: (B:2:0x0000, B:5:0x000f, B:6:0x0013, B:8:0x001b, B:10:0x0039, B:16:0x0050, B:20:0x00a5, B:22:0x00ae, B:24:0x00bf, B:26:0x00d5, B:28:0x00eb, B:29:0x0154, B:31:0x0162, B:34:0x0251, B:36:0x025b, B:39:0x028e, B:42:0x017a, B:46:0x019c, B:50:0x01b9, B:51:0x01c3, B:72:0x023f, B:75:0x0086, B:54:0x01c6, B:55:0x01cc, B:57:0x01d2, B:60:0x01e6, B:62:0x0214, B:63:0x0228, B:66:0x023a), top: B:1:0x0000, inners: #1, #2, #3 }] */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void handleGet(com.sony.mexi.orb.service.http.HttpRequest r31, com.sony.mexi.orb.service.http.HttpResponse r32) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 678
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.srctrl.webapi.servlet.PostviewResourceLoader.handleGet(com.sony.mexi.orb.service.http.HttpRequest, com.sony.mexi.orb.service.http.HttpResponse):void");
    }

    private void send(InputStream in, HttpResponse res) {
        OutputStream os = res.getOutputStream();
        if (os != null) {
            byte[] buff = new byte[102400];
            long totalWriteCount = 0;
            long startTime = System.currentTimeMillis();
            boolean isIOError = false;
            StringBuilder sbuilder = new StringBuilder();
            while (true) {
                sbuilder.delete(0, sbuilder.length());
                try {
                    int count = in.read(buff);
                    if (-1 == count) {
                        break;
                    }
                    os.write(buff, 0, count);
                    totalWriteCount += count;
                    sbuilder.append("write is added : count=").append(count).append(" total=").append(totalWriteCount);
                    Log.v(TAG, sbuilder.toString());
                } catch (IOException e) {
                    Log.e(TAG, "Read/Write Error. : " + e.getMessage());
                    isIOError = true;
                }
            }
            sbuilder.append("write end : total=").append(totalWriteCount);
            Log.v(TAG, sbuilder.toString());
            if (isIOError) {
                try {
                    res.sendStatusAsResponse(StatusCode.FORBIDDEN);
                    return;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (0 != elapsedTime) {
                Log.v(TAG, "TRANSFER SPEED: " + ((((8 * totalWriteCount) * 1000) / 1024) / elapsedTime) + "[Kibps]");
            }
        }
    }
}
