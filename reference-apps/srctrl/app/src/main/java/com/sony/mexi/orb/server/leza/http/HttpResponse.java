package com.sony.mexi.orb.server.leza.http;

import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.http.OrbHttpResponse;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.server.jni.HttpServletJni;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class HttpResponse implements OrbHttpResponse {
    private int mContentLength = -1;
    private HttpResponseOutputStream mHttpOutputStream;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpResponse(HttpServletJni httpServletJni) {
        this.mHttpOutputStream = new HttpResponseOutputStream(httpServletJni);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public OutputStream getOutputStream() {
        return this.mHttpOutputStream;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void end() {
        try {
            this.mHttpOutputStream.close();
        } catch (IOException e) {
        }
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void setHeader(String name, String value) {
        this.mHttpOutputStream.setHeader(name, value);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void sendStatusAsResponse(StatusCode code) throws IOException {
        sendStatusAsResponse(code, null);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void sendStatusAsResponse(StatusCode code, String body) throws IOException {
        this.mHttpOutputStream.setStatus(code);
        if (body != null) {
            byte[] data = body.getBytes("UTF-8");
            setContentLength(data.length);
            this.mHttpOutputStream.write(data);
        } else {
            setContentLength(0);
            this.mHttpOutputStream.writeHead();
        }
        end();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void setContentLength(int contentLength) {
        this.mContentLength = contentLength;
        setHeader(HttpDefs.HEADER_CONTENT_LENGTH, String.valueOf(this.mContentLength));
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpResponse
    public void setContentType(String contentType) {
        setHeader(HttpDefs.HEADER_CONTENT_TYPE, contentType);
    }
}
