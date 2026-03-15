package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.http.OrbHttpHandler;
import com.sony.mexi.orb.server.http.OrbHttpRequest;
import com.sony.mexi.orb.server.http.OrbHttpResponse;
import com.sony.mexi.orb.server.http.StatusCode;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class GenericHttpRequestHandler implements OrbHttpHandler {
    @Override // com.sony.mexi.orb.server.OrbService
    public void init(String path) {
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpHandler
    public final void serve(OrbHttpRequest request, OrbHttpResponse response) {
        HttpRequest req = new HttpRequest(request);
        HttpResponse res = new HttpResponse(response);
        try {
            onHttpRequest(req, res);
        } catch (IOException e) {
        }
    }

    public void onHttpRequest(HttpRequest req, HttpResponse res) throws IOException {
        String requestMethod = req.getMethod();
        if (HttpDefs.METHOD_POST.equals(requestMethod)) {
            handlePost(req, res);
            return;
        }
        if (HttpDefs.METHOD_GET.equals(requestMethod)) {
            handleGet(req, res);
            return;
        }
        if (HttpDefs.METHOD_HEAD.equals(requestMethod)) {
            handleHead(req, res);
            return;
        }
        if (HttpDefs.METHOD_PUT.equals(requestMethod)) {
            handlePut(req, res);
            return;
        }
        if (HttpDefs.METHOD_DELETE.equals(requestMethod)) {
            handleDelete(req, res);
        } else if (HttpDefs.METHOD_OPTIONS.equals(requestMethod)) {
            handleOptions(req, res);
        } else {
            res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
        }
    }

    protected void handlePost(HttpRequest req, HttpResponse res) throws IOException {
        res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleGet(HttpRequest req, HttpResponse res) throws IOException {
        res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleHead(HttpRequest req, HttpResponse res) throws IOException {
        res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handlePut(HttpRequest req, HttpResponse res) throws IOException {
        res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleDelete(HttpRequest req, HttpResponse res) throws IOException {
        res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
    }

    protected void handleOptions(HttpRequest req, HttpResponse res) throws IOException {
        res.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
    }
}
