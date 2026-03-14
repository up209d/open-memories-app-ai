package com.sony.imaging.app.base.common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;

/* loaded from: classes.dex */
public class CommonSettings {
    private static final String COLUMN_COMMON_SETTINGS_NAME = "name";
    private static final String COLUMN_COMMON_SETTINGS_VALUE = "value";
    private static final String CREATE_TABLE_COMMON_SETTINGS = "CREATE TABLE IF NOT EXISTS CommonSettings (name TEXT PRIMARY KEY, value TEXT);";
    private static final String DB_PATH = "/data/data/DLAppCommon.db";
    public static final String KEY_DISP_MODE_EVF = "DispModeEvf";
    public static final String KEY_DISP_MODE_LCD = "DispModeLcd";
    public static final String KEY_DISP_MODE_PBS = "DispModePbs";
    private static final String LOG_MSG_CLEARED_INSERTED = "Setting is inserted after all existing setting deleted. ";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_CREATESUCCESS = "A table is created or already exists. ";
    private static final String LOG_MSG_DBHASNOTOPENED = "Illegal state. Database has not been opened.";
    private static final String LOG_MSG_EQUAL = " = ";
    private static final String LOG_MSG_EXCEPTIONOCCURED = "Exception occured at ";
    private static final String LOG_MSG_INSERTED = "Setting is inserted. ";
    private static final String LOG_MSG_KEY = "key = ";
    private static final String LOG_MSG_KEYNOTFOUND = "The key does not exists on the table. ";
    private static final String LOG_MSG_MODIFINGDB = "modifing database. ";
    private static final String LOG_MSG_NAME = "name = ";
    private static final String LOG_MSG_NL = "\n";
    private static final String LOG_MSG_NOTEXPECTEDRAW = "Illegal state. The raw count is not excepted. ";
    private static final String LOG_MSG_OPENINGDB = "opening or creating database. ";
    private static final String LOG_MSG_PARSING = "parsing. ";
    private static final String LOG_MSG_RAW = "raw = ";
    private static final String LOG_MSG_TABLE = "table = ";
    private static final String LOG_MSG_TABLEEXISTS = "Table exists. count = ";
    private static final String LOG_MSG_UPDATED = "Setting is updated. ";
    private static final String LOG_MSG_VALUE = "value = ";
    private static final String QUERY_FORMAT_GET_COMMON_SETTING = "SELECT value FROM CommonSettings WHERE name = ?";
    private static final String QUERY_FORMAT_GET_COUNT_COMMON_SETTING = "SELECT COUNT(*) FROM CommonSettings WHERE name = ?";
    private static final String QUERY_FORMAT_IS_TABLE_EXISTS = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?;";
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TABLE_COMMON_SETTINGS = "CommonSettings";
    private static final String TAG = "CommonSettings";
    private static final String WHERECLAUSE_UPDATE_COMMON_SETTING = "name = ?";
    private static CommonSettings mInstance;
    private SQLiteDatabase mDb = null;

    private CommonSettings() {
    }

    public static CommonSettings getInstance() {
        if (mInstance == null) {
            mInstance = new CommonSettings();
        }
        return mInstance;
    }

    public void resume() {
        if (this.mDb == null || !this.mDb.isOpen()) {
            try {
                this.mDb = SQLiteDatabase.openDatabase(DB_PATH, null, 268435472);
                File db = new File(DB_PATH);
                db.setWritable(true, false);
                if (!isTableExists("CommonSettings")) {
                    initializeTable("CommonSettings");
                }
            } catch (Exception e) {
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_EXCEPTIONOCCURED).append(LOG_MSG_OPENINGDB).append(LOG_MSG_NL).append(e.getClass().getName()).append(LOG_MSG_NL).append(e.getStackTrace());
                Log.e("CommonSettings", STRBUILD.toString());
            }
        }
    }

    public void pause() {
        this.mDb.close();
    }

    private boolean isTableExists(String table) {
        if (this.mDb == null) {
            Log.e("CommonSettings", LOG_MSG_DBHASNOTOPENED);
            return false;
        }
        Cursor cur = this.mDb.rawQuery(QUERY_FORMAT_IS_TABLE_EXISTS, new String[]{table});
        cur.moveToFirst();
        int count = cur.getInt(0);
        cur.close();
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_TABLEEXISTS).append(count);
        Log.i("CommonSettings", STRBUILD.toString());
        return count > 0;
    }

    private void initializeTable(String table) {
        if (this.mDb == null) {
            Log.e("CommonSettings", LOG_MSG_DBHASNOTOPENED);
            return;
        }
        this.mDb.execSQL(CREATE_TABLE_COMMON_SETTINGS);
        STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_CREATESUCCESS).append(LOG_MSG_TABLE).append(table);
        Log.i("CommonSettings", STRBUILD.toString());
    }

    public String getCommonSettingString(String key) {
        if (this.mDb == null) {
            Log.e("CommonSettings", LOG_MSG_DBHASNOTOPENED);
            return null;
        }
        Cursor cur = this.mDb.rawQuery(QUERY_FORMAT_GET_COMMON_SETTING, new String[]{key});
        cur.moveToFirst();
        if (cur.getCount() == 0) {
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_KEYNOTFOUND).append(LOG_MSG_KEY).append(key).append(", ").append(LOG_MSG_TABLE).append("CommonSettings");
            Log.i("CommonSettings", STRBUILD.toString());
            return null;
        }
        if (cur.getCount() > 1) {
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_NOTEXPECTEDRAW).append(LOG_MSG_RAW).append(cur.getCount());
            Log.e("CommonSettings", STRBUILD.toString());
            return null;
        }
        String value = cur.getString(0);
        cur.close();
        STRBUILD.replace(0, STRBUILD.length(), key).append(LOG_MSG_EQUAL).append(value);
        Log.i("CommonSettings", STRBUILD.toString());
        return value;
    }

    public void putCommonSettingString(String key, String value) {
        if (this.mDb == null) {
            Log.e("CommonSettings", LOG_MSG_DBHASNOTOPENED);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMON_SETTINGS_NAME, key);
        values.put(COLUMN_COMMON_SETTINGS_VALUE, value);
        this.mDb.beginTransaction();
        try {
            Cursor cur = this.mDb.rawQuery(QUERY_FORMAT_GET_COUNT_COMMON_SETTING, new String[]{key});
            cur.moveToFirst();
            int count = cur.getInt(0);
            cur.close();
            if (count == 0) {
                this.mDb.insert("CommonSettings", null, values);
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_INSERTED).append(key).append(LOG_MSG_EQUAL).append(value);
                Log.i("CommonSettings", STRBUILD.toString());
            } else if (count == 1) {
                this.mDb.update("CommonSettings", values, WHERECLAUSE_UPDATE_COMMON_SETTING, new String[]{key});
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_UPDATED).append(key).append(LOG_MSG_EQUAL).append(value);
                Log.i("CommonSettings", STRBUILD.toString());
            } else {
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_NOTEXPECTEDRAW).append(LOG_MSG_RAW).append(cur.getCount());
                Log.e("CommonSettings", STRBUILD.toString());
                this.mDb.delete("CommonSettings", WHERECLAUSE_UPDATE_COMMON_SETTING, new String[]{key});
                this.mDb.insert("CommonSettings", null, values);
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_CLEARED_INSERTED).append(key).append(LOG_MSG_EQUAL).append(value);
                Log.i("CommonSettings", STRBUILD.toString());
            }
            this.mDb.setTransactionSuccessful();
        } catch (Exception e) {
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_EXCEPTIONOCCURED).append(LOG_MSG_MODIFINGDB).append(LOG_MSG_NL).append(LOG_MSG_NAME).append(key).append(LOG_MSG_VALUE).append(value).append(LOG_MSG_NL).append(e.getClass().getName()).append(LOG_MSG_NL).append(e.getStackTrace());
            Log.e("CommonSettings", STRBUILD.toString());
        } finally {
            this.mDb.endTransaction();
        }
    }

    public int getCommonSettingInt(String key, int defValue) {
        String value = getCommonSettingString(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_EXCEPTIONOCCURED).append(LOG_MSG_PARSING).append(LOG_MSG_NL).append(LOG_MSG_VALUE).append(value).append(LOG_MSG_NL).append(e.getClass().getName()).append(LOG_MSG_NL).append(e.getStackTrace());
                Log.e("CommonSettings", STRBUILD.toString());
                return defValue;
            }
        }
        return defValue;
    }

    public void putCommonSettingInt(String key, int value) {
        try {
            putCommonSettingString(key, Integer.toString(value));
        } catch (Exception e) {
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_EXCEPTIONOCCURED).append(LOG_MSG_PARSING).append(LOG_MSG_NL).append(LOG_MSG_VALUE).append(value).append(LOG_MSG_NL).append(e.getClass().getName()).append(LOG_MSG_NL).append(e.getStackTrace());
            Log.e("CommonSettings", STRBUILD.toString());
        }
    }
}
