package com.sony.mexi.orb.servlet;

import com.sony.mexi.orb.server.OrbResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class OrbChunkTransfer extends OrbServletBase {
    private static final long serialVersionUID = 1;
    private boolean continueChunkTransfer = true;
    private OrbResponse srvRes = null;

    protected abstract boolean doChunkTransfer(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:13:0x003f  */
    @Override // javax.servlet.http.HttpServlet
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void doGet(javax.servlet.http.HttpServletRequest r6, javax.servlet.http.HttpServletResponse r7) throws javax.servlet.ServletException, java.io.IOException {
        /*
            r5 = this;
            java.lang.String r3 = "UTF-8"
            r6.setCharacterEncoding(r3)
            java.lang.String r2 = r6.getRequestURI()
            if (r2 != 0) goto Lf
            r5.send404(r7)
        Le:
            return
        Lf:
            java.lang.String r1 = r5.getContentType(r2)
            if (r1 != 0) goto L19
            r5.send404(r7)
            goto Le
        L19:
            r3 = r7
            com.sony.mexi.orb.server.OrbResponse r3 = (com.sony.mexi.orb.server.OrbResponse) r3
            r5.srvRes = r3
            r0 = r7
            com.sony.mexi.orb.server.OrbResponse r0 = (com.sony.mexi.orb.server.OrbResponse) r0
            r0.setChunked()
            r7.setContentType(r1)
            java.lang.String r3 = "Transfer-Encoding"
            java.lang.String r4 = "chunked"
            r7.setHeader(r3, r4)
            r0.sendResponseHeaders()
            r3 = 1
            r5.continueChunkTransfer = r3
        L34:
            boolean r3 = r5.continueChunkTransfer
            if (r3 != 0) goto L3f
            r0.unsetChunked()
            r0.commit()
            goto Le
        L3f:
            boolean r3 = r5.doChunkTransfer(r6, r7)
            if (r3 == 0) goto L34
            goto L34
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.mexi.orb.servlet.OrbChunkTransfer.doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse):void");
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
        this.continueChunkTransfer = false;
    }

    public void closeConnection() throws IOException {
        this.srvRes.closeConnection();
    }
}
