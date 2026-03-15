package com.sony.imaging.app.base.shooting.camera.moviesetting;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;

/* loaded from: classes.dex */
public class BaseDatabaseAccessor extends DatabaseAccessorBase {
    private static final String DB_PATH = "/system/etc/setting/movieSetting.db";
    private static final String TAG = BaseDatabaseAccessor.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseAccessorBase
    protected SQLiteDatabase setDataBase() {
        Log.i(TAG, "setDataBase");
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            Log.i(TAG, "DB not exist");
            return null;
        }
        try {
            return SQLiteDatabase.openDatabase(DB_PATH, null, 17);
        } catch (SQLException e) {
            Log.i(TAG, "cannot open database");
            return null;
        }
    }
}
