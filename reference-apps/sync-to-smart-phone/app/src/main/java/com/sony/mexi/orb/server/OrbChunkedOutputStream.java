package com.sony.mexi.orb.server;

import java.io.IOException;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.io.SessionOutputBuffer;

/* loaded from: classes.dex */
public class OrbChunkedOutputStream extends ChunkedOutputStream {
    public OrbChunkedOutputStream(SessionOutputBuffer out) throws IOException {
        super(out);
    }

    @Override // org.apache.http.impl.io.ChunkedOutputStream
    public void flushCacheWithAppend(byte[] bufferToAppend, int off, int len) throws IOException {
        super.flushCacheWithAppend(bufferToAppend, off, len);
    }

    @Override // org.apache.http.impl.io.ChunkedOutputStream
    public void writeClosingChunk() throws IOException {
        super.writeClosingChunk();
    }
}
