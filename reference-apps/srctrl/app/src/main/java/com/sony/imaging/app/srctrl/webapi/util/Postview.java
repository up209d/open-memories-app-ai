package com.sony.imaging.app.srctrl.webapi.util;

/* loaded from: classes.dex */
public class Postview {
    private byte[] picture;
    private String url;

    public Postview(byte[] picture, String url) {
        this.picture = picture;
        this.url = url;
    }

    public void initData() {
        this.picture = null;
        this.url = null;
    }

    public void setPostview(byte[] picture, String url) {
        this.picture = picture;
        this.url = url;
    }

    public byte[] getPicture() {
        return this.picture;
    }

    public String getUrl() {
        return this.url;
    }
}
