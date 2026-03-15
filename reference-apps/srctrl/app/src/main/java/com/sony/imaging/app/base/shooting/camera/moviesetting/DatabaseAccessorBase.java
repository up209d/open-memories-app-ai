package com.sony.imaging.app.base.shooting.camera.moviesetting;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public abstract class DatabaseAccessorBase {
    private static final String AND = " AND ";
    private static final String APOS = "'";
    private static final String EQUAL = " = ";
    private static final String FIELD_NAME = "name";
    private static final String FROM = " from ";
    private static final String IN = " in ";
    private static final String IS_NULL = " is null ";
    private static final String NOT_EQUAL = " <> ";
    private static final String OR = " OR ";
    private static final String ORDER_BY = " order by ";
    private static final String SELECT_ANY_FROM = "select * from ";
    private static final String SELECT_DISTINCT = "select distinct ";
    private static final String SQL_SEARCH_TABLES = "select * from sqlite_master where type='table'";
    private static final String WHERE = " where ";
    private SQLiteDatabase mDB;
    private List<String> mTables;
    protected static boolean DEBUG = true;
    private static final StringBuilderThreadLocal STRINGBUIDER_FOR_QUERY = new StringBuilderThreadLocal();
    private static final String TAG = DatabaseAccessorBase.class.getSimpleName();

    protected abstract SQLiteDatabase setDataBase();

    public void initialize(Context context) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openDatabase() {
        this.mDB = setDataBase();
        Log.i(TAG, "open moviesetting DataBase mDB:" + this.mDB);
        if (this.mDB != null) {
            this.mTables = getTables();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closeDatabase() {
        if (this.mDB != null) {
            this.mDB.close();
            this.mDB = null;
        }
        this.mTables = null;
        Log.i(TAG, "close moviesetting DataBase");
    }

    public boolean isDbExisted() {
        return this.mDB != null;
    }

    public boolean hasTable(String tableName) {
        if (this.mTables != null) {
            return this.mTables.contains(tableName);
        }
        return false;
    }

    protected List<String> getTables() {
        if (!isDbExisted()) {
            return null;
        }
        PTag.start("search table");
        List<String> list = null;
        try {
            Cursor c = this.mDB.rawQuery(SQL_SEARCH_TABLES, null);
            boolean result = c.moveToFirst();
            if (result) {
                int count = c.getColumnCount();
                int index = c.getColumnIndex(FIELD_NAME);
                if (-1 != index) {
                    List<String> list2 = new ArrayList<>(count);
                    while (true) {
                        try {
                            if (c.isAfterLast()) {
                                list = list2;
                                break;
                            }
                            list2.add(c.getString(index));
                            if (!c.moveToNext()) {
                                list = list2;
                                break;
                            }
                        } catch (SQLiteException e) {
                            return null;
                        }
                    }
                }
            }
            PTag.end("search table");
            return list;
        } catch (SQLiteException e2) {
        }
    }

    protected Cursor searchData(String tablename, String columname, String columval, String orderBy) {
        return searchData(tablename, new String[]{columname}, new String[]{columval}, false, orderBy);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cursor searchData(String tablename, String[] columname, String[] columval, boolean acceptNull, String orderBy) {
        if (!isDbExisted()) {
            return null;
        }
        if (!hasTable(tablename)) {
            if (DEBUG) {
                Log.d(TAG, "searchData table not exist: " + tablename);
            }
            return null;
        }
        if (DEBUG) {
            Log.d(TAG, "searchData columname:" + Arrays.toString(columname) + "columval:" + Arrays.toString(columval));
        }
        String query = makeSearchQuery(tablename, columname, columval, acceptNull, orderBy);
        if (query == null) {
            return null;
        }
        try {
            Cursor c = this.mDB.rawQuery(query, null);
            boolean result = c.moveToFirst();
            if (!result) {
                c.close();
                c = null;
            }
            if (DEBUG) {
                Log.d(TAG, "searchData cursor:" + c + "moveToFirst:" + result);
                return c;
            }
            return c;
        } catch (SQLiteException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cursor searchData(String tablename, String[] columname, String[][] columval, String orderBy) {
        if (!isDbExisted()) {
            return null;
        }
        if (!hasTable(tablename)) {
            if (DEBUG) {
                Log.d(TAG, "searchData table not exist: " + tablename);
            }
            return null;
        }
        if (DEBUG) {
            Log.d(TAG, "searchData columname:" + Arrays.toString(columname) + "columval:" + Arrays.toString(columval));
        }
        String query = makeMultipleSearchQuery(tablename, columname, columval, orderBy);
        if (query == null) {
            return null;
        }
        try {
            Cursor c = this.mDB.rawQuery(query, null);
            boolean result = c.moveToFirst();
            if (!result) {
                c.close();
                c = null;
            }
            if (DEBUG) {
                Log.d(TAG, "searchData cursor:" + c + "moveToFirst:" + result);
                return c;
            }
            return c;
        } catch (SQLiteException e) {
            return null;
        }
    }

    private String makeSearchQuery(String tablename, String[] columname, String[] columval, boolean acceptNull, String orderBy) {
        StringBuilder builder = STRINGBUIDER_FOR_QUERY.get();
        builder.replace(0, builder.length(), SELECT_ANY_FROM).append(tablename);
        if (columname != null && columval != null) {
            boolean not = false;
            String value = columval[0];
            if ('!' == value.charAt(0)) {
                not = true;
                value = value.substring(1);
            }
            if (columname != null) {
                builder.append(WHERE).append(StringBuilderThreadLocal.ROUND_BRACKET_OPEN).append(columname[0]).append(not ? NOT_EQUAL : EQUAL).append(APOS).append(value).append(APOS);
                if (acceptNull) {
                    builder.append(" OR ").append(columname[0]).append(IS_NULL);
                }
                builder.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
                for (int i = 1; i < columname.length; i++) {
                    boolean not2 = false;
                    String value2 = columval[i];
                    if ('!' == value2.charAt(0)) {
                        not2 = true;
                        value2 = value2.substring(1);
                    }
                    builder.append(" AND ").append(StringBuilderThreadLocal.ROUND_BRACKET_OPEN).append(columname[i]).append(not2 ? NOT_EQUAL : EQUAL).append(APOS).append(value2).append(APOS);
                    if (acceptNull) {
                        builder.append(" OR ").append(columname[i]).append(IS_NULL);
                    }
                    builder.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
                }
            }
            if (orderBy != null) {
                builder.append(ORDER_BY).append(orderBy);
            }
        }
        return builder.toString();
    }

    private String makeMultipleSearchQuery(String tablename, String[] columname, String[][] columval, String orderBy) {
        StringBuilder builder = STRINGBUIDER_FOR_QUERY.get();
        builder.replace(0, builder.length(), SELECT_ANY_FROM).append(tablename);
        if (columname != null && columval != null) {
            String[] values = columval[0];
            builder.append(WHERE).append(StringBuilderThreadLocal.ROUND_BRACKET_OPEN);
            for (int i = 0; i < columname.length; i++) {
                builder.append(columname[i]).append(IN).append(StringBuilderThreadLocal.ROUND_BRACKET_OPEN);
                for (int j = 0; j < values.length; j++) {
                    builder.append(APOS).append(values[j]).append(APOS);
                    if (j != values.length - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
                if (i != columname.length - 1) {
                    builder.append(" AND ");
                }
            }
            builder.append(StringBuilderThreadLocal.ROUND_BRACKET_CLOSE);
            if (orderBy != null) {
                builder.append(ORDER_BY).append(orderBy);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "makeSearchQuery query:" + builder.toString());
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cursor searchDistinctData(String tablename, String columname, String orderBy) {
        if (!isDbExisted()) {
            return null;
        }
        if (!hasTable(tablename)) {
            if (DEBUG) {
                Log.d(TAG, "searchData table not exist: " + tablename);
            }
            return null;
        }
        String query = makeDistinctSearchQuery(tablename, columname, orderBy);
        if (query == null) {
            return null;
        }
        try {
            Cursor c = this.mDB.rawQuery(query, null);
            boolean result = c.moveToFirst();
            if (!result) {
                c.close();
                c = null;
            }
            if (DEBUG) {
                Log.d(TAG, "searchData cursor:" + c + "moveToFirst:" + result);
                return c;
            }
            return c;
        } catch (SQLiteException e) {
            return null;
        }
    }

    private String makeDistinctSearchQuery(String tablename, String columname, String orderBy) {
        StringBuilder builder = STRINGBUIDER_FOR_QUERY.get();
        builder.replace(0, builder.length(), SELECT_DISTINCT).append(columname).append(FROM).append(tablename);
        if (orderBy != null) {
            builder.append(ORDER_BY).append(orderBy);
        }
        if (DEBUG) {
            Log.d(TAG, "makeDistinctSearchQuery query:" + builder.toString());
        }
        return builder.toString();
    }
}
