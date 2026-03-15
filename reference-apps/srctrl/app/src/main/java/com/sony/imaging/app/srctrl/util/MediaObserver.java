package com.sony.imaging.app.srctrl.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MediaObserver extends ContentObserver {
    private static final String sExtJPG = ".JPG";
    private static final String sExtRAW = ".ARW";
    private static final String sFilePathFormat = "/%s/DCIM/%s/%s%04d%s";
    private WeakReference<MediaObserverListener> mAggregatorRef;
    private ContentResolver mContentResolver;
    private SparseArray<String> mFolderNameCache;
    private MediaInfo mMediaInfo;
    private static final String tag = MediaObserver.class.getName();
    private static boolean _canCount = false;

    /* loaded from: classes.dex */
    public interface MediaObserverListener {
        void notifyMediaContentsInfoChanged();
    }

    /* loaded from: classes.dex */
    public enum MediaType {
        UNKNOWN,
        EXTERNAL,
        INTERNAL,
        VIRTUAL
    }

    public static void setCanCount(boolean canCount) {
        _canCount = canCount;
    }

    /* loaded from: classes.dex */
    public static class MediaInfo {
        public static final int COUNT_NOT_INITIALIZED = -1;
        public int mCurrentContentsCount;
        public int mInitialContentsCount;
        public String mMediaId;
        public MediaType mMediaType = MediaType.UNKNOWN;
        public int mInitialRecorderCount = -1;

        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public MediaInfo m2clone() {
            MediaInfo info = new MediaInfo();
            synchronized (this) {
                info.mMediaId = this.mMediaId;
                info.mMediaType = this.mMediaType;
                info.mInitialContentsCount = this.mInitialContentsCount;
                info.mCurrentContentsCount = this.mCurrentContentsCount;
                info.mInitialRecorderCount = this.mInitialRecorderCount;
            }
            return info;
        }
    }

    public MediaObserver(MediaObserverListener aggregator, ContentResolver contentResolver, Handler handler, String mediaId, MediaType mediaType) {
        super(handler);
        this.mMediaInfo = new MediaInfo();
        this.mFolderNameCache = new SparseArray<>();
        this.mAggregatorRef = new WeakReference<>(aggregator);
        this.mContentResolver = contentResolver;
        synchronized (this.mMediaInfo) {
            this.mMediaInfo.mMediaId = mediaId;
            this.mMediaInfo.mMediaType = mediaType;
            int count = getContentsCount();
            this.mMediaInfo.mInitialContentsCount = count;
            this.mMediaInfo.mCurrentContentsCount = count;
        }
    }

    public boolean isMediaIdEqualsTo(String mediaId) {
        boolean equals;
        synchronized (this.mMediaInfo) {
            equals = this.mMediaInfo.mMediaId.equals(mediaId);
        }
        return equals;
    }

    public int getRecordedContentsCount() {
        int i;
        synchronized (this.mMediaInfo) {
            i = this.mMediaInfo.mCurrentContentsCount - this.mMediaInfo.mInitialContentsCount;
        }
        return i;
    }

    public int getCurrentContentsCount() {
        int i;
        synchronized (this.mMediaInfo) {
            i = this.mMediaInfo.mCurrentContentsCount;
        }
        return i;
    }

    public int getInitialContentsCount() {
        int i;
        synchronized (this.mMediaInfo) {
            i = this.mMediaInfo.mInitialContentsCount;
        }
        return i;
    }

    private int getContentsCount() {
        int ret = 0;
        synchronized (this.mMediaInfo) {
            Uri uri = AvindexStore.Images.Media.getContentUri(this.mMediaInfo.mMediaId);
            String[] projection = {"_id", "rec_order"};
            String[] selectionArgs = {Integer.toString(1)};
            String sortOrder = null;
            if (-1 == this.mMediaInfo.mInitialRecorderCount) {
                sortOrder = "rec_order DESC";
            }
            Cursor cursor = this.mContentResolver.query(uri, projection, "content_type=?", selectionArgs, sortOrder);
            if (cursor != null) {
                ret = cursor.getCount();
                if (cursor.moveToFirst()) {
                    int COLUMN_INDEX_REC_ORDER = cursor.getColumnIndex("rec_order");
                    if (-1 == this.mMediaInfo.mInitialRecorderCount && !cursor.isAfterLast()) {
                        this.mMediaInfo.mInitialRecorderCount = cursor.getInt(COLUMN_INDEX_REC_ORDER);
                        Log.v(tag, "  InitialRecOrderCount: " + this.mMediaInfo.mInitialRecorderCount);
                    }
                }
                cursor.close();
            }
        }
        return ret;
    }

    private static String getMountPoint(String mediaId, MediaType mediaType) {
        if (MediaType.EXTERNAL != mediaType) {
            Log.v(tag, "Mount point for Media type(" + mediaType.name() + ") is not defined yet.  Using the " + MediaType.EXTERNAL.name() + " one.");
            return "/mnt/sdcard";
        }
        return "/mnt/sdcard";
    }

    private static String getFolderName(int folderNumber, String mediaId, MediaType mediaType) {
        String mountPoint = getMountPoint(mediaId, mediaType);
        final File mountPointDir = new File(mountPoint + "/DCIM");
        final String folderNamePrefix = String.format("%03d", Integer.valueOf(folderNumber));
        FilenameFilter filter = new FilenameFilter() { // from class: com.sony.imaging.app.srctrl.util.MediaObserver.1
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String name) {
                return dir.equals(mountPointDir) && name.startsWith(folderNamePrefix);
            }
        };
        String[] targetDirs = mountPointDir.list(filter);
        if (targetDirs.length != 0) {
            return targetDirs[0];
        }
        Log.e(tag, "Unexpected error for the system.  Returnning null...");
        return null;
    }

    public List<String> getRecentImageFileList(int lastCount) {
        List<String> result = new ArrayList<>();
        synchronized (this.mMediaInfo) {
            Uri uri = AvindexStore.Images.Media.getContentUri(this.mMediaInfo.mMediaId);
            String[] projection = {"_id", "_data", "rec_order", IFolder.DCF_FOLDER_NUMBER, "dcf_file_number", "exist_jpeg", "exist_raw"};
            String selection = -1 != this.mMediaInfo.mInitialRecorderCount ? "content_type=? AND rec_order >?" : "content_type=?";
            String[] selectionArgs = {Integer.toString(1)};
            if (-1 != this.mMediaInfo.mInitialRecorderCount) {
                selectionArgs = new String[]{Integer.toString(1), Integer.toString(this.mMediaInfo.mInitialRecorderCount)};
            }
            Cursor cursor = this.mContentResolver.query(uri, projection, selection, selectionArgs, "rec_order DESC");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int COLUMN_INDEX_DATA = cursor.getColumnIndex("_data");
                    int COLUMN_INDEX_FOLDER_NUMBER = cursor.getColumnIndex(IFolder.DCF_FOLDER_NUMBER);
                    int COLUMN_INDEX_FILE_NUMBER = cursor.getColumnIndex("dcf_file_number");
                    int COLUMN_INDEX_EXIST_JPG = cursor.getColumnIndex("exist_jpeg");
                    int COLUMN_INDEX_EXIST_RAW = cursor.getColumnIndex("exist_raw");
                    int index = 0;
                    while (!cursor.isAfterLast() && index < lastCount) {
                        String data = cursor.getString(COLUMN_INDEX_DATA);
                        int folderNumber = cursor.getInt(COLUMN_INDEX_FOLDER_NUMBER);
                        int fileNumber = cursor.getInt(COLUMN_INDEX_FILE_NUMBER);
                        short existJPG = cursor.getShort(COLUMN_INDEX_EXIST_JPG);
                        short existRAW = cursor.getShort(COLUMN_INDEX_EXIST_RAW);
                        String folderName = this.mFolderNameCache.get(folderNumber);
                        if (folderName == null && (folderName = getFolderName(folderNumber, this.mMediaInfo.mMediaId, this.mMediaInfo.mMediaType)) != null) {
                            this.mFolderNameCache.put(folderNumber, folderName);
                        }
                        if (folderName == null) {
                            Log.e(tag, "Skipping unexpected folder name: " + folderName);
                        } else {
                            AvindexContentInfo avindexInfo = AvindexStore.Images.Media.getImageInfo(data);
                            int COLOR_TYPE = avindexInfo.getAttributeInt("ColorSpace", AudioVolumeController.INVALID_VALUE);
                            String filePrefix = "DSC0";
                            if (Integer.MIN_VALUE != COLOR_TYPE && 2 == COLOR_TYPE) {
                                filePrefix = "_DSC";
                            }
                            String ext = "";
                            if (existJPG != 0) {
                                ext = ".JPG";
                            } else if (existRAW != 0) {
                                ext = ".ARW";
                            }
                            String filePath = String.format(sFilePathFormat, this.mMediaInfo.mMediaId, folderName, filePrefix, Integer.valueOf(fileNumber), ext);
                            result.add(filePath);
                        }
                        index++;
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            } else {
                result = null;
            }
        }
        return result;
    }

    public MediaInfo getMediaInfo() {
        MediaInfo m2clone;
        synchronized (this.mMediaInfo) {
            m2clone = this.mMediaInfo.m2clone();
        }
        return m2clone;
    }

    @Override // android.database.ContentObserver
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.v(tag, "MediaObserver#onChange(" + selfChange + LogHelper.MSG_CLOSE_BRACKET);
        if (_canCount) {
            synchronized (this.mMediaInfo) {
                this.mMediaInfo.mCurrentContentsCount = getContentsCount();
            }
            MediaObserverListener listener = this.mAggregatorRef.get();
            if (listener != null) {
                listener.notifyMediaContentsInfoChanged();
            }
        }
    }
}
