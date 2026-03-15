package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class ContentsManager {
    private static final int CHKVIEW_STACK_CALLER = 3;
    private static final float H_SCALE_WIDE_PANEL = 0.75f;
    public static final int IMAGE_TYPE_MAIN = 1;
    public static final int IMAGE_TYPE_SCREEN_NAIL = 2;
    private static final String MSG_TAG_NO_VIEWMODE = "NO_VIEWMODE";
    public static final String NOTIFICATION_TAG_CONTENT_CHANGED = "content_changed";
    public static final String NOTIFICATION_TAG_CURRENT_FILE = "current file";
    public static final int PB_MODE_OPTIMIZED_IMAGE = 1;
    public static final int PB_MODE_THUMBNAIL_CACHE = 2;
    static final int PB_MODE_UNKNOWN = 0;
    public static final PointF SCALE_SQUARE_PIXEL;
    public static final PointF SCALE_WIDE_PANEL_PIXEL = new PointF();
    private static ContentsManager mInstance;
    private static ThumbnailOption mScratchOption;
    private QueryCallback mUserQueryCallback;
    private int mPbMode = 0;
    private QueryCallback mQueryCallback = new QueryCallback() { // from class: com.sony.imaging.app.base.playback.contents.ContentsManager.1
        @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.QueryCallback
        public void onCallback(boolean result) {
            if (result) {
                ContentsManager.this.mObserver.setData(ContentsManager.this.getContentInfo(ContentsManager.this.getContentsId()));
            }
            if (ContentsManager.this.mUserQueryCallback != null) {
                ContentsManager.this.mUserQueryCallback.onCallback(result);
            }
        }
    };
    private ViewMode mViewMode = null;
    private ProviderHelper mProviderHelper = new AVIndexProvider();
    private ContentsObserver mObserver = new ContentsObserver(true);

    /* loaded from: classes.dex */
    public interface MoveHelper {
        boolean apply(ViewMode viewMode);
    }

    /* loaded from: classes.dex */
    public interface QueryCallback {
        void onCallback(boolean z);
    }

    /* loaded from: classes.dex */
    public static class ThumbnailOption {
        public boolean rotateIt = false;
        public boolean clipByAspect = false;
        public PointF postScale = null;
    }

    static {
        SCALE_WIDE_PANEL_PIXEL.x = H_SCALE_WIDE_PANEL;
        SCALE_WIDE_PANEL_PIXEL.y = 1.0f;
        SCALE_SQUARE_PIXEL = new PointF();
        SCALE_SQUARE_PIXEL.x = 1.0f;
        SCALE_SQUARE_PIXEL.y = 1.0f;
        mScratchOption = new ThumbnailOption();
        mInstance = new ContentsManager();
    }

    private ContentsManager() {
        setPbMode(0);
    }

    public static ContentsManager getInstance() {
        return mInstance;
    }

    public void initialize(Context context) {
        setPbMode(0);
        this.mUserQueryCallback = null;
        this.mProviderHelper.initialize(context.getContentResolver());
    }

    public boolean isInitialized() {
        return this.mProviderHelper.isInitialized();
    }

    public void terminate() {
        if (this.mViewMode != null) {
            this.mViewMode.terminate();
            this.mViewMode = null;
        }
        this.mProviderHelper.terminate();
        setPbMode(0);
        this.mUserQueryCallback = null;
    }

    public void setViewMode(Class<?> viewMode) {
        this.mProviderHelper.invalidateCachedInfo();
        if (this.mViewMode != null) {
            this.mViewMode.terminate();
        }
        try {
            this.mViewMode = (ViewMode) viewMode.newInstance();
            this.mViewMode.initialize(this.mProviderHelper);
            this.mViewMode.registerObserver(new ContentObserver(new Handler()) { // from class: com.sony.imaging.app.base.playback.contents.ContentsManager.2
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    ContentsManager.this.mObserver.requestNotify(ContentsManager.NOTIFICATION_TAG_CONTENT_CHANGED);
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e2) {
            e2.printStackTrace();
        }
    }

    public void setContentsColumns(String[] columns) {
        if (checkViewMode()) {
            this.mViewMode.setContentsProjection(columns);
        }
    }

    public Class<?> getViewMode() {
        if (this.mViewMode != null) {
            return this.mViewMode.getClass();
        }
        return null;
    }

    private boolean checkViewMode() {
        if (this.mViewMode != null) {
            return true;
        }
        Log.w(MSG_TAG_NO_VIEWMODE, Thread.currentThread().getStackTrace()[3].getMethodName());
        return false;
    }

    public ContentResolver getContentResolver() {
        return this.mProviderHelper.getResolver();
    }

    public void setPbMode(int mode) {
        if (mode != this.mPbMode) {
            this.mPbMode = mode;
            this.mProviderHelper.invalidateCachedInfo();
            if (mode == 2) {
                this.mObserver.setData(getContentInfo(getContentsId()));
            }
        }
    }

    public void startDecodingBuffer() {
        if (this.mViewMode != null) {
            ContentsIdentifier id = this.mViewMode.getResumeId();
            this.mProviderHelper.getOptimizedImage(id, 2, null);
        }
    }

    public boolean isInitialQueryDone() {
        if (checkViewMode()) {
            return this.mViewMode.isInitialQueryDone();
        }
        return false;
    }

    public boolean startInitialQuery(QueryCallback callback, boolean forceQuery) {
        if (!checkViewMode()) {
            return false;
        }
        this.mUserQueryCallback = callback;
        return this.mViewMode.startInitialQuery(this.mQueryCallback, forceQuery);
    }

    public void cancelInitialQuery() {
        if (checkViewMode()) {
            this.mUserQueryCallback = null;
            this.mViewMode.cancelInitialQuery();
        }
    }

    public boolean requeryData() {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.requeryData();
        if (result && getGroupCount() == 0 && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public int getGroupCount() {
        if (checkViewMode()) {
            return this.mViewMode.getGroupCount();
        }
        return 0;
    }

    public int getGroupPosition() {
        if (checkViewMode()) {
            return this.mViewMode.getGroupPosition();
        }
        return -1;
    }

    public int getContentsCount() {
        if (checkViewMode()) {
            return this.mViewMode.getContentsCount();
        }
        return 0;
    }

    public int getContentsPosition() {
        if (checkViewMode()) {
            return this.mViewMode.getContentsPosition();
        }
        return -1;
    }

    public int getContentsTotalCount() {
        if (checkViewMode()) {
            return this.mViewMode.getContentsTotalCount();
        }
        return 0;
    }

    public int getContentsTotalPosition() {
        if (checkViewMode()) {
            return this.mViewMode.getContentsTotalPosition();
        }
        return -1;
    }

    public int getContentsCountOfGroup(int groupId) {
        if (checkViewMode()) {
            return this.mViewMode.getContentsCountOfGroup(groupId);
        }
        return 0;
    }

    public boolean moveTo(int position) {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.moveTo(position);
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public boolean moveToNext() {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.moveToNext();
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public boolean moveToPrevious() {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.moveToPrevious();
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public boolean moveToFirst() {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.moveToFirst();
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public boolean moveToLast() {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.moveToLast();
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public boolean moveGroupTo(int position) {
        if (checkViewMode()) {
            return this.mViewMode.moveGroupTo(position);
        }
        return false;
    }

    public boolean moveGroupToNext(boolean doLoopBack) {
        if (checkViewMode()) {
            return this.mViewMode.moveGroupToNext(doLoopBack);
        }
        return false;
    }

    public boolean moveGroupToPrevious(boolean doLoopBack) {
        if (checkViewMode()) {
            return this.mViewMode.moveGroupToPrevious(doLoopBack);
        }
        return false;
    }

    public boolean moveToEntryPosition() {
        if (!checkViewMode()) {
            return false;
        }
        boolean result = this.mViewMode.moveToEntryPosition();
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
            return result;
        }
        return result;
    }

    public MoveHelper getMoveHelperToCurrentFile() {
        SearchInfo info;
        if (!checkViewMode()) {
            return null;
        }
        if (this.mViewMode.isInitialQueryDone()) {
            info = this.mViewMode.getSearchInfo(null);
        } else {
            info = this.mViewMode.getSearchInfoFromResumeContents(null);
        }
        return new MoveHelperToSearchInfo(info);
    }

    public boolean moveByHelper(MoveHelper helper) {
        boolean result = helper.apply(this.mViewMode);
        if (result && this.mPbMode == 2) {
            this.mObserver.setData(this.mProviderHelper.getContentInfo(getContentsId()));
        }
        return result;
    }

    public boolean isFirst() {
        if (checkViewMode()) {
            return this.mViewMode.isFirst();
        }
        return false;
    }

    public boolean isLast() {
        if (checkViewMode()) {
            return this.mViewMode.isLast();
        }
        return false;
    }

    public void setNotificationListener(NotificationListener listener) {
        if (isInitialized()) {
            this.mObserver.setNotificationListener(listener);
        }
    }

    public void removeNotificationListener(NotificationListener listener) {
        this.mObserver.removeNotificationListener(listener);
    }

    public Object getValue(String tag) {
        if (isInitialized()) {
            return this.mObserver.getValue(tag);
        }
        return null;
    }

    /* loaded from: classes.dex */
    public static class HistogramData {
        private static final int COLOR_RESORUTION = 128;
        public int[] yData = new int[COLOR_RESORUTION];
        public int[] rData = new int[COLOR_RESORUTION];
        public int[] gData = new int[COLOR_RESORUTION];
        public int[] bData = new int[COLOR_RESORUTION];

        HistogramData() {
        }
    }

    public ContentsIdentifier getContentsId() {
        return isInitialQueryDone() ? this.mViewMode.getContentsIdentifier() : getResumeId();
    }

    public ContentsIdentifier getResumeId() {
        if (checkViewMode()) {
            return this.mViewMode.getResumeId();
        }
        return null;
    }

    public void rememberContentPos() {
        if (checkViewMode()) {
            this.mViewMode.setContentPos();
        }
    }

    public ContentsIdentifier getContentsIdAt(int position) {
        if (checkViewMode()) {
            return this.mViewMode.getContentsIdentifierAt(position);
        }
        return null;
    }

    public SearchInfo getSearchInfo(SearchInfo info) {
        if (!checkViewMode()) {
            return null;
        }
        if (isInitialQueryDone()) {
            return this.mViewMode.getSearchInfo(info);
        }
        return this.mViewMode.getSearchInfoFromResumeContents(info);
    }

    public SearchInfo getSearchInfoAt(int position, SearchInfo info) {
        if (checkViewMode()) {
            return this.mViewMode.getSearchInfoAt(position, info);
        }
        return null;
    }

    public SearchInfo getSearchInfoWithDateTime(SearchInfo info) {
        if (!checkViewMode()) {
            return null;
        }
        if (isInitialQueryDone()) {
            return this.mViewMode.getSearchInfoWithDateTime(info);
        }
        return this.mViewMode.getSearchInfoFromResumeContents(info);
    }

    public SearchInfo getSearchInfoWithDateTimeAt(int position, SearchInfo info) {
        if (checkViewMode()) {
            return this.mViewMode.getSearchInfoWithDateTimeAt(position, info);
        }
        return null;
    }

    public ContentInfo getContentInfo(ContentsIdentifier id) {
        return this.mProviderHelper.getContentInfo(id);
    }

    public ContentInfo getContentInfoFromCache(ContentsIdentifier id) {
        return this.mProviderHelper.getContentInfoFromCache(id);
    }

    public boolean hasThumbnail(ContentsIdentifier id) {
        return this.mProviderHelper.hasThumbnail(id);
    }

    public Bitmap getThumbnail(ContentsIdentifier id, boolean rotateIt) {
        mScratchOption.rotateIt = rotateIt;
        mScratchOption.clipByAspect = true;
        mScratchOption.postScale = SCALE_WIDE_PANEL_PIXEL;
        return this.mProviderHelper.getThumbnail(id, mScratchOption);
    }

    public Bitmap getThumbnail(ContentsIdentifier id, ThumbnailOption option) {
        return this.mProviderHelper.getThumbnail(id, option);
    }

    @Deprecated
    public Bitmap getJpeg(ContentsIdentifier id) {
        return this.mProviderHelper.getJpeg(id);
    }

    public OptimizedImage getOptimizedImage(ContentsIdentifier id, int imageType) {
        ContentInfo[] info = null;
        if (this.mPbMode == 1 && this.mObserver != null) {
            info = new ContentInfo[1];
        }
        OptimizedImage img = this.mProviderHelper.getOptimizedImage(id, imageType, info);
        if (info != null && info[0] != null) {
            this.mObserver.setData(info[0]);
        } else if (img == null) {
            this.mObserver.setData(null);
        }
        return img;
    }

    public OptimizedImage getOptimizedImageWithoutCache(ContentsIdentifier id, int imageType) {
        return this.mProviderHelper.getOptimizedImageWithoutCache(id, imageType, null);
    }

    public OptimizedImage getOptimizedImageWithoutCache(ContentsIdentifier id, int imageType, ContentInfo[] info) {
        return this.mProviderHelper.getOptimizedImageWithoutCache(id, imageType, info);
    }

    public int getInt(String tag) {
        return !checkViewMode() ? AudioVolumeController.INVALID_VALUE : this.mViewMode.getInt(tag);
    }

    public String getString(String tag) {
        if (checkViewMode()) {
            return this.mViewMode.getString(tag);
        }
        return null;
    }

    public long getLong(String tag) {
        if (checkViewMode()) {
            return this.mViewMode.getLong(tag);
        }
        return Long.MIN_VALUE;
    }

    public int getIntAt(int position, String tag) {
        return !checkViewMode() ? AudioVolumeController.INVALID_VALUE : this.mViewMode.getIntAt(position, tag);
    }

    public String getStringAt(int position, String tag) {
        if (checkViewMode()) {
            return this.mViewMode.getStringAt(position, tag);
        }
        return null;
    }

    public long getLongAt(int position, String tag) {
        if (checkViewMode()) {
            return this.mViewMode.getLongAt(position, tag);
        }
        return Long.MIN_VALUE;
    }

    public int getIntOfGroupAt(int position, String tag) {
        return !checkViewMode() ? AudioVolumeController.INVALID_VALUE : this.mViewMode.getIntOfGroupAt(position, tag);
    }

    public String getStringOfGroupAt(int position, String tag) {
        if (checkViewMode()) {
            return this.mViewMode.getStringOfGroupAt(position, tag);
        }
        return null;
    }

    public long getLongOfGroupAt(int position, String tag) {
        if (checkViewMode()) {
            return this.mViewMode.getLongOfGroupAt(position, tag);
        }
        return Long.MIN_VALUE;
    }

    @Deprecated
    public boolean delete(ContentsIdentifier id) {
        return this.mProviderHelper.delete(id);
    }

    public boolean isEmpty() {
        return getContentsCount() == 0;
    }

    public boolean isVirtualEmpty() {
        return getResumeId() == null;
    }
}
