package com.sony.imaging.app.synctosmartphone.webapi.servlet;

import android.content.res.AssetManager;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.orb.servlet.MimeType;
import com.sony.mexi.orb.servlet.OrbResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class OrbAndroidResourceLoader extends OrbResourceLoader {
    private static final String TAG = OrbAndroidResourceLoader.class.getSimpleName();
    private static AssetManager assetManager = null;
    private static final long serialVersionUID = 1;

    public static void setAssertManager(AssetManager am) {
        Log.d(TAG, "AssetManager start");
        assetManager = am;
    }

    public String getRootPath() {
        Log.d(TAG, "getRootPath start");
        return AutoSyncConstants.SERVLET_ROOT_PATH_WEBAPI;
    }

    @Override // com.sony.mexi.orb.servlet.OrbResourceLoader, javax.servlet.http.HttpServlet
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String type;
        Log.d(TAG, "doGet start");
        req.setCharacterEncoding(AutoSyncConstants.TEXT_ENCODING_UTF8);
        if (req.getQueryString() != null) {
            send404(res);
            return;
        }
        Log.d(TAG, "doGet req.getRequestURI");
        String uri = req.getRequestURI();
        if (uri == null) {
            send404(res);
            return;
        }
        if (uri.equals(AutoSyncConstants.SERVLET_ROOT_PATH_WEBAPI)) {
            Log.d(TAG, "doGet SERVLET_ROOT_PATH_WEBAPI");
            type = MimeType.getType(AutoSyncConstants.FILE_EXTENTION_HTML);
        } else {
            Log.d(TAG, "doGet not SERVLET_ROOT_PATH_WEBAPI or SERVLET_ROOT_PATH_CONTENTS");
            type = getContentType(uri);
            if (type == null) {
                Log.d(TAG, "doGet type null");
                send404(res);
                return;
            }
        }
        Log.d(TAG, "doGet getContentStream");
        InputStream is = getContentStream(uri);
        if (is == null) {
            send404(res);
            return;
        }
        Log.d(TAG, "doGet setContentType");
        res.setContentType(type);
        byte[] data = new byte[4096];
        int total = 0;
        Log.d(TAG, "doGet getOutputStream");
        OutputStream os = res.getOutputStream();
        while (true) {
            int size = is.read(data);
            if (size != -1) {
                os.write(data, 0, size);
                total += size;
            } else {
                Log.d(TAG, "doGet setContentLength");
                res.setContentLength(total);
                log(uri + ExposureModeController.SOFT_SNAP + type + ExposureModeController.SOFT_SNAP + total);
                return;
            }
        }
    }

    @Override // com.sony.mexi.orb.servlet.OrbResourceLoader
    protected InputStream getContentStream(String uri) {
        InputStream inputStream = null;
        Log.d(TAG, "getContentStream start");
        try {
            Log.v("OrbAndroidResourceLoader", "Requested URL: " + uri);
            String rootPath = getRootPath();
            Log.v("OrbAndroidResourceLoader", "Root  Path:    " + rootPath);
            if (assetManager != null && uri.startsWith(rootPath)) {
                if (uri.equals(rootPath)) {
                    Log.v("OrbAndroidResourceLoader", "Try to load index.html");
                    inputStream = assetManager.open(AutoSyncConstants.WEBAPI_ROOT_FILENAME);
                } else {
                    Log.v("OrbAndroidResourceLoader", "Try to load " + uri.substring(rootPath.length()));
                    inputStream = assetManager.open(uri.substring(rootPath.length()));
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "getContentStream exception");
        }
        return inputStream;
    }
}
