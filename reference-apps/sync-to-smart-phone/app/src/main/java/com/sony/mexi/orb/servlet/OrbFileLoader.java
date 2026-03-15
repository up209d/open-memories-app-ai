package com.sony.mexi.orb.servlet;

import java.io.InputStream;

/* loaded from: classes.dex */
public class OrbFileLoader extends OrbResourceLoader {
    private static final long serialVersionUID = 1;
    private String mappedRoot;
    private String root;

    public OrbFileLoader(String root, String mappedRoot) {
        this.root = root;
        this.mappedRoot = mappedRoot;
    }

    public String getRootPath() {
        return this.root;
    }

    @Override // com.sony.mexi.orb.servlet.OrbResourceLoader
    protected InputStream getContentStream(String uri) {
        if (!uri.startsWith(this.root)) {
            return null;
        }
        String mappedPath = String.valueOf(this.mappedRoot) + uri.substring(this.root.length());
        return OrbFileLoader.class.getResourceAsStream(mappedPath);
    }
}
