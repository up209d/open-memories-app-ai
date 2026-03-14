package com.sony.imaging.app.digitalfilter.common;

import android.graphics.Bitmap;
import java.io.Serializable;

/* loaded from: classes.dex */
public class TimeLapseBO implements Serializable {
    private static final long serialVersionUID = 1;
    private int id;
    private int themeName = 0;
    private String startDate = null;
    private String startTime = null;
    private long startUtcDateTime = 0;
    private long endUtcDateTime = 0;
    private int shootingNumber = 0;
    private String movieMode = null;
    private int aspectRatio = 0;
    private int width = 0;
    private int height = 0;
    private int fps = 0;
    private int saveImage = 0;
    private String version = "100";
    private String attr1 = null;
    private String attr2 = null;
    private String mFullPathFileName = null;
    private String mSequelFullPathFileName = null;
    private String shootingMode = "STILL";
    private Bitmap optimizedBitmap = null;

    public Bitmap getOptimizedBitmap() {
        return this.optimizedBitmap;
    }

    public int getAspectRatio() {
        return this.aspectRatio;
    }

    public void setAspectRatio(int aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void setOptimizedBitmap(Bitmap optimizedBitmap) {
        this.optimizedBitmap = optimizedBitmap;
    }

    public String getMovieMode() {
        return this.movieMode;
    }

    public void setMovieMode(String moviemode) {
        this.movieMode = moviemode;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThemeName() {
        return this.themeName;
    }

    public void setThemeName(int themename) {
        this.themeName = themename;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startdate) {
        this.startDate = startdate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String starttime) {
        this.startTime = starttime;
    }

    public long getStartUtcDateTime() {
        return this.startUtcDateTime;
    }

    public void setStartUtcDateTime(long startutcdatetime) {
        this.startUtcDateTime = startutcdatetime;
    }

    public long getEndUtcDateTime() {
        return this.endUtcDateTime;
    }

    public void setEndUtcDateTime(long endutcdatetime) {
        this.endUtcDateTime = endutcdatetime;
    }

    public int getShootingNumber() {
        return this.shootingNumber;
    }

    public void setShootingNumber(int shootingnumber) {
        this.shootingNumber = shootingnumber;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int mWidth) {
        this.width = mWidth;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int mHeight) {
        this.height = mHeight;
    }

    public int getFps() {
        return this.fps;
    }

    public void setFps(int mFps) {
        this.fps = mFps;
    }

    public String getStartFullPathFileName() {
        return this.mFullPathFileName;
    }

    public void setStartFullPathFileName(String fullPathFileName) {
        this.mFullPathFileName = fullPathFileName;
    }

    public String getSequelFullPathFileName() {
        return this.mSequelFullPathFileName;
    }

    public void setSequelFullPathFileName(String fullPathFileName) {
        this.mSequelFullPathFileName = fullPathFileName;
    }

    public String getShootingMode() {
        return this.shootingMode;
    }

    public void setShootingMode(String shootingMode) {
        this.shootingMode = shootingMode;
    }

    public void setSaveImage(int saveImage) {
        this.saveImage = saveImage;
    }

    public int getSaveImage() {
        return this.saveImage;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr1() {
        return this.attr1;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public String getAttr2() {
        return this.attr2;
    }
}
