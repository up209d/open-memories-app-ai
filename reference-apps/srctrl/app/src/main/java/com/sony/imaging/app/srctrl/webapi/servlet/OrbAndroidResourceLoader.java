package com.sony.imaging.app.srctrl.webapi.servlet;

import android.content.res.AssetManager;
import android.util.Log;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.service.http.GenericHttpRequestHandler;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class OrbAndroidResourceLoader extends GenericHttpRequestHandler {
    private static AssetManager assetManager = null;
    private static final long serialVersionUID = 1;

    public static void setAssertManager(AssetManager am) {
        assetManager = am;
    }

    public String getRootPath() {
        return SRCtrlConstants.SERVLET_ROOT_PATH_WEBAPI;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public void handleGet(HttpRequest req, HttpResponse res) throws IOException {
        String type;
        try {
            String uri = req.getRequestURI();
            if (uri == null) {
                try {
                    res.sendStatusAsResponse(StatusCode.NOT_FOUND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            int index = uri.lastIndexOf(46);
            if (index < 0) {
                type = "application/octet-stream";
            } else {
                String ext = uri.substring(index + 1);
                type = MimeType.getType(ext);
                Log.v("OrbAndroidResourceLoader", "MimeType : " + type);
            }
            res.setContentType(type);
            InputStream is = null;
            try {
                is = getContentStream(uri);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (is == null) {
                try {
                    res.sendStatusAsResponse(StatusCode.NOT_FOUND);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } else {
                if (!send(is, res.getOutputStream())) {
                    res.sendStatusAsResponse(StatusCode.NOT_FOUND);
                }
                is.close();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        } finally {
            res.end();
        }
    }

    private boolean send(InputStream is, OutputStream os) {
        if (is == null || os == null) {
            return false;
        }
        byte[] data = new byte[4096];
        while (true) {
            try {
                int size = is.read(data);
                if (-1 != size) {
                    os.write(data, 0, size);
                } else {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    InputStream getContentStream(String uri) throws IOException {
        Log.v("OrbAndroidResourceLoader", "Requested URL: " + uri);
        String rootPath = getRootPath();
        Log.v("OrbAndroidResourceLoader", "Root  Path:    " + rootPath);
        if (assetManager == null || !uri.startsWith(rootPath)) {
            return null;
        }
        if (uri.equals(rootPath)) {
            Log.v("OrbAndroidResourceLoader", "Try to load index.html");
            InputStream is = assetManager.open(SRCtrlConstants.WEBAPI_ROOT_FILENAME);
            return is;
        }
        Log.v("OrbAndroidResourceLoader", "Try to load " + uri.substring(rootPath.length()));
        InputStream is2 = assetManager.open(uri.substring(rootPath.length()));
        return is2;
    }
}
