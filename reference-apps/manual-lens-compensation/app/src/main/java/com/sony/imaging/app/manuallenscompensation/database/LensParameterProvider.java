package com.sony.imaging.app.manuallenscompensation.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.database.LensParameterProviderDefinition;
import java.io.File;
import java.util.HashMap;

/* loaded from: classes.dex */
public class LensParameterProvider extends ContentProvider {
    private static final int CODE_LENSES = 1;
    private static final int CODE_LENS_ID = 2;
    private static final String DATABASE_FILE = "lensparameter.db";
    private static final String DATABASE_NAME = "/data/data/lensparameter.db";
    private static final String DATABASE_PATH = "/data/data/";
    private static final int DATABASE_VERSION = 1;
    private static final String LENS_TABLE_NAME = "parames";
    private static final String TAG = "LensParameterProvider";
    private static HashMap<String, String> sProjectionMap;
    private static UriMatcher sUriMatcher = new UriMatcher(-1);
    private DatabaseHelper mOpenHelper;

    /* loaded from: classes.dex */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private long openCount;

        DatabaseHelper(Context context) {
            super(context, LensParameterProvider.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
            this.openCount = 0L;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            setDBPermission();
            db.execSQL("CREATE TABLE parames (_id INTEGER PRIMARY KEY,Number INTEGER,lens TEXT,FValue TEXT,focalLength TEXT,LIGHT_VIGNETTING_CORRECTION INTEGER,RED_COLOR_VIGNETTING_CORRECTION INTEGER,BLUE_COLOR_VIGNETTING_CORRECTION INTEGER,RED_CHROMATIC_ABERRATION_CORRECTION INTEGER,BLUE_CHROMATIC_ABERRATION_CORRECTION INTEGER,DISTORTION_CORRECTION INTEGER,OPTION_FLG INTEGER);");
            File journal_file_new = new File(LensParameterProvider.DATABASE_PATH, "lensparameter.db-journal");
            if (journal_file_new.exists()) {
                journal_file_new.setWritable(true, false);
                journal_file_new.setReadable(true, false);
            }
        }

        private void setDBPermission() {
            AppLog.enter(LensParameterProvider.TAG, AppLog.getMethodName() + "before file creation");
            File db_file = new File(LensParameterProvider.DATABASE_PATH, LensParameterProvider.DATABASE_FILE);
            AppLog.enter(LensParameterProvider.TAG, AppLog.getMethodName());
            try {
                if (db_file.exists()) {
                    AppLog.enter(LensParameterProvider.TAG, AppLog.getMethodName() + "db_file exist true");
                    db_file.setWritable(true, false);
                    db_file.setReadable(true, false);
                    AppLog.enter(LensParameterProvider.TAG, AppLog.getMethodName() + "db_file exist true");
                }
            } finally {
                AppLog.enter(LensParameterProvider.TAG, AppLog.getMethodName() + "finally");
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(LensParameterProvider.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS parames");
            onCreate(db);
        }

        public synchronized void closeDb() {
            setDBPermission();
            AppLog.info(LensParameterProvider.TAG, " Database logs: DatabaseOpenHelper.closeDb() openCount: " + this.openCount);
            this.openCount--;
            if (0 == this.openCount) {
                AppLog.info(LensParameterProvider.TAG, " Database logs: SqliteOpneHelper.close() ");
                super.close();
            }
            File journal_file = new File(LensParameterProvider.DATABASE_PATH, "lensparameter.db-journal");
            if (journal_file.exists()) {
                journal_file.delete();
                Log.i(LensParameterProvider.TAG, "closeDb. Delete journal");
            }
        }

        public synchronized SQLiteDatabase getReadableDb() {
            AppLog.info(LensParameterProvider.TAG, " Database logs: getReadableDb()");
            this.openCount++;
            return super.getReadableDatabase();
        }

        public synchronized SQLiteDatabase getWritableDb() {
            AppLog.info(LensParameterProvider.TAG, " Database logs: getWritableDb()");
            this.openCount++;
            return super.getWritableDatabase();
        }
    }

    /* loaded from: classes.dex */
    private static class LensCursor extends CursorWrapper {
        DatabaseHelper mDbHelper;

        public LensCursor(Cursor cursor, DatabaseHelper helper) {
            super(cursor);
            this.mDbHelper = helper;
        }

        @Override // android.database.CursorWrapper, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            super.close();
            AppLog.info(LensParameterProvider.TAG, " Database logs: CursorWrapper.close() ");
            this.mDbHelper.closeDb();
        }
    }

    static {
        sUriMatcher.addURI(LensParameterProviderDefinition.AUTHORITY, LENS_TABLE_NAME, 1);
        sUriMatcher.addURI(LensParameterProviderDefinition.AUTHORITY, "parames/#", 2);
        sProjectionMap = new HashMap<>();
        sProjectionMap.put("_id", "_id");
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.LENS_NAME, LensParameterProviderDefinition.LensColumns.LENS_NAME);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.NUMBER, LensParameterProviderDefinition.LensColumns.NUMBER);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.F_VALUE, LensParameterProviderDefinition.LensColumns.F_VALUE);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.FOCAL_LENGTH, LensParameterProviderDefinition.LensColumns.FOCAL_LENGTH);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.LIGHT_VIGNETTING, LensParameterProviderDefinition.LensColumns.LIGHT_VIGNETTING);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.RED_COLOR_VIGNETTING, LensParameterProviderDefinition.LensColumns.RED_COLOR_VIGNETTING);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.BLUE_COLOR_VIGNETTING, LensParameterProviderDefinition.LensColumns.BLUE_COLOR_VIGNETTING);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.RED_CHROMATIC_ABERRATION, LensParameterProviderDefinition.LensColumns.RED_CHROMATIC_ABERRATION);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.BLUE_CHROMATIC_ABERRATION, LensParameterProviderDefinition.LensColumns.BLUE_CHROMATIC_ABERRATION);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.DISTORTION, LensParameterProviderDefinition.LensColumns.DISTORTION);
        sProjectionMap.put(LensParameterProviderDefinition.LensColumns.OPTION_FLG, LensParameterProviderDefinition.LensColumns.OPTION_FLG);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        File journal_file = new File(DATABASE_PATH, "lensparameter.db-journal");
        if (journal_file.exists()) {
            journal_file.delete();
            Log.e(TAG, "There exist journal. Delete journal.");
        }
        this.mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String orderBy;
        AppLog.info(TAG, " Database logs: query()++++ ");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(LENS_TABLE_NAME);
        switch (sUriMatcher.match(uri)) {
            case 1:
                queryBuilder.setProjectionMap(sProjectionMap);
                break;
            case 2:
                queryBuilder.setProjectionMap(sProjectionMap);
                queryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = LensParameterProviderDefinition.LensColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        SQLiteDatabase db = this.mOpenHelper.getReadableDb();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        LensCursor myCursor = new LensCursor(cursor, this.mOpenHelper);
        AppLog.info(TAG, " Database logs: query()----");
        return myCursor;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                return LensParameterProviderDefinition.LensColumns.CONTENT_TYPE;
            case 2:
                return LensParameterProviderDefinition.LensColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues source) {
        ContentValues values;
        if (sUriMatcher.match(uri) != 1) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (source != null) {
            values = new ContentValues(source);
        } else {
            values = new ContentValues();
        }
        if (!values.containsKey(LensParameterProviderDefinition.LensColumns.LENS_NAME)) {
            values.put(LensParameterProviderDefinition.LensColumns.LENS_NAME, "");
        }
        SQLiteDatabase db = this.mOpenHelper.getWritableDb();
        try {
            long row = db.insert(LENS_TABLE_NAME, LensParameterProviderDefinition.LensColumns.LENS_NAME, values);
            if (row <= 0) {
                throw new SQLException("Failed to insert row into " + uri);
            }
            Uri lensUri = ContentUris.withAppendedId(LensParameterProviderDefinition.LensColumns.CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(lensUri, null);
            return lensUri;
        } finally {
            this.mOpenHelper.closeDb();
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = this.mOpenHelper.getWritableDb();
        try {
            switch (sUriMatcher.match(uri)) {
                case 1:
                    count = db.delete(LENS_TABLE_NAME, selection, selectionArgs);
                    break;
                case 2:
                    String id = uri.getPathSegments().get(1);
                    count = db.delete(LENS_TABLE_NAME, "_id=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } finally {
            this.mOpenHelper.closeDb();
        }
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = this.mOpenHelper.getWritableDb();
        try {
            switch (sUriMatcher.match(uri)) {
                case 1:
                    count = db.update(LENS_TABLE_NAME, values, selection, selectionArgs);
                    break;
                case 2:
                    String id = uri.getPathSegments().get(1);
                    count = db.update(LENS_TABLE_NAME, values, "_id=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } finally {
            this.mOpenHelper.closeDb();
        }
    }
}
