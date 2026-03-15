package com.sony.imaging.app.srctrl.playback.contents;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.SearchInfo;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.SRCtrlPlaybackUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.Content;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.Original;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public class PrepareTransferList {
    protected static final String CONTENT_KIND_MP4 = "movie_mp4";
    protected static final String CONTENT_KIND_STILL = "still";
    protected static final String CONTENT_KIND_UNKNOWN = "";
    protected static final String CONTENT_KIND_XAVCS = "movie_xavcs";
    protected static final int DATE_LENGTH = 8;
    protected static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    protected static final int MAX_LIST_COUNT = 100;
    protected static final String REMOTE_PLAY_TYPE_SIMPLE_STREAMING = "simpleStreaming";
    protected static final String SCHEME_STORAGE = "storage";
    protected static final String SORT_ASCENDING = "ascending";
    protected static final String SORT_DESCENDING = "descending";
    protected static final String SORT_NOT_SPECIFIED = "";
    protected static final String SOURCE_MEMORYCARD1 = "storage:memoryCard1";
    protected static final String STILL_OBJECT_JPEG = "jpeg";
    protected static final String STILL_OBJECT_MPO = "mpo";
    protected static final String STILL_OBJECT_RAW = "raw";
    protected static final String STILL_OBJECT_UNKNOWN = "";
    protected static final String STRING_EMPTY = "";
    protected static final String STR_FALSE = "false";
    protected static final String STR_TRUE = "true";
    protected static final String TARGET_ALL = "all";
    protected static final String TARGET_EMPTY = "";
    protected static final String TYPE_DIRECTORY = "directory";
    protected static final String TYPE_MP4 = "movie_mp4";
    protected static final String TYPE_STILL = "still";
    protected static final String TYPE_XAVCS = "movie_xavcs";
    protected static final String URI_PATH = "?path=";
    public static final String VIEW_DATE = "date";
    public static final String VIEW_FLAT = "flat";
    public static final String VIEW_NEUTRAL = "neutral";
    protected static PrepareTransferList instance;
    protected static boolean mCancelState;
    protected static int mCnt;
    protected static Content[] mContentList;
    protected static CountDownLatch mCountLatch;
    protected static CountDownLatch mListLatch;
    private static final String TAG = PrepareTransferList.class.getName();
    protected static String mCurrentView = "";
    protected static String[] mCurrentContentTypes = null;
    protected static TimeZone tz = TimeZone.getTimeZone("HOGE");
    protected static Date date = new Date();
    protected static final Object mLock = new Object();
    protected String[] mSchemeList = {SCHEME_STORAGE};
    protected String[] mSourceList = {SOURCE_MEMORYCARD1};
    protected String[] mSupportTypeList = {AntiHandBlurController.STILL, "movie_mp4", "movie_xavcs"};
    protected int[] mSupportImediaTypeList = {1, 4, 256};
    protected String[] mSupportTargetList = {TARGET_ALL, ""};
    public String[] mSupportViewList = {VIEW_DATE, VIEW_FLAT};
    protected String[] mSupportSort = {SORT_ASCENDING, SORT_DESCENDING, ""};
    ContentCountCallback mContentCountCallback = new ContentCountCallback();
    ContentListCallback mContentListCallback = new ContentListCallback();

    /* loaded from: classes.dex */
    public enum ContentUrlType {
        CONTENT_ORG,
        CONTENT_THUMB,
        CONTENT_SCN,
        CONTENT_VGA,
        CONTENT_UNKNOWN
    }

    private static void setInstance(PrepareTransferList anInstance) {
        if (instance == null) {
            instance = anInstance;
        }
    }

    protected PrepareTransferList() {
        setInstance(this);
    }

    public static synchronized PrepareTransferList getInstance() {
        PrepareTransferList prepareTransferList;
        synchronized (PrepareTransferList.class) {
            if (instance == null) {
                new PrepareTransferList();
            }
            prepareTransferList = instance;
        }
        return prepareTransferList;
    }

    public String currentViewMode() {
        return mCurrentView;
    }

    public String[] getSchemeValue() {
        if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return null;
        }
        return this.mSchemeList;
    }

    public String[] getSourceList(String scheme) {
        if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return null;
        }
        if (scheme == null) {
            Log.e(TAG, "getSourceList(scheme = null)");
            return null;
        }
        if (SCHEME_STORAGE.equals(scheme)) {
            String[] sourceList = this.mSourceList;
            return sourceList;
        }
        Log.e(TAG, "getSourceList(scheme = " + scheme + LogHelper.MSG_CLOSE_BRACKET);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ContentCountCallback implements ContentsManager.QueryCallback {
        public String mCurrentDate;
        public String mCurrentTarget;
        public String mCurrentUri;

        ContentCountCallback() {
        }

        @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.QueryCallback
        public void onCallback(boolean result) {
            if (!result) {
                Log.e(PrepareTransferList.TAG, "getContentCount() = query error");
                PrepareTransferList.mCnt = 0;
            } else if (!this.mCurrentUri.startsWith(PrepareTransferList.SOURCE_MEMORYCARD1)) {
                Log.e(PrepareTransferList.TAG, "getContentCount() = URI ERROR");
            } else if (PrepareTransferList.TARGET_ALL.equals(this.mCurrentTarget)) {
                PrepareTransferList.mCnt = ContentsManager.getInstance().getContentsTotalCount();
            } else if (!PrepareTransferList.VIEW_FLAT.equals(PrepareTransferList.mCurrentView)) {
                Log.i(PrepareTransferList.TAG, "getContentCount() = URI " + this.mCurrentDate + LogHelper.MSG_OPEN_BRACKET + this.mCurrentDate.length() + LogHelper.MSG_CLOSE_BRACKET);
                if (8 == this.mCurrentDate.length()) {
                    boolean changeDateResult = PrepareTransferList.this.changeDate(this.mCurrentDate, 0);
                    if (changeDateResult) {
                        PrepareTransferList.mCnt = ContentsManager.getInstance().getContentsCount();
                    } else {
                        PrepareTransferList.mCnt = 0;
                    }
                } else if (this.mCurrentDate.length() == 0) {
                    PrepareTransferList.mCnt = ContentsManager.getInstance().getGroupCount();
                }
            } else {
                PrepareTransferList.mCnt = ContentsManager.getInstance().getContentsTotalCount();
            }
            Log.i(PrepareTransferList.TAG, "getContentCount() = " + PrepareTransferList.mCnt);
            PrepareTransferList.mCountLatch.countDown();
        }
    }

    public int getContentCount(String uri, String[] type, String target, String view) {
        mCountLatch = new CountDownLatch(1);
        try {
            mCountLatch.await();
        } catch (InterruptedException e) {
            mCnt = -1;
            e.printStackTrace();
        }
        return mCnt;
    }

    public void initState() {
        synchronized (mLock) {
            mCancelState = false;
        }
    }

    public void cancelState() {
        synchronized (mLock) {
            mCancelState = true;
        }
        if (mCountLatch != null) {
            Log.i(TAG, "cancel getContentCount.");
            mCnt = -1;
            mCountLatch.countDown();
        }
        if (mListLatch != null) {
            Log.i(TAG, "cancel getContentList.");
            mContentList = null;
            mListLatch.countDown();
        }
    }

    public boolean isCancelState() {
        boolean ret;
        synchronized (mLock) {
            ret = mCancelState;
        }
        return ret;
    }

    public int getContentCountProxy(String uri, String[] type, String target, String view) {
        mCnt = -1;
        if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return mCnt;
        }
        if (uri == null) {
            Log.e(TAG, "getContentList()  URI is null");
            mCountLatch.countDown();
            return mCnt;
        }
        boolean bSupportViewType = false;
        String[] arr$ = this.mSupportViewList;
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            String supportView = arr$[i$];
            if (!supportView.equals(view)) {
                i$++;
            } else {
                bSupportViewType = true;
                break;
            }
        }
        if (!bSupportViewType) {
            Log.e(TAG, "getContentCount()  This view is not supported.");
            mCountLatch.countDown();
            return mCnt;
        }
        String date2 = uri.replace(SOURCE_MEMORYCARD1, "").replaceAll("[^0-9]", "");
        if (type != null && date2.length() == 0) {
            Log.e(TAG, "getContentCount()  type is not null but date length is zero.");
            mCountLatch.countDown();
            return mCnt;
        }
        if (type != null && !isFilterTypeValid(type)) {
            Log.e(TAG, "getContentCount()  types are not invalid.");
            mCountLatch.countDown();
            mCnt = 0;
            return mCnt;
        }
        setViewMode(view);
        filterByContentType(type);
        this.mContentCountCallback.mCurrentUri = uri;
        this.mContentCountCallback.mCurrentDate = date2;
        this.mContentCountCallback.mCurrentTarget = target;
        ContentsManager mgr = ContentsManager.getInstance();
        if (!mgr.isInitialQueryDone()) {
            mgr.startInitialQuery(this.mContentCountCallback, false);
        } else {
            this.mContentCountCallback.onCallback(true);
        }
        return mCnt;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ContentListCallback implements ContentsManager.QueryCallback {
        public int mCurrentContentCount;
        public String mCurrentDate;
        public int mCurrentStartIndex;
        public String mCurrentTarget;
        public String mCurrentUri;

        ContentListCallback() {
        }

        @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.QueryCallback
        public void onCallback(boolean result) {
            synchronized (SRCtrlPlaybackUtil.getContentTransferLock()) {
                if (result) {
                    if (this.mCurrentUri.startsWith(PrepareTransferList.SOURCE_MEMORYCARD1)) {
                        Log.i(PrepareTransferList.TAG, "getContentList() = URI " + this.mCurrentDate + LogHelper.MSG_OPEN_BRACKET + this.mCurrentDate.length() + LogHelper.MSG_CLOSE_BRACKET);
                        if (8 == this.mCurrentDate.length()) {
                            if (!PrepareTransferList.VIEW_DATE.equals(PrepareTransferList.mCurrentView)) {
                                Log.e(PrepareTransferList.TAG, "getContentList() = date unmatch ERROR");
                            } else {
                                boolean changeDateResult = PrepareTransferList.this.changeDate(this.mCurrentDate, 0);
                                if (changeDateResult) {
                                    try {
                                        PrepareTransferList.mContentList = PrepareTransferList.this.createContentList(this.mCurrentStartIndex, this.mCurrentContentCount);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        PrepareTransferList.mContentList = null;
                                    }
                                }
                            }
                        } else if (this.mCurrentDate.length() == 0) {
                            if (this.mCurrentTarget != null && !this.mCurrentTarget.equals("")) {
                                Log.e(PrepareTransferList.TAG, "getContentList() = parameter error");
                            } else if (PrepareTransferList.VIEW_DATE.equals(PrepareTransferList.mCurrentView)) {
                                try {
                                    PrepareTransferList.mContentList = PrepareTransferList.this.createDateList(this.mCurrentStartIndex, this.mCurrentContentCount);
                                } catch (ParseException e2) {
                                    e2.printStackTrace();
                                    PrepareTransferList.mContentList = null;
                                }
                            } else if (!PrepareTransferList.VIEW_FLAT.equals(PrepareTransferList.mCurrentView)) {
                                Log.e(PrepareTransferList.TAG, "getContentList() =Target null Type ERROR");
                            } else {
                                try {
                                    PrepareTransferList.mContentList = PrepareTransferList.this.createContentList(this.mCurrentStartIndex, this.mCurrentContentCount);
                                } catch (ParseException e3) {
                                    e3.printStackTrace();
                                    PrepareTransferList.mContentList = null;
                                }
                            }
                        }
                    }
                    Log.e(PrepareTransferList.TAG, "getContentList() = URI ERROR");
                }
                PrepareTransferList.mListLatch.countDown();
            }
        }
    }

    public Content[] getContentList(String uri, int stIdx, int cnt, String[] type, String target, String view, String sort) {
        mListLatch = new CountDownLatch(1);
        try {
            mListLatch.await();
        } catch (InterruptedException e) {
            mContentList = null;
            e.printStackTrace();
        }
        return mContentList;
    }

    public synchronized Content[] getContentListProxy(String uri, Integer stIdx, Integer cnt, String[] type, String target, String view, String sort) {
        Content[] contentArr;
        mContentList = null;
        if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            contentArr = mContentList;
        } else if (uri == null) {
            Log.e(TAG, "getContentList()  URI is null");
            mListLatch.countDown();
            contentArr = mContentList;
        } else if (stIdx.intValue() < 0 || cnt.intValue() <= 0) {
            Log.e(TAG, "getContentList()  stIdx or cnt are  out of limit.");
            mListLatch.countDown();
            contentArr = mContentList;
        } else {
            boolean bSupportViewType = false;
            String[] arr$ = this.mSupportViewList;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                String supportView = arr$[i$];
                if (!supportView.equals(view)) {
                    i$++;
                } else {
                    bSupportViewType = true;
                    break;
                }
            }
            if (!bSupportViewType) {
                Log.e(TAG, "getContentList()  This view is not supported.");
                mListLatch.countDown();
                contentArr = mContentList;
            } else {
                String date2 = uri.replace(SOURCE_MEMORYCARD1, "").replaceAll("[^0-9]", "");
                if (type != null && date2.length() == 0) {
                    Log.e(TAG, "getContentList()  type is not null but date length is zero.");
                    mListLatch.countDown();
                    contentArr = mContentList;
                } else if (type != null && !isFilterTypeValid(type)) {
                    Log.e(TAG, "getContentList()  types are not invalid.");
                    mListLatch.countDown();
                    contentArr = mContentList;
                } else {
                    synchronized (SRCtrlPlaybackUtil.getContentTransferLock()) {
                        setViewMode(view);
                        boolean bForceQuery = setContentOrder(sort);
                        boolean bDoneQuery = filterByContentType(type);
                        this.mContentListCallback.mCurrentUri = uri;
                        this.mContentListCallback.mCurrentDate = date2;
                        this.mContentListCallback.mCurrentTarget = target;
                        this.mContentListCallback.mCurrentStartIndex = stIdx.intValue();
                        this.mContentListCallback.mCurrentContentCount = cnt.intValue();
                        ContentsManager mgr = ContentsManager.getInstance();
                        if (!mgr.isInitialQueryDone() || (bForceQuery && !bDoneQuery)) {
                            mgr.startInitialQuery(this.mContentListCallback, true);
                        } else {
                            this.mContentListCallback.onCallback(true);
                        }
                    }
                    contentArr = mContentList;
                }
            }
        }
        return contentArr;
    }

    protected Content[] createContentList(int stIdx, int cnt) throws ParseException {
        ContentsManager mgr = ContentsManager.getInstance();
        int num = mgr.getContentsCount();
        String mediaId = AvindexStore.getExternalMediaIds()[0];
        if (stIdx >= num) {
            return null;
        }
        if (num < stIdx + cnt) {
            cnt = num - stIdx;
        }
        if (100 < cnt) {
            cnt = 100;
        }
        Content[] contentList = new Content[cnt];
        String ContentTransferDirectory = SRCtrlEnvironment.getInstance().getContentTransferBaseURL();
        for (int index = stIdx; index < stIdx + cnt; index++) {
            if (mgr.moveTo(index)) {
                int offset = index - stIdx;
                contentList[offset] = new Content();
                ContentsIdentifier contId = mgr.getContentsId();
                StringBuffer uriBase = new StringBuffer(contId.data);
                long localTime = mgr.getLong("content_created_local_date_time");
                int timeZone = mgr.getInt("time_zone");
                boolean bIsExistJpeg = mgr.getInt("exist_jpeg") > 0;
                boolean bIsExistRAW = mgr.getInt("exist_raw") > 0;
                boolean bIsExistMPO = mgr.getInt("exist_mpo") > 0;
                boolean bIsExistJpegOrRAW = bIsExistJpeg || bIsExistRAW;
                int contentCount = (bIsExistJpeg ? 1 : 0) + (bIsExistRAW ? 1 : 0) + (bIsExistMPO ? 1 : 0);
                int contentType = mgr.getInt("content_type");
                ContentInfo contentInfo = (ContentInfo) mgr.getValue(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE);
                String fileName = contentInfo.getString("DCF_TBLFileName");
                uriBase.append(SRCtrlConstants.URI_CONTENT_ID_SEPARATOR);
                uriBase.append(contId._id);
                uriBase.append(SRCtrlConstants.URI_CONTENT_ID_SEPARATOR);
                uriBase.append(contentType);
                uriBase.append(SRCtrlConstants.URI_CONTENT_ID_SEPARATOR);
                uriBase.append(mediaId);
                String encUri = SRCtrlPlaybackUtil.urlEncode(uriBase.toString());
                if (encUri == null) {
                    Log.i(TAG, "It fails to encode content URL.");
                    return null;
                }
                contentList[offset].title = "";
                contentList[offset].isProtected = "";
                contentList[offset].createdTime = createIso8601Date(localTime, timeZone);
                contentList[offset].content = new com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.ContentInfo();
                if (1 == contentType) {
                    contentList[offset].uri = "image:content?contentId=" + encUri;
                    if (fileName != null) {
                        fileName = fileName.replace(SRCtrlConstants.MPO_FILE_SUFFIX, ".JPG").replace(SRCtrlConstants.RAW_FILE_SUFFIX, ".JPG");
                    }
                    contentList[offset].content.original = new Original[contentCount];
                    for (int column = 0; column < contentCount; column++) {
                        contentList[offset].content.original[column] = new Original();
                        if (bIsExistJpeg) {
                            contentList[offset].content.original[column].url = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_ORGJPEG + encUri;
                            contentList[offset].content.original[column].stillObject = STILL_OBJECT_JPEG;
                            contentList[offset].content.original[column].fileName = fileName;
                            bIsExistJpeg = false;
                        } else if (bIsExistRAW) {
                            contentList[offset].content.original[column].url = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_ORGRAW + encUri;
                            contentList[offset].content.original[column].stillObject = "raw";
                            String fileNameRaw = fileName;
                            if (fileNameRaw != null) {
                                fileNameRaw = fileName.replace(".JPG", SRCtrlConstants.RAW_FILE_SUFFIX);
                            }
                            contentList[offset].content.original[column].fileName = fileNameRaw;
                            bIsExistRAW = false;
                        } else if (bIsExistMPO) {
                            contentList[offset].content.original[column].url = "";
                            contentList[offset].content.original[column].stillObject = STILL_OBJECT_MPO;
                            String fileNameMpo = fileName;
                            if (fileNameMpo != null) {
                                fileNameMpo = fileName.replace(".JPG", SRCtrlConstants.MPO_FILE_SUFFIX);
                            }
                            contentList[offset].content.original[column].fileName = fileNameMpo;
                            bIsExistMPO = false;
                        }
                    }
                    if (bIsExistJpegOrRAW) {
                        contentList[offset].content.largeUrl = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_LARGE + encUri;
                        contentList[offset].content.smallUrl = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_SMALL + encUri;
                        contentList[offset].content.thumbnailUrl = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_THUMB + encUri;
                    }
                } else {
                    contentList[offset].uri = "video:content?contentId=" + encUri;
                    contentList[offset].content.original = new Original[1];
                    contentList[offset].content.original[0] = new Original();
                    contentList[offset].content.original[0].url = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_ORG + encUri;
                    contentList[offset].content.original[0].stillObject = "";
                    contentList[offset].content.original[0].fileName = fileName;
                    contentList[offset].content.thumbnailUrl = ContentTransferDirectory + SRCtrlConstants.CONTENTTRANSFER_THUMB + encUri;
                }
                contentList[offset].folderNo = String.format("%03d", Integer.valueOf(mgr.getInt(IFolder.DCF_FOLDER_NUMBER)));
                contentList[offset].fileNo = String.format("%04d", Integer.valueOf(mgr.getInt("dcf_file_number")));
                if (1 == contentType) {
                    contentList[offset].contentKind = AntiHandBlurController.STILL;
                } else if (4 == contentType) {
                    contentList[offset].contentKind = "movie_mp4";
                } else if (256 == contentType) {
                    contentList[offset].contentKind = "movie_xavcs";
                } else {
                    contentList[offset].contentKind = "";
                }
                contentList[offset].isPlayable = STR_FALSE;
                contentList[offset].isBrowsable = STR_FALSE;
                if (4 == contentType || 256 == contentType) {
                    contentList[offset].remotePlayType = new String[1];
                    contentList[offset].remotePlayType[0] = REMOTE_PLAY_TYPE_SIMPLE_STREAMING;
                } else {
                    contentList[offset].remotePlayType = null;
                }
            } else {
                Log.i(TAG, "It fails to move content index.");
                return null;
            }
        }
        return contentList;
    }

    protected Content[] createDateList(int stIdx, int cnt) throws ParseException {
        int num = ContentsManager.getInstance().getGroupCount();
        if (stIdx >= num) {
            return null;
        }
        if (num < stIdx + cnt) {
            cnt = num - stIdx;
        }
        Content[] contentList = new Content[cnt];
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat cnvformat = new SimpleDateFormat("yyyy-MM-dd");
        for (int index = stIdx; index < stIdx + cnt; index++) {
            if (ContentsManager.getInstance().moveGroupTo(index)) {
                int offset = index - stIdx;
                String strLocalDate = ContentsManager.getInstance().getStringOfGroupAt(index, ILocalDate.CONTENT_CREATED_LOCAL_DATE);
                contentList[offset] = new Content();
                contentList[offset].title = strLocalDate;
                Date dateLocalDate = format.parse(strLocalDate);
                contentList[offset].uri = "storage:memoryCard1?path=" + cnvformat.format(dateLocalDate);
                contentList[offset].isProtected = "";
                contentList[offset].isPlayable = STR_FALSE;
                contentList[offset].isBrowsable = "true";
                contentList[offset].contentKind = TYPE_DIRECTORY;
            } else {
                return null;
            }
        }
        return contentList;
    }

    protected String createIso8601Date(long localDateTime, int timeZone) {
        long utcDateTime = localDateTime - ((timeZone * 60) * SsdpDevice.RETRY_INTERVAL);
        tz.setRawOffset(timeZone * 60 * SsdpDevice.RETRY_INTERVAL);
        date.setTime(utcDateTime);
        SimpleDateFormat simpleDateTime = new SimpleDateFormat(ISO8601_FORMAT);
        simpleDateTime.setTimeZone(tz);
        StringBuilder sb = new StringBuilder();
        sb.append(simpleDateTime.format(Long.valueOf(date.getTime())));
        sb.insert(sb.length() - 2, ":");
        String time = sb.toString();
        return time;
    }

    protected boolean changeDate(String date2, int position) {
        return ContentsManager.getInstance().moveByHelper(new ChangeDateHelper(date2, position));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class ChangeDateHelper implements ContentsManager.MoveHelper {
        private final String mDate;
        private final int mPosition;

        public ChangeDateHelper(String date, int position) {
            this.mDate = date;
            this.mPosition = position;
        }

        @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.MoveHelper
        public boolean apply(ViewMode viewMode) {
            SearchInfo info = new SearchInfo(0, 0, this.mDate, null);
            if (viewMode == null) {
                return false;
            }
            int index = viewMode.findGroup(info);
            Log.i(PrepareTransferList.TAG, "apply call findGroup() = " + index);
            if (-1 == index) {
                return false;
            }
            boolean result = viewMode.moveGroupTo(index);
            if (result) {
                return viewMode.moveTo(this.mPosition);
            }
            return result;
        }
    }

    public void setViewMode(String view) {
        Log.i(TAG, "setViewMode = " + view);
        if (!view.equals(mCurrentView) || "neutral".equals(view)) {
            ContentsManager mgr = ContentsManager.getInstance();
            if (VIEW_FLAT.equals(view)) {
                mgr.setViewMode(MixedTypeFlatViewMode.class);
                mCurrentView = VIEW_FLAT;
            } else if (VIEW_DATE.equals(view)) {
                mgr.setViewMode(MixedTypeDateViewModeEx.class);
                mCurrentView = VIEW_DATE;
            } else {
                mgr.setViewMode(NeutralViewMode.class);
                mCurrentView = "neutral";
            }
        }
    }

    protected boolean setContentOrder(String sort) {
        MixedTypeDateViewModeEx viewMode;
        if (sort == null) {
            sort = "";
        }
        boolean isSortTypeAsc = !sort.equals(SORT_DESCENDING);
        if (VIEW_FLAT.equals(mCurrentView)) {
            MixedTypeFlatViewMode viewMode2 = (MixedTypeFlatViewMode) ContentsManager.getInstance().getViewModeInstance();
            if (viewMode2 == null) {
                return false;
            }
            boolean bForceQuery = viewMode2.setContentOrder(isSortTypeAsc);
            return bForceQuery;
        }
        if (!VIEW_DATE.equals(mCurrentView) || (viewMode = (MixedTypeDateViewModeEx) ContentsManager.getInstance().getViewModeInstance()) == null) {
            return false;
        }
        boolean bForceQuery2 = viewMode.setContentOrder(isSortTypeAsc);
        return bForceQuery2;
    }

    protected boolean isFilterTypeValid(String[] type) {
        int numOfAvailableType = 0;
        if (type != null) {
            String[] arr$ = this.mSupportTypeList;
            for (String supportType : arr$) {
                for (String setType : type) {
                    if (supportType.equals(setType)) {
                        numOfAvailableType++;
                    }
                }
            }
        }
        return numOfAvailableType > 0;
    }

    protected boolean filterByContentType(String[] type) {
        boolean ret = false;
        boolean bNeed = false;
        if (type == null) {
            Log.i(TAG, "filterByContentType type = null");
        } else {
            Log.i(TAG, "filterByContentType type = " + type.toString());
            for (int i = 0; i < type.length; i++) {
                Log.i(TAG, "[" + i + "]" + type[i].toString());
            }
        }
        if (mCurrentContentTypes == null) {
            Log.i(TAG, "                    prev = null");
        } else {
            Log.i(TAG, "                    prev = " + mCurrentContentTypes.toString());
            for (int i2 = 0; i2 < mCurrentContentTypes.length; i2++) {
                Log.i(TAG, "[" + i2 + "]" + mCurrentContentTypes[i2].toString());
            }
        }
        int numOfAvailableType = 0;
        if (type != null) {
            String[] arr$ = this.mSupportTypeList;
            for (String supportType : arr$) {
                for (String setType : type) {
                    if (supportType.equals(setType)) {
                        numOfAvailableType++;
                    }
                }
            }
        }
        String[] availableTypes = null;
        if (numOfAvailableType > 0) {
            int cnt = 0;
            availableTypes = new String[numOfAvailableType];
            String[] arr$2 = this.mSupportTypeList;
            for (String supportType2 : arr$2) {
                for (String setType2 : type) {
                    if (supportType2.equals(setType2) && cnt < numOfAvailableType) {
                        availableTypes[cnt] = setType2;
                        cnt++;
                    }
                }
            }
        }
        if ((availableTypes == null && mCurrentContentTypes != null) || (availableTypes != null && mCurrentContentTypes == null)) {
            bNeed = true;
        }
        if (availableTypes != null && mCurrentContentTypes != null && availableTypes.length == mCurrentContentTypes.length) {
            int cnt2 = 0;
            while (true) {
                if (cnt2 >= mCurrentContentTypes.length) {
                    break;
                }
                if (Arrays.asList(availableTypes).contains(mCurrentContentTypes[cnt2])) {
                    cnt2++;
                } else {
                    bNeed = true;
                    break;
                }
            }
        } else if (availableTypes == null && mCurrentContentTypes == null) {
            bNeed = false;
        } else {
            bNeed = true;
        }
        if (bNeed) {
            mCurrentContentTypes = availableTypes;
            String[] setTypes = null;
            if (availableTypes != null) {
                int cnt3 = 0;
                setTypes = new String[availableTypes.length];
                String[] arr$3 = availableTypes;
                for (String setType3 : arr$3) {
                    int cnt22 = 0;
                    while (true) {
                        if (cnt22 >= this.mSupportTypeList.length) {
                            break;
                        }
                        if (!setType3.equals(this.mSupportTypeList[cnt22])) {
                            cnt22++;
                        } else {
                            setTypes[cnt3] = String.valueOf(this.mSupportImediaTypeList[cnt22]);
                            cnt3++;
                            break;
                        }
                    }
                }
            }
            if (setTypes == null) {
                Log.i(TAG, "                setTypes = null");
            } else {
                Log.i(TAG, "                setTypes = " + setTypes.toString());
                String[] arr$4 = setTypes;
                for (String setType4 : arr$4) {
                    Log.i(TAG, setType4 + ",");
                }
            }
            if (VIEW_FLAT.equals(mCurrentView)) {
                MixedTypeFlatViewMode viewMode = (MixedTypeFlatViewMode) ContentsManager.getInstance().getViewModeInstance();
                if (viewMode != null) {
                    ret = viewMode.filterByContentType(setTypes);
                }
            } else {
                MixedTypeDateViewModeEx viewMode2 = (MixedTypeDateViewModeEx) ContentsManager.getInstance().getViewModeInstance();
                if (viewMode2 != null) {
                    ret = viewMode2.filterByContentType(setTypes);
                }
            }
        }
        Log.i(TAG, "filterByContentType(" + ret + LogHelper.MSG_CLOSE_BRACKET);
        return ret;
    }
}
