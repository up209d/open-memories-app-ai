package com.sony.imaging.app.soundphoto.database;

/* loaded from: classes.dex */
public class AutoSync {
    private long endUtcTime;
    private int id;
    private int numberOfImages;
    private long startUtcTime;

    public AutoSync() {
    }

    public AutoSync(long startUtcTime, long endUtcTime, int numberOfImages) {
        this.startUtcTime = startUtcTime;
        this.endUtcTime = endUtcTime;
        this.numberOfImages = numberOfImages;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartUtcTime() {
        return this.startUtcTime;
    }

    public void setStartUtcTime(long startUtcTime) {
        this.startUtcTime = startUtcTime;
    }

    public long getEndUtcTime() {
        return this.endUtcTime;
    }

    public void setEndUtcTime(long endUtcTime) {
        this.endUtcTime = endUtcTime;
    }

    public int getNumberOfImages() {
        return this.numberOfImages;
    }

    public void setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    public String toString() {
        return "ID : " + this.id + " START UTC TIME : " + this.startUtcTime + " END UTC TIME : " + this.endUtcTime + " NUMBER OF IMAGES: " + this.numberOfImages;
    }
}
