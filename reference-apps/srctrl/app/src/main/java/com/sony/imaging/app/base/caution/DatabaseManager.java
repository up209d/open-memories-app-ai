package com.sony.imaging.app.base.caution;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/* loaded from: classes.dex */
public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static DatabaseManager cautionDatabaseManager = new DatabaseManager();
    private DiademDatabaseAccessor diademAccs = null;
    private DLAppDatabaseAccessor dlAppAccs = null;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return cautionDatabaseManager;
    }

    public void initialize(Context context) {
        this.diademAccs = new DiademDatabaseAccessor();
        this.dlAppAccs = new DLAppDatabaseAccessor();
        this.diademAccs.initialize(context);
        this.dlAppAccs.initialize(context);
        this.diademAccs.openDatabase();
        this.dlAppAccs.openDatabase();
        Log.i(TAG, "initialize");
    }

    public void terminate() {
        this.diademAccs.closeDatabase();
        this.dlAppAccs.closeDatabase();
        Log.i(TAG, "terminate");
    }

    public Cursor searchData(String columname, String columval, String tablename, int kind) {
        Cursor c;
        if (kind == 0 || kind == 262144) {
            c = this.diademAccs.searchData(columname, columval, tablename);
        } else {
            c = this.dlAppAccs.searchData(columname, columval, tablename);
        }
        Log.i(TAG, "searchData cursor:" + c);
        return c;
    }
}
