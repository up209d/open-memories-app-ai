package com.sony.imaging.app.base.playback.contents;

import android.content.ContentUris;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.OptimizedImageFactory;
import com.sony.scalar.provider.AvindexStore;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class AVIndexProvider extends ProviderHelper {
    private static final int CACHE_CAPACITY = 36;
    private static final String MSG_CANCEL_WAIT_DELETE = "delete wait cancelled";
    private static final String MSG_DECODE_FAILED = "decodeImage failed";
    private static final String MSG_DECODE_TIME = "decode time : ";
    private static final String MSG_DELETE_FAILED = "deleteImage failed";
    private static final String MSG_END_WAIT_DELETE = "delete wait end";
    private static final String MSG_GET_IMG = "getOptimizedImage ";
    private static final String MSG_GET_IMG_FROM_CACHE = "getOptimizedImage cache found: ";
    private static final String MSG_GET_INFO_FAILED = "getContentInfo return empty info because data is null";
    private static final String MSG_GET_INFO_FROM_IMG_CACHE = "getContentInfo get from OptImgCache";
    private static final String MSG_GET_INFO_FROM_INFO_CACHE = "getContentInfo get from InfoCache";
    private static final String MSG_GET_INFO_INVALID_ID = "getContentInfo id is null";
    private static final String MSG_GET_INFO_NOT_CACHED = "getContentInfo not found in cache";
    private static final String MSG_GET_THUMBNAIL_INVALID_ID = "getThumbnail id is null";
    private static final String MSG_GET_THUMB_FROM_INFO = "getThumbnail get from InfoCache";
    private static final String MSG_GET_THUMB_NOT_CACHED = "getThumbnail not found in cache";
    private static final String MSG_OBSERVER_ON_CHANGE = "ContentObserver onChange";
    private static final String MSG_RELEASE_IMG_CACHE = "releases OptimizedImage Cache: ";
    private static final String MSG_START_WAIT_DELETE = "delete wait start";
    private static String TAG = "AVIndexProvider";
    private static final int WAIT_FOR_STUB_DELETION = 1000;
    private OptImgCache mOptImgCache = new OptImgCache();
    private ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.sony.imaging.app.base.playback.contents.AVIndexProvider.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            Log.d(AVIndexProvider.TAG, AVIndexProvider.MSG_OBSERVER_ON_CHANGE);
            synchronized (AVIndexProvider.this) {
                AVIndexProvider.this.notifyAll();
            }
        }
    };
    private InfoCache mInfoCache = new InfoCache(36);

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    @Deprecated
    public Bitmap getJpeg(ContentsIdentifier id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inTargetDensity = 1;
        ContentsManager.ThumbnailOption option = new ContentsManager.ThumbnailOption();
        Bitmap bmp = getThumbnail(id, option);
        return bmp;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public boolean hasThumbnail(ContentsIdentifier id) {
        if (id == null) {
            Log.w(TAG, MSG_GET_THUMBNAIL_INVALID_ID);
            return false;
        }
        String data = id.data;
        AVIndexContentInfo info = this.mInfoCache.get(data);
        if (info != null) {
            Log.d(TAG, MSG_GET_THUMB_FROM_INFO);
        } else {
            Log.d(TAG, MSG_GET_THUMB_NOT_CACHED);
            info = new AVIndexContentInfo(this.mResolver, id);
        }
        return info.hasThumbnail();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public Bitmap getThumbnail(ContentsIdentifier id, ContentsManager.ThumbnailOption option) {
        if (id == null) {
            Log.w(TAG, MSG_GET_THUMBNAIL_INVALID_ID);
            return null;
        }
        String data = id.data;
        AVIndexContentInfo info = this.mInfoCache.get(data);
        if (info != null) {
            Log.d(TAG, MSG_GET_THUMB_FROM_INFO);
        } else {
            Log.d(TAG, MSG_GET_THUMB_NOT_CACHED);
            info = new AVIndexContentInfo(this.mResolver, id);
        }
        return info.getThumbnail(option);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class OptImgCache {
        private String mID = null;
        private int mType = 0;
        private AVIndexContentInfo mInfo = null;
        private OptimizedImage mImg = null;

        protected OptImgCache() {
        }

        protected String getId() {
            return this.mID;
        }

        protected AVIndexContentInfo getInfo() {
            return this.mInfo;
        }

        protected OptimizedImage getImg() {
            return this.mImg;
        }

        protected void setData(String id, int type, AVIndexContentInfo info, OptimizedImage img) {
            close();
            this.mID = id;
            this.mType = type;
            this.mInfo = info;
            this.mImg = img;
        }

        protected boolean hasData(String id, int imageType) {
            return id.equals(this.mID) && imageType == this.mType;
        }

        protected void close() {
            if (this.mImg != null) {
                Log.i(AVIndexProvider.TAG, LogHelper.getScratchBuilder(AVIndexProvider.MSG_RELEASE_IMG_CACHE).append(this.mID).append(", ").append(this.mType).toString());
                this.mImg.release();
            }
            this.mImg = null;
            this.mID = null;
            this.mInfo = null;
        }
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public OptimizedImage getOptimizedImage(ContentsIdentifier id, int imageType, ContentInfo[] info) {
        if (id == null || id.data == null) {
            return null;
        }
        if (this.mOptImgCache.hasData(id.data, imageType)) {
            Log.d(TAG, LogHelper.getScratchBuilder(MSG_GET_IMG_FROM_CACHE).append(id.data).append(", ").append(imageType).toString());
        } else {
            this.mOptImgCache.close();
            OptimizedImageFactory.Options options = new OptimizedImageFactory.Options();
            options.imageType = imageType != 2 ? 3 : 2;
            options.bBasicInfo = true;
            options.bCamInfo = true;
            options.bExtCamInfo = true;
            options.bGpsInfo = true;
            long start = System.currentTimeMillis();
            OptimizedImage img = OptimizedImageFactory.decodeImage(id.data, options);
            long end = System.currentTimeMillis();
            Log.i(TAG, LogHelper.getScratchBuilder(MSG_DECODE_TIME).append(end - start).toString());
            if (img == null) {
                Log.w(TAG, MSG_DECODE_FAILED);
            }
            AVIndexContentInfo avInfo = null;
            if (options.outContentInfo != null) {
                avInfo = new AVIndexContentInfo(this.mResolver, id);
                avInfo.setData(options.outContentInfo);
            }
            if (img == null && avInfo == null) {
                return null;
            }
            this.mOptImgCache.setData(id.data, imageType, avInfo, img);
        }
        if (info != null) {
            info[0] = this.mOptImgCache.getInfo();
        }
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_GET_IMG).append(id.data).append(this.mOptImgCache).toString());
        return this.mOptImgCache.getImg();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public OptimizedImage getOptimizedImageWithoutCache(ContentsIdentifier id, int imageType, ContentInfo[] info) {
        if (id == null || id.data == null) {
            return null;
        }
        boolean needInfo = info != null;
        OptimizedImageFactory.Options options = new OptimizedImageFactory.Options();
        options.imageType = imageType == 2 ? 2 : 3;
        options.bBasicInfo = needInfo;
        options.bCamInfo = needInfo;
        options.bExtCamInfo = needInfo;
        options.bGpsInfo = needInfo;
        long start = System.currentTimeMillis();
        OptimizedImage img = OptimizedImageFactory.decodeImage(id.data, options);
        long end = System.currentTimeMillis();
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_DECODE_TIME).append(end - start).toString());
        if (img == null) {
            Log.w(TAG, MSG_DECODE_FAILED);
        }
        if (needInfo) {
            AVIndexContentInfo avInfo = null;
            if (options.outContentInfo != null) {
                avInfo = new AVIndexContentInfo(this.mResolver, id);
                avInfo.setData(options.outContentInfo);
            }
            info[0] = avInfo;
            return img;
        }
        return img;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public synchronized boolean delete(ContentsIdentifier id) {
        boolean result;
        Uri uri = id.getContentUri();
        this.mResolver.registerContentObserver(ContentUris.withAppendedId(uri, id._id), true, this.mObserver);
        result = AvindexStore.Images.Media.deleteImage(this.mResolver, uri, id._id);
        if (result) {
            if (Environment.DEVICE_TYPE == 3) {
                Log.d(TAG, MSG_START_WAIT_DELETE);
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.w(TAG, MSG_CANCEL_WAIT_DELETE);
                    e.printStackTrace();
                }
                Log.d(TAG, MSG_END_WAIT_DELETE);
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
        Log.w(TAG, MSG_DELETE_FAILED);
        this.mResolver.unregisterContentObserver(this.mObserver);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InfoCache extends LinkedHashMap<String, AVIndexContentInfo> {
        private static final long serialVersionUID = -9097991261355896192L;
        private final int mCapacity;

        public InfoCache(int capacity) {
            super((capacity + capacity) >> 1);
            this.mCapacity = capacity;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<String, AVIndexContentInfo> eldest) {
            return size() > this.mCapacity;
        }

        synchronized AVIndexContentInfo get(String data) {
            AVIndexContentInfo info;
            info = null;
            if (containsKey(data)) {
                info = (AVIndexContentInfo) remove(data);
                put(data, info);
            }
            return info;
        }
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public AVIndexContentInfo getContentInfo(ContentsIdentifier id) {
        if (id == null) {
            Log.w(TAG, MSG_GET_INFO_INVALID_ID);
            return null;
        }
        String data = id.data;
        if (data == null) {
            Log.w(TAG, MSG_GET_INFO_FAILED);
            return new AVIndexContentInfo(this.mResolver, id);
        }
        if (data.equals(this.mOptImgCache.getId())) {
            Log.d(TAG, MSG_GET_INFO_FROM_IMG_CACHE);
            return this.mOptImgCache.getInfo();
        }
        AVIndexContentInfo info = this.mInfoCache.get(data);
        if (info != null) {
            Log.d(TAG, MSG_GET_INFO_FROM_INFO_CACHE);
            return info;
        }
        Log.d(TAG, MSG_GET_INFO_NOT_CACHED);
        AVIndexContentInfo info2 = new AVIndexContentInfo(this.mResolver, id);
        synchronized (this.mInfoCache) {
            this.mInfoCache.put(data, info2);
        }
        return info2;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public ContentInfo getContentInfoFromCache(ContentsIdentifier id) {
        if (id == null) {
            return null;
        }
        String data = id.data;
        AVIndexContentInfo info = this.mInfoCache.get(data);
        return info;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public void invalidateCachedInfo() {
        synchronized (this.mInfoCache) {
            this.mInfoCache.clear();
        }
        this.mOptImgCache.close();
    }
}
