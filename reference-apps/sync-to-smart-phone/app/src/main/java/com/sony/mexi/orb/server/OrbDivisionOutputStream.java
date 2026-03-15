package com.sony.mexi.orb.server;

import java.io.IOException;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.io.SessionOutputBuffer;

/* loaded from: classes.dex */
public class OrbDivisionOutputStream extends ChunkedOutputStream {
    private SessionOutputBuffer out;

    public OrbDivisionOutputStream(SessionOutputBuffer out) throws IOException {
        super(out);
        this.out = out;
    }

    @Override // org.apache.http.impl.io.ChunkedOutputStream
    public void flushCacheWithAppend(byte[] bufferToAppend, int off, int len) throws IOException {
        this.out.write(bufferToAppend, off, len);
    }
}
