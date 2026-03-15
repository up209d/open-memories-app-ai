package com.sony.imaging.app.base.playback.contents;

/* loaded from: classes.dex */
public class SearchInfo {
    public int fileNumber;
    public int folderNumber;
    String localDate;
    public long localDateTime;
    String utcDate;
    public long utcDateTime;

    public SearchInfo(int folderNumber, int fileNumber) {
        this.folderNumber = -1;
        this.fileNumber = -1;
        this.localDateTime = Long.MIN_VALUE;
        this.utcDateTime = Long.MIN_VALUE;
        this.localDate = null;
        this.utcDate = null;
        this.folderNumber = folderNumber;
        this.fileNumber = fileNumber;
    }

    public SearchInfo(int folderNumber, int fileNumber, String localDate, String utcDate) {
        this(folderNumber, fileNumber);
        this.localDate = localDate;
        this.utcDate = utcDate;
    }

    public SearchInfo(int folderNumber, int fileNumber, String localDate, String utcDate, long localDateTime, long utcDateTime) {
        this(folderNumber, fileNumber, localDate, utcDate);
        this.localDateTime = localDateTime;
        this.utcDateTime = utcDateTime;
    }

    public int getFolderNumber() {
        return this.folderNumber;
    }

    public int getFileNumber() {
        return this.fileNumber;
    }

    public String getLocalDate() {
        return this.localDate;
    }

    public String getUtcDate() {
        return this.utcDate;
    }

    public long getLocalDateTime() {
        return this.localDateTime;
    }

    public long getUtcDateTime() {
        return this.utcDateTime;
    }
}
