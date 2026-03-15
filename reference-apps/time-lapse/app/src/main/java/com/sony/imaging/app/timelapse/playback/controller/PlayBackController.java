package com.sony.imaging.app.timelapse.playback.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.sony.imaging.app.avi.AviParser;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseOperations;
import com.sony.imaging.app.timelapse.databaseutil.TimeLapseBO;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.metadatamanager.TLException;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.util.FileHelper;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.OptimizedImageFactory;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private TimeLapseBO mTimeLapseBO = null;
    private ContentResolver mContentResolver = null;
    private int mImageNum = 0;
    private byte currentPBState = 0;
    private byte prevPBState = 0;
    private byte mPlayBackStateBuffer = 0;
    private AviFileList mAviFileList = null;

    private PlayBackController() {
    }

    public static PlayBackController getInstance() {
        if (mInstance == null) {
            mInstance = new PlayBackController();
        }
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
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            boolean bFlag = this.cursor.move(10);
            return bFlag;
        }
        this.mImageNum += 10;
        if (this.mImageNum < this.mTimeLapseBO.getShootingNumber() - 1) {
            return true;
        }
        this.mImageNum = this.mTimeLapseBO.getShootingNumber() - 1;
        return false;
    }

    public boolean moveToNext(int skip, int end) {
        int pos = this.cursor.getPosition() + skip;
        if (pos <= end) {
            return this.cursor.moveToPosition(pos);
        }
        this.cursor.moveToPosition(end);
        return false;
    }

    public void moveToFirst() {
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            this.cursor.moveToFirst();
        } else {
            this.mImageNum = 0;
        }
    }

    public boolean moveToPrevious() {
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            boolean isTrue = this.cursor.moveToPrevious();
            return isTrue;
        }
        this.mImageNum--;
        if (this.mImageNum > 0) {
            return true;
        }
        this.mImageNum = 0;
        return false;
    }

    public boolean moveToPrevious(int skip, int start) {
        int pos = this.cursor.getPosition() - skip;
        if (pos >= start) {
            return this.cursor.moveToPosition(pos);
        }
        this.cursor.moveToPosition(start);
        return false;
    }

    public int getCount() {
        return this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL") ? this.cursor.getCount() : this.mTimeLapseBO.getShootingNumber();
    }

    public int getPosition() {
        return this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL") ? this.cursor.getPosition() : this.mImageNum;
    }

    public void moveToPosition(int currentPosition) {
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            this.cursor.moveToPosition(currentPosition);
        } else {
            this.mImageNum = currentPosition;
        }
    }

    public String getTime() {
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor == null) {
                return "";
            }
            String str = getCursorPBData("content_created_local_time");
            return str;
        }
        String str2 = this.mTimeLapseBO.getStartTime();
        return str2;
    }

    public String getDate() {
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor == null) {
                return "";
            }
            String str = getCursorPBData(ILocalDate.CONTENT_CREATED_LOCAL_DATE);
            return str;
        }
        String str2 = this.mTimeLapseBO.getStartDate();
        return str2;
    }

    public String getFolderNumber() {
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor == null) {
                return null;
            }
            String str = getCursorPBData(IFolder.DCF_FOLDER_NUMBER);
            return str + "-" + getCursorPBData("dcf_file_number").trim();
        }
        String str2 = TLCommonUtil.getInstance().getAviFoldername(this.mTimeLapseBO.getStartFullPathFileName());
        return str2 + "-" + TLCommonUtil.getInstance().getAviFilename(this.mTimeLapseBO.getStartFullPathFileName());
    }

    public int getShootingnum() {
        return this.mTimeLapseBO.getShootingNumber();
    }

    public TimeLapseBO getmTimeLapseBO() {
        return this.mTimeLapseBO;
    }

    public void setmTimeLapseBO(TimeLapseBO mTimeLapseBO) {
        this.mTimeLapseBO = mTimeLapseBO;
    }

    public void setContentResolver(ContentResolver cr) {
        this.mContentResolver = cr;
    }

    private void closeCursor() {
        if (this.cursor != null) {
            this.cursor.close();
            this.cursor = null;
        }
    }

    public void releaseOptimizedImage() {
        if (this.mOptimizedImage != null) {
            this.mOptimizedImage.release();
            this.mOptimizedImage = null;
        }
    }

    private String getCursorPBData(String info) {
        int count = 0;
        if (this.cursor != null) {
            count = this.cursor.getCount();
        } else {
            AppLog.info(TAG, TAG + " 2013_IM_SCALARA_DLAPP6 IMDLAPP6-398. [Timelapse 2.0][UTD] App freezes on deleting screen");
        }
        if (count < 1) {
            return "";
        }
        AppLog.info(TAG, "info " + info);
        int index = this.cursor.getColumnIndex(info);
        AppLog.info(TAG, "index " + index);
        AppLog.info(TAG, "cursor " + this.cursor);
        return this.cursor.getString(index);
    }

    private String getAviPrefix(String aviFileName) {
        String prefixName = aviFileName.substring(0, aviFileName.length() - "00.AVI".length());
        return prefixName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AviFileList {
        private int index = 0;
        private int totalFrameNum = 0;
        private int offsetFrames = 0;
        ArrayList<String> mFileName = new ArrayList<>();
        ArrayList<Integer> mFrameNum = new ArrayList<>();
        ArrayList<Integer> mOffsetFrames = new ArrayList<>();

        public void clear() {
            this.index = 0;
            this.totalFrameNum = 0;
            this.offsetFrames = 0;
            if (this.mFileName != null) {
                this.mFileName.clear();
                this.mFileName = null;
            }
            if (this.mFrameNum != null) {
                this.mFrameNum.clear();
                this.mFrameNum = null;
            }
            if (this.mOffsetFrames != null) {
                this.mOffsetFrames.clear();
                this.mOffsetFrames = null;
            }
        }

        public void add(String fileName, int frameNum) {
            this.mFileName.add(fileName);
            this.mFrameNum.add(Integer.valueOf(frameNum));
            this.mOffsetFrames.add(Integer.valueOf(this.offsetFrames));
            this.offsetFrames += frameNum;
            this.totalFrameNum = this.offsetFrames;
        }

        public boolean setChangedAviFile(int offset) {
            int i = this.mOffsetFrames.size() - 1;
            while (i >= 0 && this.mOffsetFrames.get(i).intValue() > offset) {
                i--;
            }
            if (offset > this.totalFrameNum - 1) {
                i = this.mOffsetFrames.size() - 1;
            }
            if (i != this.index) {
                this.index = i;
                return true;
            }
            return false;
        }

        public String getCurrFileName() {
            return this.mFileName.get(this.index);
        }

        public int getImageNumOfCurrFile(int frameNum) {
            int offset = frameNum - this.mOffsetFrames.get(this.index).intValue();
            if (offset > this.mFrameNum.get(this.index).intValue() - 1) {
                return (this.mFrameNum.get(this.index).intValue() + this.mOffsetFrames.get(this.index).intValue()) - 1;
            }
            return offset;
        }

        public int getFrameNumOfCurrFile() {
            return this.mFrameNum.get(this.index).intValue();
        }

        public int getTotalFrameNum() {
            return this.totalFrameNum;
        }
    }

    private void createAviFileList() {
        AviParser aviParser = new AviParser();
        AviParser.Options aviOptions = new AviParser.Options();
        if (this.mAviFileList == null) {
            this.mAviFileList = new AviFileList();
        } else {
            this.mAviFileList.clear();
        }
        aviOptions.width = this.mTimeLapseBO.getWidth();
        aviOptions.height = this.mTimeLapseBO.getHeight();
        aviOptions.fps = this.mTimeLapseBO.getFps();
        aviOptions.libpath = TimeLapseConstants.LIB_PATH;
        String aviPrefix = getAviPrefix(this.mTimeLapseBO.getStartFullPathFileName());
        for (int i = 0; i < 10; i++) {
            aviOptions.fullPathFileName = aviPrefix + String.format("%02d", Integer.valueOf(i)) + ".AVI";
            if (aviParser.open(aviOptions)) {
                this.mAviFileList.add(aviOptions.fullPathFileName, aviParser.getFrameNum());
                try {
                    aviParser.close();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                if (this.mTimeLapseBO.getShootingNumber() <= this.mAviFileList.getTotalFrameNum()) {
                    break;
                }
            } else {
                if (i == 0) {
                    break;
                }
            }
        }
    }

    public void intializeCursorData(int currentPlay) {
        try {
            AppLog.info(TAG, "intializeCursorData currentPlay: " + currentPlay);
            this.mTimeLapseBO = DataBaseOperations.getInstance().getTimeLapseBOList().get(currentPlay);
            if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
                closeCursor();
                this.cursor = DataBaseOperations.getInstance().queryLastContent(this.mTimeLapseBO);
                if (this.cursor != null) {
                    this.cursor.moveToFirst();
                }
            } else {
                createAviFileList();
                createAviParserOptionData();
            }
        } catch (Exception e) {
            Log.d(TAG, "Cursor null");
        }
    }

    private void createAviParserOptionData() {
        this.mImageNum = 0;
        this.mAviOptions = new AviParser.Options();
        this.mAviOptions.width = this.mTimeLapseBO.getWidth();
        this.mAviOptions.height = this.mTimeLapseBO.getHeight();
        this.mAviOptions.fps = this.mTimeLapseBO.getFps();
        this.mAviOptions.fullPathFileName = this.mTimeLapseBO.getStartFullPathFileName();
        this.mAviOptions.libpath = TimeLapseConstants.LIB_PATH;
        if (this.mAviParser == null) {
            this.mAviParser = new AviParser();
            initializeAviParserData();
        }
        this.mAviParser.open(this.mAviOptions);
    }

    private void updateAviParserOptionData() {
        this.mAviOptions.fullPathFileName = this.mAviFileList.getCurrFileName();
        try {
            this.mAviParser.close();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        this.mAviParser.open(this.mAviOptions);
    }

    public AviParser getAviParser() {
        return this.mAviParser;
    }

    public boolean isDataExist() {
        if (this.mTimeLapseBO != null && this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            if (this.cursor == null || this.cursor.getCount() <= 0) {
                return false;
            }
            return true;
        }
        if (this.mAviParser == null) {
            return false;
        }
        return true;
    }

    private void releaseAviParser() {
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
    }

    private void releaseAviFileList() {
        if (this.mAviFileList != null) {
            this.mAviFileList.clear();
            this.mAviFileList = null;
        }
    }

    public void releaseAllAllocatedData() {
        AppLog.info(TAG, "releaseAllAllocatedData");
        releaseBitmapData();
        releaseAviParser();
        releaseAviFileList();
        releaseOptimizedImage();
        closeCursor();
        this.mImageNum = 0;
    }

    private void releaseBitmapData() {
        if (this.mThumbnailBitmap != null && !this.mThumbnailBitmap.isRecycled()) {
            AppLog.trace(TAG, "createBitmap() of PlaybackController Bitmap Recycled");
            this.mThumbnailBitmap.recycle();
            this.mThumbnailBitmap = null;
        }
    }

    private void initializeAviParserData() {
        this.dsp = DSP.createProcessor("sony-di-dsp");
        this.mOptimizedImage = this.dsp.createImage(this.mAviOptions.width, this.mAviOptions.height);
        this.jpeg = this.dsp.createBuffer(JPEGDBSIZE);
    }

    public OptimizedImage getPlayBackOptimizedImage() {
        if (this.mTimeLapseBO != null) {
            if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
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
        return this.mOptimizedImage;
    }

    public OptimizedImage getPlayBackMainImage(boolean isReleased) {
        if (this.mTimeLapseBO != null) {
            if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
                if (isReleased) {
                    releaseOptimizedImage();
                }
                String uri = getCursorPBData("_data");
                OptimizedImageFactory.Options options = new OptimizedImageFactory.Options();
                options.bBasicInfo = true;
                options.imageType = 3;
                this.mOptimizedImage = OptimizedImageFactory.decodeImage(uri, options);
            } else {
                try {
                    this.mOptimizedImage = getFrame();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
        return this.mOptimizedImage;
    }

    private OptimizedImage getFrame() {
        if (this.mAviFileList.setChangedAviFile(this.mImageNum)) {
            updateAviParserOptionData();
        }
        this.mAviParser.getFrame(this.mAviFileList.getImageNumOfCurrFile(this.mImageNum), this.jpeg);
        this.mAviParser.decodeFrame(this.jpeg, this.mOptimizedImage);
        Log.d(TAG, "getFrame() returns ***** " + this.mOptimizedImage + ExposureModeController.SOFT_SNAP + this.mImageNum + "  " + this.jpeg);
        return this.mOptimizedImage;
    }

    public synchronized void deletePlaybackImages(int currentPlay) throws TLException {
        try {
            this.mTimeLapseBO = DataBaseOperations.getInstance().getTimeLapseBOList().get(currentPlay);
            DataBaseOperations.getInstance().removeElementAt(currentPlay);
            if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
                closeCursor();
                this.cursor = DataBaseOperations.getInstance().queryLastContent(this.mTimeLapseBO);
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
    }

    private void deleteJpegImages() {
        do {
            String uniqId = this.cursor.getString(this.cursor.getColumnIndex("_data"));
            ContentsIdentifier contentIdentifier = new ContentsIdentifier(this.cursor.getLong(this.cursor.getColumnIndex("_id")), uniqId, getMediaId());
            deleteContents(contentIdentifier);
            CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.DELETE_FRAME);
        } while (this.cursor.moveToNext());
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        dataBaseOperations.deleteRowFromDB(this.mTimeLapseBO);
        DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
            dataBaseOperations.importDatabase();
        }
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.UPDATE_DELETED_FRAME);
    }

    public void unregisteredJpegContent() {
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        dataBaseOperations.deleteRowFromDB(this.mTimeLapseBO);
        DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
            dataBaseOperations.importDatabase();
        }
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.UPDATE_DELETED_FRAME);
    }

    public void updatedJpegCount() {
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        dataBaseOperations.updateJpegCountsOfDB(this.mTimeLapseBO, this.cursor.getCount());
        DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
            dataBaseOperations.importDatabase();
        }
    }

    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    private int deleteContents(ContentsIdentifier id) {
        Uri uri = id.getContentUri();
        boolean result = AvindexStore.Images.Media.deleteImage(this.mContentResolver, uri, id._id);
        if (result) {
            AvindexStore.Images.waitAndUpdateDatabase(this.mContentResolver, id.mediaId);
        }
        return result ? 1 : 0;
    }

    private void deleteAvi_ThumbnailFiles() throws TLException {
        File file = new File(this.mTimeLapseBO.getStartFullPathFileName());
        if (FileHelper.exists(file)) {
            file.delete();
        }
        String path = this.mTimeLapseBO.getStartFullPathFileName();
        int index = path.indexOf(StringBuilderThreadLocal.PERIOD);
        String thumbnail = path.substring(0, index) + ".THM";
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.UPDATE_DELETED_FRAME_AVI);
        File file2 = new File(thumbnail);
        if (FileHelper.exists(file2)) {
            file2.delete();
            Log.d(TAG, "Thumnail file deleted");
        }
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        dataBaseOperations.deleteRowFromDB(this.mTimeLapseBO);
        DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
            dataBaseOperations.importDatabase();
        }
        CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.UPDATE_DELETED_FRAME);
    }

    public void createBitmap(TimeLapseBO tlBO, ContentResolver mResolver) {
        this.mContentResolver = mResolver;
        this.mTimeLapseBO = tlBO;
        this.mThumbnailBitmap = tlBO.getOptimizedBitmap();
        releaseBitmapData();
        closeCursor();
        this.cursor = DataBaseOperations.getInstance().queryLastContent(this.mTimeLapseBO);
        if (this.cursor != null && this.cursor.getCount() > 0) {
            this.cursor.moveToFirst();
            String uniqId = this.cursor.getString(this.cursor.getColumnIndex("_data"));
            ContentsIdentifier contentIdentifier = new ContentsIdentifier(this.cursor.getLong(this.cursor.getColumnIndex("_id")), uniqId, getMediaId());
            AvindexContentInfo mInfo = AvindexStore.Images.Media.getImageInfo(contentIdentifier.data);
            byte[] data = mInfo.getThumbnail();
            tlBO.setAspectRatio(mInfo.getAttributeInt("AspectRatio", 3));
            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                this.mThumbnailBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            }
            tlBO.setOptimizedBitmap(this.mThumbnailBitmap);
        }
        this.mTimeLapseBO = null;
        closeCursor();
    }

    public Bitmap getBitmap(TimeLapseBO tlBO, ContentResolver mResolver) {
        this.mContentResolver = mResolver;
        this.mTimeLapseBO = tlBO;
        if (this.mTimeLapseBO.getShootingMode().equalsIgnoreCase("STILL")) {
            createBitmap(this.mTimeLapseBO, mResolver);
            Bitmap b = tlBO.getOptimizedBitmap();
            return b;
        }
        Bitmap b2 = TLCommonUtil.getInstance().getThumbnailFilename(this.mTimeLapseBO.getStartFullPathFileName());
        return b2;
    }

    public boolean isSupportedByAngleShiftAddOn() {
        boolean isSupported = true;
        AppLog.info(TAG, "isSupportedByAngleShiftAddOn ListViewLayout.listPosition:" + ListViewLayout.listPosition);
        intializeCursorData(ListViewLayout.listPosition);
        if (this.cursor == null || this.cursor.getCount() == 0) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_NG_FILE);
            return false;
        }
        if (getmTimeLapseBO().getShootingNumber() != this.cursor.getCount()) {
            Log.i(TAG, "updatedJpegCount");
            updatedJpegCount();
        }
        String uniqId = this.cursor.getString(this.cursor.getColumnIndex("_data"));
        ContentsIdentifier contentIdentifier = new ContentsIdentifier(this.cursor.getLong(this.cursor.getColumnIndex("_id")), uniqId, getMediaId());
        AvindexContentInfo mInfo = AvindexStore.Images.Media.getImageInfo(contentIdentifier.data);
        int aspect = mInfo.getAttributeInt("AspectRatio", 3);
        int width = mInfo.getAttributeInt("ImageWidth", 0);
        int height = mInfo.getAttributeInt("ImageLength", 0);
        int contentType = mInfo.getAttributeInt("ContentType", 0);
        List<ScalarProperties.PictureSize> list = ScalarProperties.getSupportedPictureSizes();
        ScalarProperties.PictureSize large_32 = list.get(0);
        ScalarProperties.PictureSize large_169 = list.get(3);
        int maxWidth = Math.max(large_32.width, large_169.width);
        int maxHeight = Math.max(large_32.height, large_169.height);
        int maxSize = Math.max(large_32.width * large_32.height, large_169.width * large_169.height);
        if (list.size() > 6) {
            ScalarProperties.PictureSize large_43 = list.get(6);
            maxWidth = Math.max(maxWidth, large_43.width);
            maxHeight = Math.max(maxHeight, large_43.height);
            maxSize = Math.max(maxSize, large_43.width * large_43.height);
        }
        if (list.size() > 9) {
            ScalarProperties.PictureSize large_11 = list.get(9);
            maxWidth = Math.max(maxWidth, large_11.width);
            maxHeight = Math.max(maxHeight, large_11.height);
            maxSize = Math.max(maxSize, large_11.width * large_11.height);
        }
        AppLog.info(TAG, "isSupportedByAngleShiftAddOn aspect :" + aspect);
        AppLog.info(TAG, "isSupportedByAngleShiftAddOn width :" + width + ", height :" + height);
        AppLog.info(TAG, "isSupportedByAngleShiftAddOn maxWidth :" + maxWidth);
        AppLog.info(TAG, "isSupportedByAngleShiftAddOn maxHeight :" + maxHeight);
        AppLog.info(TAG, "isSupportedByAngleShiftAddOn maxSize :" + maxSize);
        AngleShiftSetting.getInstance().setImageAspect(aspect);
        AngleShiftSetting.getInstance().setImageSize(width, height);
        if (aspect != 1 && aspect != 10 && aspect != 0) {
            if (aspect == 3) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_NG_FILE);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_UNSUPPORTED_ASPECT);
            }
            isSupported = false;
        } else if (width < 1920) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_UNSUPPORTED_SIZE);
            isSupported = false;
        } else if (width * height > maxSize || width > maxWidth || height > maxHeight) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_NG_FILE);
            isSupported = false;
        } else if (15 == contentType && !TLCommonUtil.getInstance().isSupportedRAW()) {
            AppLog.info(TAG, "Application does not support RAW on this device.");
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_NG_FILE);
            isSupported = false;
        } else if (!canDecodeImage(contentIdentifier)) {
            AppLog.info(TAG, "Cannot decode this content on this Device");
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_NG_FILE);
            isSupported = false;
        } else {
            AngleShiftSetting.getInstance().setAngleShiftFirstImageWidth(width);
            AngleShiftSetting.getInstance().setAngleShiftFirstImageHeight(height);
        }
        return isSupported;
    }

    private boolean canDecodeImage(ContentsIdentifier contentIdentifier) {
        OptimizedImage optimizedImage;
        if (contentIdentifier == null || (optimizedImage = ContentsManager.getInstance().getOptimizedImageWithoutCache(contentIdentifier, 1)) == null) {
            return false;
        }
        optimizedImage.release();
        return true;
    }
}
