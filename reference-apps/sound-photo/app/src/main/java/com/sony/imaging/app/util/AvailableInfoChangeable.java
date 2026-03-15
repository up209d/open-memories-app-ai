package com.sony.imaging.app.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class AvailableInfoChangeable extends AvailableInfoImpl {
    private static final int COPY_BYTE_VAL = 1024;
    private static final StringBuilderThreadLocal STRBULD_TL = new StringBuilderThreadLocal();
    private static final String TAG = "AvailableInfoChangeable";
    private Context mContext;

    public AvailableInfoChangeable(String db) {
        super(db);
        this.mContext = null;
    }

    public AvailableInfoChangeable(Context context, String db) {
        this(db);
        this.mContext = context;
    }

    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    protected void initialize() {
        try {
            copyDataBaseFromAsset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.initialize();
        Cursor c = null;
        int count = -1;
        try {
            StringBuilder builder = STRBULD_TL.get();
            builder.replace(0, builder.length(), "select count(*) from factor_records").append(MovieFormatController.Settings.SEMI_COLON);
            if (AvailableInfo.DEBUG) {
                Log.i(TAG, "start query: " + ((Object) builder));
            }
            c = this.mDB.rawQuery(builder.toString(), null);
            boolean b = c.moveToFirst();
            if (b) {
                count = c.getInt(0);
            }
            if (AvailableInfo.DEBUG) {
                Log.i(TAG, "finish : " + count);
            }
            this.currentFactor = new byte[((count - 1) / 8) + 1];
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    protected void setFactor(String factorId, boolean value) {
        int bit = isFactorHelper(factorId);
        byte data = (byte) (1 << (bit % 8));
        if (value) {
            byte[] bArr = this.currentFactor;
            int i = bit / 8;
            bArr[i] = (byte) (bArr[i] | data);
        } else {
            byte[] bArr2 = this.currentFactor;
            int i2 = bit / 8;
            bArr2[i2] = (byte) (bArr2[i2] & (data ^ (-1)));
        }
    }

    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    protected void update() {
    }

    private void copyDataBaseFromAsset() throws IOException {
        Log.i(TAG, "[START]copyDataBaseFromAsset");
        if (this.mContext != null) {
            InputStream mInput = this.mContext.getAssets().open(this.mDbPath);
            OutputStream mOutput = this.mContext.openFileOutput(this.mDbPath, 0);
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

    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    protected String getDataBasePath() {
        return this.mContext.getFilesDir() + SPConstants.FILE_SEPARATER + this.mDbPath;
    }
}
