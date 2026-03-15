package com.sony.mexi.orb.servlet;

import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.json.JsValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class OrbServletBase extends HttpServlet {
    private static final long serialVersionUID = 1;
    protected boolean connectionClose = true;
    private OrbServletLogger logger = new OrbServletLogger() { // from class: com.sony.mexi.orb.servlet.OrbServletBase.1
        private static final long serialVersionUID = 1;

        @Override // com.sony.mexi.orb.servlet.OrbServletLogger
        public void log(String msg) {
            System.out.println(msg);
        }
    };

    public void setLogger(OrbServletLogger logger) {
        this.logger = logger;
    }

    @Override // javax.servlet.GenericServlet
    public void log(String msg) {
        this.logger.log(msg);
    }

    public void debug(String msg) {
        this.logger.debug(msg);
    }

    public Map<String, String> parseQuery(HttpServletRequest req) {
        String query = req.getQueryString();
        if (query == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        int keyStart = 0;
        int valStart = 0;
        String key = null;
        String val = "";
        int len = query.length();
        for (int i = 0; i < len; i++) {
            if (query.charAt(i) == '=') {
                if (val != null && i > keyStart) {
                    key = query.substring(keyStart, i);
                    valStart = i + 1;
                    val = null;
                }
                return null;
            }
            if (query.charAt(i) == '&') {
                if (key != null && i > valStart) {
                    val = query.substring(valStart, i);
                    map.put(key, val);
                    keyStart = i + 1;
                    key = null;
                }
                return null;
            }
            continue;
        }
        if (key != null && valStart < len) {
            String val2 = query.substring(valStart);
            map.put(key, val2);
            return map;
        }
        return null;
    }

    public void send400(HttpServletResponse res) throws IOException {
        sendError(res, HttpServletResponse.SC_BAD_REQUEST, "{\"error\":[400,\"Bad Request\"]}");
    }

    public void send403(HttpServletResponse res) throws IOException {
        sendError(res, HttpServletResponse.SC_FORBIDDEN, "{\"error\":[403,\"Forbidden\"]}");
    }

    public void send404(HttpServletResponse res) throws IOException {
        sendError(res, HttpServletResponse.SC_NOT_FOUND, "{\"error\":[404,\"Not Found\"]}");
    }

    public void send501(HttpServletResponse res) throws IOException {
        sendError(res, HttpServletResponse.SC_NOT_IMPLEMENTED, "{\"error\":[501,\"Not Implemented\"]}");
    }

    private void sendError(HttpServletResponse res, int sc, String msg) throws IOException {
        res.setStatus(sc);
        send(res, msg);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendJsValue(HttpServletResponse res, JsValue val) throws IOException {
        send(res, val.toString());
    }

    private void send(HttpServletResponse res, String body) throws IOException {
        res.setContentType(MimeType.APPLICATION_JSON);
        res.setContentLength(body.getBytes(AutoSyncConstants.TEXT_ENCODING_UTF8).length);
        if (this.connectionClose) {
            res.setHeader("Connection", "close");
        }
        PrintWriter out = res.getWriter();
        out.print(body);
        debug("send: " + body);
    }
}
