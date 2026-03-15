package com.sony.mexi.orb.servlet;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class OrbResourceLoader extends OrbServletBase {
    private static final long serialVersionUID = 1;

    protected abstract InputStream getContentStream(String str);

    @Override // javax.servlet.http.HttpServlet
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding(AutoSyncConstants.TEXT_ENCODING_UTF8);
        if (req.getQueryString() != null) {
            send404(res);
            return;
        }
        String uri = req.getRequestURI();
        if (uri == null) {
            send404(res);
            return;
        }
        String type = getContentType(uri);
        if (type == null) {
            send404(res);
            return;
        }
        InputStream is = getContentStream(uri);
        if (is == null) {
            send404(res);
            return;
        }
        res.setContentType(type);
        byte[] data = new byte[4096];
        int total = 0;
        OutputStream os = res.getOutputStream();
        while (true) {
            int size = is.read(data);
            if (size != -1) {
                os.write(data, 0, size);
                total += size;
            } else {
                res.setContentLength(total);
                log(String.valueOf(uri) + ExposureModeController.SOFT_SNAP + type + ExposureModeController.SOFT_SNAP + total);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getContentType(String uri) {
        int index = uri.lastIndexOf(46);
        if (index < 0) {
            return "application/octet-stream";
        }
        String ext = uri.substring(index + 1);
        return MimeType.getType(ext);
    }
}
