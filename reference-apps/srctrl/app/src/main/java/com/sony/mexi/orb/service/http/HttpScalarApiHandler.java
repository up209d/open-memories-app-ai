package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.http.OrbHttpInputStream;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.orb.service.MethodCallInfoCollector;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceInformationBase;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.orb.service.ScalarApiInvoker;
import com.sony.mexi.webapi.Protocol;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public abstract class HttpScalarApiHandler extends GenericHttpRequestHandler implements OrbServiceProvider {
    private final ScalarApiInvoker invoker = new ScalarApiInvoker(this);
    private Authenticator mAuthenticator = null;
    private OrbAbstractVersion mProvider = null;

    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    protected final void handlePost(HttpRequest req, HttpResponse res) throws IOException {
        String body;
        HttpEndPoint client = new HttpEndPoint(req, res);
        MethodCallInfoCollector collector = new MethodCallInfoCollector();
        collector.setConnectionInfo(client, getTransportProtocol());
        int contentLength = req.getContentLength();
        OrbHttpInputStream inputStream = null;
        try {
            inputStream = (OrbHttpInputStream) req.getInputStream();
            if (contentLength < 0) {
                body = readToStreamEnd(inputStream);
            } else {
                body = readWithContentLength(inputStream, contentLength);
            }
            this.invoker.onWebApiRequest(client, body, collector);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public final void handleGet(HttpRequest req, HttpResponse res) throws IOException {
        super.handleGet(req, res);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public final void handleDelete(HttpRequest req, HttpResponse res) throws IOException {
        super.handleDelete(req, res);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public final void handleHead(HttpRequest req, HttpResponse res) throws IOException {
        super.handleHead(req, res);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public final void handlePut(HttpRequest req, HttpResponse res) throws IOException {
        super.handlePut(req, res);
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public Authenticator getAuthenticator() {
        return this.mAuthenticator;
    }

    public final void setAuthenticator(Authenticator authenticator) {
        this.mAuthenticator = authenticator;
        this.invoker.setAuthenticator(authenticator);
    }

    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler, com.sony.mexi.orb.server.OrbService
    public void init(String path) {
        initService();
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final void addVersion(OrbAbstractVersion version) {
        this.mProvider = version;
        this.invoker.addVersion(version);
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final void addVersion(String name, OrbAbstractVersion version) {
        this.mProvider = version;
        this.invoker.addVersion(name, version);
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public OrbAbstractVersion getProvider() {
        return this.mProvider;
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public final OrbServiceInformationBase[] getServiceInformation() {
        return this.invoker.getServiceInformation();
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public boolean authorize(String method, OrbClient client) {
        return true;
    }

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public Protocol getTransportProtocol() {
        return Protocol.HTTP_POST;
    }

    private String readWithContentLength(InputStream inputStream, int contentLength) throws IOException {
        byte[] postData = new byte[contentLength];
        int offset = 0;
        int toRead = contentLength;
        do {
            int readSize = inputStream.read(postData, offset, toRead);
            if (readSize == -1) {
                throw new IOException("Unexpected end of stream");
            }
            toRead -= readSize;
            offset += readSize;
        } while (toRead > 0);
        return new String(postData, "UTF-8");
    }

    private String readToStreamEnd(OrbHttpInputStream inputStream) throws IOException {
        ByteArrayOutputStream postData = new ByteArrayOutputStream();
        while (true) {
            byte[] data = inputStream.readNextBuffer();
            if (data != null) {
                postData.write(data);
            } else {
                return postData.toString("UTF-8");
            }
        }
    }
}
