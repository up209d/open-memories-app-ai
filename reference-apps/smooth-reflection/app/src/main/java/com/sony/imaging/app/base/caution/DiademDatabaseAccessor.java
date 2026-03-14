package com.sony.imaging.app.base.caution;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/* loaded from: classes.dex */
class DiademDatabaseAccessor extends DatabaseAccessorBase {
    private static final String TAG = DiademDatabaseAccessor.class.getSimpleName();

    @Override // com.sony.imaging.app.base.caution.DatabaseAccessorBase
    SQLiteDatabase setDataBase() {
        Log.i(TAG, "setDataBase");
        return SQLiteDatabase.openDatabase("system/etc/CautionRsrcTable.db", null, 17);
    }
}
