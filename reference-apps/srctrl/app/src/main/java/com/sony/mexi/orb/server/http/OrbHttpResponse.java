package com.sony.mexi.orb.server.http;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public interface OrbHttpResponse {
    void end();

    OutputStream getOutputStream();

    void sendStatusAsResponse(StatusCode statusCode) throws IOException;

    void sendStatusAsResponse(StatusCode statusCode, String str) throws IOException;

    void setContentLength(int i);

    void setContentType(String str);

    void setHeader(String str, String str2);
}
