package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.RelativeLayoutGroup;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public abstract class AbstractZoombarRefresh extends RelativeLayoutGroup {
    public static final int CATEGORY_ILDC_E = 1;
    private static final int DISPLAYING_TIME = 2000;
    public static final int INTVAL_UI_MAIN_FEATURE_MOVIE = 1;
    private static final String LOG_MSG_ON_ATTACHED_TO_WINDOW = "onAttachedToWindow";
    private static final String LOG_MSG_ON_DETTACHED_TO_WINDOW = "onDettachedToWindow";
    private static final String LOG_MSG_ON_NOTIFY = "onNotify";
    private static final String LOG_MSG_START_DISPLAYING_TIMER = "startDisplayingTimer";
    private static final String LOG_MSG_STOP_DISPLAYING_TIMER = "stopDisplayingTimer";
    public static final String PROP_DEVICE_ZOOM_LEVER = "device.zoom.lever";
    public static final String PROP_MODEL_CATEGORY = "model.category";
    public static final String PROP_UI_MAIN_FEATURE = "ui.main.feature";
    public static final int STATUS_ZOOM_LEVER_SUPPORTED = 1;
    private static final String TAG = AbstractZoombarRefresh.class.getSimpleName();
    CameraSetting mCameraSetting;
    protected int mCategory;
    protected Handler mHandler;
    protected boolean mIsEnableDigitalZoom;
    protected boolean mIsEnableFocalLength;
    protected boolean mIsMainFeatureStillImage;
    protected boolean mIsWithZoomLever;
    private NotificationListener mNotificationListener;
    protected int mPowerZoomStatus;

    public AbstractZoombarRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsEnableFocalLength = false;
        this.mIsEnableDigitalZoom = false;
        this.mIsWithZoomLever = false;
        this.mIsMainFeatureStillImage = true;
        this.mCategory = 1;
        this.mPowerZoomStatus = 3;
        this.mCameraSetting = CameraSetting.getInstance();
        this.mNotificationListener = getNotificationListener();
        this.mHandler = new Handler() { // from class: com.sony.imaging.app.base.shooting.widget.AbstractZoombarRefresh.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                AbstractZoombarRefresh.this.updateVisibility(4);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ZoombarRefreshListener implements NotificationListener {
        private final String[] tags = {CameraNotificationManager.FOCAL_LENGTH_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.POWER_ZOOM_CHANGED, CameraNotificationManager.ZOOM_INFO_CHANGED};

        protected ZoombarRefreshListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.i(AbstractZoombarRefresh.TAG, "onNotify: " + tag);
            AbstractZoombarRefresh.this.refresh();
            if (CameraNotificationManager.FOCAL_LENGTH_CHANGED.equals(tag)) {
                if (!AbstractZoombarRefresh.this.mIsEnableFocalLength) {
                    AbstractZoombarRefresh.this.mIsEnableFocalLength = true;
                    return;
                }
            } else if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                if (!AbstractZoombarRefresh.this.mIsEnableFocalLength) {
                    AbstractZoombarRefresh.this.mIsEnableFocalLength = true;
                    return;
                } else if (!AbstractZoombarRefresh.this.mIsEnableDigitalZoom) {
                    AbstractZoombarRefresh.this.mIsEnableDigitalZoom = true;
                    return;
                }
            } else if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag) || CameraNotificationManager.POWER_ZOOM_CHANGED.equals(tag)) {
                AbstractZoombarRefresh.this.mIsEnableFocalLength = false;
                AbstractZoombarRefresh.this.mIsEnableDigitalZoom = false;
                AbstractZoombarRefresh.this.mPowerZoomStatus = AbstractZoombarRefresh.this.mCameraSetting.getPowerZoomStatus();
                AbstractZoombarRefresh.this.updateLensInfo();
                AbstractZoombarRefresh.this.updateDisplayPattern();
            }
            if (AbstractZoombarRefresh.this.isReady()) {
                Log.d(AbstractZoombarRefresh.TAG, "isReady = 0");
                AbstractZoombarRefresh.this.updateVisibility(0);
                AbstractZoombarRefresh.this.updateDisplayInformation();
                AbstractZoombarRefresh.this.startDisplayingTimer();
                return;
            }
            Log.d(AbstractZoombarRefresh.TAG, "isReady = 4");
            AbstractZoombarRefresh.this.updateVisibility(4);
            AbstractZoombarRefresh.this.stopDisplayingTimer();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isPFverOver2()) {
            int zoomLeverStatus = ScalarProperties.getInt("device.zoom.lever");
            if (zoomLeverStatus == 1) {
                this.mIsWithZoomLever = true;
            }
            int mainfeature = ScalarProperties.getInt("ui.main.feature");
            if (mainfeature == 1) {
                this.mIsMainFeatureStillImage = false;
            }
        }
        this.mCategory = ScalarProperties.getInt("model.category");
        this.mPowerZoomStatus = this.mCameraSetting.getPowerZoomStatus();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.setNotificationListener(this.mNotificationListener);
        refresh();
        updateLensInfo();
        updateDisplayPattern();
        updateVisibility(4);
        Log.i(TAG, LOG_MSG_ON_ATTACHED_TO_WINDOW);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.removeNotificationListener(this.mNotificationListener);
        stopDisplayingTimer();
        updateVisibility(4);
        Log.i(TAG, LOG_MSG_ON_DETTACHED_TO_WINDOW);
    }

    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new ZoombarRefreshListener();
        }
        return this.mNotificationListener;
    }

    public static boolean isPFverOver2() {
        if (2 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return true;
    }

    protected void startDisplayingTimer() {
        stopDisplayingTimer();
        this.mHandler.sendEmptyMessageDelayed(0, 2000L);
        Log.i(TAG, LOG_MSG_START_DISPLAYING_TIMER);
    }

    protected void stopDisplayingTimer() {
        this.mHandler.removeMessages(0);
        Log.i(TAG, LOG_MSG_STOP_DISPLAYING_TIMER);
    }

    protected boolean isReady() {
        return true;
    }

    protected void updateVisibility(int visible) {
    }

    protected void refresh() {
    }

    protected void updateLensInfo() {
    }

    protected void updateDisplayPattern() {
    }

    protected void updateDisplayInformation() {
    }
}
