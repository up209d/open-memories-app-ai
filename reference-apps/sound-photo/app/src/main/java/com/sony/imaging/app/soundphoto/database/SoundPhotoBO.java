package com.sony.imaging.app.soundphoto.database;

import android.graphics.Bitmap;
import java.io.Serializable;

/* loaded from: classes.dex */
public class SoundPhotoBO implements Serializable {
    private static final long serialVersionUID = 1;
    private int _imageId;
    private int mFileNumber;
    private int mFolderNumber;
    private String mFilePath = null;
    private int mAudioDuration = 0;
    private int mFileSize = 0;
    private Bitmap optimizedBitmap = null;

    public int getmAudioDuration() {
        return this.mAudioDuration;
    }

    public void setmAudioDuration(int mAudioDuration) {
        this.mAudioDuration = mAudioDuration;
    }

    public int getmFileSize() {
        return this.mFileSize;
    }

    public void setmFileSize(int mFileSize) {
        this.mFileSize = mFileSize;
    }

    public Bitmap getOptimizedBitmap() {
        return this.optimizedBitmap;
    }

    public void setOptimizedBitmap(Bitmap optimizedBitmap) {
        this.optimizedBitmap = optimizedBitmap;
    }

    public String getFilePath() {
        return this.mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public int getContentId() {
        return this._imageId;
    }

    public void setContentID(int _id) {
        this._imageId = _id;
    }

    public int getFolderNumber() {
        return this.mFolderNumber;
    }

    public void setFolderNumber(int folderNumber) {
        this.mFolderNumber = folderNumber;
    }

    public int getFileNumber() {
        return this.mFileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.mFileNumber = fileNumber;
    }
}
