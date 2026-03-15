package com.sony.imaging.app.snsdirect.util;

import java.io.Serializable;

/* loaded from: classes.dex */
public class ImageDataInfo implements Serializable {
    private Long date = null;
    private int file_num = 0;
    private int folder_num = 0;
    private String comment = null;

    public int getFileNum() {
        return this.file_num;
    }

    public int getFolderNum() {
        return this.folder_num;
    }

    public Long getDate() {
        return this.date;
    }

    public String getComment() {
        return this.comment;
    }

    public void setFileNum(int fileNum) {
        this.file_num = fileNum;
    }

    public void setFolderNum(int folderNum) {
        this.folder_num = folderNum;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
