package com.sony.imaging.app.synctosmartphone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.sony.imaging.app.synctosmartphone.commonUtil.AppContext;
import com.sony.imaging.app.synctosmartphone.commonUtil.AppLog;
import com.sony.imaging.app.util.DatabaseAdapterIf;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class DataBaseAdapter implements DatabaseAdapterIf {
    public static final String DATABASE_FILENAME = "SyncToSmartPhoneTables.sql";
    public static final int DATABASE_MODE_READ = 2;
    public static final int DATABASE_MODE_WRITE = 1;
    public static final String DATABASE_NAME = "autosync.db";
    public static final String INTERNAL_DATABASE_PATH = "/data/data/com.sony.imaging.app.synctosmartphone/databases/";
    public static final String PRIVATE_DATABASE_NAME = "APP00001.BIN";
    private static DataBaseAdapter sDataBaseAdapter = null;
    private DBHelper mDbHelper;
    private final String TABLE_AUTO_SYNC = "AutoSync";
    private final String TAG = AppLog.getClassName();
    private final float VERSION = 1.0f;
    private final int DB_VERSION = 10;
    private SQLiteDatabase mSQLiteDatabase = null;

    @Override // com.sony.imaging.app.util.DatabaseAdapterIf
    public void createNewEmptyInternalDb() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DataBaseAdapter dataBaseAdapter = getInstance();
        dataBaseAdapter.open(1);
        dataBaseAdapter.delete("AutoSync", null, null);
        dataBaseAdapter.close();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private DataBaseAdapter(Context context) {
        this.mDbHelper = new DBHelper(context, DATABASE_NAME, null, 10);
        if (this.mDbHelper != null) {
            AppLog.info(this.TAG, "Instance of DBHelper is created.");
        } else {
            AppLog.info(this.TAG, "Instance of DBHelper is not created.");
        }
    }

    public static DataBaseAdapter getInstance() {
        if (sDataBaseAdapter == null) {
            sDataBaseAdapter = new DataBaseAdapter(AppContext.getAppContext());
        }
        AppLog.info(AppLog.getClassName(), "Instance of DataBaseAdapter is created.");
        return sDataBaseAdapter;
    }

    public void open(int databaseOpeningMode) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        try {
            if (this.mDbHelper != null && 1 == databaseOpeningMode) {
                this.mSQLiteDatabase = this.mDbHelper.getWritableDatabase();
                AppLog.info(this.TAG, "Database opened in WRITE mode.");
            } else if (this.mDbHelper != null && 2 == databaseOpeningMode) {
                this.mSQLiteDatabase = this.mDbHelper.getReadableDatabase();
                AppLog.info(this.TAG, "Database opened in READ mode.");
            } else {
                AppLog.info(this.TAG, "mDbHelper is null.");
            }
        } catch (SQLiteException ex) {
            AppLog.error(this.TAG, "Database is not opened due to the following exception " + ex.toString());
        } catch (NullPointerException ex2) {
            AppLog.error(this.TAG, "NullPointerException :" + ex2.toString());
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void close() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mSQLiteDatabase != null) {
            this.mDbHelper.close();
            this.mSQLiteDatabase = null;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public long insert(String tableName, ContentValues contentValues) {
        this.mSQLiteDatabase.beginTransaction();
        long retVal = this.mSQLiteDatabase.insertOrThrow(tableName, null, contentValues);
        this.mSQLiteDatabase.setTransactionSuccessful();
        this.mSQLiteDatabase.endTransaction();
        AppLog.info(this.TAG, "Record inserted successfully with ID :  " + retVal + " in" + tableName + " table.");
        return retVal;
    }

    public int update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        this.mSQLiteDatabase.beginTransaction();
        int retVal = this.mSQLiteDatabase.update(tableName, contentValues, whereClause, whereArgs);
        this.mSQLiteDatabase.setTransactionSuccessful();
        this.mSQLiteDatabase.endTransaction();
        AppLog.info(this.TAG, retVal + " record updated successfully in " + tableName + " table.");
        return retVal;
    }

    public int delete(String tableName, String whereClause, String[] whereArgs) {
        this.mSQLiteDatabase.beginTransaction();
        int retVal = this.mSQLiteDatabase.delete(tableName, whereClause, whereArgs);
        this.mSQLiteDatabase.setTransactionSuccessful();
        this.mSQLiteDatabase.endTransaction();
        AppLog.info(this.TAG, retVal + " record deleted successfully in " + tableName + " table.");
        return retVal;
    }

    public Cursor query(String tableName, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return this.mSQLiteDatabase.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return this.mSQLiteDatabase.rawQuery(sql, selectionArgs);
    }

    public Cursor query(String sql, String[] selectionArgs) {
        return this.mSQLiteDatabase.rawQuery(sql, selectionArgs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DBHelper extends SQLiteOpenHelper {
        private final String TAG;

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.TAG = AppLog.getClassName();
            AppLog.checkIf(this.TAG, "DBHelper is creating .");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            AppLog.enter(this.TAG, AppLog.getMethodName());
            if (true == createDataBase(db)) {
                AppLog.checkIf(this.TAG, "DATABASE TABLES ARE CREATED SUCCESSFULLY.");
            } else {
                AppLog.checkIf(this.TAG, "DATABASE TABLES ARE NOT CREATED SUCCESSFULLY.");
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        private boolean createDataBase(SQLiteDatabase db) {
            AppLog.enter(this.TAG, AppLog.getMethodName());
            boolean retVal = false;
            try {
                InputStream inputStream = AppContext.getAppContext().getAssets().open(DataBaseAdapter.DATABASE_FILENAME);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    String dbQuery = bufferedReader.readLine();
                    if (dbQuery == null) {
                        break;
                    }
                    AppLog.info(this.TAG, "Create table query: " + dbQuery);
                    try {
                        db.execSQL(dbQuery);
                        retVal = true;
                    } catch (SQLException e) {
                        AppLog.error(this.TAG, "SQL Query is not executed successfully : " + e.toString());
                    }
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e2) {
                AppLog.error(this.TAG, "Unable to read the file : " + e2.toString());
            } catch (NullPointerException ex) {
                AppLog.error(this.TAG, "IOStream is not created : " + ex.toString());
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return retVal;
        }
    }
}
