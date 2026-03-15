package com.sony.mexi.orb.android.servlet;

import android.content.res.AssetManager;
import android.util.Log;
import com.sony.mexi.orb.servlet.OrbChunkTransfer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class OrbAndroidChunkTransfer extends OrbChunkTransfer {
    private static AssetManager assetManager = null;
    private static final long serialVersionUID = 1;
    private static int count = 0;
    private static Map<Long, Object> threadIdToObj = new HashMap();

    public static void setAssertManager(AssetManager am) {
        assetManager = am;
    }

    public String getRootPath() {
        return "/chunk/";
    }

    private String getCancelString() {
        return "cancel";
    }

    private String getExtraHeaderFieldForCancel() {
        return "X-Scalar-CancelId";
    }

    protected InputStream getContentStream(String uri) {
        try {
            return assetManager.open(uri);
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.servlet.OrbChunkTransfer, javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Long.valueOf(-1L);
        Thread thread = Thread.currentThread();
        Long threadId = Long.valueOf(thread.getId());
        String uri = req.getRequestURI();
        String pathAfterRootPath = uri.substring(getRootPath().length());
        if (pathAfterRootPath.startsWith(getCancelString())) {
            String requestedThreadId = pathAfterRootPath.substring(getCancelString().length() + 1);
            Long requestedId = Long.valueOf(requestedThreadId);
            OrbChunkTransfer chunkTransferInstance = (OrbChunkTransfer) threadIdToObj.get(requestedId);
            Log.d("OrbChunkTransfer cancel() ThreadId ->", requestedThreadId);
            chunkTransferInstance.cancel();
            threadIdToObj.remove(requestedId);
            int threadIdToObjNum = threadIdToObj.size();
            String msgStr = Integer.toString(threadIdToObjNum);
            Log.d("ThreadIdToObj deleted threadId num->", msgStr);
            return;
        }
        if (this != threadIdToObj.get(threadId)) {
            threadIdToObj.put(threadId, this);
            int threadIdToObjNum2 = threadIdToObj.size();
            String msgStr2 = Integer.toString(threadIdToObjNum2);
            Log.d("Added New ThreadId threadIdToObj num ->", msgStr2);
        }
        res.setHeader(getExtraHeaderFieldForCancel(), threadId.toString());
        super.doGet(req, res);
    }

    @Override // com.sony.mexi.orb.servlet.OrbChunkTransfer
    protected boolean doChunkTransfer(HttpServletRequest req, HttpServletResponse res) {
        String uri;
        byte[] data = new byte[4096];
        OutputStream os = null;
        try {
            os = res.getOutputStream();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        req.getRequestURI();
        if (count % 2 == 0) {
            uri = "text1.txt";
        } else {
            uri = "text2.txt";
        }
        InputStream is = getContentStream(uri);
        while (true) {
            try {
                int size = is.read(data);
                if (size == -1) {
                    break;
                }
                os.write(data, 0, size);
            } catch (IOException e22) {
                e22.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            res.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (count < 10) {
            count++;
            return true;
        }
        count = 0;
        return false;
    }
}
