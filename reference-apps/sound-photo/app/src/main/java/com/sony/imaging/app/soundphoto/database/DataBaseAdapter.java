package com.sony.imaging.app.soundphoto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.sony.imaging.app.soundphoto.util.AppContext;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.util.DatabaseAdapterIf;
import com.sony.imaging.app.util.FileHelper;
import java.io.File;

/* loaded from: classes.dex */
public class DataBaseAdapter implements DatabaseAdapterIf {
    private static final int DB_VERSION = 10;
    private static final float VERSION = 1.0f;
    private DBHelper mDbHelper;
    private static final String TAG = DataBaseAdapter.class.getName();
    private static DataBaseAdapter sDataBaseAdapter = null;
    private SQLiteDatabase mSQLiteDatabase = null;
    boolean isDataBaseCorrupt = false;

    @Override // com.sony.imaging.app.util.DatabaseAdapterIf
    public void createNewEmptyInternalDb() {
        File internalDbFile = new File(DBConstants.INTERNAL_DATABASE_PATH + DBConstants.DATABASE_NAME);
        if (FileHelper.exists(internalDbFile)) {
            internalDbFile.delete();
        }
        DataBaseAdapter dataBaseAdapter = getInstance();
        dataBaseAdapter.open(1);
        dataBaseAdapter.close();
    }

    public boolean getDatabaseStatusOpen() {
        return this.mSQLiteDatabase != null;
    }

    public DataBaseAdapter(Context context) {
        this.mDbHelper = new DBHelper(context, DBConstants.DATABASE_NAME, null, 10);
    }

    public static DataBaseAdapter getInstance() {
        if (sDataBaseAdapter == null) {
            sDataBaseAdapter = new DataBaseAdapter(AppContext.getAppContext());
        }
        return sDataBaseAdapter;
    }

    public void open(int databaseOpeningMode) {
        Log.d(TAG, "open");
        try {
            if (1 == databaseOpeningMode) {
                this.mSQLiteDatabase = this.mDbHelper.getWritableDatabase();
            } else if (2 == databaseOpeningMode) {
                this.mSQLiteDatabase = this.mDbHelper.getReadableDatabase();
            }
        } catch (SQLiteException ex) {
            Log.d(TAG, ex.toString());
        }
    }

    public void close() {
        if (this.mSQLiteDatabase != null) {
            this.mDbHelper.close();
            this.mSQLiteDatabase = null;
        }
    }

    public long insert(String tableName, ContentValues contentValues) {
        this.mSQLiteDatabase.beginTransaction();
        long id = this.mSQLiteDatabase.insertOrThrow(tableName, null, contentValues);
        this.mSQLiteDatabase.setTransactionSuccessful();
        this.mSQLiteDatabase.endTransaction();
        AppLog.exit(TAG, "mSQLiteDatabase.insertOrThrow");
        return id;
    }

    public int update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        this.mSQLiteDatabase.beginTransaction();
        int retVal = this.mSQLiteDatabase.update(tableName, contentValues, whereClause, whereArgs);
        this.mSQLiteDatabase.setTransactionSuccessful();
        this.mSQLiteDatabase.endTransaction();
        AppLog.info(TAG, "TESTTAG update photo  no = " + retVal + " photoID = " + whereClause + "retVal =" + retVal);
        return retVal;
    }

    public int delete(String tableName, String whereClause, String[] whereArgs) {
        this.mSQLiteDatabase.beginTransaction();
        int retVal = this.mSQLiteDatabase.delete(tableName, whereClause, whereArgs);
        this.mSQLiteDatabase.setTransactionSuccessful();
        this.mSQLiteDatabase.endTransaction();
        return retVal;
    }

    public Cursor query(String tableName, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return this.mSQLiteDatabase.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor query(String sql, String[] selectionArgs) {
        return this.mSQLiteDatabase.rawQuery(sql, selectionArgs);
    }

    public int getAvailableTotalFiles() {
        int max_rows = -1;
        Cursor c = null;
        try {
            try {
                c = query("SELECT COUNT(*) AS Total FROM tblSoundPhoto", null);
                c.moveToFirst();
                max_rows = c.getInt(c.getColumnIndexOrThrow("Total"));
                if (c != null) {
                    c.close();
                    c = null;
                }
            } catch (Exception e) {
                Log.d(TAG, "max row is 0 " + e.toString());
                if (c != null) {
                    c.close();
                    c = null;
                }
            }
            return max_rows;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DBHelper extends SQLiteOpenHelper {
        private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            createDataBase(db);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0030  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void createDataBase(android.database.sqlite.SQLiteDatabase r10) {
            /*
                r9 = this;
                r1 = 0
                android.content.Context r7 = com.sony.imaging.app.soundphoto.util.AppContext.getAppContext()     // Catch: java.io.IOException -> L46
                android.content.res.AssetManager r7 = r7.getAssets()     // Catch: java.io.IOException -> L46
                java.lang.String r8 = "dbProperties.sql"
                java.io.InputStream r6 = r7.open(r8)     // Catch: java.io.IOException -> L46
                r7 = 1024(0x400, float:1.435E-42)
                byte[] r0 = new byte[r7]     // Catch: java.io.IOException -> L46
                r2 = r1
            L14:
                int r7 = r6.read(r0)     // Catch: java.io.IOException -> L6a
                r8 = -1
                if (r7 == r8) goto L22
                java.lang.String r1 = new java.lang.String     // Catch: java.io.IOException -> L6a
                r1.<init>(r0)     // Catch: java.io.IOException -> L6a
                r2 = r1
                goto L14
            L22:
                r1 = r2
            L23:
                java.util.StringTokenizer r3 = new java.util.StringTokenizer
                java.lang.String r7 = "/"
                r3.<init>(r1, r7)
            L2a:
                boolean r7 = r3.hasMoreTokens()
                if (r7 == 0) goto L60
                r4 = 0
                java.lang.String r4 = r3.nextToken()     // Catch: java.util.NoSuchElementException -> L53
            L35:
                r10.execSQL(r4)     // Catch: android.database.SQLException -> L39
                goto L2a
            L39:
                r5 = move-exception
                java.lang.String r7 = com.sony.imaging.app.soundphoto.database.DataBaseAdapter.access$100()
                java.lang.String r8 = r5.toString()
                android.util.Log.d(r7, r8)
                goto L2a
            L46:
                r5 = move-exception
            L47:
                java.lang.String r7 = com.sony.imaging.app.soundphoto.database.DataBaseAdapter.access$100()
                java.lang.String r8 = r5.toString()
                android.util.Log.d(r7, r8)
                goto L23
            L53:
                r5 = move-exception
                java.lang.String r7 = com.sony.imaging.app.soundphoto.database.DataBaseAdapter.access$100()
                java.lang.String r8 = r5.toString()
                android.util.Log.d(r7, r8)
                goto L35
            L60:
                java.lang.String r7 = com.sony.imaging.app.soundphoto.database.DataBaseAdapter.access$100()
                java.lang.String r8 = "DATABASE TABLES ARE CREATED SUCCESSFULLY."
                android.util.Log.d(r7, r8)
                return
            L6a:
                r5 = move-exception
                r1 = r2
                goto L47
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.soundphoto.database.DataBaseAdapter.DBHelper.createDataBase(android.database.sqlite.SQLiteDatabase):void");
        }
    }

    public boolean isDBStatusCorrupt() {
        return this.isDataBaseCorrupt;
    }

    public int getTotalCount() {
        if (this.mSQLiteDatabase == null) {
            open(2);
        }
        int totalCount = getAvailableTotalFiles();
        close();
        return totalCount;
    }

    public void setDBCorruptStatus(boolean dbStatus) {
        this.isDataBaseCorrupt = dbStatus;
    }
}
