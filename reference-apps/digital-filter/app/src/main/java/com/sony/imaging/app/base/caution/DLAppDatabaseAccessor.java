package com.sony.imaging.app.base.caution;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
class DLAppDatabaseAccessor extends DatabaseAccessorBase {
    private static final int COPY_BYTE_VAL = 1024;
    private static final String DB_DLAPP_NAME = "DLApp.db";
    private static final String DB_DLAPP_NAME_ASSET = "DLAppCautionSample.db";
    private static final String TAG = DLAppDatabaseAccessor.class.getSimpleName();
    private Context mContext = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.base.caution.DatabaseAccessorBase
    public void initialize(Context context) {
        super.initialize(context);
        this.mContext = context;
        createDatabase();
        Log.i(TAG, "initialize context:" + context);
    }

    @Override // com.sony.imaging.app.base.caution.DatabaseAccessorBase
    SQLiteDatabase setDataBase() {
        String copyFilePath = this.mContext.getFilesDir() + "/" + DB_DLAPP_NAME;
        Log.i(TAG, "setDataBase copyFilePath:" + copyFilePath);
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(copyFilePath, null, 17);
            return db;
        } catch (SQLiteException e) {
            Log.i(TAG, "DBFile doesn't exist");
            return null;
        }
    }

    private void createDatabase() {
        boolean dbExist = checkDataBaseExist();
        Log.i(TAG, "createDatabase dbExist:" + dbExist);
        try {
            copyDataBaseFromAsset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDataBaseExist() {
        File f = this.mContext.getFileStreamPath(DB_DLAPP_NAME);
        String path = f.getAbsolutePath();
        File dbFile = new File(path);
        return dbFile.exists();
    }

    private void copyDataBaseFromAsset() throws IOException {
        Log.i(TAG, "[START]copyDataBaseFromAsset");
        InputStream mInput = this.mContext.getAssets().open(DB_DLAPP_NAME_ASSET);
        OutputStream mOutput = this.mContext.openFileOutput(DB_DLAPP_NAME, 0);
        byte[] buffer = new byte[COPY_BYTE_VAL];
        while (true) {
            int size = mInput.read(buffer);
            if (size >= 0) {
                mOutput.write(buffer, 0, size);
            } else {
                mOutput.flush();
                mOutput.close();
                mInput.close();
                Log.i(TAG, "[END]copyDataBaseFromAsset");
                return;
            }
        }
    }
}
