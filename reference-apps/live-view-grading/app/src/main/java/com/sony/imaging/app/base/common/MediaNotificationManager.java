package com.sony.imaging.app.base.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MediaNotificationManager extends NotificationManager {
    private static final IntentFilter AVAILABLE_SIZE_INTENTS;
    static final int FACTOR_ID_ACCESSING = 0;
    static final int FACT_CHECKING = 0;
    static final int FACT_MOUNTED_CARD = 2;
    static final int FACT_MOUNTED_UNKNOWN = 1;
    static final int FACT_UNMOUNTED = 3;
    private static final String LOG_MSG_GERREMAINING = "getAvailableCount returns ";
    private static final String LOG_MSG_INTENTRECEIVED = "intent received = ";
    private static final IntentFilter MEDIA_INTENTS = new IntentFilter();
    public static final int MEDIA_REC_ERROR_INITIAL_VALUE = -100;
    public static final int MEDIA_STATE_ACCESSING = 1;
    public static final int MEDIA_STATE_ERROR_FORMAT = 3;
    public static final int MEDIA_STATE_ERROR_UNMOUTABLE = 4;
    private static final HashMap<String, Integer> MEDIA_STATE_MAP;
    public static final int MEDIA_STATE_MOUNTED = 2;
    public static final int MEDIA_STATE_NOCARD = 0;
    public static final int MEDIA_TYPE_MS = 2;
    public static final int MEDIA_TYPE_SD = 1;
    public static final int MEDIA_TYPE_UNKNOWN = 0;
    private static final String TAG = "MediaNotificationManager";
    public static final String TAG_MEDIA_REMAINING_CHANGE = "com.sony.imaging.app.base.common.MediaInformationManager.RemainingChange";
    public static final String TAG_MEDIA_STATUS_CHANGE = "com.sony.imaging.app.base.common.MediaInformationManager.MediaStatusChange";
    public static final String TAG_MOVIE_REC_ERROR = "movieRecError";
    public static final String TAG_MOVIE_REC_REMAIN_TIME_CHANGED = "movieRecRemainTimeChanged";
    private static final ArrayList<Integer> WHITE_LIST_ACCESSING_CAUTION;
    private static MediaNotificationManager mTheInstance;
    private CameraListener mCameraListener;
    private CameraNotificationManager mCameraNotifier;
    private boolean mGone;
    private boolean mHolding;
    private int mInsertedMediaType;
    private boolean mIsEyeFi;
    private boolean mIsFakeMS;
    private String mMediaId;
    private MediaInfo mMediaInfo;
    private int mMediaState;
    private int mMediaType;
    private int mMovieRecErrorFactor;
    private HashSet<String> mReceivedTagDuringHolding;
    private MediaEventBroadcastReceiver mReceiver;
    private int mRemainMovieRecTime;
    private int mRemainingAmount;

    static {
        MEDIA_INTENTS.addAction("android.intent.action.MEDIA_CHECKING");
        MEDIA_INTENTS.addAction("android.intent.action.MEDIA_MOUNTED");
        MEDIA_INTENTS.addAction("android.intent.action.MEDIA_NOFS");
        MEDIA_INTENTS.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        MEDIA_INTENTS.addAction("android.intent.action.MEDIA_UNMOUNTED");
        MEDIA_INTENTS.addAction("android.intent.action.MEDIA_REMOVED");
        MEDIA_INTENTS.addDataScheme("file");
        AVAILABLE_SIZE_INTENTS = new IntentFilter("com.sony.scalar.providers.avindex.action.AVINDEX_MEDIA_AVAILABLE_SIZE_CHANGED");
        MEDIA_STATE_MAP = new HashMap<>();
        MEDIA_STATE_MAP.put("removed", 0);
        MEDIA_STATE_MAP.put("unmounted", 0);
        MEDIA_STATE_MAP.put("checking", 1);
        MEDIA_STATE_MAP.put("nofs", 3);
        MEDIA_STATE_MAP.put("mounted", 2);
        MEDIA_STATE_MAP.put("unmountable", 4);
        MEDIA_STATE_MAP.put("shared", 0);
        MEDIA_STATE_MAP.put("bad_removal", 0);
        MEDIA_STATE_MAP.put("mounted_ro", 2);
        WHITE_LIST_ACCESSING_CAUTION = new ArrayList<>();
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            WHITE_LIST_ACCESSING_CAUTION.add(17);
            WHITE_LIST_ACCESSING_CAUTION.add(6);
            WHITE_LIST_ACCESSING_CAUTION.add(7);
            WHITE_LIST_ACCESSING_CAUTION.add(21);
            WHITE_LIST_ACCESSING_CAUTION.add(3);
            return;
        }
        WHITE_LIST_ACCESSING_CAUTION.add(9);
        WHITE_LIST_ACCESSING_CAUTION.add(10);
        WHITE_LIST_ACCESSING_CAUTION.add(11);
    }

    private MediaNotificationManager() {
        super(true, true);
        this.mReceivedTagDuringHolding = new HashSet<>();
        this.mGone = true;
        this.mRemainMovieRecTime = 0;
        this.mMovieRecErrorFactor = -100;
        this.mMediaId = AvindexStore.getExternalMediaIds()[0];
        this.mReceiver = new MediaEventBroadcastReceiver();
        this.mCameraNotifier = CameraNotificationManager.getInstance();
        this.mCameraListener = new CameraListener();
    }

    public static MediaNotificationManager getInstance() {
        if (mTheInstance == null) {
            mTheInstance = new MediaNotificationManager();
        }
        return mTheInstance;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return null;
    }

    public boolean isMounted() {
        boolean isMountedState = this.mMediaState == 2;
        boolean isValidMediaType = this.mMediaType == 2 || this.mMediaType == 1;
        return isMountedState && isValidMediaType;
    }

    public int getInsertedMediaType() {
        return this.mInsertedMediaType;
    }

    public boolean isNoCard() {
        return this.mMediaState == 0;
    }

    public boolean isError() {
        return this.mMediaState == 3 || this.mMediaState == 4;
    }

    public boolean isFakeMs() {
        return this.mIsFakeMS;
    }

    public boolean isEyeFi() {
        return this.mIsEyeFi;
    }

    public int getMediaState() {
        return this.mMediaState;
    }

    public int getRemaining() {
        return this.mRemainingAmount;
    }

    public void resume(Context c) {
        c.registerReceiver(this.mReceiver, MEDIA_INTENTS);
        c.registerReceiver(this.mReceiver, AVAILABLE_SIZE_INTENTS);
        this.mCameraNotifier.setNotificationListener(this.mCameraListener);
        updateStatus();
        initHoldStatus();
        this.mGone = false;
    }

    public void pause(Context c) {
        CautionUtilityClass.getInstance().disapperFactor(0);
        this.mGone = true;
        c.unregisterReceiver(this.mReceiver);
        this.mCameraNotifier.removeNotificationListener(this.mCameraListener);
    }

    void initHoldStatus() {
        this.mHolding = false;
        this.mReceivedTagDuringHolding.clear();
    }

    public void hold(boolean hold) {
        this.mHolding = hold;
        if (hold) {
            this.mReceivedTagDuringHolding.clear();
            return;
        }
        if (1 == this.mMediaState) {
            requestCautionAccessing();
        } else {
            CautionUtilityClass.getInstance().disapperFactor(0);
        }
        Iterator i$ = this.mReceivedTagDuringHolding.iterator();
        while (i$.hasNext()) {
            String tag = i$.next();
            notify(tag);
        }
    }

    void updateStatus() {
        this.mMediaInfo = AvindexStore.getMediaInfo(this.mMediaId);
        this.mMediaType = this.mMediaInfo.getMediaType();
        this.mInsertedMediaType = this.mMediaType;
        this.mIsFakeMS = this.mMediaInfo.isFakeMs();
        this.mIsEyeFi = this.mMediaInfo.isEyeFi();
        String env = android.os.Environment.getExternalStorageState();
        this.mMediaState = MEDIA_STATE_MAP.get(env).intValue();
    }

    public void updateRemainingAmount() {
        this.mRemainingAmount = AvindexStore.Images.getAvailableCount(this.mMediaId);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GERREMAINING).append(this.mRemainingAmount);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    public void notifyRemainingAmountChange() {
        if (this.mHolding) {
            this.mReceivedTagDuringHolding.add(TAG_MEDIA_REMAINING_CHANGE);
        } else {
            notify(TAG_MEDIA_REMAINING_CHANGE);
        }
    }

    void refresh() {
        updateStatus();
        updateRemainingAmount();
    }

    void requestCautionAccessing() {
        int mode = CautionUtilityClass.getInstance().getMode();
        if (WHITE_LIST_ACCESSING_CAUTION.contains(Integer.valueOf(mode))) {
            CautionUtilityClass.getInstance().requestFactor(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MediaEventBroadcastReceiver extends BroadcastReceiver {
        MediaEventBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String tag;
            if (!MediaNotificationManager.this.mGone) {
                String action = intent.getAction();
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), MediaNotificationManager.LOG_MSG_INTENTRECEIVED).append(action);
                Log.i(MediaNotificationManager.TAG, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                if (action.equals("com.sony.scalar.providers.avindex.action.AVINDEX_MEDIA_AVAILABLE_SIZE_CHANGED")) {
                    tag = MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE;
                    MediaNotificationManager.this.updateRemainingAmount();
                } else {
                    tag = MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE;
                    MediaNotificationManager.this.refresh();
                    if (action.equals("android.intent.action.MEDIA_CHECKING")) {
                        MediaNotificationManager.this.mMediaType = 0;
                        if (!MediaNotificationManager.this.mHolding) {
                            MediaNotificationManager.this.requestCautionAccessing();
                        }
                    } else if (!MediaNotificationManager.this.mHolding) {
                        CautionUtilityClass.getInstance().disapperFactor(0);
                    }
                }
                if (MediaNotificationManager.this.mHolding) {
                    MediaNotificationManager.this.mReceivedTagDuringHolding.add(tag);
                } else {
                    MediaNotificationManager.this.notify(tag);
                }
            }
        }
    }

    public void updateRemainMovieRecTime(int remainMovieRecTime) {
        this.mRemainMovieRecTime = remainMovieRecTime;
        notify(TAG_MOVIE_REC_REMAIN_TIME_CHANGED);
    }

    public int getRemainMovieRecTime() {
        return this.mRemainMovieRecTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CameraListener implements NotificationListener {
        private final String[] TAGS = {CameraNotificationManager.REC_MODE_CHANGED};

        CameraListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (!MediaNotificationManager.this.isNoCard() && tag.equals(CameraNotificationManager.REC_MODE_CHANGED)) {
                MediaNotificationManager.this.updateRemainingAmount();
                MediaNotificationManager.this.notifyRemainingAmountChange();
            }
        }
    }

    public void updateMovieRecErrorFactor(int movieRecErrorFactor) {
        this.mMovieRecErrorFactor = movieRecErrorFactor;
        notify(TAG_MOVIE_REC_ERROR);
    }

    public int getMovieRecErrorFactor() {
        return this.mMovieRecErrorFactor;
    }
}
