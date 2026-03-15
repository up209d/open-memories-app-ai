package com.sony.mexi.orb.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

/* loaded from: classes.dex */
public class OrbOutputStream extends ServletOutputStream {
    private ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.outStream.write(b, off, len);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.outStream.write(b);
    }

    public String toString() {
        return this.outStream.toString();
    }

    public void writeTo(OutputStream out) throws IOException {
        this.outStream.writeTo(out);
    }

    public void writeDivisionTo(OrbDivisionOutputStream divisionOutStream) throws IOException {
        byte[] bufferdData = this.outStream.toByteArray();
        divisionOutStream.flushCacheWithAppend(bufferdData, 0, bufferdData.length);
        this.outStream.reset();
    }

    public void writeChunkTo(OrbChunkedOutputStream chunkedOutStream) throws IOException {
        byte[] bufferdData = this.outStream.toByteArray();
        chunkedOutStream.flushCacheWithAppend(bufferdData, 0, bufferdData.length);
        this.outStream.reset();
    }
}
