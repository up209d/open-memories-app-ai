package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.http.OrbHttpResponse;
import com.sony.mexi.orb.server.http.StatusCode;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class HttpResponse implements OrbHttpResponse {
    private final OrbHttpResponse mRes;

    public HttpResponse(OrbHttpResponse res) {
        this.mRes = res;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public OutputStream getOutputStream() {
        return this.mRes.getOutputStream();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void setHeader(String name, String value) {
        this.mRes.setHeader(name, value);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void sendStatusAsResponse(StatusCode code) throws IOException {
        this.mRes.sendStatusAsResponse(code);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void sendStatusAsResponse(StatusCode code, String body) throws IOException {
        this.mRes.sendStatusAsResponse(code, body);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void setContentLength(int length) {
        this.mRes.setContentLength(length);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void setContentType(String value) {
        this.mRes.setContentType(value);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void end() {
        this.mRes.end();
    }
}
