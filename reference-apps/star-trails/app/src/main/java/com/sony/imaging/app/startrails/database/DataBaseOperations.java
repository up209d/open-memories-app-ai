package com.sony.imaging.app.startrails.database;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.startrails.AppContext;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
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
    private ArrayList<StarTrailsBO> mSTBOList = null;

    public static DataBaseOperations getInstance() {
        if (sDataBaseOperations == null) {
            sDataBaseOperations = new DataBaseOperations();
        }
        return sDataBaseOperations;
    }

    public ArrayList<StarTrailsBO> getStartrailsBOList() {
        if (this.mSTBOList == null) {
            this.mSTBOList = new ArrayList<>();
        }
        return this.mSTBOList;
    }

    public void setStartrailsBOList() {
        this.mSTBOList = getList();
    }

    public void removeElementAt(int index) {
        if (this.mSTBOList != null && this.mSTBOList.size() > 0) {
            this.mSTBOList.remove(index);
        }
    }

    public void removeAllBOObjects() {
        if (this.mSTBOList != null) {
            int size = this.mSTBOList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bmp = this.mSTBOList.get(i).getOptimizedBitmap();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
            this.mSTBOList.clear();
            this.mSTBOList = null;
        }
    }

    public void saveJpegGroup(StarTrailsBO stBO) {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COL_THEMENAME, Integer.valueOf(stBO.getThemeName()));
        contentValues.put(DBConstants.COL_START_DATE, stBO.getStartDate());
        contentValues.put(DBConstants.COL_START_TIME, stBO.getStartTime());
        contentValues.put(DBConstants.COL_START_UTC_DATETIME, Long.valueOf(stBO.getStartUtcDateTime()));
        contentValues.put(DBConstants.COL_END_UTC_DATETIME, Long.valueOf(stBO.getEndUtcDateTime()));
        contentValues.put(DBConstants.COL_SHOOTINGNUMBER, Integer.valueOf(stBO.getShootingNumber()));
        contentValues.put(DBConstants.COL_SHOOTING_MODE, stBO.getShootingMode());
        contentValues.put(DBConstants.COL_WIDTH, Integer.valueOf(stBO.getWidth()));
        contentValues.put(DBConstants.COL_HEIGHT, Integer.valueOf(stBO.getHeight()));
        contentValues.put(DBConstants.COL_FPS, Integer.valueOf(stBO.getFps()));
        contentValues.put(DBConstants.COL_STREAK_LEVEL, Integer.valueOf(stBO.getStreakLevel()));
        contentValues.put(DBConstants.COL_FILE_PATH, stBO.getFullPathFileName());
        contentValues.put(DBConstants.COL_MOVIE_MODE, stBO.getMovieMode());
        dataBaseAdapter.insert(DBConstants.TAB_TABLE_NAME, contentValues);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
    }

    public ArrayList<StarTrailsBO> getList() {
        removeAllBOObjects();
        this.mSTBOList = new ArrayList<>();
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
                StarTrailsBO stBO = new StarTrailsBO();
                stBO.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_ID)));
                stBO.setThemeName(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_THEMENAME)));
                stBO.setStartDate(cursor.getString(cursor.getColumnIndex(DBConstants.COL_START_DATE)));
                stBO.setStartTime(cursor.getString(cursor.getColumnIndex(DBConstants.COL_START_TIME)));
                stBO.setStartUtcDateTime(cursor.getLong(cursor.getColumnIndex(DBConstants.COL_START_UTC_DATETIME)));
                stBO.setEndUtcDateTime(cursor.getLong(cursor.getColumnIndex(DBConstants.COL_END_UTC_DATETIME)));
                stBO.setShootingNumber(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_SHOOTINGNUMBER)));
                stBO.setShootingMode(cursor.getString(cursor.getColumnIndex(DBConstants.COL_SHOOTING_MODE)));
                stBO.setWidth(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_WIDTH)));
                stBO.setHeight(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_HEIGHT)));
                stBO.setFps(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_FPS)));
                stBO.setStreakLevel(cursor.getInt(cursor.getColumnIndex(DBConstants.COL_STREAK_LEVEL)));
                String filePathName = cursor.getString(cursor.getColumnIndex(DBConstants.COL_FILE_PATH));
                if (filePathName != null && !filePathName.contains(externalPath)) {
                    filePathName = updateFilepath(filePathName);
                }
                stBO.setFullPathFileName(filePathName);
                stBO.setMovieMode(cursor.getString(cursor.getColumnIndex(DBConstants.COL_MOVIE_MODE)));
                this.mSTBOList.add(stBO);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        return this.mSTBOList;
    }

    private String updateFilepath(String filePathName) {
        if (filePathName.contains(OTHER_FILE_PATH)) {
            String retPathName = filePathName.replace(OTHER_FILE_PATH, AB_FILE_PATH);
            return retPathName;
        }
        return filePathName;
    }

    public Cursor queryLastContent(StarTrailsBO tlBo) {
        String[] CONTENTS_QUERY_PROJECTION = {"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, "content_created_utc_date_time", ILocalDate.CONTENT_CREATED_LOCAL_DATE, "content_created_local_time"};
        String where = "content_created_utc_date_time BETWEEN '" + tlBo.getStartUtcDateTime() + "'" + ViewMode.AND + "'" + tlBo.getEndUtcDateTime() + "'";
        return AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), CONTENTS_QUERY_PROJECTION, where, null, null);
    }

    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    public boolean deleteRowFromDB(StarTrailsBO tlBO) {
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

    public void exportXMLtoMedia() throws IOException {
        String extPath = STUtility.getInstance().getFilePathOnMedia() + STConstants.TYPE_INFO_DB_PATH;
        String databaseFilePath = STUtility.getInstance().getFilePathOnMedia() + STConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME;
        AssetManager am = AppContext.getAppContext().getResources().getAssets();
        InputStream is = am.open(STConstants.TYPE_INFO_FILENAME);
        byte[] buffer = new byte[is.available()];
        while (is.read(buffer) != -1) {
            DatabaseUtil.exportDataToMedia(extPath, STConstants.TYPE_INFO_FILENAME, buffer, databaseFilePath);
        }
        is.close();
    }

    public DatabaseUtil.DbResult importDatabase() {
        File internalDbFile = new File(DBConstants.INTERNAL_DATABASE_PATH + DBConstants.DATABASE_NAME);
        File mediaDbFile = new File(STUtility.getInstance().getFilePathOnMedia() + STConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME);
        return DatabaseUtil.importDatabaseFromMedia(mediaDbFile, internalDbFile);
    }

    public boolean databaseRecovery() {
        Log.d(TAG, "databaseRecovery");
        boolean bResult = false;
        if (!DatabaseUtil.isInternalDbValid()) {
            Log.d(TAG, "databaseRecovery: internal db invalid");
            return false;
        }
        setStartrailsBOList();
        ArrayList<StarTrailsBO> starTrailsBOList = getStartrailsBOList();
        if (starTrailsBOList == null) {
            Log.d(TAG, "databaseRecovery: timelapseBOList null");
            return false;
        }
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        Iterator<StarTrailsBO> itr = starTrailsBOList.iterator();
        if (!AvindexStore.waitLoadMediaComplete(AvindexStore.getExternalMediaIds()[0])) {
            return false;
        }
        while (itr.hasNext()) {
            StarTrailsBO tlBo = itr.next();
            String where = "ID = '" + tlBo.getId() + "'";
            if (tlBo.getShootingMode().equalsIgnoreCase("STILL")) {
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
            } else if (!STUtility.getInstance().isAviFileExist(tlBo.getFullPathFileName())) {
                if (!isMediaExist()) {
                    break;
                }
                STUtility.getInstance().deleteThumbnailFile(tlBo.getFullPathFileName());
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
        File extDbFile = new File(STUtility.getInstance().getFilePathOnMedia() + STConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME);
        return DatabaseUtil.exportDatabaseToMedia(extDbFile, intDbFile);
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
            dBaseOperation.setStartrailsBOList();
            return;
        }
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            Log.d(TAG, "refreshDatabase: import database error");
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        }
    }
}
