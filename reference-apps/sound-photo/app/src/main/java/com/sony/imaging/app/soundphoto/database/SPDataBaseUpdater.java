package com.sony.imaging.app.soundphoto.database;

import android.os.Environment;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SPDataBaseUpdater {
    private static final String TAG = "SPDataBaseUpdater";
    private static SPDataBaseUpdater mDataBaseInitiator;

    public static SPDataBaseUpdater getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mDataBaseInitiator == null) {
            mDataBaseInitiator = new SPDataBaseUpdater();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mDataBaseInitiator;
    }

    public void saveSoundPhotoBOData() {
        AppLog.enter(TAG, "createSoundPhotoFile saved in Database");
        if (SPUtil.getInstance().getmRecordingInfoArraySequence() != null) {
            AppLog.trace(TAG, "createSoundPhotoFile saved in Database saved  SPUtil.getInstance().size()= " + SPUtil.getInstance().getmRecordingInfoArraySequence().size());
            if (SPUtil.getInstance().getmRecordingInfoArraySequence().size() > 0) {
                updateDataBase(0, 1);
                DataBaseOperations.getInstance().exportDatabase();
                AppLog.trace(TAG, "createSoundPhotoFile saved in Database saved finished");
            }
        }
        AppLog.exit(TAG, "createSoundPhotoFile saved in Database");
    }

    public void saveAllSoundPhotoBOData() {
        AppLog.enter(TAG, "createSoundPhotoFile saved in Database saveAllSoundPhotoBOData");
        if (SPUtil.getInstance().getmRecordingInfoArraySequence() != null) {
            int size = SPUtil.getInstance().getmRecordingInfoArraySequence().size();
            AppLog.trace(TAG, "createSoundPhotoFile saved in Database saved  SPUtil.getInstance().size()= " + size);
            if (size > 0) {
                updateDataBase(0, size);
            }
            int sizeLatest = SPUtil.getInstance().getmRecordingInfoArraySequence().size();
            if (size < sizeLatest) {
                updateDataBase(size, sizeLatest);
            }
            DataBaseOperations.getInstance().exportDatabase();
            AppLog.trace(TAG, "createSoundPhotoFile saved in Database saved finished");
            SPUtil.getInstance().getmRecordingInfoArraySequence().clear();
        }
        AppLog.exit(TAG, "createSoundPhotoFile saved in Database");
    }

    private void updateDataBase(int startSize, int endSize) {
        AppLog.enter(TAG, AppLog.getMethodName());
        for (int index = startSize; index <= endSize - 1; index++) {
            AppLog.trace(TAG, "createSoundPhotoFile saved in Database  index= " + index);
            CameraEx.StoreImageInfo info = SPUtil.getInstance().getmRecordingInfoArraySequence().poll();
            String _filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + SPConstants.FILE_SEPARATER + info.DirectoryName + info.FileName + ".JPG";
            AppLog.trace(TAG, "createSoundPhotoFile saved in Database saved _filePath= " + _filePath);
            DataBaseOperations.getInstance().saveJpeg(0, _filePath, info.DirectoryNo, info.FileNo);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
