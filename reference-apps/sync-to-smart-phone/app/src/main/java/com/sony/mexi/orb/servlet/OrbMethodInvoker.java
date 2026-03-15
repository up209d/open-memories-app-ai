package com.sony.mexi.orb.servlet;

import com.sony.imaging.app.synctosmartphone.webapi.definition.Name;
import com.sony.mexi.json.JsArray;
import com.sony.mexi.json.JsError;
import com.sony.mexi.json.JsNumber;
import com.sony.mexi.json.JsObject;
import com.sony.mexi.json.JsString;
import com.sony.mexi.json.JsValue;
import com.sony.mexi.json.Json;
import com.sony.mexi.orb.server.OrbRequestHeaders;
import com.sony.mexi.orb.servlet.serviceguide.OrbServiceGuideServlet;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.VersionHandler;
import com.sony.mexi.webapi.serviceguide.v1_1.ServiceInformation;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class OrbMethodInvoker extends OrbServletBase {
    private static final long serialVersionUID = 1;
    private TreeMap<String, OrbVersionBase> versionMap = new TreeMap<>(new VersionComparator());

    private void sendResult(HttpServletResponse res, JsValue val, long id) throws IOException {
        JsObject obj = new JsObject();
        obj.put("result", val);
        if (id >= 0) {
            obj.put("id", new JsNumber(Long.valueOf(id)));
        }
        sendJsValue(res, obj);
    }

    private void sendResults(HttpServletResponse res, JsValue val, long id) throws IOException {
        JsObject obj = new JsObject();
        obj.put("results", val);
        if (id >= 0) {
            obj.put("id", new JsNumber(Long.valueOf(id)));
        }
        sendJsValue(res, obj);
    }

    private void sendError(HttpServletResponse res, JsValue val, long id) throws IOException {
        JsObject obj = new JsObject();
        obj.put("error", val);
        if (id > 0) {
            obj.put("id", new JsNumber(Long.valueOf(id)));
        }
        sendJsValue(res, obj);
    }

    private void sendError(HttpServletResponse res, int code, String msg, long id) throws IOException {
        JsArray err = new JsArray(new JsValue[0]);
        err.add(new JsNumber(Integer.valueOf(code)));
        err.add(new JsString(msg));
        sendError(res, err, id);
        if (code >= 400 && code < 600) {
            res.setStatus(code);
        }
    }

    private void sendStatus(HttpServletResponse res, JsValue val, long id) throws IOException {
        JsObject obj = new JsObject();
        obj.put("status", val);
        if (id > 0) {
            obj.put("id", new JsNumber(Long.valueOf(id)));
        }
        sendJsValue(res, obj);
    }

    private void sendStatus(HttpServletResponse res, int code, String msg, long id) throws IOException {
        JsArray status = new JsArray(new JsValue[0]);
        status.add(new JsNumber(Integer.valueOf(code)));
        status.add(new JsString(msg));
        sendStatus(res, status, id);
    }

    protected final long getId(String idStr) {
        long id = -1;
        if (idStr != null) {
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                return -1L;
            }
        }
        return id;
    }

    protected final void invokeMethod(HttpServletRequest req, HttpServletResponse res, String name, JsArray params, JsValue versionVal, long id) throws IOException {
        try {
            String versionStr = versionVal == null ? this.versionMap.firstKey() : versionVal.toJavaString();
            OrbVersionBase version = this.versionMap.get(versionStr);
            if (version == null) {
                sendError(res, Status.UNSUPPORTED_VERSION.toInt(), versionStr, id);
                return;
            }
            OrbMethod method = version.getMethod(name);
            if (method == null) {
                sendError(res, Status.NO_SUCH_METHOD.toInt(), name, id);
                return;
            }
            JsValue val = method.invoke(req, res, params);
            if (val.type() == JsValue.Type.ERROR) {
                int code = ((JsError) val).getNumber();
                String msg = ((JsError) val).getMessage();
                if (msg == null) {
                    msg = "";
                }
                if (code == Status.OK.toInt()) {
                    sendStatus(res, code, msg, id);
                    return;
                } else {
                    sendError(res, code, msg, id);
                    return;
                }
            }
            if (method.hasContinuousCallbacks()) {
                sendResults(res, val, id);
            } else {
                sendResult(res, val, id);
            }
        } catch (RuntimeException e) {
            sendError(res, Status.ILLEGAL_REQUEST.toInt(), "Illegal JSON", id);
        }
    }

    /* loaded from: classes.dex */
    public class VersionComparator implements Comparator<String>, Serializable {
        private static final long serialVersionUID = 1;

        public VersionComparator() {
        }

        @Override // java.util.Comparator
        public int compare(String s1, String s2) {
            int diff = -1;
            if (s1 == s2) {
                diff = 0;
            } else if (s1 != null && s2 != null) {
                String[] ns1 = s1.split("\\.");
                String[] ns2 = s2.split("\\.");
                try {
                    if (ns1.length == 2 && ns2.length == 2 && (diff = Integer.parseInt(ns2[0]) - Integer.parseInt(ns1[0])) == 0) {
                        diff = Integer.parseInt(ns2[1]) - Integer.parseInt(ns1[1]);
                    }
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
            return diff;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void addVersion(OrbVersionBase version) throws ServletException {
        addVersion(version.getServiceVersion(), version);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void addVersion(String name, OrbVersionBase version) throws ServletException {
        if (name.compareTo(version.getServiceVersion()) != 0 || this.versionMap.get(name) != null) {
            throw new ServletException();
        }
        this.versionMap.put(name, version);
        version.init(this);
    }

    @Override // javax.servlet.http.HttpServlet
    protected final void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        send501(res);
    }

    @Override // javax.servlet.http.HttpServlet
    protected final void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int contentLength = req.getContentLength();
        if (contentLength <= 0) {
            sendError(res, Status.ILLEGAL_REQUEST.toInt(), "Illegal JSON", -1L);
            return;
        }
        debug("content-length: " + contentLength);
        ServletInputStream sis = req.getInputStream();
        byte[] postData = new byte[contentLength];
        int offset = 0;
        int len = contentLength;
        do {
            int readSize = sis.readLine(postData, offset, len);
            len -= readSize;
            offset += readSize;
        } while (len > 0);
        String body = new String(postData);
        debug("receive: " + body);
        JsValue bodyVal = Json.parse(body);
        if (bodyVal == null || bodyVal.type() != JsValue.Type.OBJECT) {
            sendError(res, Status.ILLEGAL_REQUEST.toInt(), "Illegal JSON", -1L);
            return;
        }
        JsValue idVal = ((JsObject) bodyVal).get("id");
        if (idVal != null && idVal.isInt()) {
            long id = idVal.toJavaInt();
            if (id <= 0) {
                sendError(res, Status.ILLEGAL_REQUEST.toInt(), "Illegal JSON", id);
                return;
            }
            JsValue params = ((JsObject) bodyVal).get("params");
            if (params == null || params.type() != JsValue.Type.ARRAY) {
                sendError(res, Status.ILLEGAL_REQUEST.toInt(), bodyVal.toString(), id);
                return;
            }
            JsValue methodVal = ((JsObject) bodyVal).get("method");
            if (methodVal == null || methodVal.type() != JsValue.Type.STRING) {
                sendError(res, Status.ILLEGAL_REQUEST.toInt(), bodyVal.toString(), id);
                return;
            }
            String name = methodVal.toJavaString();
            if (name == null) {
                sendError(res, Status.ANY.toInt(), "no method name", id);
                return;
            }
            if (!authorize(name, new OrbRequestHeaders(req))) {
                sendError(res, Status.FORBIDDEN.toInt(), "Forbidden", id);
                return;
            }
            if (name.equals(Name.GET_VERSIONS)) {
                invokeGetVersions(res, (JsArray) params, id);
                return;
            }
            if (name.equals(Name.GET_METHOD_TYPES)) {
                invokeGetMethodTypes(res, (JsArray) params, id);
                return;
            }
            String requestURI = req.getRequestURI();
            if (requestURI == null) {
                sendError(res, Status.BAD_REQUEST.toInt(), "Bad Request", id);
                return;
            }
            if (requestURI.endsWith("/guide")) {
                ((JsArray) params).clear();
                ((JsArray) params).add(new JsString(requestURI));
            }
            invokeMethod(req, res, name, (JsArray) params, ((JsObject) bodyVal).get("version"), id);
            return;
        }
        sendError(res, Status.ILLEGAL_REQUEST.toInt(), "Illegal JSON", -1L);
    }

    protected boolean authorize(String method, OrbRequestHeaders reqHeaders) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class VersionHandlerImpl extends OrbClientCallbacks implements VersionHandler {
        private VersionHandlerImpl() {
        }

        /* synthetic */ VersionHandlerImpl(OrbMethodInvoker orbMethodInvoker, VersionHandlerImpl versionHandlerImpl) {
            this();
        }

        @Override // com.sony.mexi.webapi.VersionHandler
        public void handleVersions(String[] versions) {
            this.response = new JsArray(new JsValue[0]);
            ((JsArray) this.response).add(new JsArray(versions));
        }
    }

    private void invokeGetVersions(HttpServletResponse res, JsArray params, long id) throws IOException {
        String[] versions;
        VersionHandlerImpl versionHandlerImpl = null;
        if (params.length() == 0) {
            VersionHandlerImpl callbacks = new VersionHandlerImpl(this, versionHandlerImpl);
            if (this instanceof OrbServiceGuideServlet) {
                versions = new String[]{"1.0"};
            } else {
                versions = getVersions();
            }
            callbacks.handleVersions(versions);
            JsValue result = callbacks.getServerResponse();
            if (result != null) {
                sendResult(res, result, id);
                return;
            } else {
                sendError(res, Status.UNSUPPORTED_OPERATION.toInt(), "no result", id);
                return;
            }
        }
        sendError(res, Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument", id);
    }

    private void invokeGetMethodTypes(HttpServletResponse res, JsArray params, long id) throws IOException {
        if (params.length() != 1) {
            sendError(res, Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument", id);
            return;
        }
        JsValue versionVal = params.get(0);
        if (versionVal == null || !versionVal.isString()) {
            sendError(res, Status.ILLEGAL_REQUEST.toInt(), "illegal request", id);
            return;
        }
        OrbMethodTypeHandler callbacks = new OrbMethodTypeHandler();
        String versionStr = versionVal.toJavaString();
        if (versionStr.equals("")) {
            Object[] keys = this.versionMap.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String versionStr2 = (String) keys[(keys.length - i) - 1];
                OrbVersionBase version = this.versionMap.get(versionStr2);
                if (!(version instanceof com.sony.mexi.orb.servlet.serviceguide.v1_1.OrbServiceGuideServlet)) {
                    version.invokeGetMethodTypes(callbacks, versionStr2);
                }
            }
        } else {
            OrbVersionBase version2 = this.versionMap.get(versionStr);
            if (version2 == null) {
                sendError(res, Status.UNSUPPORTED_VERSION.toInt(), versionStr, id);
                return;
            }
            version2.invokeGetMethodTypes(callbacks, versionStr);
        }
        JsValue result = callbacks.getServerResponse();
        if (result == null) {
            sendError(res, Status.UNSUPPORTED_OPERATION.toInt(), "no result", id);
        } else {
            sendResults(res, result, id);
        }
    }

    public final OrbMethod inherit(String name, OrbMethod parameter) {
        OrbMethod method = null;
        Iterator<OrbVersionBase> itr = this.versionMap.values().iterator();
        while (itr.hasNext() && method == null) {
            OrbVersionBase version = itr.next();
            method = version.getMethodBySignature(name, parameter);
        }
        if (method == null) {
            throw new IllegalArgumentException("not found super method");
        }
        return method;
    }

    public final ServiceInformation[] getServiceInformation() {
        ServiceInformation[] info = new ServiceInformation[this.versionMap.size()];
        String[] versions = getVersions();
        for (int i = 0; i < versions.length; i++) {
            info[i] = this.versionMap.get(versions[i]).getServiceInformation();
        }
        return info;
    }

    public final String[] getVersions() {
        String[] versions = (String[]) this.versionMap.keySet().toArray(new String[0]);
        Collections.reverse(Arrays.asList(versions));
        return versions;
    }
}
