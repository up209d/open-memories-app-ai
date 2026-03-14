package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public interface IMedia extends AvindexStore.Images.ImageColumns {
    String convColumn(String str);

    String[] convColumns(String[] strArr);

    int convContentType(int i);

    boolean deleteImage(ContentResolver contentResolver, ContentsIdentifier contentsIdentifier);

    Cursor getContentFocusPoint(ContentResolver contentResolver, Uri uri);

    Cursor getContentFocusPoint(ContentResolver contentResolver, Uri uri, String str);

    AvindexContentInfo getContentInfo(String str);

    AvindexContentInfo getContentInfoFull(String str);

    Uri getContentUri(String str);

    int getCursorIndex(Cursor cursor, int i, int i2);

    int getCursorIndex(Cursor cursor, String str, String[] strArr);

    boolean setContentFocusPoint(ContentResolver contentResolver, ContentsIdentifier contentsIdentifier);
}
