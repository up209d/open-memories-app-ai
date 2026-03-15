package com.sony.mexi.orb.servlet;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.orb.server.OrbResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class OrbDivisionTransfer extends OrbServletBase {
    private static final long serialVersionUID = 1;
    private boolean continueDivisionTransfer = true;
    private OrbResponse srvRes = null;
    private int total = 0;

    protected abstract boolean doDivisionTransfer(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding(AutoSyncConstants.TEXT_ENCODING_UTF8);
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
        this.srvRes = (OrbResponse) res;
        OrbResponse orbRes = (OrbResponse) res;
        orbRes.setDivision();
        res.setContentType(type);
        res.setContentLength(this.total);
        res.setHeader("Connection", "close");
        log(String.valueOf(uri) + ExposureModeController.SOFT_SNAP + type + ExposureModeController.SOFT_SNAP + this.total);
        orbRes.sendResponseHeaders();
        this.continueDivisionTransfer = true;
        while (this.continueDivisionTransfer) {
            doDivisionTransfer(req, res);
        }
        orbRes.unsetDivision();
        orbRes.commit();
    }

    protected String getContentType(String uri) {
        int index = uri.lastIndexOf(46);
        if (index < 0) {
            return "application/octet-stream";
        }
        String ext = uri.substring(index + 1);
        return MimeType.getType(ext);
    }

    public void cancel() {
        this.continueDivisionTransfer = false;
    }

    public void closeConnection() throws IOException {
        this.srvRes.closeConnection();
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return this.total;
    }
}
