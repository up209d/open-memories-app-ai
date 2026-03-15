package com.sony.mexi.orb.server.http;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public abstract class OrbHttpInputStream extends InputStream {
    public abstract byte[] readNextBuffer() throws IOException;
}
