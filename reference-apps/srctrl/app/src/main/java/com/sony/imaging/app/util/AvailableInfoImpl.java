package com.sony.imaging.app.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AvailableInfoImpl {
    protected static final String COLON = ":";
    protected static final String COMMA = ",";
    protected static final String END_BRACKET = ")";
    protected static final String END_SIGN = "'";
    protected static final String END_SIGN2 = ";";
    protected static final String GET_FACTOR = "select factor from inh_records where feature in('";
    protected static final String GET_FACTOR_BIT = "select bit from factor_records where factor=='";
    protected static final String SEPARATER = ",'";
    private static final String TAG = "AvailavleInfoImpl";
    private final StringBuilderThreadLocal SELCTORS;
    protected byte[] currentFactor;
    protected SQLiteDatabase mDB;
    protected String mDbPath;

    protected abstract String getDataBasePath();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void setFactor(String str, boolean z);

    protected abstract void update();

    public AvailableInfoImpl() {
        this.mDB = null;
        this.SELCTORS = new StringBuilderThreadLocal();
        this.mDbPath = null;
    }

    public AvailableInfoImpl(String db) {
        this.mDB = null;
        this.SELCTORS = new StringBuilderThreadLocal();
        this.mDbPath = null;
        this.mDbPath = db;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initialize() {
        String path = getDataBasePath();
        if (this.mDB == null) {
            if (AvailableInfo.DEBUG) {
                Log.i(TAG, "initialize start");
            }
            this.mDB = SQLiteDatabase.openDatabase(path, null, 17);
            if (AvailableInfo.DEBUG) {
                Log.i(TAG, "initialize finish");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void terminate() {
        if (this.mDB != null) {
            this.mDB.close();
            this.mDB = null;
        }
        this.currentFactor = null;
    }

    public boolean isFactor(String factorID) {
        int bit = isFactorHelper(factorID);
        if (AvailableInfo.DEBUG) {
            Log.i(TAG, "compare with: " + convert2Hex(this.currentFactor));
        }
        if (-1 != bit) {
            return compare(this.currentFactor, bit);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001b, code lost:            if (r1.moveToFirst() != false) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x001d, code lost:            r3 = r1.getBlob(0);     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0024, code lost:            if (com.sony.imaging.app.util.AvailableInfo.DEBUG == false) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0026, code lost:            android.util.Log.i(com.sony.imaging.app.util.AvailableInfoImpl.TAG, convert2Hex(r3));        android.util.Log.i(com.sony.imaging.app.util.AvailableInfoImpl.TAG, "compare with: " + convert2Hex(r8.currentFactor));     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x004d, code lost:            r4 = compare(r3, r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0051, code lost:            if (r4 != false) goto L32;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0057, code lost:            if (r1.moveToNext() != false) goto L34;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean isInhibitionWithFactor(java.lang.String r9, java.util.List<java.lang.String> r10) {
        /*
            r8 = this;
            r4 = 0
            int r0 = r8.isFactorHelper(r9)
            r5 = -1
            if (r5 == r0) goto Le
            byte[] r5 = r8.currentFactor
            boolean r4 = compare(r5, r0)
        Le:
            if (r4 == 0) goto L5e
            r1 = 0
            android.database.Cursor r1 = r8.getFactorCursor(r10)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            if (r1 == 0) goto L59
            boolean r5 = r1.moveToFirst()     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            if (r5 == 0) goto L59
        L1d:
            r5 = 0
            byte[] r3 = r1.getBlob(r5)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            boolean r5 = com.sony.imaging.app.util.AvailableInfo.DEBUG     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            if (r5 == 0) goto L4d
            java.lang.String r5 = "AvailavleInfoImpl"
            java.lang.String r6 = convert2Hex(r3)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            android.util.Log.i(r5, r6)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            java.lang.String r5 = "AvailavleInfoImpl"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            r6.<init>()     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            java.lang.String r7 = "compare with: "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            byte[] r7 = r8.currentFactor     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            java.lang.String r7 = convert2Hex(r7)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            android.util.Log.i(r5, r6)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
        L4d:
            boolean r4 = compare(r3, r0)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            if (r4 != 0) goto L59
            boolean r5 = r1.moveToNext()     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L69
            if (r5 != 0) goto L1d
        L59:
            if (r1 == 0) goto L5e
            r1.close()
        L5e:
            return r4
        L5f:
            r2 = move-exception
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L69
            if (r1 == 0) goto L5e
            r1.close()
            goto L5e
        L69:
            r5 = move-exception
            if (r1 == 0) goto L6f
            r1.close()
        L6f:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.util.AvailableInfoImpl.isInhibitionWithFactor(java.lang.String, java.util.List):boolean");
    }

    private Cursor getFactorCursor(List<String> inhFeatureIDs) {
        Cursor cursor = null;
        if (!inhFeatureIDs.isEmpty()) {
            StringBuilder selector = this.SELCTORS.get();
            selector.replace(0, selector.length(), GET_FACTOR);
            for (String feature : inhFeatureIDs) {
                selector.append(feature).append(END_SIGN).append(SEPARATER);
            }
            int lastIndex = selector.length() - 1;
            selector.delete(lastIndex - SEPARATER.length(), lastIndex);
            selector.append(")");
            if (AvailableInfo.DEBUG) {
                Log.i(TAG, "start query: " + ((Object) selector));
            }
            cursor = this.mDB.rawQuery(selector.toString(), null);
            if (AvailableInfo.DEBUG) {
                Log.i(TAG, "finish");
            }
        }
        return cursor;
    }

    public boolean isInhibition(String inhFeatureID) {
        List<String> param = new ArrayList<>();
        param.add(inhFeatureID);
        return isInhibition(param);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x003e, code lost:            r3 = compare(r2, r7.currentFactor);     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0044, code lost:            if (r3 != false) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x004a, code lost:            if (r0.moveToNext() != false) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x000c, code lost:            if (r0.moveToFirst() != false) goto L7;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x000e, code lost:            r2 = r0.getBlob(0);     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0015, code lost:            if (com.sony.imaging.app.util.AvailableInfo.DEBUG == false) goto L10;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0017, code lost:            android.util.Log.i(com.sony.imaging.app.util.AvailableInfoImpl.TAG, convert2Hex(r2));        android.util.Log.i(com.sony.imaging.app.util.AvailableInfoImpl.TAG, "compare with: " + convert2Hex(r7.currentFactor));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean isInhibition(java.util.List<java.lang.String> r8) {
        /*
            r7 = this;
            r3 = 0
            r0 = 0
            android.database.Cursor r0 = r7.getFactorCursor(r8)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            if (r0 == 0) goto L4c
            boolean r4 = r0.moveToFirst()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            if (r4 == 0) goto L4c
        Le:
            r4 = 0
            byte[] r2 = r0.getBlob(r4)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            boolean r4 = com.sony.imaging.app.util.AvailableInfo.DEBUG     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            if (r4 == 0) goto L3e
            java.lang.String r4 = "AvailavleInfoImpl"
            java.lang.String r5 = convert2Hex(r2)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            android.util.Log.i(r4, r5)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            java.lang.String r4 = "AvailavleInfoImpl"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            r5.<init>()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            java.lang.String r6 = "compare with: "
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            byte[] r6 = r7.currentFactor     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            java.lang.String r6 = convert2Hex(r6)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            android.util.Log.i(r4, r5)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
        L3e:
            byte[] r4 = r7.currentFactor     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            boolean r3 = compare(r2, r4)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            if (r3 != 0) goto L4c
            boolean r4 = r0.moveToNext()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L5c
            if (r4 != 0) goto Le
        L4c:
            if (r0 == 0) goto L51
            r0.close()
        L51:
            return r3
        L52:
            r1 = move-exception
            r1.printStackTrace()     // Catch: java.lang.Throwable -> L5c
            if (r0 == 0) goto L51
            r0.close()
            goto L51
        L5c:
            r4 = move-exception
            if (r0 == 0) goto L62
            r0.close()
        L62:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.util.AvailableInfoImpl.isInhibition(java.util.List):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int isFactorHelper(String factorID) {
        int ret = -1;
        StringBuilder selector = this.SELCTORS.get();
        selector.replace(0, selector.length(), GET_FACTOR_BIT).append(factorID).append(END_SIGN);
        if (AvailableInfo.DEBUG) {
            Log.i(TAG, "start query: " + ((Object) selector));
        }
        Cursor c = null;
        try {
            try {
                c = this.mDB.rawQuery(selector.toString(), null);
                if (AvailableInfo.DEBUG) {
                    Log.i(TAG, "finish");
                }
                boolean b = c.moveToFirst();
                if (b) {
                    ret = c.getInt(0);
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
                if (c != null) {
                    c.close();
                }
            }
            return ret;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean compare(byte[] factor, byte[] current) {
        int count = current.length;
        for (int i = 0; i < count; i++) {
            if ((current[i] & factor[i]) != 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean compare(byte[] target, int bit) {
        byte targetByte = target[bit / 8];
        return ((targetByte >> (bit % 8)) & 1) == 1;
    }

    protected static String convert2Hex(byte[] factor) {
        String data = "";
        if (factor != null) {
            for (byte b : factor) {
                data = data + String.format("%02x", Integer.valueOf(b & LiveviewCommon.COMMON_HEADER_START_BYTE));
            }
        }
        return data;
    }
}
