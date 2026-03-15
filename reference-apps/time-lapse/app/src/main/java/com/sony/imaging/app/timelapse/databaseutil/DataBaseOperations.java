package com.sony.imaging.app.timelapse.databaseutil;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.scalar.provider.AvindexStore;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class DataBaseOperations {
    private static final String AB_FILE_PATH = "/mnt/sdcard/";
    private static final String OTHER_FILE_PATH = "/storage/sdcard0/";
    private static final String TAG = DataBaseOperations.class.getName();
    private static DataBaseOperations sDataBaseOperations;
    private ArrayList<TimeLapseBO> mTimeLapseBOList = null;

    public static DataBaseOperations getInstance() {
        if (sDataBaseOperations == null) {
            sDataBaseOperations = new DataBaseOperations();
        }
        return sDataBaseOperations;
    }

    public ArrayList<TimeLapseBO> getTimeLapseBOList() {
        if (this.mTimeLapseBOList == null) {
            this.mTimeLapseBOList = new ArrayList<>();
        }
        return this.mTimeLapseBOList;
    }

    public void setTimeLapseBOList() {
        this.mTimeLapseBOList = getList();
    }

    public void removeElementAt(int index) {
        if (this.mTimeLapseBOList != null && this.mTimeLapseBOList.size() > 0) {
            this.mTimeLapseBOList.remove(index);
        }
    }

    public void removeAllBOObjects() {
        if (this.mTimeLapseBOList != null) {
            int size = this.mTimeLapseBOList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bmp = this.mTimeLapseBOList.get(i).getOptimizedBitmap();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
            this.mTimeLapseBOList.clear();
            this.mTimeLapseBOList = null;
        }
    }

    public void saveJpegGroup(TimeLapseBO timelapseBO) {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COL_THEMENAME, Integer.valueOf(timelapseBO.getThemeName()));
        contentValues.put(DBConstants.COL_START_DATE, timelapseBO.getStartDate());
        contentValues.put(DBConstants.COL_START_TIME, timelapseBO.getStartTime());
        contentValues.put(DBConstants.COL_START_UTC_DATETIME, Long.valueOf(timelapseBO.getStartUtcDateTime()));
        contentValues.put(DBConstants.COL_END_UTC_DATETIME, Long.valueOf(timelapseBO.getEndUtcDateTime()));
        contentValues.put(DBConstants.COL_SHOOTINGNUMBER, Integer.valueOf(timelapseBO.getShootingNumber()));
        contentValues.put(DBConstants.COL_SHOOTING_MODE, timelapseBO.getShootingMode());
        contentValues.put(DBConstants.COL_WIDTH, Integer.valueOf(timelapseBO.getWidth()));
        contentValues.put(DBConstants.COL_HEIGHT, Integer.valueOf(timelapseBO.getHeight()));
        contentValues.put(DBConstants.COL_FPS, Integer.valueOf(timelapseBO.getFps()));
        contentValues.put(DBConstants.COL_FILE_PATH, timelapseBO.getStartFullPathFileName());
        contentValues.put(DBConstants.COL_MOVIE_MODE, timelapseBO.getMovieMode());
        dataBaseAdapter.insert(DBConstants.TAB_TABLE_NAME, contentValues);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
    }

    public ArrayList<TimeLapseBO> getList() {
        removeAllBOObjects();
        this.mTimeLapseBOList = new ArrayList<>();
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(2);
        Cursor cursor = dataBaseAdapter.query("select * from tblPlay", null);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                Log.d(TAG, "getList: No records available");
                return null;
            }
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            cursor.moveToFirst();
            do {
                TimeLapseBO timelapseBO = new TimeLapseBO();
                timelapseBO.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_ID)));
                timelapseBO.setThemeName(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_THEMENAME)));
                timelapseBO.setStartDate(cursor.getString(cursor.getColumnIndex(DBConstants.COL_START_DATE)));
                timelapseBO.setStartTime(cursor.getString(cursor.getColumnIndex(DBConstants.COL_START_TIME)));
                timelapseBO.setStartUtcDateTime(cursor.getLong(cursor.getColumnIndex(DBConstants.COL_START_UTC_DATETIME)));
                timelapseBO.setEndUtcDateTime(cursor.getLong(cursor.getColumnIndex(DBConstants.COL_END_UTC_DATETIME)));
                timelapseBO.setShootingNumber(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_SHOOTINGNUMBER)));
                timelapseBO.setShootingMode(cursor.getString(cursor.getColumnIndex(DBConstants.COL_SHOOTING_MODE)));
                timelapseBO.setWidth(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_WIDTH)));
                timelapseBO.setHeight(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_HEIGHT)));
                timelapseBO.setFps(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_FPS)));
                String filePathName = cursor.getString(cursor.getColumnIndex(DBConstants.COL_FILE_PATH));
                if (filePathName != null && !filePathName.contains(externalPath)) {
                    filePathName = updateFilepath(filePathName);
                }
                timelapseBO.setStartFullPathFileName(filePathName);
                timelapseBO.setMovieMode(cursor.getString(cursor.getColumnIndex(DBConstants.COL_MOVIE_MODE)));
                this.mTimeLapseBOList.add(timelapseBO);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        return this.mTimeLapseBOList;
    }

    private String updateFilepath(String filePathName) {
        if (filePathName.contains(OTHER_FILE_PATH)) {
            String retPathName = filePathName.replace(OTHER_FILE_PATH, AB_FILE_PATH);
            return retPathName;
        }
        return filePathName;
    }

    public Cursor queryLastContent(TimeLapseBO tlBo) {
        String[] CONTENTS_QUERY_PROJECTION = {"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, "content_created_utc_date_time", ILocalDate.CONTENT_CREATED_LOCAL_DATE, "content_created_local_time"};
        String where = "content_created_utc_date_time BETWEEN '" + tlBo.getStartUtcDateTime() + "'" + ViewMode.AND + "'" + tlBo.getEndUtcDateTime() + "'";
        if (TLCommonUtil.getInstance().canQueryforRecoveryDB()) {
            String[] mediaId = AvindexStore.getExternalMediaIds();
            Intent intent = new Intent("com.sony.scalar.database.avindex.action.AVINDEX_DATABASE_UPDATED");
            intent.putExtra("com.sony.scalar.database.avindex.extra.mediaid", mediaId[0]);
            AppContext.getAppContext().sendOrderedBroadcast(intent, null);
        }
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
        return AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), CONTENTS_QUERY_PROJECTION, where, null, null);
    }

    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    public boolean deleteRowFromDB(TimeLapseBO tlBO) {
        boolean bResult = false;
        String whereClause = "ID = '" + tlBO.getId() + "'";
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        int result = dataBaseAdapter.delete(DBConstants.TAB_TABLE_NAME, whereClause, null);
        if (result > 0) {
            Log.d(TAG, "deleteRowFromDB - row delted:" + result);
            bResult = true;
        }
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        return bResult;
    }

    public boolean updateJpegCountsOfDB(TimeLapseBO tlBO, int count) {
        ContentValues contentValues = new ContentValues();
        String[] whereArgs = {Integer.toString(tlBO.getId())};
        contentValues.put(DBConstants.COL_SHOOTINGNUMBER, Integer.valueOf(count));
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        int result = dataBaseAdapter.update(DBConstants.TAB_TABLE_NAME, contentValues, "ID=?", whereArgs);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        tlBO.setShootingNumber(count);
        if (result <= 0) {
            return false;
        }
        Log.d(TAG, "databaseRecovery: Still shooting number updated");
        return true;
    }

    public void exportXMLtoMedia() throws IOException {
        String extPath = TLCommonUtil.getInstance().getFilePathOnMedia() + TimeLapseConstants.TYPE_INFO_DB_PATH;
        String databaseFilePath = TLCommonUtil.getInstance().getFilePathOnMedia() + TimeLapseConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME;
        AssetManager am = AppContext.getAppContext().getResources().getAssets();
        InputStream is = am.open(TimeLapseConstants.TYPE_INFO_FILENAME);
        byte[] buffer = new byte[is.available()];
        while (is.read(buffer) != -1) {
            DatabaseUtil.exportDataToMedia(extPath, TimeLapseConstants.TYPE_INFO_FILENAME, buffer, databaseFilePath);
        }
        is.close();
    }

    public DatabaseUtil.DbResult importDatabase() {
        File internalDbFile = new File(DBConstants.INTERNAL_DATABASE_PATH + DBConstants.DATABASE_NAME);
        File mediaDbFile = new File(TLCommonUtil.getInstance().getFilePathOnMedia() + TimeLapseConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME);
        DatabaseUtil.DbResult result = DatabaseUtil.importDatabaseFromMedia(mediaDbFile, internalDbFile);
        System.gc();
        return result;
    }

    public boolean databaseRecovery() {
        Log.d(TAG, "databaseRecovery");
        boolean bResult = false;
        if (!DatabaseUtil.isInternalDbValid()) {
            Log.d(TAG, "databaseRecovery: internal db invalid");
            return false;
        }
        setTimeLapseBOList();
        ArrayList<TimeLapseBO> timeLapseBOList = getTimeLapseBOList();
        if (timeLapseBOList == null) {
            Log.d(TAG, "databaseRecovery: timelapseBOList null");
            return false;
        }
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        Iterator<TimeLapseBO> itr = timeLapseBOList.iterator();
        if (!AvindexStore.waitLoadMediaComplete(AvindexStore.getExternalMediaIds()[0])) {
            return false;
        }
        while (itr.hasNext()) {
            TimeLapseBO tlBo = itr.next();
            String where = "ID = '" + tlBo.getId() + "'";
            if (tlBo.getShootingMode().equalsIgnoreCase("STILL")) {
                if (TLCommonUtil.getInstance().canQueryforRecoveryDB()) {
                    Cursor cursor = queryLastContent(tlBo);
                    cursor.moveToFirst();
                    if (cursor == null || cursor.getCount() == 0) {
                        if (!isMediaExist()) {
                            break;
                        }
                        int result = dataBaseAdapter.delete(DBConstants.TAB_TABLE_NAME, where, null);
                        Log.d(TAG, "databaseRecovery: Still deleted: " + result);
                        if (result > 0) {
                            itr.remove();
                            bResult = true;
                        }
                    }
                    if (cursor != null && cursor.getCount() != tlBo.getShootingNumber()) {
                        ContentValues contentValues = new ContentValues();
                        String[] whereArgs = {Integer.toString(tlBo.getId())};
                        contentValues.put(DBConstants.COL_SHOOTINGNUMBER, Integer.valueOf(cursor.getCount()));
                        int result2 = dataBaseAdapter.update(DBConstants.TAB_TABLE_NAME, contentValues, "ID=?", whereArgs);
                        tlBo.setShootingNumber(cursor.getCount());
                        if (result2 > 0) {
                            Log.d(TAG, "databaseRecovery: Still shooting number updated");
                            bResult = true;
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.toString());
                    }
                } else {
                    continue;
                }
            } else if (!TLCommonUtil.getInstance().isAviFileExist(tlBo.getStartFullPathFileName())) {
                if (!isMediaExist()) {
                    break;
                }
                TLCommonUtil.getInstance().deleteThumbnailFile(tlBo.getStartFullPathFileName());
                int result3 = dataBaseAdapter.delete(DBConstants.TAB_TABLE_NAME, where, null);
                Log.d(TAG, "databaseRecovery: Avi deleted: " + result3);
                if (result3 > 0) {
                    itr.remove();
                    bResult = true;
                }
            } else {
                continue;
            }
        }
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        DatabaseUtil.setIsDatabaseChecked(true);
        return bResult;
    }

    private boolean isMediaExist() {
        String env = Environment.getExternalStorageState();
        boolean ret = "mounted".equals(env);
        return ret;
    }

    public DatabaseUtil.DbResult exportDatabase() {
        File intDbFile = new File(DBConstants.INTERNAL_DATABASE_PATH + DBConstants.DATABASE_NAME);
        File extDbFile = new File(TLCommonUtil.getInstance().getFilePathOnMedia() + TimeLapseConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME);
        DatabaseUtil.DbResult result = DatabaseUtil.exportDatabaseToMedia(extDbFile, intDbFile);
        System.gc();
        return result;
    }

    public void refreshDatabase() {
        Log.d(TAG, "refreshDatabase");
        DataBaseOperations dBaseOperation = getInstance();
        DatabaseUtil.DbResult result = dBaseOperation.importDatabase();
        if (DatabaseUtil.DbResult.SUCCEEDED == result) {
            Log.d(TAG, "refreshDatabase: import succeeded");
            if (!DatabaseUtil.isDatabaseChecked()) {
                if (true == dBaseOperation.databaseRecovery()) {
                    DatabaseUtil.DbResult result2 = dBaseOperation.exportDatabase();
                    if (DatabaseUtil.DbResult.DB_ERROR == result2) {
                        Log.d(TAG, "refreshDatabase: export db error");
                        DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                        return;
                    } else if (DatabaseUtil.DbResult.SUCCEEDED != result2) {
                        Log.d(TAG, "refreshDatabase - Export failed Result:" + result2);
                        dBaseOperation.importDatabase();
                        return;
                    } else {
                        Log.d(TAG, "refreshDatabase: export successful");
                        return;
                    }
                }
                return;
            }
            dBaseOperation.setTimeLapseBOList();
            return;
        }
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            Log.d(TAG, "refreshDatabase: import database error");
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        }
    }
}
