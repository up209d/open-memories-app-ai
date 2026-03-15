package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.http.OrbHttpRequest;
import java.io.InputStream;

/* loaded from: classes.dex */
public class HttpRequest implements OrbHttpRequest {
    private final OrbHttpRequest mReq;

    public HttpRequest(OrbHttpRequest req) {
        this.mReq = req;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public synchronized InputStream getInputStream() {
        return this.mReq.getInputStream();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public boolean addDisconnectListener(OrbHttpRequest.DisconnectListener listener) {
        return this.mReq.addDisconnectListener(listener);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public boolean removeDisconnectListener(OrbHttpRequest.DisconnectListener listener) {
        return this.mReq.removeDisconnectListener(listener);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getMethod() {
        return this.mReq.getMethod();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getRemoteAddress() {
        return this.mReq.getRemoteAddress();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getRequestURI() {
        return this.mReq.getRequestURI();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getHeader(String name) {
        return this.mReq.getHeader(name);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getLocalAddress() {
        return this.mReq.getLocalAddress();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public int getLocalPort() {
        return this.mReq.getLocalPort();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public int getContentLength() {
        return this.mReq.getContentLength();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getCharset() {
        return this.mReq.getCharset();
    }
}
