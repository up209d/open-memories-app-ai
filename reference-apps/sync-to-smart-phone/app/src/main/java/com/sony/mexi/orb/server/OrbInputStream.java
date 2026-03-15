package com.sony.mexi.orb.server;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

/* loaded from: classes.dex */
public class OrbInputStream extends ServletInputStream {
    private InputStream inputstream;

    public OrbInputStream(InputStream is) {
        this.inputstream = is;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return this.inputstream.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return this.inputstream.read(b);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        return this.inputstream.read(b, off, len);
    }
}
