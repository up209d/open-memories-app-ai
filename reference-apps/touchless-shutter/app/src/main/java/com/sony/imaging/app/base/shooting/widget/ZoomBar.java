package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.RelativeLayoutGroupOneByN;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class ZoomBar extends RelativeLayoutGroupOneByN {
    private static final int BASE_ICON_COUNT = 2;
    private static final int BASE_ICON_DIVIDED_BAR = 1;
    private static final int BASE_ICON_NON_DIVIDED_BAR = 0;
    public static final int CATEGORY_CAM = 3;
    public static final int CATEGORY_DSC = 2;
    public static final int CATEGORY_ILDC_A = 0;
    public static final int CATEGORY_ILDC_E = 1;
    public static final int CATEGORY_UNKNOWN = -100;
    private static final int DISPLAYING_TIME = 2000;
    public static final int DISPLAY_PATTERN_DIGIT = 2;
    public static final int DISPLAY_PATTERN_MULT_MAG = 1;
    public static final int DISPLAY_PATTERN_OPT = 4;
    public static final int DISPLAY_PATTERN_OPT_DIGIT_FOCAL = 0;
    public static final int DISPLAY_PATTERN_OPT_FOCAL = 3;
    public static final int DISPLAY_PATTERN_UNDEFINED = 10;
    private static final int FOCAL_LENGTH_STEP = 10;
    private static final int INIT_ZOOM_MAG = 100;
    public static final int INTVAL_UI_MAIN_FEATURE_MOVIE = 1;
    public static final int INTVAL_UI_MAIN_FEATURE_STILL = 0;
    private static final String LOG_MSG_ISREADY = "isReady";
    private static final String LOG_MSG_ON_ATTACHED_TO_WINDOW = "onAttachedToWindow";
    private static final String LOG_MSG_ON_DETTACHED_TO_WINDOW = "onDettachedToWindow";
    private static final String LOG_MSG_ON_FINISH_INFLATE = "onFinishInflate";
    private static final String LOG_MSG_ON_NOTIFY = "onNotify";
    private static final String LOG_MSG_REFRESH = "refresh";
    private static final String LOG_MSG_START_DISPLAYING_TIMER = "startDisplayingTimer";
    private static final String LOG_MSG_STOP_DISPLAYING_TIMER = "stopDisplayingTimer";
    private static final String LOG_MSG_UPDATEDISPLAYINFORMATION = "updateDisplayInformation";
    private static final String LOG_MSG_UPDATEDISPLAYPATTERN = "updateDisplayPattern";
    private static final String LOG_MSG_UPDATEVISIBILITY = "updateVisibility";
    private static final String LOG_MSG_ZOOM_BAR = "ZoomBar";
    public static final String PROP_DEVICE_ZOOM_LEVER = "device.zoom.lever";
    public static final String PROP_MODEL_CATEGORY = "model.category";
    public static final String PROP_UI_MAIN_FEATURE = "ui.main.feature";
    public static final int STATUS_POWER_ZOOM_AVAILABLE = 1;
    public static final int STATUS_POWER_ZOOM_INAPPLICABLE = 3;
    public static final int STATUS_POWER_ZOOM_UNAVAILABLE = 2;
    public static final int STATUS_ZOOM_LEVER_NOT_SUPPORTED = 0;
    public static final int STATUS_ZOOM_LEVER_SUPPORTED = 1;
    private static final String TAG = "ZoomBar";
    private static final String UPDATE_LENS_INFO = "updateLensInfo";
    private ImageView mBaseIcon;
    private int[] mBaseIconIds;
    CameraSetting mCameraSetting;
    private int mCategory;
    private int mCurrentFocalLength;
    private int mCurrentMag;
    DigitalZoomController mDigitalZoomController;
    private int mDispPattern;
    private TextView mFocalLengthText;
    private String mFormat;
    private Handler mHandler;
    private boolean mIsDigitalZoomAvailable;
    private boolean mIsDigitalZoomStatus;
    private boolean mIsEnableDigitalZoom;
    private boolean mIsEnableFocalLength;
    private boolean mIsMainFeatureStillImage;
    private boolean mIsWithZoomLever;
    private int mMaxFocalLength;
    private int mMinFocalLength;
    private NotificationListener mNotificationListener;
    private ImageView mPositionIcon;
    private int mPositionMargin;
    private int mPowerZoomStatus;
    private int mViewHight;
    private int mViewWidh;

    public ZoomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentFocalLength = 0;
        this.mMaxFocalLength = 0;
        this.mMinFocalLength = 0;
        this.mNotificationListener = new ZoomListener();
        this.mIsEnableFocalLength = false;
        this.mIsEnableDigitalZoom = false;
        this.mIsWithZoomLever = false;
        this.mIsMainFeatureStillImage = true;
        this.mCategory = 1;
        this.mPowerZoomStatus = 3;
        this.mIsDigitalZoomAvailable = false;
        this.mIsDigitalZoomStatus = false;
        this.mCameraSetting = CameraSetting.getInstance();
        this.mDigitalZoomController = DigitalZoomController.getInstance();
        this.mFormat = getResources().getString(R.string.roamingText1);
        this.mBaseIcon = new ImageView(context, attrs);
        this.mPositionIcon = new ImageView(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, com.sony.imaging.app.base.R.styleable.ZoomBar);
        this.mBaseIconIds = new int[2];
        this.mBaseIconIds[0] = attr.getResourceId(0, -1);
        this.mBaseIconIds[1] = attr.getResourceId(1, -1);
        Drawable position = attr.getDrawable(2);
        this.mPositionMargin = attr.getDimensionPixelSize(3, 0);
        this.mPositionIcon.setImageDrawable(position);
        RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(-2, -2);
        ViewGroup.LayoutParams iconLayout = new RelativeLayout.LayoutParams(-2, -2);
        baseLayout.addRule(10);
        baseLayout.addRule(9);
        addView(this.mBaseIcon, baseLayout);
        addView(this.mPositionIcon, iconLayout);
        this.mHandler = new Handler() { // from class: com.sony.imaging.app.base.shooting.widget.ZoomBar.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                ZoomBar.this.updateVisibility(4);
            }
        };
        Log.i("ZoomBar", "ZoomBar");
    }

    protected void refresh() {
        this.mCurrentFocalLength = this.mCameraSetting.getFocalLength();
        this.mCurrentMag = this.mDigitalZoomController.getCurrentDigitalZoomMagnification();
        this.mIsDigitalZoomAvailable = this.mDigitalZoomController.isZoomAvailable();
        this.mIsDigitalZoomStatus = this.mDigitalZoomController.isDigitalZoomStatus();
        Log.i("ZoomBar", LOG_MSG_REFRESH);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isReady() {
        boolean ret = false;
        switch (this.mDispPattern) {
            case 0:
            case 3:
            case 4:
                if ((this.mMaxFocalLength > 0 || this.mMaxFocalLength > 0 || this.mMaxFocalLength >= this.mMinFocalLength) && 1 == this.mPowerZoomStatus && this.mCurrentFocalLength >= this.mMinFocalLength && this.mMaxFocalLength >= this.mCurrentFocalLength && this.mCurrentFocalLength != 0 && this.mIsEnableFocalLength) {
                    if (!this.mIsDigitalZoomAvailable) {
                        ret = true;
                        break;
                    } else if (this.mIsEnableDigitalZoom) {
                        ret = true;
                        break;
                    }
                }
                break;
            case 1:
                if (this.mIsEnableFocalLength) {
                    if (!this.mIsDigitalZoomAvailable) {
                        ret = true;
                        break;
                    } else if (this.mIsEnableDigitalZoom) {
                        ret = true;
                        break;
                    }
                }
                break;
            case 2:
                if (this.mIsEnableDigitalZoom && this.mIsDigitalZoomStatus) {
                    ret = true;
                    break;
                }
                break;
        }
        Log.i("ZoomBar", LOG_MSG_ISREADY);
        return ret;
    }

    public static boolean isPFverOver2() {
        if (2 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return true;
    }

    protected void updateDisplayPattern() {
        int dispPattern;
        if (this.mIsMainFeatureStillImage) {
            if (1 == this.mCategory) {
                if (this.mIsDigitalZoomAvailable) {
                    if (ExecutorCreator.getInstance().isSpinalZoom()) {
                        if (1 == this.mPowerZoomStatus) {
                            dispPattern = 0;
                        } else {
                            dispPattern = 2;
                        }
                    } else {
                        dispPattern = 3;
                    }
                } else {
                    dispPattern = 3;
                }
            } else if (this.mIsWithZoomLever) {
                dispPattern = 1;
            } else {
                dispPattern = 2;
            }
        } else {
            dispPattern = 4;
        }
        this.mDispPattern = dispPattern;
        Log.i("ZoomBar", "updateDisplayPatterndisp pattern:" + dispPattern);
    }

    protected void updateVisibility(int visible) {
        if (visible != 0 && visible != 4) {
            visible = 4;
        }
        setVisibility(visible);
        if (visible == 0) {
            switch (this.mDispPattern) {
                case 0:
                    this.mBaseIcon.setImageResource(this.mBaseIconIds[1]);
                    this.mBaseIcon.setVisibility(0);
                    this.mPositionIcon.setVisibility(0);
                    this.mFocalLengthText.setVisibility(0);
                    break;
                case 1:
                    if (100 == DigitalZoomController.getInstance().getMaxDigitalZoomMagnification(DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION)) {
                        this.mBaseIcon.setImageResource(this.mBaseIconIds[0]);
                    } else {
                        this.mBaseIcon.setImageResource(this.mBaseIconIds[1]);
                    }
                    this.mBaseIcon.setVisibility(0);
                    this.mPositionIcon.setVisibility(0);
                    this.mFocalLengthText.setVisibility(4);
                    break;
                case 2:
                case 4:
                    this.mBaseIcon.setImageResource(this.mBaseIconIds[0]);
                    this.mBaseIcon.setVisibility(0);
                    this.mPositionIcon.setVisibility(0);
                    this.mFocalLengthText.setVisibility(4);
                    break;
                case 3:
                    this.mBaseIcon.setImageResource(this.mBaseIconIds[0]);
                    this.mBaseIcon.setVisibility(0);
                    this.mPositionIcon.setVisibility(0);
                    this.mFocalLengthText.setVisibility(0);
                    break;
                default:
                    this.mBaseIcon.setVisibility(4);
                    this.mPositionIcon.setVisibility(4);
                    this.mFocalLengthText.setVisibility(4);
                    break;
            }
        }
        Log.i("ZoomBar", LOG_MSG_UPDATEVISIBILITY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisplayInformation() {
        double point = 0.0d;
        double pos_length = this.mPositionIcon.getWidth();
        int available_length = this.mBaseIcon.getWidth();
        int margin = this.mPositionMargin;
        switch (this.mDispPattern) {
            case 0:
                point = calcIndicatorPositionByMultiple();
                if (this.mFocalLengthText != null) {
                    int focallength = this.mCurrentFocalLength / 10;
                    String str = String.format(this.mFormat, Integer.valueOf(focallength));
                    this.mFocalLengthText.setText(str);
                    break;
                }
                break;
            case 1:
                if (100 == DigitalZoomController.getInstance().getMaxDigitalZoomMagnification(DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION)) {
                    point = calcIndicatorPositionBySingle(100, 0, this.mDigitalZoomController.getOpticalZoomPosition(), (available_length / 2) - margin);
                    break;
                } else {
                    point = calcIndicatorPositionBySingle(200, 0, this.mDigitalZoomController.getDigitalZoomPosition() + this.mDigitalZoomController.getOpticalZoomPosition(), available_length - (margin * 2));
                    break;
                }
            case 2:
                point = calcIndicatorPositionBySingle(this.mDigitalZoomController.getMaxDigitalZoomMagnification(null), 100, this.mCurrentMag, (available_length / 2) - margin);
                break;
            case 3:
                point = calcIndicatorPositionBySingle(this.mMaxFocalLength, this.mMinFocalLength, this.mCurrentFocalLength, (available_length / 2) - margin);
                if (this.mFocalLengthText != null) {
                    int focallength2 = this.mCurrentFocalLength / 10;
                    String str2 = String.format(this.mFormat, Integer.valueOf(focallength2));
                    this.mFocalLengthText.setText(str2);
                    break;
                }
                break;
            case 4:
                point = calcIndicatorPositionBySingle(100, 0, this.mDigitalZoomController.getOpticalZoomPosition(), (available_length / 2) - margin);
                break;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.mPositionIcon.getLayoutParams();
        params.setMargins((int) point, 0, this.mViewWidh - ((int) (point + pos_length)), this.mViewHight - this.mPositionIcon.getHeight());
        this.mPositionIcon.setLayoutParams(params);
        Log.i("ZoomBar", LOG_MSG_UPDATEDISPLAYINFORMATION);
    }

    private double calcIndicatorPositionBySingle(int maxValue, int minValue, int currentValue, int bar_length) {
        int margin = this.mPositionMargin;
        double pos_length = this.mPositionIcon.getWidth();
        double point = currentValue - minValue;
        int range = maxValue - minValue;
        return ((point / range) * (bar_length - pos_length)) + margin + 0.5d;
    }

    private double calcIndicatorPositionByMultiple() {
        int margin = this.mPositionMargin;
        int base_width = (this.mBaseIcon.getWidth() / 2) - margin;
        double width = this.mPositionIcon.getWidth();
        if (this.mDigitalZoomController.isDigitalZoomMagOverInitValue()) {
            double point = this.mCurrentMag - 100;
            int range = this.mDigitalZoomController.getMaxDigitalZoomMagnification(null) - 100;
            return ((point / range) * (base_width - width)) + (this.mBaseIcon.getWidth() / 2) + 0.5d;
        }
        double point2 = this.mCurrentFocalLength - this.mMinFocalLength;
        int range2 = this.mMaxFocalLength - this.mMinFocalLength;
        return ((point2 / range2) * (base_width - width)) + margin + 0.5d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLensInfo() {
        CameraEx.LensInfo lensInfo = this.mCameraSetting.getLensInfo();
        if (lensInfo == null) {
            this.mMinFocalLength = 0;
            this.mMaxFocalLength = 0;
            Log.i("ZoomBar", "updateLensInfo: Lens is dettached");
        } else {
            CameraEx.WideTele wideTele = lensInfo.FocalLength;
            this.mMinFocalLength = wideTele.wide;
            this.mMaxFocalLength = wideTele.tele;
            Log.i("ZoomBar", UPDATE_LENS_INFO);
        }
    }

    /* loaded from: classes.dex */
    private class ZoomListener implements NotificationListener {
        private final String[] tags;

        private ZoomListener() {
            this.tags = new String[]{CameraNotificationManager.FOCAL_LENGTH_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.POWER_ZOOM_CHANGED, CameraNotificationManager.ZOOM_INFO_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.i("ZoomBar", "onNotify: " + tag);
            ZoomBar.this.refresh();
            if (CameraNotificationManager.FOCAL_LENGTH_CHANGED.equals(tag)) {
                if (!ZoomBar.this.mIsEnableFocalLength) {
                    ZoomBar.this.mIsEnableFocalLength = true;
                    return;
                }
            } else if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                if (!ZoomBar.this.mIsEnableFocalLength) {
                    ZoomBar.this.mIsEnableFocalLength = true;
                    return;
                } else if (!ZoomBar.this.mIsEnableDigitalZoom) {
                    ZoomBar.this.mIsEnableDigitalZoom = true;
                    return;
                }
            } else if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag) || CameraNotificationManager.POWER_ZOOM_CHANGED.equals(tag)) {
                ZoomBar.this.mIsEnableFocalLength = false;
                ZoomBar.this.mIsEnableDigitalZoom = false;
                ZoomBar.this.mPowerZoomStatus = ZoomBar.this.mCameraSetting.getPowerZoomStatus();
                ZoomBar.this.updateLensInfo();
                ZoomBar.this.updateDisplayPattern();
            }
            if (ZoomBar.this.isReady()) {
                ZoomBar.this.updateVisibility(0);
                ZoomBar.this.updateDisplayInformation();
                ZoomBar.this.startDisplayingTimer();
            } else {
                ZoomBar.this.updateVisibility(4);
                ZoomBar.this.stopDisplayingTimer();
            }
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
            int mainfeature = ScalarProperties.getInt(PROP_UI_MAIN_FEATURE);
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
        Log.i("ZoomBar", LOG_MSG_ON_ATTACHED_TO_WINDOW);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mFocalLengthText = (TextView) findViewById(com.sony.imaging.app.base.R.id.focalLength);
        this.mViewHight = getHeight();
        this.mViewWidh = getWidth();
        Log.i("ZoomBar", LOG_MSG_ON_FINISH_INFLATE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.removeNotificationListener(this.mNotificationListener);
        stopDisplayingTimer();
        updateVisibility(4);
        Log.i("ZoomBar", LOG_MSG_ON_DETTACHED_TO_WINDOW);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDisplayingTimer() {
        stopDisplayingTimer();
        this.mHandler.sendEmptyMessageDelayed(0, 2000L);
        Log.i("ZoomBar", LOG_MSG_START_DISPLAYING_TIMER);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopDisplayingTimer() {
        this.mHandler.removeMessages(0);
        Log.i("ZoomBar", LOG_MSG_STOP_DISPLAYING_TIMER);
    }
}
