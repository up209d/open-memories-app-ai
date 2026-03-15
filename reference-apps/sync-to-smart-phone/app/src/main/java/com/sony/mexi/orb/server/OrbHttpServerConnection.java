package com.sony.mexi.orb.server;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;

/* loaded from: classes.dex */
public class OrbHttpServerConnection extends DefaultHttpServerConnection {
    private OrbChunkedOutputStream chunkedOutStream;
    private OrbDivisionOutputStream divisionOutStream;
    private boolean unsetChunked = false;

    @Override // org.apache.http.impl.SocketHttpServerConnection
    protected SessionOutputBuffer createHttpDataTransmitter(Socket socket, int buffersize, HttpParams params) throws IOException {
        SessionOutputBuffer sessionOut = super.createHttpDataTransmitter(socket, buffersize, params);
        this.chunkedOutStream = new OrbChunkedOutputStream(sessionOut);
        this.divisionOutStream = new OrbDivisionOutputStream(sessionOut);
        return sessionOut;
    }

    @Override // org.apache.http.impl.AbstractHttpServerConnection, org.apache.http.HttpServerConnection
    public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
        if (!this.unsetChunked) {
            super.sendResponseHeader(response);
        }
    }

    @Override // org.apache.http.impl.AbstractHttpServerConnection, org.apache.http.HttpServerConnection
    public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
        if (!this.unsetChunked) {
            super.sendResponseEntity(response);
        }
    }

    public void unsetChunked() {
        this.unsetChunked = true;
    }

    public OrbChunkedOutputStream getChunkedOutputStream() {
        return this.chunkedOutStream;
    }

    public OrbDivisionOutputStream getDivisionOutputStream() {
        return this.divisionOutStream;
    }
}
