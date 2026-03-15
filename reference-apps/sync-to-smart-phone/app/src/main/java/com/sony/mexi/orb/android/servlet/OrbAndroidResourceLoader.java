package com.sony.mexi.orb.android.servlet;

import android.content.res.AssetManager;
import com.sony.mexi.orb.servlet.OrbResourceLoader;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class OrbAndroidResourceLoader extends OrbResourceLoader {
    private static AssetManager assetManager = null;
    private static final long serialVersionUID = 1;

    public static void setAssertManager(AssetManager am) {
        assetManager = am;
    }

    public String getRootPath() {
        return "/doc/";
    }

    @Override // com.sony.mexi.orb.servlet.OrbResourceLoader
    protected InputStream getContentStream(String uri) {
        try {
            if (assetManager == null || !uri.startsWith(getRootPath())) {
                return null;
            }
            String path = uri.charAt(0) == '/' ? uri.substring(1) : uri;
            return assetManager.open(path);
        } catch (IOException e) {
            return null;
        }
    }
}
