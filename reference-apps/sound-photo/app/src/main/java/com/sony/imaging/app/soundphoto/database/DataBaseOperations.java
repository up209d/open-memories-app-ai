package com.sony.imaging.app.soundphoto.database;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.soundphoto.util.AppContext;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.provider.AvindexStore;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class DataBaseOperations {
    private static DataBaseOperations sDataBaseOperations;
    private static final String TAG = DataBaseOperations.class.getName();
    public static int sTotalCount = -1;
    private ArrayList<String> mSTBOList = null;
    private ArrayList<Integer> mSTFolderList = null;
    ArrayList<SoundPhotoCursorData> mLocalSoundDataList = null;

    public static DataBaseOperations getInstance() {
        if (sDataBaseOperations == null) {
            sDataBaseOperations = new DataBaseOperations();
        }
        return sDataBaseOperations;
    }

    public ArrayList<String> getSoundPhotoBOList() {
        if (this.mSTBOList == null) {
            this.mSTBOList = new ArrayList<>();
            setSoundPhotoBOList();
        }
        return this.mSTBOList;
    }

    public ArrayList<Integer> getSoundPhotoFolderList() {
        if (this.mSTFolderList == null) {
            createSoundPhotoFolderList();
        }
        return this.mSTFolderList;
    }

    private void createSoundPhotoFolderList() {
        this.mSTFolderList = new ArrayList<>();
    }

    public void setSoundPhotoBOList() {
        this.mSTBOList = getList();
        sTotalCount = -1;
        setTotalFiles();
    }

    public void removeElementAt(int index) {
        if (this.mSTBOList != null && this.mSTBOList.size() > 0) {
            this.mSTBOList.remove(index);
        }
    }

    public void removeAllBOObjects() {
        if (this.mSTBOList != null) {
            this.mSTBOList.clear();
            this.mSTBOList = null;
        }
    }

    public ArrayList<String[]> getDbTableDetails() {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(2);
        Cursor c = dataBaseAdapter.query("SELECT * FROM sqlite_master WHERE type='table'", null);
        ArrayList<String[]> result = new ArrayList<>();
        result.add(c.getColumnNames());
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String[] temp = new String[c.getColumnCount()];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = c.getString(i);
            }
            result.add(temp);
            c.moveToNext();
        }
        return result;
    }

    public void saveJpeg(SoundPhotoBO stBO) {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COL_CONTENT_ID, Integer.valueOf(stBO.getContentId()));
        contentValues.put(DBConstants.COL_FILE_PATH, stBO.getFilePath());
        contentValues.put(DBConstants.COL_FOLDER_NUMBER, Integer.valueOf(stBO.getFolderNumber()));
        contentValues.put(DBConstants.COL_FILE_NUMBER, Integer.valueOf(stBO.getFileNumber()));
        dataBaseAdapter.insert(DBConstants.TAB_TABLE_NAME, contentValues);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
    }

    public void saveJpeg(int _contentId, String _filePath, int _folderNumber, int __mFileNumber) {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COL_CONTENT_ID, Integer.valueOf(_contentId));
        contentValues.put(DBConstants.COL_FILE_PATH, _filePath);
        contentValues.put(DBConstants.COL_FOLDER_NUMBER, Integer.valueOf(_folderNumber));
        contentValues.put(DBConstants.COL_FILE_NUMBER, Integer.valueOf(__mFileNumber));
        contentValues.put(DBConstants.COL_UPDATE_STATUS, (Integer) 0);
        dataBaseAdapter.insert(DBConstants.TAB_TABLE_NAME, contentValues);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
    }

    public void update(ArrayList<String[]> fileList) {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COL_UPDATE_STATUS, (Integer) 1);
        StringBuffer whereClause = new StringBuffer();
        for (int i = 0; i < fileList.size(); i++) {
            String[] file = fileList.get(i);
            whereClause.append(" ( folder_number= " + file[0] + ViewMode.AND + DBConstants.COL_FILE_NUMBER + "= " + file[1] + " )");
            if (i < fileList.size() - 1) {
                whereClause.append(ViewMode.OR);
            }
        }
        dataBaseAdapter.update(DBConstants.TAB_TABLE_NAME, contentValues, whereClause.toString(), null);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
    }

    public void printDBTable() {
        DataBaseAdapter db = DataBaseAdapter.getInstance();
        db.open(2);
        Cursor cursor = db.query("SELECT * FROM tblSoundPhoto", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int folderNumberIndex = cursor.getColumnIndex(DBConstants.COL_FOLDER_NUMBER);
                int fileNumberIndex = cursor.getColumnIndex(DBConstants.COL_FILE_NUMBER);
                int filepathIndex = cursor.getColumnIndex(DBConstants.COL_FILE_PATH);
                int fileNumber = cursor.getInt(fileNumberIndex);
                int folderNumber = cursor.getInt(folderNumberIndex);
                String filePath = cursor.getString(filepathIndex);
                AppLog.info(TAG, "PRINT  Folder Number == " + folderNumber + " fileNumber == " + fileNumber + " filePath == " + filePath);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x003b, code lost:            if (r1.moveToFirst() != false) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x003d, code lost:            r3 = r1.getString(r1.getColumnIndex(com.sony.imaging.app.soundphoto.database.DBConstants.COL_FILE_PATH));        r7.mSTBOList.add(r3);     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0050, code lost:            if (r1.moveToNext() != false) goto L22;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0052, code lost:            if (r1 == null) goto L18;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0054, code lost:            r1.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0058, code lost:            if (r2 == null) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x005a, code lost:            r2.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x005e, code lost:            r7.mSTBOList.trimToSize();     */
    /* JADX WARN: Code restructure failed: missing block: B:24:?, code lost:            return r7.mSTBOList;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.ArrayList<java.lang.String> getList() {
        /*
            r7 = this;
            r4 = 0
            r7.removeAllBOObjects()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r7.mSTBOList = r5
            java.lang.String r0 = "select file_path from tblSoundPhoto"
            r2 = 0
            com.sony.imaging.app.soundphoto.database.DataBaseAdapter r2 = com.sony.imaging.app.soundphoto.database.DataBaseAdapter.getInstance()
            r5 = 2
            r2.open(r5)
            r1 = 0
            android.database.Cursor r1 = r2.query(r0, r4)
            if (r1 == 0) goto L37
            int r5 = r1.getCount()
            if (r5 != 0) goto L37
            java.lang.String r5 = com.sony.imaging.app.soundphoto.database.DataBaseOperations.TAG
            java.lang.String r6 = "getList: No records available"
            android.util.Log.d(r5, r6)
            if (r1 == 0) goto L30
            r1.close()
            r1 = 0
        L30:
            if (r2 == 0) goto L36
            r2.close()
            r2 = 0
        L36:
            return r4
        L37:
            boolean r4 = r1.moveToFirst()
            if (r4 == 0) goto L52
        L3d:
            java.lang.String r4 = "file_path"
            int r4 = r1.getColumnIndex(r4)
            java.lang.String r3 = r1.getString(r4)
            java.util.ArrayList<java.lang.String> r4 = r7.mSTBOList
            r4.add(r3)
            boolean r4 = r1.moveToNext()
            if (r4 != 0) goto L3d
        L52:
            if (r1 == 0) goto L58
            r1.close()
            r1 = 0
        L58:
            if (r2 == 0) goto L5e
            r2.close()
            r2 = 0
        L5e:
            java.util.ArrayList<java.lang.String> r4 = r7.mSTBOList
            r4.trimToSize()
            java.util.ArrayList<java.lang.String> r4 = r7.mSTBOList
            goto L36
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.soundphoto.database.DataBaseOperations.getList():java.util.ArrayList");
    }

    public Cursor queryLastContent(SoundPhotoBO tlBo) {
        String[] CONTENTS_QUERY_PROJECTION = {"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, "content_created_utc_date_time", ILocalDate.CONTENT_CREATED_LOCAL_DATE, "content_created_local_time"};
        String where = "dcf_folder_number = '" + tlBo.getFolderNumber() + "' && dcf_file_number = '" + tlBo.getFileNumber() + "'";
        return AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), CONTENTS_QUERY_PROJECTION, where, null, null);
    }

    public void initializeSoundDataList() {
        this.mLocalSoundDataList = new ArrayList<>();
    }

    public void deInitializeSoundDataList() {
        if (this.mLocalSoundDataList != null) {
            this.mLocalSoundDataList.clear();
            this.mLocalSoundDataList = null;
        }
    }

    public ArrayList<SoundPhotoCursorData> getLocalSoundDataList() {
        return this.mLocalSoundDataList;
    }

    public String getTotalFileFromLocalDB() {
        StringBuffer whereClause = new StringBuffer();
        DataBaseAdapter db = DataBaseAdapter.getInstance();
        db.open(2);
        Cursor cursor = db.query("SELECT DISTINCT folder_number FROM tblSoundPhoto", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int folderNumberIndex = cursor.getColumnIndex(DBConstants.COL_FOLDER_NUMBER);
                int folderNumber = cursor.getInt(folderNumberIndex);
                String fileList = getFileList(folderNumber);
                if (fileList != null && fileList.length() > 1) {
                    whereClause.append(StringBuilderThreadLocal.ROUND_BRACKET_OPEN);
                    whereClause.append(fileList);
                    whereClause.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
                    if (!cursor.isLast()) {
                        whereClause.append(ViewMode.OR);
                    }
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        if (whereClause.length() < 1) {
            return null;
        }
        AppLog.info(TAG, "mWhereClause =" + ((Object) whereClause));
        return whereClause.toString();
    }

    public String getFileList(int folderNumber) {
        StringBuffer whereClause = new StringBuffer();
        String query = "SELECT file_number FROM tblSoundPhoto where folder_number = " + folderNumber + ViewMode.AND + DBConstants.COL_UPDATE_STATUS + " = 0";
        DataBaseAdapter db = DataBaseAdapter.getInstance();
        Cursor cursor = db.query(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                whereClause.append(" ( dcf_folder_number = " + folderNumber + " ) AND ( ");
            }
            while (cursor.moveToNext()) {
                int fileNumberIndex = cursor.getColumnIndex(DBConstants.COL_FILE_NUMBER);
                int fileNumber = cursor.getInt(fileNumberIndex);
                whereClause.append("dcf_file_number = " + fileNumber);
                if (!cursor.isLast()) {
                    whereClause.append(ViewMode.OR);
                } else {
                    whereClause.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        AppLog.info(TAG, "mWhereClause =" + ((Object) whereClause));
        if (whereClause.length() < 1) {
            return null;
        }
        AppLog.info(TAG, "mWhereClause =" + ((Object) whereClause));
        return whereClause.toString();
    }

    public String getQueryWhereClauseFromLocalDb(int folderNumber) {
        StringBuffer whereClause = new StringBuffer();
        String query = "SELECT file_number FROM tblSoundPhoto where folder_number = " + folderNumber;
        DataBaseAdapter db = DataBaseAdapter.getInstance();
        db.open(2);
        Cursor cursor = db.query(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                whereClause.append(" ( dcf_folder_number = " + folderNumber + " ) AND ( ");
            }
            while (cursor.moveToNext()) {
                int fileNumberIndex = cursor.getColumnIndex(DBConstants.COL_FILE_NUMBER);
                int fileNumber = cursor.getInt(fileNumberIndex);
                whereClause.append("dcf_file_number = " + fileNumber);
                if (!cursor.isLast()) {
                    whereClause.append(ViewMode.OR);
                } else {
                    whereClause.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
                }
            }
            this.mLocalSoundDataList.add(new SoundPhotoCursorData(folderNumber, cursor.getCount()));
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        AppLog.info(TAG, "mWhereClause =" + ((Object) whereClause));
        return whereClause.toString();
    }

    public String getAllFolderWhereClauseFromLocalDb() {
        StringBuffer whereClause = new StringBuffer();
        DataBaseAdapter db = DataBaseAdapter.getInstance();
        db.open(2);
        Cursor cursor = db.query("SELECT DISTINCT folder_number FROM tblSoundPhoto", null);
        if (this.mSTFolderList != null) {
            this.mSTFolderList.clear();
        } else {
            createSoundPhotoFolderList();
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int folderNumberIndex = cursor.getColumnIndex(DBConstants.COL_FOLDER_NUMBER);
                int folderNumber = cursor.getInt(folderNumberIndex);
                this.mSTFolderList.add(Integer.valueOf(folderNumber));
                whereClause.append("(dcf_folder_number = " + folderNumber + " )");
                if (!cursor.isLast()) {
                    whereClause.append(ViewMode.OR);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        if (whereClause.length() < 1) {
            return null;
        }
        AppLog.info(TAG, "mWhereClause =" + ((Object) whereClause));
        return whereClause.toString();
    }

    public int getTotalFiles() {
        if (sTotalCount == -1) {
            setTotalFiles();
        }
        return sTotalCount;
    }

    public int setTotalFiles() {
        if (sTotalCount == -1) {
            sTotalCount = DataBaseAdapter.getInstance().getTotalCount();
        }
        return sTotalCount;
    }

    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    public boolean deleteRowFromDB(SoundPhotoBO tlBO) {
        boolean bResult = false;
        String whereClause = "file_path = '" + tlBO.getFilePath() + "'";
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

    public boolean deleteRowFromDB(String filePath) {
        boolean bResult = false;
        String whereClause = "file_path = '" + filePath + "'";
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
        printDBTable();
        return bResult;
    }

    public boolean deleteRowFromDBMultiple(String whereClause) {
        boolean bResult = false;
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
        printDBTable();
        return bResult;
    }

    public boolean deleteMultipleRowsFromDB(String deleteMultipleQuery) {
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.open(1);
        dataBaseAdapter.query(deleteMultipleQuery, null);
        if (dataBaseAdapter != null) {
            dataBaseAdapter.close();
        }
        return false;
    }

    public boolean deleteRowFromDB(int folderNumber, int fileNumber) {
        boolean bResult = false;
        String whereClause = "folder_number = " + folderNumber + DBConstants.COL_FILE_NUMBER + " = " + fileNumber;
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
        String extPath = SPUtil.getInstance().getFilePathOnMedia() + SPConstants.TYPE_INFO_DB_PATH;
        String databaseFilePath = SPUtil.getInstance().getFilePathOnMedia() + SPConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME;
        AssetManager am = AppContext.getAppContext().getResources().getAssets();
        InputStream is = am.open(SPConstants.TYPE_INFO_FILENAME);
        byte[] buffer = new byte[is.available()];
        while (is.read(buffer) != -1) {
            DatabaseUtil.exportDataToMedia(extPath, SPConstants.TYPE_INFO_FILENAME, buffer, databaseFilePath);
        }
        is.close();
    }

    public DatabaseUtil.DbResult importDatabase() {
        File internalDbFile = new File(DBConstants.INTERNAL_DATABASE_PATH + DBConstants.DATABASE_NAME);
        File mediaDbFile = new File(SPUtil.getInstance().getFilePathOnMedia() + SPConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME);
        return DatabaseUtil.importDatabaseFromMedia(mediaDbFile, internalDbFile);
    }

    public boolean databaseRecovery() {
        String queryPath;
        Log.d(TAG, "databaseRecovery");
        boolean bResult = false;
        if (!DatabaseUtil.isInternalDbValid()) {
            Log.d(TAG, "databaseRecovery: internal db invalid");
            return false;
        }
        setSoundPhotoBOList();
        ArrayList<String> soundPhotoBOList = getSoundPhotoBOList();
        if (soundPhotoBOList == null) {
            Log.d(TAG, "databaseRecovery: soundPhotoBOList null");
            refreshDatabase();
            DatabaseUtil.setIsDatabaseChecked(true);
            return false;
        }
        Iterator<String> itr = soundPhotoBOList.iterator();
        if (!AvindexStore.waitLoadMediaComplete(AvindexStore.getExternalMediaIds()[0])) {
            return false;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("file_path IN (");
        while (itr.hasNext()) {
            boolean result = false;
            String soBo = itr.next();
            File file = new File(soBo);
            if (!isMediaExist()) {
                break;
            }
            if (!file.exists()) {
                result = true;
                buffer.append("'" + soBo + "'");
                buffer.append(",");
            }
            if (result) {
                itr.remove();
                bResult = true;
            }
        }
        if (buffer.charAt(buffer.length() - 1) == ',') {
            queryPath = buffer.substring(0, buffer.length() - 1);
        } else {
            queryPath = buffer.toString();
        }
        String queryPath2 = queryPath.concat(LogHelper.MSG_CLOSE_BRACKET);
        getInstance().deleteRowFromDBMultiple(queryPath2);
        refreshDatabase();
        DatabaseUtil.setIsDatabaseChecked(true);
        AppLog.exit(TAG, "queryPath= " + queryPath2);
        printDBTable();
        return bResult;
    }

    private boolean isMediaExist() {
        String env = Environment.getExternalStorageState();
        boolean ret = "mounted".equals(env);
        return ret;
    }

    public DatabaseUtil.DbResult exportDatabase() {
        File intDbFile = new File(DBConstants.INTERNAL_DATABASE_PATH + DBConstants.DATABASE_NAME);
        File extDbFile = new File(SPUtil.getInstance().getFilePathOnMedia() + SPConstants.TYPE_INFO_DB_PATH + DBConstants.PRIVATE_FILE_NAME);
        return DatabaseUtil.exportDatabaseToMedia(extDbFile, intDbFile);
    }

    public void refreshDatabase() {
        Log.d(TAG, "refreshDatabase");
        DataBaseOperations dBaseOperation = getInstance();
        DatabaseUtil.DbResult result = dBaseOperation.importDatabase();
        if (DatabaseUtil.DbResult.SUCCEEDED == result) {
            Log.d(TAG, "refreshDatabase: import succeeded");
            if (!DatabaseUtil.isDatabaseChecked()) {
                DatabaseUtil.DbResult result2 = dBaseOperation.exportDatabase();
                if (DatabaseUtil.DbResult.DB_ERROR == result2) {
                    Log.d(TAG, "refreshDatabase: export db error");
                    DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                } else if (DatabaseUtil.DbResult.SUCCEEDED != result2) {
                    Log.d(TAG, "refreshDatabase - Export failed Result:" + result2);
                    dBaseOperation.importDatabase();
                } else {
                    Log.d(TAG, "refreshDatabase: export successful");
                }
            }
            dBaseOperation.setSoundPhotoBOList();
            return;
        }
        if (DatabaseUtil.DbResult.DB_ERROR == result) {
            Log.d(TAG, "refreshDatabase: import database error");
            DataBaseAdapter.getInstance().setDBCorruptStatus(true);
        }
    }

    public int getBeforeFileCount(int countOfOneBefore) {
        int previoustotalCount = 0;
        if (getSoundPhotoFolderList().contains(Integer.valueOf(countOfOneBefore))) {
            int position = getSoundPhotoFolderList().indexOf(Integer.valueOf(countOfOneBefore));
            for (int index = 0; index < position; index++) {
                SoundPhotoCursorData spData = getLocalSoundDataList().get(index);
                previoustotalCount += spData.fileCount;
            }
        }
        return previoustotalCount;
    }
}
