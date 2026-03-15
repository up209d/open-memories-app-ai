package com.sony.imaging.app.startrails.playback.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.sony.imaging.app.avi.AviParser;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.database.StarTrailsBO;
import com.sony.imaging.app.startrails.metadatamanager.STException;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.FileHelper;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.OptimizedImageFactory;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;
import java.io.File;

/* loaded from: classes.dex */
public class PlayBackController {
    public static final byte BUFFER_PLAYBACK_STATE = 0;
    public static final byte INDEX_PLAYBACK_STATE = 1;
    private static final int JPEGDBSIZE = 2097152;
    private static final int MAX_SKIP = 10;
    public static final byte NO_MEMORY_PLAYBACK_STATE = 1;
    public static final byte SHOOTING_STATE = 2;
    public static final byte SINGLE_PLAYBACK_STATE = 0;
    private static final String TAG = PlayBackController.class.getName();
    public static PlayBackController mInstance = null;
    private AviParser mAviParser = null;
    private AviParser.Options mAviOptions = null;
    private DSP dsp = null;
    private DeviceBuffer jpeg = null;
    private OptimizedImage mOptimizedImage = null;
    private Bitmap mThumbnailBitmap = null;
    private Cursor cursor = null;
    private StarTrailsBO mStarTrailsBO = null;
    private ContentResolver mContentResolver = null;
    private int mImageNum = 0;
    private byte currentPBState = 0;
    private byte prevPBState = 0;
    private byte mPlayBackStateBuffer = 0;

    private PlayBackController() {
    }

    public static PlayBackController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mInstance == null) {
            mInstance = new PlayBackController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mInstance;
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public byte getCurrentPBState() {
        return this.currentPBState;
    }

    public void setCurrentPBState(byte playbackState) {
        this.currentPBState = playbackState;
    }

    public byte getPrevPBState() {
        return this.prevPBState;
    }

    public void setPrevPBState(byte playbackState) {
        this.prevPBState = playbackState;
    }

    public byte getBufferPBState() {
        return this.mPlayBackStateBuffer;
    }

    public void setBufferPBState(byte playbackState) {
        this.mPlayBackStateBuffer = playbackState;
    }

    public void setDefaultPBState() {
        this.currentPBState = (byte) 0;
        this.prevPBState = (byte) 0;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean moveToNext() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean bFlag = true;
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            bFlag = this.cursor.move(10);
        } else {
            this.mImageNum += 10;
            if (this.mImageNum >= this.mStarTrailsBO.getShootingNumber() - 1) {
                bFlag = false;
                this.mImageNum = this.mStarTrailsBO.getShootingNumber() - 1;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bFlag;
    }

    public void moveToFirst() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            this.cursor.moveToFirst();
        } else {
            this.mImageNum = 0;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean moveToPrevious() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isTrue = true;
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            isTrue = this.cursor.moveToPrevious();
        } else {
            this.mImageNum--;
            if (this.mImageNum <= 0) {
                isTrue = false;
                this.mImageNum = 0;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isTrue;
    }

    public int getCount() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            return this.cursor.getCount();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mStarTrailsBO.getShootingNumber();
    }

    public int getPosition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            return this.cursor.getPosition();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mImageNum;
    }

    public void moveToPosition(int currentPosition) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            this.cursor.moveToPosition(currentPosition);
        } else {
            this.mImageNum = currentPosition;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getTime() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor == null) {
                return "";
            }
            String str = getCursorPBData("content_created_local_time");
            return str;
        }
        String str2 = this.mStarTrailsBO.getStartTime();
        return str2;
    }

    public String getDate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor == null) {
                return "";
            }
            String str = getCursorPBData(ILocalDate.CONTENT_CREATED_LOCAL_DATE);
            return str;
        }
        String str2 = this.mStarTrailsBO.getStartDate();
        return str2;
    }

    public String getFolderNumber() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String str = null;
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor != null) {
                String str2 = getCursorPBData(IFolder.DCF_FOLDER_NUMBER);
                str = str2 + "-" + getCursorPBData("dcf_file_number").trim();
            }
        } else {
            String str3 = STUtility.getInstance().getAviFoldername(this.mStarTrailsBO.getFullPathFileName());
            str = str3 + "-" + STUtility.getInstance().getAviFilename(this.mStarTrailsBO.getFullPathFileName());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return str;
    }

    public int getShootingnum() {
        return this.mStarTrailsBO.getShootingNumber();
    }

    public StarTrailsBO getmStarTrailsBO() {
        return this.mStarTrailsBO;
    }

    public void setmStarTrailsBO(StarTrailsBO mStarTrailsBO) {
        this.mStarTrailsBO = mStarTrailsBO;
    }

    public void setContentResolver(ContentResolver cr) {
        this.mContentResolver = cr;
    }

    private void closeCursor() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.cursor != null) {
            this.cursor.close();
            this.cursor = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void releaseOptimizedImage() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mOptimizedImage != null) {
            this.mOptimizedImage.release();
            this.mOptimizedImage = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getCursorPBData(String info) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int count = 0;
        if (this.cursor != null) {
            count = this.cursor.getCount();
        } else {
            AppLog.info(TAG, TAG + " 2013_IM_SCALARA_DLAPP6 IMDLAPP6-398. [Timelapse 2.0][UTD] App freezes on deleting screen");
        }
        if (count < 1) {
            return "";
        }
        int index = this.cursor.getColumnIndex(info);
        String string = this.cursor.getString(index);
        AppLog.exit(TAG, AppLog.getMethodName());
        return string;
    }

    public void intializeCursorData(int currentPlay) {
        AppLog.enter(TAG, AppLog.getMethodName());
        try {
            this.mStarTrailsBO = DataBaseOperations.getInstance().getStartrailsBOList().get(currentPlay);
            if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
                closeCursor();
                this.cursor = DataBaseOperations.getInstance().queryLastContent(this.mStarTrailsBO);
                if (this.cursor != null) {
                    this.cursor.moveToFirst();
                }
            } else {
                createAviParserOptionData();
            }
        } catch (Exception e) {
            Log.d(TAG, "Cursor null");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void createAviParserOptionData() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mImageNum = 0;
        this.mAviOptions = new AviParser.Options();
        this.mAviOptions.width = this.mStarTrailsBO.getWidth();
        this.mAviOptions.height = this.mStarTrailsBO.getHeight();
        this.mAviOptions.fps = this.mStarTrailsBO.getFps();
        this.mAviOptions.fullPathFileName = this.mStarTrailsBO.getFullPathFileName();
        this.mAviOptions.libpath = STConstants.LIB_PATH;
        if (this.mAviParser == null) {
            this.mAviParser = new AviParser();
            initializeAviParserData();
        }
        this.mAviParser.open(this.mAviOptions);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public AviParser getAviParser() {
        return this.mAviParser;
    }

    public boolean isDataExist() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean result = false;
        if (this.mStarTrailsBO != null && this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor != null && this.cursor.getCount() > 0) {
                result = true;
            }
        } else if (this.mAviParser != null) {
            result = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return result;
    }

    private void releaseAviParser() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mAviParser != null) {
            try {
                this.mAviParser.close();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            this.mAviParser = null;
        }
        if (this.jpeg != null) {
            this.jpeg.release();
            this.jpeg = null;
        }
        if (this.dsp != null) {
            this.dsp.release();
            this.dsp = null;
        }
        this.mImageNum = 0;
        this.mAviOptions = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void releaseAllAllocatedData() {
        AppLog.enter(TAG, AppLog.getMethodName());
        releaseBitmapData();
        releaseAviParser();
        releaseOptimizedImage();
        closeCursor();
        this.mImageNum = 0;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void releaseBitmapData() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mThumbnailBitmap != null && !this.mThumbnailBitmap.isRecycled()) {
            AppLog.trace(TAG, "createBitmap() of PlaybackController Bitmap Recycled");
            this.mThumbnailBitmap.recycle();
            this.mThumbnailBitmap = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void initializeAviParserData() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.dsp = DSP.createProcessor("sony-di-dsp");
        this.mOptimizedImage = this.dsp.createImage(this.mAviOptions.width, this.mAviOptions.height);
        this.jpeg = this.dsp.createBuffer(JPEGDBSIZE);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public OptimizedImage getPlayBackOptimizedImage() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStarTrailsBO != null) {
            if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
                releaseOptimizedImage();
                String uri = getCursorPBData("_data");
                OptimizedImageFactory.Options options = new OptimizedImageFactory.Options();
                options.bBasicInfo = true;
                this.mOptimizedImage = OptimizedImageFactory.decodeImage(uri, options);
            } else {
                try {
                    this.mOptimizedImage = getFrame();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mOptimizedImage;
    }

    private OptimizedImage getFrame() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mAviParser.getFrame(this.mImageNum, this.jpeg);
        this.mAviParser.decodeFrame(this.jpeg, this.mOptimizedImage);
        Log.d(TAG, "getFrame() returns ***** " + this.mOptimizedImage + ExposureModeController.SOFT_SNAP + this.mImageNum + "  " + this.jpeg);
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mOptimizedImage;
    }

    public synchronized void deletePlaybackImages(int currentPlay) throws STException {
        AppLog.enter(TAG, AppLog.getMethodName());
        try {
            this.mStarTrailsBO = DataBaseOperations.getInstance().getStartrailsBOList().get(currentPlay);
            DataBaseOperations.getInstance().removeElementAt(currentPlay);
            if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
                closeCursor();
                this.cursor = DataBaseOperations.getInstance().queryLastContent(this.mStarTrailsBO);
                if (this.cursor != null) {
                    this.cursor.moveToFirst();
                }
                deleteJpegImages();
            } else {
                releaseAviParser();
                deleteAvi_ThumbnailFiles();
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to delete the PlayBack images");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void deleteJpegImages() {
        AppLog.enter(TAG, AppLog.getMethodName());
        do {
            String uniqId = this.cursor.getString(this.cursor.getColumnIndex("_data"));
            ContentsIdentifier contentIdentifier = new ContentsIdentifier(this.cursor.getLong(this.cursor.getColumnIndex("_id")), uniqId, getMediaId());
            deleteContents(contentIdentifier);
            CameraNotificationManager.getInstance().requestNotify(STConstants.DELETE_FRAME);
        } while (this.cursor.moveToNext());
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        dataBaseOperations.deleteRowFromDB(this.mStarTrailsBO);
        DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
            dataBaseOperations.importDatabase();
        }
        CameraNotificationManager.getInstance().requestNotify(STConstants.UPDATE_DELETED_FRAME);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected String getMediaId() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        AppLog.exit(TAG, AppLog.getMethodName());
        return externalMedias[0];
    }

    private int deleteContents(ContentsIdentifier id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Uri uri = id.getContentUri();
        boolean result = AvindexStore.Images.Media.deleteImage(this.mContentResolver, uri, id._id);
        if (result) {
            AvindexStore.Images.waitAndUpdateDatabase(this.mContentResolver, id.mediaId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return result ? 1 : 0;
    }

    private void deleteAvi_ThumbnailFiles() throws STException {
        AppLog.enter(TAG, AppLog.getMethodName());
        File file = new File(this.mStarTrailsBO.getFullPathFileName());
        if (FileHelper.exists(file)) {
            file.delete();
        }
        String path = this.mStarTrailsBO.getFullPathFileName();
        int index = path.indexOf(StringBuilderThreadLocal.PERIOD);
        String thumbnail = path.substring(0, index) + ".THM";
        CameraNotificationManager.getInstance().requestNotify(STConstants.UPDATE_DELETED_FRAME_AVI);
        File file2 = new File(thumbnail);
        if (FileHelper.exists(file2)) {
            file2.delete();
            Log.d(TAG, "Thumnail file deleted");
        }
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        dataBaseOperations.deleteRowFromDB(this.mStarTrailsBO);
        DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
            dataBaseOperations.importDatabase();
        }
        CameraNotificationManager.getInstance().requestNotify(STConstants.UPDATE_DELETED_FRAME);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void createBitmap(StarTrailsBO tlBO, ContentResolver mResolver) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mContentResolver = mResolver;
        this.mStarTrailsBO = tlBO;
        this.mThumbnailBitmap = tlBO.getOptimizedBitmap();
        releaseBitmapData();
        closeCursor();
        this.cursor = DataBaseOperations.getInstance().queryLastContent(this.mStarTrailsBO);
        if (this.cursor != null && this.cursor.getCount() > 0) {
            this.cursor.moveToFirst();
            String uniqId = this.cursor.getString(this.cursor.getColumnIndex("_data"));
            ContentsIdentifier contentIdentifier = new ContentsIdentifier(this.cursor.getLong(this.cursor.getColumnIndex("_id")), uniqId, getMediaId());
            AvindexContentInfo mInfo = AvindexStore.Images.Media.getImageInfo(contentIdentifier.data);
            byte[] data = mInfo.getThumbnail();
            tlBO.setAspectRatio(mInfo.getAttributeInt("AspectRatio", 0));
            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                this.mThumbnailBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            }
            tlBO.setOptimizedBitmap(this.mThumbnailBitmap);
        }
        this.mStarTrailsBO = null;
        AppLog.exit(TAG, AppLog.getMethodName());
        closeCursor();
    }

    public Bitmap getBitmap(StarTrailsBO tlBO, ContentResolver mResolver) {
        Bitmap b;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mContentResolver = mResolver;
        this.mStarTrailsBO = tlBO;
        if (this.mStarTrailsBO.getShootingMode().equalsIgnoreCase("STILL")) {
            createBitmap(this.mStarTrailsBO, mResolver);
            b = tlBO.getOptimizedBitmap();
        } else {
            b = STUtility.getInstance().getThumbnailFilename(this.mStarTrailsBO.getFullPathFileName());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return b;
    }
}
