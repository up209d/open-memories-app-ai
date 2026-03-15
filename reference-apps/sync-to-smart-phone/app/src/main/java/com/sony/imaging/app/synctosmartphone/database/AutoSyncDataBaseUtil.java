package com.sony.imaging.app.synctosmartphone.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IUtcDate;
import com.sony.imaging.app.synctosmartphone.commonUtil.AppContext;
import com.sony.imaging.app.synctosmartphone.commonUtil.AppLog;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.TimeUtil;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class AutoSyncDataBaseUtil {
    public static final int DBUF_MAX = 2097152;
    public static final int MAX_SOUND_DATA_SIZE = 2097152;
    public static final String MS_CARD_PATH = "/MSSONY/CAM_APPS/APP_SYNC";
    public static final String SD_CARD_PATH = "/PRIVATE/SONY/APP_SYNC";
    private static AutoSyncDataBaseUtil sAutoSyncDataBaseUtil = null;
    private static boolean mbReadyForScreennail = false;
    private final String TAG = AppLog.getClassName();
    private final String TABLE_AUTO_SYNC = "AutoSync";
    public final String COL_ID = "ID";
    public final String COL_START_UTC_TIME = "StartUtcTime";
    public final String COL_END_UTC_TIME = "EndUtcTime";
    public final String COL_NUMBER_OF_IMAGES = "NumberOfImages";
    private boolean mbDataBaseCorruptStatus = false;
    private ArrayList<AutoSync> mAutoSyncList = null;
    private AutoSync mAutoSync = null;
    private long mLastStartTime = 0;
    private Cursor mCursor = null;
    private int mCFileNumber = 0;
    private int mCFolderNumber = 0;
    private int mCExistJpeg = 0;
    private int mCExistRAW = 0;
    private int mCUTCDateTime = 0;
    private int mCurrentCursorCount = 0;
    private int mNumOfReservationFiles = 0;
    private long mScreennailSize = 0;
    private boolean mb2MTransfer = true;
    private int mNumOfIllegalDBEntries = 0;
    private int mNumOfIllegalFiles = 0;
    private DeviceBuffer dbuf = null;
    private final String DATETIME_FORMAT_YMD = "yyyy-M-d h:mm a";
    private final String DATETIME_FORMAT_MDY_E = "MMM-d-yyyy h:mm a";
    private final String DATETIME_FORMAT_MDY = "M-d-yyyy h:mm a";
    private final String DATETIME_FORMAT_DMY = "d-M-yyyy H:mm";
    private boolean mScreennailFromAVIndex = false;

    public native int changeRotateInfoMSL(int i, byte[] bArr);

    public native boolean checkSoundPhoto(String str);

    public native void closeMSL();

    public native double createScreennail(int i, int i2, String str, int i3, long j);

    public native int directBuffer(byte[] bArr, int i, int i2);

    public native int finishSoundPhotoProcess();

    public native int getRotateInfoMSL(String str);

    public native double getScreennailDataSize();

    public native int getSoundDataLen();

    public native int getSoundPhoto(ByteBuffer byteBuffer, int i);

    public native boolean isScreennailExisting();

    public native int openMSL(String str);

    public native int setScreennailDevieceBuffer(int i, int i2, int i3, ByteBuffer byteBuffer, int i4, int i5, boolean z);

    static {
        System.loadLibrary("GetScreennail");
    }

    private AutoSyncDataBaseUtil() {
        AppLog.checkIf(this.TAG, "Instance of AutoSyncDataBaseUtil is created.");
    }

    public static AutoSyncDataBaseUtil getInstance() {
        AppLog.enter(AppLog.getClassName(), AppLog.getMethodName());
        if (sAutoSyncDataBaseUtil == null) {
            sAutoSyncDataBaseUtil = new AutoSyncDataBaseUtil();
        }
        AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
        return sAutoSyncDataBaseUtil;
    }

    public boolean insertAutoSync(AutoSync autoSync) {
        boolean retVal;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (autoSync == null) {
            return false;
        }
        AppLog.checkIf(this.TAG, "Auto Sync Content --- " + autoSync.toString());
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put("StartUtcTime", Long.valueOf(autoSync.getStartUtcTime()));
        contentValues.put("EndUtcTime", Long.valueOf(autoSync.getEndUtcTime()));
        contentValues.put("NumberOfImages", Integer.valueOf(autoSync.getNumberOfImages()));
        long recordID = dataBaseAdapter.insert("AutoSync", contentValues);
        if (-1 != recordID) {
            retVal = true;
            if (this.mAutoSyncList != null) {
                autoSync.setId((int) recordID);
                this.mAutoSyncList.add(autoSync);
            }
            AppLog.checkIf(this.TAG, "One AutoSync saved successfully with ID " + recordID);
        } else {
            retVal = false;
            AppLog.checkIf(this.TAG, "AutoSync saving failed and record ID is " + recordID);
        }
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }

    public ArrayList<AutoSync> getAutoSyncListFromDatabase() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        clearAutoSyncList();
        this.mAutoSyncList = new ArrayList<>();
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(2);
        Cursor cursor = dataBaseAdapter.query("select * from AutoSync;", null);
        if (cursor == null || (cursor != null && cursor.getCount() == 0)) {
            AppLog.checkIf(this.TAG, "No record found in AutoSync table.");
            if (cursor != null) {
                cursor.close();
            }
            if (dataBaseAdapter == null) {
                return null;
            }
            dataBaseAdapter.close();
            return null;
        }
        AppLog.checkIf(this.TAG, cursor.getCount() + " record found in AutoSync table, Traversing cursor.....");
        cursor.moveToFirst();
        do {
            AutoSync autoSync = new AutoSync();
            autoSync.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            autoSync.setStartUtcTime(cursor.getLong(cursor.getColumnIndex("StartUtcTime")));
            autoSync.setEndUtcTime(cursor.getLong(cursor.getColumnIndex("EndUtcTime")));
            autoSync.setNumberOfImages(cursor.getInt(cursor.getColumnIndex("NumberOfImages")));
            AppLog.checkIf(this.TAG, " Auto Sync Info --- " + autoSync.toString());
            this.mAutoSyncList.add(autoSync);
        } while (cursor.moveToNext());
        if (cursor != null) {
            cursor.close();
        }
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mAutoSyncList;
    }

    public ArrayList<AutoSync> getAutoSyncList() {
        return this.mAutoSyncList;
    }

    public int getNumberOfTransferReservationFiles(long lastStartTime) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int numOfFiles = 0;
        if (this.mAutoSyncList == null) {
            return 0;
        }
        int num = this.mAutoSyncList.size();
        for (int i = 0; i < num; i++) {
            AutoSync autoSync = this.mAutoSyncList.get(i);
            AppLog.checkIf(this.TAG, "AutoSync StartUtcTime = " + autoSync.getStartUtcTime());
            AppLog.checkIf(this.TAG, "AutoSync EndUtcTime   = " + autoSync.getEndUtcTime());
            if (autoSync.getStartUtcTime() < autoSync.getEndUtcTime()) {
                Cursor cursor = queryAutoSyncContent(autoSync);
                if (cursor != null) {
                    AppLog.checkIf(this.TAG, "Auto Sync CursorCount = " + cursor.getCount());
                    numOfFiles += cursor.getCount();
                    cursor.close();
                } else {
                    AppLog.checkIf(this.TAG, "Auto Sync Cursor is null");
                }
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return numOfFiles;
    }

    public int beginGetReservationFiles(long lastStartTime) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mNumOfIllegalDBEntries = 0;
        this.mNumOfIllegalFiles = 0;
        int imageSize = SyncBackUpUtil.getInstance().getImageSize(2097152);
        this.mb2MTransfer = imageSize != 0;
        DatabaseUtil.DbResult result = importDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            setDataBaseCorruptStatus(true);
            return 0;
        }
        this.mCursor = null;
        this.mLastStartTime = lastStartTime;
        this.mNumOfReservationFiles = getNumberOfTransferReservationFiles(lastStartTime);
        if (this.mNumOfReservationFiles == 0) {
            return this.mNumOfReservationFiles;
        }
        if (!moveToNextCursor()) {
            this.mNumOfReservationFiles = 0;
        }
        while (true) {
            if (this.mNumOfReservationFiles <= 0 || (!this.mb2MTransfer && isCurrentOriginalImageFileEnabled())) {
                break;
            }
            String filePath = Environment.getExternalStorageDirectory() + "/" + getCurrentUrl();
            String file = String.format("%1$05d", Integer.valueOf(this.mCursor.getInt(this.mCFileNumber)));
            String folder = String.format("%1$03d", Integer.valueOf(this.mCursor.getInt(this.mCFolderNumber)));
            int scnRet = beginGetScreennail(filePath, file, folder);
            if (scnRet == 0 && mbReadyForScreennail && 0 < this.mScreennailSize) {
                break;
            }
            endGetScreennail();
            if (!moveToNextReservationFile()) {
                this.mNumOfReservationFiles = 0;
                break;
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mNumOfReservationFiles;
    }

    public boolean moveToNextReservationFile() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bret = true;
        synchronized (this) {
            if (this.mAutoSyncList == null) {
                return false;
            }
            if (this.mCursor == null) {
                return false;
            }
            while (true) {
                if (true != bret) {
                    break;
                }
                endGetScreennail();
                long startUtc = SyncBackUpUtil.getInstance().getStartUTC(0L);
                if (this.mNumOfReservationFiles == 0) {
                    Log.d(this.TAG, "moveToNextReservationFile 1 startUtc = " + startUtc);
                    if (0 != startUtc) {
                        SyncBackUpUtil.getInstance().setStartUTC(getUTCTime());
                    }
                    bret = false;
                } else {
                    bret = this.mCursor.moveToNext();
                    if (!bret) {
                        this.mAutoSync.setStartUtcTime(this.mAutoSync.getEndUtcTime());
                        Log.d(this.TAG, "moveToNextReservationFile 1 = " + this.mAutoSync.toString());
                        updateAutoSyncInfo();
                        bret = moveToNextCursor();
                        if (!bret) {
                            Log.d(this.TAG, "moveToNextReservationFile 1 startUtc = " + startUtc);
                            if (0 != startUtc) {
                                SyncBackUpUtil.getInstance().setStartUTC(getUTCTime());
                            }
                        }
                    } else {
                        long crntUtc = this.mCursor.getLong(this.mCUTCDateTime);
                        this.mAutoSync.setStartUtcTime(crntUtc);
                        Log.d(this.TAG, "moveToNextReservationFile 2 = " + this.mAutoSync.toString());
                        updateAutoSync();
                    }
                    this.mNumOfReservationFiles--;
                    if (bret) {
                        if (!this.mb2MTransfer && isCurrentOriginalImageFileEnabled()) {
                            break;
                        }
                        String filePath = Environment.getExternalStorageDirectory() + "/" + getCurrentUrl();
                        String file = String.format("%1$05d", Integer.valueOf(this.mCursor.getInt(this.mCFileNumber)));
                        String folder = String.format("%1$03d", Integer.valueOf(this.mCursor.getInt(this.mCFolderNumber)));
                        int scnRet = beginGetScreennail(filePath, file, folder);
                        if (scnRet == 0 && mbReadyForScreennail && 0 < this.mScreennailSize) {
                            break;
                        }
                    }
                }
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return bret;
        }
    }

    public void endGetReservationFiles() {
        synchronized (this) {
            AppLog.enter(this.TAG, AppLog.getMethodName());
            endGetScreennail();
            if (this.mCursor != null) {
                this.mCursor.close();
                this.mCursor = null;
            }
            this.mLastStartTime = 0L;
            this.mCFileNumber = 0;
            this.mCFolderNumber = 0;
            this.mCExistJpeg = 0;
            this.mCExistRAW = 0;
            this.mCUTCDateTime = 0;
            this.mCurrentCursorCount = 0;
            this.mNumOfReservationFiles = 0;
            this.mb2MTransfer = false;
            if (this.mAutoSyncList != null) {
                if (this.mAutoSync != null) {
                    updateAutoSyncInfo();
                    AppLog.exit(this.TAG, AppLog.getMethodName());
                }
            }
        }
    }

    public boolean moveToNextCursor() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bret = false;
        int num = this.mAutoSyncList.size();
        if (this.mCursor != null) {
            this.mCursor.close();
            this.mCursor = null;
        }
        int i = 0;
        while (true) {
            if (i >= num) {
                break;
            }
            setAutoSync(this.mAutoSyncList.get(0));
            AppLog.checkIf(this.TAG, "mAutoSync StartUtcTime = " + this.mAutoSync.getStartUtcTime());
            AppLog.checkIf(this.TAG, "mAutoSync EndUtcTime   = " + this.mAutoSync.getEndUtcTime());
            AppLog.checkIf(this.TAG, "mAutoSync ID           = " + this.mAutoSync.getId());
            if (this.mAutoSync.getStartUtcTime() >= this.mAutoSync.getEndUtcTime()) {
                updateAutoSync();
            } else {
                Cursor cursor = queryAutoSyncContent(this.mAutoSync);
                AppLog.checkIf(this.TAG, "query end");
                if (cursor != null) {
                    this.mCurrentCursorCount = cursor.getCount();
                    if (this.mCurrentCursorCount > 0) {
                        this.mCursor = cursor;
                        AppLog.checkIf(this.TAG, "Auto Sync CursorCount = " + cursor.getCount());
                        break;
                    }
                    this.mAutoSync.setEndUtcTime(this.mAutoSync.getStartUtcTime());
                    updateAutoSync();
                    cursor.close();
                    AppLog.checkIf(this.TAG, "Auto Sync Cursor has no entry.");
                } else {
                    this.mAutoSync.setEndUtcTime(this.mAutoSync.getStartUtcTime());
                    updateAutoSync();
                    AppLog.checkIf(this.TAG, "Auto Sync Cursor is null.");
                }
            }
            i++;
        }
        if (this.mCursor != null && true == this.mCursor.moveToFirst()) {
            this.mCFileNumber = this.mCursor.getColumnIndex("dcf_file_number");
            this.mCFolderNumber = this.mCursor.getColumnIndex(IFolder.DCF_FOLDER_NUMBER);
            this.mCExistJpeg = this.mCursor.getColumnIndex("exist_jpeg");
            this.mCExistRAW = this.mCursor.getColumnIndex("exist_raw");
            this.mCUTCDateTime = this.mCursor.getColumnIndex("content_created_utc_date_time");
            bret = true;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bret;
    }

    public String getCurrentUrl() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        StringBuffer url = new StringBuffer(ConstantsSync.DCIM_DIR_NAME);
        AvindexContentInfo mInfo = AvindexStore.Images.Media.getImageInfo(this.mCursor.getString(this.mCursor.getColumnIndex("_data")));
        String dirName = mInfo.getAttribute("DCF_TBLDirName");
        String fileName = mInfo.getAttribute("DCF_TBLFileName");
        AppLog.checkIf(this.TAG, "fileName is " + fileName);
        if (fileName != null) {
            fileName = fileName.replace(ConstantsSync.RAW_FILE_SUFFIX, ConstantsSync.DSC_FILE_SUFFIX);
        }
        url.append(dirName);
        url.append("/");
        url.append(fileName);
        AppLog.checkIf(this.TAG, "getCurrentUrl = " + url.toString());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return url.toString();
    }

    public String getCurrentFileName() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AvindexContentInfo mInfo = AvindexStore.Images.Media.getImageInfo(this.mCursor.getString(this.mCursor.getColumnIndex("_data")));
        String fileName = mInfo.getAttribute("DCF_TBLFileName");
        if (fileName != null) {
            fileName = fileName.replace(ConstantsSync.RAW_FILE_SUFFIX, ConstantsSync.DSC_FILE_SUFFIX);
        }
        AppLog.checkIf(this.TAG, "getCurrentFileName = " + fileName);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return fileName;
    }

    public boolean isCurrentJpegFileExist() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bret = this.mCursor.getShort(this.mCExistJpeg) != 0;
        AppLog.checkIf(this.TAG, "isCurrentJpegFileExist = " + bret);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bret;
    }

    public boolean isCurrentRAWFileExist() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bret = this.mCursor.getShort(this.mCExistRAW) != 0;
        AppLog.checkIf(this.TAG, "isCurrentRAWFileExist = " + bret);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bret;
    }

    public DatabaseUtil.DbResult importDatabase() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DatabaseUtil.DbResult bResult = null;
        File intDbFile = new File("/data/data/com.sony.imaging.app.synctosmartphone/databases/autosync.db");
        File extDbFile = new File(getFilePathOnMedia() + "/" + DataBaseAdapter.PRIVATE_DATABASE_NAME);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        if (intDbFile != null && extDbFile != null) {
            bResult = DatabaseUtil.importDatabaseFromMedia(extDbFile, intDbFile);
            AppLog.checkIf(this.TAG, "Internal and Externel database files are not null");
            if (DatabaseUtil.DbResult.SUCCEEDED == bResult) {
                AppLog.checkIf(this.TAG, "Internal and Externel database files are not null");
                getAutoSyncListFromDatabase();
            } else {
                clearAutoSyncList();
            }
        } else {
            AppLog.checkIf(this.TAG, "Internal and Externel database files are null");
        }
        return bResult;
    }

    public DatabaseUtil.DbResult exportDatabase() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        File intDbFile = new File("/data/data/com.sony.imaging.app.synctosmartphone/databases/autosync.db");
        File extDbFile = new File(getFilePathOnMedia() + "/" + DataBaseAdapter.PRIVATE_DATABASE_NAME);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        if (intDbFile != null && extDbFile != null) {
            DatabaseUtil.DbResult bResult = DatabaseUtil.exportDatabaseToMedia(extDbFile, intDbFile);
            AppLog.checkIf(this.TAG, "Internal and Externel database files are not null");
            return bResult;
        }
        AppLog.checkIf(this.TAG, "Internal and Externel database files are null");
        return null;
    }

    public boolean deleteDatabase() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        clearAutoSyncList();
        File extDbFile = new File(getFilePathOnMedia() + "/" + DataBaseAdapter.PRIVATE_DATABASE_NAME);
        boolean bret = extDbFile.delete();
        long startUtc = SyncBackUpUtil.getInstance().getStartUTC(0L);
        if (0 != startUtc) {
            SyncBackUpUtil.getInstance().setStartUTC(getUTCTime());
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bret;
    }

    public Cursor queryAutoSyncContent(AutoSync autoSync) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (autoSync == null) {
            return null;
        }
        String mediaId = AvindexStore.getExternalMediaIds()[0];
        AvindexStore.loadMedia(mediaId, 1);
        if (!AvindexStore.waitLoadMediaComplete(mediaId)) {
            Log.i(this.TAG, "load cancelled");
            return null;
        }
        if (!AvindexStore.Images.waitAndUpdateDatabase(AppContext.getAppContext().getContentResolver(), mediaId)) {
            Log.i(this.TAG, "Update cancelled");
            return null;
        }
        String[] projection = {"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, "content_created_utc_date_time", IUtcDate.CONTENT_CREATED_UTC_DATE, "content_created_utc_time", ILocalDate.CONTENT_CREATED_LOCAL_DATE, "content_created_local_time", "exist_jpeg", "exist_raw"};
        AppLog.checkIf(this.TAG, "AutoSync StartUtcTime = " + autoSync.getStartUtcTime());
        AppLog.checkIf(this.TAG, "AutoSync EndUtcTime   = " + autoSync.getEndUtcTime());
        String selection = "content_created_utc_date_time BETWEEN '" + autoSync.getStartUtcTime() + "'" + ViewMode.AND + "'" + autoSync.getEndUtcTime() + "' AND (exist_jpeg=1" + ViewMode.OR + "exist_raw=1)";
        AppLog.checkIf(this.TAG, "selection : " + selection);
        AppLog.checkIf(this.TAG, "query start");
        Cursor cursor = AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]), projection, selection, null, "rec_order ASC");
        AppLog.checkIf(this.TAG, "query end");
        if (cursor != null) {
            AppLog.checkIf(this.TAG, "Auto Sync CursorCount = " + cursor.getCount());
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return cursor;
        }
        AppLog.checkIf(this.TAG, "Auto Sync Cursor is null");
        return cursor;
    }

    public boolean getDataBaseCorruptStatus() {
        return this.mbDataBaseCorruptStatus;
    }

    public void setDataBaseCorruptStatus(boolean bStatus) {
        this.mbDataBaseCorruptStatus = bStatus;
    }

    public boolean updateAutoSyncInfo() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bResult = false;
        if (this.mAutoSync == null) {
            AppLog.checkIf(this.TAG, "Auto Sync is not null, returning .....");
            return false;
        }
        if (updateAutoSync()) {
            bResult = true;
            DatabaseUtil.DbResult dbResult = exportDatabase();
            AppLog.checkIf(this.TAG, "Export database result: " + dbResult);
            if (DatabaseUtil.DbResult.DB_ERROR == dbResult) {
                setDataBaseCorruptStatus(true);
            } else if (DatabaseUtil.DbResult.SUCCEEDED != dbResult) {
                importDatabase();
            }
        } else {
            AppLog.checkIf(this.TAG, "Auto Sync info is not updated in list");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bResult;
    }

    public String getFormatedTimeStamp(long timeStamp) {
        String format;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Date date = new Date(timeStamp);
        BackupReader.RecordedDateMode recordedDateMode = BackupReader.getRecordedDateMode();
        if (recordedDateMode == BackupReader.RecordedDateMode.YY_MM_DD) {
            format = "yyyy-M-d h:mm a";
        } else if (recordedDateMode == BackupReader.RecordedDateMode.MM_DD_YY_ENG) {
            format = "MMM-d-yyyy h:mm a";
        } else if (recordedDateMode == BackupReader.RecordedDateMode.MM_DD_YY_NUM) {
            format = "M-d-yyyy h:mm a";
        } else {
            format = "d-M-yyyy H:mm";
        }
        SimpleDateFormat simpleDateFormatDateTime = new SimpleDateFormat(format, Locale.US);
        String time = simpleDateFormatDateTime.format(Long.valueOf(date.getTime()));
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return time;
    }

    public AutoSync getAutoSync() {
        return this.mAutoSync;
    }

    public void setAutoSync(AutoSync autoSync) {
        this.mAutoSync = autoSync;
    }

    public boolean isAutoSyncListSizeHundred() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mAutoSyncList == null || (this.mAutoSyncList != null && true == this.mAutoSyncList.isEmpty())) {
            AppLog.checkIf(this.TAG, "AutoSync list is null or empty");
            this.mAutoSyncList = getAutoSyncListFromDatabase();
        }
        int size = -1;
        if (this.mAutoSyncList != null) {
            size = this.mAutoSyncList.size();
        }
        AppLog.checkIf(this.TAG, "AutoSync list size : " + size);
        if (size >= 100) {
            return true;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return false;
    }

    private String getFilePathOnMedia() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String mFilePathOnMedia = null;
        String[] mMediaIds = AvindexStore.getExternalMediaIds();
        if (mMediaIds[0] != null) {
            MediaInfo mInfo = AvindexStore.getMediaInfo(mMediaIds[0]);
            int mMediaId = mInfo.getMediaType();
            if (2 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + MS_CARD_PATH;
            } else if (1 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + SD_CARD_PATH;
            }
            AppLog.checkIf(this.TAG, " Files path on Media regarding this application " + mFilePathOnMedia);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return mFilePathOnMedia;
    }

    public boolean isMediaExist() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String env = Environment.getExternalStorageState();
        boolean ret = "mounted".equals(env);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return ret;
    }

    public void clearAutoSyncList() {
        AppLog.enter(AppLog.getClassName(), AppLog.getMethodName());
        if (this.mAutoSyncList != null) {
            this.mAutoSyncList.clear();
            this.mAutoSyncList = null;
            AppLog.checkIf(this.TAG, "All ContinuousShot Objects removed successfully from the list.");
        } else {
            AppLog.checkIf(this.TAG, " ContinuousShot list is null");
        }
        AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
    }

    private long getTimeStamp() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Calendar cal = Calendar.getInstance();
        cal.clear();
        PlainCalendar pc = TimeUtil.getCurrentCalendar();
        cal.set(pc.year, pc.month - 1, pc.day, pc.hour, pc.minute, pc.second);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return cal.getTimeInMillis();
    }

    private int getTimeDiff() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        PlainTimeZone p = TimeUtil.getCurrentTimeZone();
        int diff = p.gmtDiff;
        int diff2 = diff + p.summerTimeDiff;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return diff2;
    }

    public long getUTCTime() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Calendar cal = Calendar.getInstance();
        cal.clear();
        PlainCalendar pc = TimeUtil.getCurrentCalendar();
        cal.set(pc.year, pc.month - 1, pc.day, pc.hour, pc.minute, pc.second);
        PlainTimeZone p = TimeUtil.getCurrentTimeZone();
        int diffGMT = p.gmtDiff;
        int diffSummerTime = p.summerTimeDiff;
        cal.add(12, -diffGMT);
        cal.add(12, -diffSummerTime);
        AppLog.checkIf(this.TAG, "Universal TimeInMillis: " + cal.getTimeInMillis());
        AppLog.checkIf(this.TAG, "Hour: " + cal.get(10) + "Minute: " + cal.get(12));
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return cal.getTimeInMillis();
    }

    private boolean updateAutoSync() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bResult = false;
        if (this.mAutoSync == null) {
            return false;
        }
        Cursor cursor = queryAutoSyncContent(this.mAutoSync);
        if (cursor == null || (cursor != null && cursor.getCount() <= 0)) {
            AppLog.checkIf(this.TAG, "Auto Sync cursor is null or count is less than one, deleting Auto Sync record.... ");
            String selection = "ID = '" + this.mAutoSync.getId() + "'";
            DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
            dataBaseAdapter.open(1);
            int queryResult = dataBaseAdapter.delete("AutoSync", selection, null);
            dataBaseAdapter.close();
            if (queryResult > 0) {
                AppLog.checkIf(this.TAG, "Auto Sync deleted from AutoSync table.");
                if (this.mAutoSyncList != null && this.mAutoSyncList.remove(this.mAutoSync)) {
                    this.mAutoSync = null;
                    AppLog.checkIf(this.TAG, "Auto Sync deleted from list successfully");
                    bResult = true;
                } else {
                    AppLog.checkIf(this.TAG, "Auto Sync can't deleted from list");
                }
            } else {
                AppLog.checkIf(this.TAG, "Auto Sync can't deleted from AutoSync table.");
            }
        } else {
            AppLog.checkIf(this.TAG, "Auto Sync cursor is not null or count is not less than one, updating Auto Sync record.... ");
            String[] selectionArgs = {Integer.toString(this.mAutoSync.getId())};
            ContentValues contentValues = new ContentValues();
            contentValues.put("StartUtcTime", Long.valueOf(this.mAutoSync.getStartUtcTime()));
            contentValues.put("EndUtcTime", Long.valueOf(this.mAutoSync.getEndUtcTime()));
            contentValues.put("NumberOfImages", Integer.valueOf(cursor.getCount()));
            DataBaseAdapter dataBaseAdapter2 = DataBaseAdapter.getInstance();
            dataBaseAdapter2.open(1);
            int queryResult2 = dataBaseAdapter2.update("AutoSync", contentValues, "ID=?", selectionArgs);
            dataBaseAdapter2.close();
            if (queryResult2 > 0) {
                AppLog.checkIf(this.TAG, "Auto Sync updated from AutoSync table.");
                this.mAutoSync.setNumberOfImages(cursor.getCount());
                AppLog.checkIf(this.TAG, "Auto Sync updated in list successfully");
                bResult = true;
            } else {
                AppLog.checkIf(this.TAG, "Auto Sync can't updated in AutoSync table.");
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return bResult;
    }

    private byte[] getScreennailFromAvindex(String file, String folder, String filePath) {
        ByteBuffer sound_data;
        int sound_photo_len;
        AppLog.checkIf(this.TAG, "getScreennailFromAvindex in ");
        long startTime = System.currentTimeMillis();
        String mediaId = AvindexStore.getExternalMediaIds()[0];
        AvindexStore.loadMedia(mediaId, 1);
        if (!AvindexStore.waitLoadMediaComplete(mediaId)) {
            Log.i(this.TAG, "load cancelled");
            return null;
        }
        if (!AvindexStore.Images.waitAndUpdateDatabase(AppContext.getAppContext().getContentResolver(), mediaId)) {
            Log.i(this.TAG, "Update cancelled");
            return null;
        }
        String[] projection = {"_id", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, "content_created_utc_date_time"};
        String[] selectionArgs = {file, folder};
        Cursor cursor = AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]), projection, "dcf_file_number=? AND dcf_folder_number=?", selectionArgs, null);
        if (cursor == null || (cursor != null && cursor.getCount() == 0)) {
            AppLog.checkIf(this.TAG, "No record found in AutoSync table.");
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
        AppLog.checkIf(this.TAG, cursor.getCount() + " record found in AutoSync table, Traversing cursor.....");
        if (cursor.moveToFirst()) {
            long imageId = cursor.getLong(cursor.getColumnIndex("_id"));
            byte[] orgStill = AvindexStore.Images.Media.getScreennail(AppContext.getAppContext().getContentResolver(), AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]), imageId);
            if (orgStill != null) {
                this.mScreennailSize = orgStill.length;
                int rotate = getRotateInfoMSL(filePath);
                AppLog.checkIf(this.TAG, " Get rotate : " + rotate + ", filepath : " + filePath);
                if (this.dbuf != null) {
                    this.dbuf.release();
                    this.dbuf = null;
                }
                try {
                    Log.i(this.TAG, "HEAP_MESURE: dbuf create screennail not Exist");
                    DSP dsp = DSP.createProcessor("sony-di-dsp");
                    if (dsp != null) {
                        Log.i(this.TAG, "HEAP_MESURE_CHECK: dbuf create.");
                        this.dbuf = dsp.createBuffer((int) 4194304);
                    }
                    if (this.dbuf != null) {
                        this.mScreennailSize = orgStill.length;
                        this.dbuf.write(orgStill, 0, orgStill.length, 0);
                        Log.v(this.TAG, "InputStreamSize: : " + this.mScreennailSize);
                        int imgAddress = dsp.getPropertyAsInt(this.dbuf, "memory-address");
                        int imgSize = dsp.getPropertyAsInt(this.dbuf, "memory-size");
                        if (dsp != null) {
                            dsp.release();
                        }
                        Log.v(this.TAG, "dbuf imgAddress : " + imgAddress + "imageSize:" + imgSize);
                        Log.v(this.TAG, "Check SoundPhoto Start.  filePath : " + filePath);
                        boolean isSPF = checkSoundPhoto(filePath);
                        if (isSPF) {
                            Log.i(this.TAG, "HEAP_MESURE: sound_data create screennail not Exist");
                            sound_data = ByteBuffer.allocateDirect(2097152);
                            sound_photo_len = sound_data.capacity();
                        } else {
                            sound_data = null;
                            sound_photo_len = 0;
                        }
                        Log.v(this.TAG, "Get Data (Still only or Added sound) Start.  filePath : " + filePath);
                        int dlength = setScreennailDevieceBuffer(imgAddress, imgSize, (int) this.mScreennailSize, sound_data, sound_photo_len, rotate, isSPF);
                        if (dlength >= 0) {
                            this.mScreennailSize = dlength;
                        } else {
                            Log.v(this.TAG, "This file is not SoundPhoto.");
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.TAG, e.toString());
                    e.printStackTrace();
                } finally {
                    int err = finishSoundPhotoProcess();
                    Log.i(this.TAG, "finishSoundPhotoProcess ret = " + err);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "getScreennailFromAvindex ElapsedTime: " + elapsedTime + "[millis]");
        }
        return null;
    }

    public boolean setAutoSyncMode(boolean bIsOn) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean ret = false;
        DatabaseUtil.DbResult result = importDatabase();
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            setDataBaseCorruptStatus(true);
            return false;
        }
        long currentUTC = getUTCTime();
        long startUTC = SyncBackUpUtil.getInstance().getStartUTC(0L);
        if (this.mAutoSyncList != null) {
            int listSize = this.mAutoSyncList.size();
            long backupEndUTC = 0;
            boolean bInsert = false;
            if (listSize > 0) {
                this.mAutoSync = this.mAutoSyncList.get(listSize - 1);
                backupEndUTC = this.mAutoSync.getEndUtcTime();
                if (this.mAutoSync.getEndUtcTime() < startUTC || startUTC == 0) {
                    this.mAutoSync = new AutoSync();
                    AutoSync autoSync = this.mAutoSync;
                    if (0 == startUTC) {
                        startUTC = currentUTC;
                    }
                    autoSync.setStartUtcTime(startUTC);
                    this.mAutoSync.setEndUtcTime(currentUTC);
                    bInsert = true;
                } else {
                    this.mAutoSync.setEndUtcTime(currentUTC);
                }
            } else {
                if (this.mAutoSync == null) {
                    this.mAutoSync = new AutoSync();
                }
                if (0 == startUTC) {
                    this.mAutoSync.setStartUtcTime(currentUTC);
                } else {
                    this.mAutoSync.setStartUtcTime(startUTC);
                }
                this.mAutoSync.setEndUtcTime(currentUTC);
                bInsert = true;
            }
            AppLog.checkIf(this.TAG, "AutoSync StartUtcTime = " + this.mAutoSync.getStartUtcTime());
            AppLog.checkIf(this.TAG, "AutoSync EndUtcTime   = " + this.mAutoSync.getEndUtcTime());
            Cursor cursor = queryAutoSyncContent(this.mAutoSync);
            if (cursor == null) {
                return false;
            }
            int numOfContents = cursor.getCount();
            if (numOfContents > 0) {
                this.mAutoSync.setNumberOfImages(numOfContents);
                if (bInsert) {
                    ret = insertAutoSync(this.mAutoSync);
                } else {
                    this.mAutoSync.setNumberOfImages(numOfContents);
                    ret = updateAutoSync();
                }
            } else if (!bInsert) {
                this.mAutoSync.setEndUtcTime(backupEndUTC);
                ret = updateAutoSync();
            } else {
                ret = true;
            }
            if (ret) {
                DatabaseUtil.DbResult dbResult = exportDatabase();
                AppLog.checkIf(this.TAG, "Export database result: " + dbResult);
                if (DatabaseUtil.DbResult.DB_ERROR == dbResult) {
                    setDataBaseCorruptStatus(true);
                    ret = false;
                } else if (DatabaseUtil.DbResult.SUCCEEDED != dbResult) {
                    importDatabase();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        SyncBackUpUtil.getInstance().setStartUTC(!bIsOn ? 0L : currentUTC);
        AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
        return ret;
    }

    public boolean isNeedUpdateDatabase() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bret = updateDatabase(true);
        AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
        return bret;
    }

    public boolean updateDatabase(boolean bCheck) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        boolean bret = false;
        boolean isNeedInsert = false;
        Cursor cursor = null;
        long startUtc = SyncBackUpUtil.getInstance().getStartUTC(0L);
        if (0 == startUtc) {
            Log.d(this.TAG, "no need to update database");
            AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
            return false;
        }
        long currentUtc = getUTCTime();
        if (!isMediaExist()) {
            Log.d(this.TAG, "no Media");
            AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
            return false;
        }
        if (DatabaseUtil.DbResult.DB_ERROR == importDatabase()) {
            setDataBaseCorruptStatus(true);
            AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
            return false;
        }
        if (!bCheck) {
            if (this.mAutoSync != null) {
                this.mAutoSync = null;
            }
            int num = this.mAutoSyncList.size();
            if (num > 0) {
                this.mAutoSync = this.mAutoSyncList.get(num - 1);
                if (startUtc == this.mAutoSync.getEndUtcTime()) {
                    this.mAutoSync.setEndUtcTime(currentUtc);
                    bret = updateAutoSync();
                } else {
                    isNeedInsert = true;
                }
            }
            if (num == 0 || true == isNeedInsert) {
                this.mAutoSync = new AutoSync();
                this.mAutoSync.setId(0);
                this.mAutoSync.setNumberOfImages(0);
                this.mAutoSync.setStartUtcTime(startUtc);
                this.mAutoSync.setEndUtcTime(currentUtc);
                cursor = queryAutoSyncContent(this.mAutoSync);
                if (cursor != null && cursor.getCount() > 0) {
                    this.mAutoSync.setNumberOfImages(cursor.getCount());
                    bret = insertAutoSync(this.mAutoSync);
                }
            }
            SyncBackUpUtil.getInstance().setStartUTC(currentUtc);
            if (bret) {
                DatabaseUtil.DbResult result = exportDatabase();
                AppLog.checkIf(this.TAG, "Export database result: " + result);
                if (DatabaseUtil.DbResult.DB_ERROR == result) {
                    setDataBaseCorruptStatus(true);
                    bret = false;
                } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
                    importDatabase();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } else {
            bret = true;
        }
        AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
        return bret;
    }

    public int beginGetScreennail(String filePath, String file, String folder) {
        long startTime = System.currentTimeMillis();
        boolean screennailFromMSL = false;
        this.mScreennailSize = 0L;
        int mslErr = openMSL(filePath);
        Log.d(this.TAG, "called JNI openMSL : " + mslErr);
        boolean isSPF = checkSoundPhoto(filePath);
        if (mslErr == 0 && isScreennailExisting()) {
            getScreennailFromMSL(isSPF);
            screennailFromMSL = true;
        }
        if (0 == this.mScreennailSize) {
            getScreennailFromAvindex(file, folder, filePath);
            screennailFromMSL = false;
        }
        if (0 != this.mScreennailSize) {
            mbReadyForScreennail = true;
            Log.v(this.TAG, "Check and Get SoundPhoto Start.  filePath : " + filePath);
            if (isSPF) {
                if (!screennailFromMSL) {
                    Log.v(this.TAG, "This file is SoundPhoto by screennailFromAVIndex.");
                } else {
                    Log.v(this.TAG, "This file is SoundPhoto by screennailFromMSL.");
                    Log.i(this.TAG, "HEAP_MESURE: sound_data create screennail Exist");
                    ByteBuffer sound_data = ByteBuffer.allocateDirect(2097152);
                    int sound_data_capacity = sound_data.capacity();
                    int sound_len = getSoundPhoto(sound_data, sound_data_capacity);
                    if (sound_len <= 0 || sound_len > sound_data_capacity) {
                        Log.i(this.TAG, "SoundData length is false. sound_len = " + sound_len);
                    } else {
                        this.mScreennailSize += sound_len;
                        Log.i(this.TAG, "mScreennailSize = " + this.mScreennailSize);
                        int err = finishSoundPhotoProcess();
                        Log.i(this.TAG, "finishSoundPhotoProcess end." + err);
                    }
                    Log.i(this.TAG, "HEAP_MESURE_CHECK: gc after sound null.");
                    System.gc();
                }
            } else {
                Log.v(this.TAG, "This file is not SoundPhoto.");
            }
            Log.v(this.TAG, "Check and Get SoundPhoto end.");
        } else if (!isJpegImageFileExist(filePath) && !isRawImageFileExist(filePath)) {
            this.mNumOfIllegalDBEntries++;
        } else {
            this.mNumOfIllegalFiles++;
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "beginGetScreennail ElapsedTime: " + elapsedTime + "[millis]");
            return 0;
        }
        return 0;
    }

    public int getNumOfIllegalDBEntries() {
        return this.mNumOfIllegalDBEntries;
    }

    public int getNumOfIllegalDBFiles() {
        return this.mNumOfIllegalFiles;
    }

    public long getScreennailSize() {
        if (!mbReadyForScreennail) {
            return 0L;
        }
        long screennailSize = this.mScreennailSize;
        return screennailSize;
    }

    public DeviceBuffer getScreennail() {
        Log.i(this.TAG, "HEAP_MESURE_CHECK: getScreennail.");
        Log.i(this.TAG, "getScreennail in");
        if (this.dbuf == null) {
            Log.i(this.TAG, "dbuf is null");
        }
        this.mScreennailFromAVIndex = true;
        return this.dbuf;
    }

    public void releaseDbuf() {
        Log.i(this.TAG, "releaseDbuf in");
        if (this.dbuf != null) {
            this.dbuf.release();
            this.dbuf = null;
        }
    }

    public int getFileSize() {
        Log.i(this.TAG, "getFileSize is called. mScreennailFromAVIndex: " + this.mScreennailFromAVIndex + ", mScreennailSize: " + this.mScreennailSize);
        if (!this.mScreennailFromAVIndex) {
            return 0;
        }
        int retSize = (int) this.mScreennailSize;
        return retSize;
    }

    private void getScreennailFromMSL(boolean spfFile) {
        long bufferSize;
        long startTime = System.currentTimeMillis();
        if (!spfFile) {
            bufferSize = 2097152;
        } else {
            bufferSize = 4194304;
        }
        String softwareName = ScalarProperties.getString("model.name");
        String firmVer = ScalarProperties.getFirmwareVersion();
        String softwareName2 = softwareName + " v" + firmVer;
        if (this.dbuf != null) {
            this.dbuf.release();
            this.dbuf = null;
        }
        try {
            DSP dsp = DSP.createProcessor("sony-di-dsp");
            if (dsp != null) {
                Log.i(this.TAG, "HEAP_MESURE_CHECK: dbuf create.");
                this.dbuf = dsp.createBuffer((int) bufferSize);
            }
            if (this.dbuf != null) {
                int imgAddress = dsp.getPropertyAsInt(this.dbuf, "memory-address");
                int imgSize = dsp.getPropertyAsInt(this.dbuf, "memory-size");
                long localTime = getTimeStamp() / 1000;
                double dlength = createScreennail(imgAddress, imgSize, softwareName2, getTimeDiff(), localTime);
                Log.d(this.TAG, "imgAddress : " + imgAddress);
                Log.d(this.TAG, "called JNI createScreennail : " + dlength);
                if (0.0d < dlength) {
                    this.mScreennailSize = (long) dlength;
                }
            }
        } catch (Exception e) {
            Log.e(this.TAG, e.toString());
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "getScreennailFromMSL ElapsedTime: " + elapsedTime + "[millis]");
        }
    }

    public void endGetScreennail() {
        Log.i(this.TAG, "HEAP_MESURE_CHECK: endGetScreennail.");
        long startTime = System.currentTimeMillis();
        releaseDbuf();
        closeMSL();
        System.gc();
        mbReadyForScreennail = false;
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "endGetScreennail ElapsedTime: " + elapsedTime + "[millis]");
        }
    }

    private Long getImageActualfileSize(Cursor cursor) {
        long startTime = System.currentTimeMillis();
        AvindexContentInfo info = AvindexStore.Images.Media.getImageInfo(cursor.getString(cursor.getColumnIndex("_data")));
        Long imageSize = Long.valueOf(info.getAttributeLong("FILE_SYSTEMFileSize", 0L));
        Log.d(this.TAG, " *** fileSize = " + imageSize);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "getImageActualfileSize ElapsedTime: " + elapsedTime + "[millis]");
        }
        return imageSize;
    }

    private boolean isCurrentOriginalImageFileEnabled() {
        long startTime = System.currentTimeMillis();
        String filePath = Environment.getExternalStorageDirectory() + "/" + getCurrentUrl();
        File orgFile = new File(filePath);
        boolean bRet = orgFile.exists();
        Log.d(this.TAG, " *** original ImageFile Exist = " + bRet);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "isCurrentOriginalImageFileEnabled ElapsedTime: " + elapsedTime + "[millis]");
        }
        return bRet;
    }

    private boolean isJpegImageFileExist(String filePath) {
        long startTime = System.currentTimeMillis();
        File jpegFile = new File(filePath);
        boolean bRet = jpegFile.exists();
        Log.d(this.TAG, " *** jpeg ImageFile Exist = " + bRet);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "isJpegImageFileExist ElapsedTime: " + elapsedTime + "[millis]");
        }
        return bRet;
    }

    private boolean isRawImageFileExist(String filePath) {
        long startTime = System.currentTimeMillis();
        String rawFilePath = filePath.replaceAll(ConstantsSync.DSC_FILE_SUFFIX, ConstantsSync.RAW_FILE_SUFFIX);
        File rawFile = new File(rawFilePath);
        boolean bRet = rawFile.exists();
        Log.d(this.TAG, " *** raw ImageFile Exist = " + bRet);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(this.TAG, "isRawImageFileExist ElapsedTime: " + elapsedTime + "[millis]");
        }
        return bRet;
    }
}
