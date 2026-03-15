package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import com.sony.imaging.app.startrails.util.STConstants;

/* loaded from: classes.dex */
public class DummyGroupCursor implements Cursor {
    @Override // android.database.Cursor
    public int getCount() {
        return 1;
    }

    @Override // android.database.Cursor
    public int getPosition() {
        return 0;
    }

    @Override // android.database.Cursor
    public boolean move(int offset) {
        return offset == 0;
    }

    @Override // android.database.Cursor
    public boolean moveToPosition(int position) {
        return position == 0;
    }

    @Override // android.database.Cursor
    public boolean moveToFirst() {
        return true;
    }

    @Override // android.database.Cursor
    public boolean moveToLast() {
        return true;
    }

    @Override // android.database.Cursor
    public boolean moveToNext() {
        return true;
    }

    @Override // android.database.Cursor
    public boolean moveToPrevious() {
        return true;
    }

    @Override // android.database.Cursor
    public boolean isFirst() {
        return true;
    }

    @Override // android.database.Cursor
    public boolean isLast() {
        return true;
    }

    @Override // android.database.Cursor
    public boolean isBeforeFirst() {
        return false;
    }

    @Override // android.database.Cursor
    public boolean isAfterLast() {
        return false;
    }

    @Override // android.database.Cursor
    public int getColumnIndex(String columnName) {
        return -1;
    }

    @Override // android.database.Cursor
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return -1;
    }

    @Override // android.database.Cursor
    public String getColumnName(int columnIndex) {
        return null;
    }

    @Override // android.database.Cursor
    public String[] getColumnNames() {
        return null;
    }

    @Override // android.database.Cursor
    public int getColumnCount() {
        return 0;
    }

    @Override // android.database.Cursor
    public byte[] getBlob(int columnIndex) {
        return null;
    }

    @Override // android.database.Cursor
    public String getString(int columnIndex) {
        return null;
    }

    @Override // android.database.Cursor
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
    }

    @Override // android.database.Cursor
    public short getShort(int columnIndex) {
        return (short) 0;
    }

    @Override // android.database.Cursor
    public int getInt(int columnIndex) {
        return 0;
    }

    @Override // android.database.Cursor
    public long getLong(int columnIndex) {
        return 0L;
    }

    @Override // android.database.Cursor
    public float getFloat(int columnIndex) {
        return STConstants.INVALID_APERTURE_VALUE;
    }

    @Override // android.database.Cursor
    public double getDouble(int columnIndex) {
        return 0.0d;
    }

    @Override // android.database.Cursor
    public boolean isNull(int columnIndex) {
        return false;
    }

    @Override // android.database.Cursor
    public void deactivate() {
    }

    @Override // android.database.Cursor
    public boolean requery() {
        return true;
    }

    @Override // android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // android.database.Cursor
    public boolean isClosed() {
        return false;
    }

    @Override // android.database.Cursor
    public void registerContentObserver(ContentObserver observer) {
    }

    @Override // android.database.Cursor
    public void unregisterContentObserver(ContentObserver observer) {
    }

    @Override // android.database.Cursor
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override // android.database.Cursor
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override // android.database.Cursor
    public void setNotificationUri(ContentResolver cr, Uri uri) {
    }

    @Override // android.database.Cursor
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override // android.database.Cursor
    public Bundle getExtras() {
        return null;
    }

    @Override // android.database.Cursor
    public Bundle respond(Bundle extras) {
        return null;
    }

    @Override // android.database.Cursor
    public int getType(int columnIndex) {
        return 0;
    }
}
