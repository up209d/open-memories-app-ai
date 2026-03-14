package com.sony.imaging.app.base.caution;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/* loaded from: classes.dex */
abstract class DatabaseAccessorBase {
    private static final String SEARCH_FROM_APOTYPE = "* from APOType where ";
    private static final String SEARCH_FROM_BOOTFAILED = "* from bootFailedFactorIds where ";
    private static final String SEARCH_FROM_CAUTONID2RESOURCEID = "* from cautionId2ResourceId where ";
    private static final String SEARCH_FROM_IMAGEIDS = "* from imageIds where ";
    private static final String SEARCH_FROM_LEDIDS = "* from ledIds where ";
    private static final String SEARCH_FROM_PRIORITY = "* from priority where ";
    private static final String SEARCH_FROM_RESOURCEID2CAUTIONDATA = "* from resourceId2CautionData where ";
    private static final String SEARCH_FROM_STRINGIDS = "* from stringIds where ";
    private static final String TAG = DatabaseAccessorBase.class.getSimpleName();
    private SQLiteDatabase mDB;

    abstract SQLiteDatabase setDataBase();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(Context context) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openDatabase() {
        this.mDB = setDataBase();
        Log.i(TAG, "openDataBase mDB:" + this.mDB);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeDatabase() {
        if (this.mDB != null) {
            this.mDB.close();
        }
        Log.i(TAG, "closeDataBase mDB:" + this.mDB);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cursor searchData(String columname, String columval, String tablename) {
        Cursor c = null;
        String query = makeSearchQuery(columname, columval, tablename);
        if (query != null) {
            try {
                c = this.mDB.rawQuery(query, null);
                boolean result = c.moveToFirst();
                if (!result) {
                    c.close();
                    c = null;
                }
                Log.i(TAG, "searchData cursor:" + c + "moveToFirst:" + result);
            } catch (SQLiteException e) {
                return null;
            }
        }
        return c;
    }

    private String makeSearchQuery(String columname, String columval, String tablename) {
        String query;
        if (tablename == "cautionId2ResourceId") {
            query = "select " + SEARCH_FROM_CAUTONID2RESOURCEID + columname + " = '" + columval + "';";
        } else if (tablename == "resourceId2CautionData") {
            query = "select " + SEARCH_FROM_RESOURCEID2CAUTIONDATA + columname + " = '" + columval + "';";
        } else if (tablename == "stringIds") {
            query = "select " + SEARCH_FROM_STRINGIDS + columname + " = '" + columval + "';";
        } else if (tablename == "imageIds") {
            query = "select " + SEARCH_FROM_IMAGEIDS + columname + " = '" + columval + "';";
        } else if (tablename == "ledIds") {
            query = "select " + SEARCH_FROM_LEDIDS + columname + " = '" + columval + "';";
        } else if (tablename == "priority") {
            query = "select " + SEARCH_FROM_PRIORITY + columname + " = '" + columval + "';";
        } else if (tablename == "bootFailedFactorIds") {
            query = "select " + SEARCH_FROM_BOOTFAILED + columname + " = '" + columval + "';";
        } else if (tablename == "APOType") {
            query = "select " + SEARCH_FROM_APOTYPE + columname + " = '" + columval + "';";
        } else {
            Log.e(TAG, "makeSearchQuery error table name tablename:" + tablename);
            return null;
        }
        Log.i(TAG, "makeSearchQuery query:" + query);
        return query;
    }
}
