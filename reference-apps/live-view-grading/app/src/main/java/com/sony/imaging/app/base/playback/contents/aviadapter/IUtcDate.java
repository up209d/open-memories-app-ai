package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.database.Cursor;

/* loaded from: classes.dex */
public interface IUtcDate extends ICommonContainer {
    public static final String CONTENT_CREATED_UTC_DATE = "content_created_utc_date";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.sony.scalar.avindex.utcdate";
    public static final String COUNT_OF_ONE_BEFORE = "count_of_one_before";
    public static final String _COUNT = "_count";
    public static final String _ID = "_id";

    int getCursorIndex(Cursor cursor, String str);
}
