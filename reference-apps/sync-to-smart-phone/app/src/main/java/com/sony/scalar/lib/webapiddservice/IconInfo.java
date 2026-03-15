package com.sony.scalar.lib.webapiddservice;

/* loaded from: classes.dex */
public class IconInfo {
    Integer mDepth;
    Integer mHeight;
    String mMimetype;
    String mUrl;
    Integer mWidth;

    public IconInfo() {
        this.mUrl = "";
        this.mMimetype = "";
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDepth = 0;
    }

    public IconInfo(String url, String mime, int width, int height, int depth) {
        this.mUrl = "";
        this.mMimetype = "";
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDepth = 0;
        this.mUrl = url;
        this.mMimetype = mime;
        this.mWidth = Integer.valueOf(width);
        this.mHeight = Integer.valueOf(height);
        this.mDepth = Integer.valueOf(depth);
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setMimetype(String type) {
        this.mMimetype = type;
    }

    public String getMimetype() {
        return this.mMimetype;
    }

    public void setWidth(int width) {
        this.mWidth = Integer.valueOf(width);
    }

    public Integer getWidth() {
        return this.mWidth;
    }

    public void setHeight(int height) {
        this.mHeight = Integer.valueOf(height);
    }

    public Integer getHeight() {
        return this.mHeight;
    }

    public void setDepth(int depth) {
        this.mDepth = Integer.valueOf(depth);
    }

    public Integer getDepth() {
        return this.mDepth;
    }
}
