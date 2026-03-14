package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.List;

/* loaded from: classes.dex */
public class DigitalZoomSettingView extends ActiveLayout {
    private static final String LOG_MSG_UPDATE_BACKGROUND_BAR_INFOMATION = "updateBackGroundBarInformation";
    private static final String TAG = "DigitalZoomSettingView";
    private static final int ZOOM_BAR_1RANGE = 1;
    private static final int ZOOM_BAR_2RANGE = 2;
    private static final int ZOOM_BAR_COUNT = 3;
    private static final int ZOOM_BAR_NORANGE = 0;
    private static final int ZOOM_CLEAR_IMAGE = 2;
    private static final int ZOOM_OPTICAL = 0;
    private static final int ZOOM_PRECISION = 3;
    private static final int ZOOM_SMART = 1;
    private static final int ZOOM_TYPE_NUM = 4;
    private int mBar_margin_left;
    private int mBar_margin_top;
    CameraSetting mCameraSetting;
    private int mCurrentZoomMag;
    private DigitalZoomController mDigitalZoomController;
    private ImageView mDigitalZoomIconView;
    private String mFormat;
    private int mIndicatorMargin;
    private boolean mIsVisibleOnMovieRecording;
    private ImageView mLeftArrowView;
    private NotificationListener mNotificationListener;
    private ImageView mPositionIconView;
    private int mPositionMargin;
    private ImageView mRightArrowView;
    boolean mZoomAvailable;
    private int mZoomBarLength;
    private int[] mZoomIconResIds;
    private int[] mZoomMag;
    private int mZoomMagMaxValue;
    private int mZoomMagMinValue;
    private TextView mZoomMagnificationTextView;
    private int[] mZoomRangeBarIds;
    private ImageView mZoomRangeBarView;
    private int mZoomTypeNum;

    public DigitalZoomSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsVisibleOnMovieRecording = true;
        this.mZoomAvailable = false;
        this.mNotificationListener = null;
        this.mCameraSetting = CameraSetting.getInstance();
        this.mDigitalZoomController = DigitalZoomController.getInstance();
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.DigitalZoomSettingView);
        this.mFormat = getResources().getString(android.R.string.region_picker_section_all);
        this.mDigitalZoomIconView = new ImageView(context, attrs);
        this.mLeftArrowView = new ImageView(context, attrs);
        this.mRightArrowView = new ImageView(context, attrs);
        this.mZoomRangeBarView = new ImageView(context, attrs);
        this.mPositionIconView = new ImageView(context, attrs);
        this.mZoomMagnificationTextView = new TextView(context, attrs);
        this.mZoomRangeBarIds = new int[3];
        this.mZoomRangeBarIds[0] = attr.getResourceId(6, -1);
        this.mZoomRangeBarIds[1] = attr.getResourceId(7, -1);
        this.mZoomRangeBarIds[2] = attr.getResourceId(8, -1);
        this.mZoomMag = new int[4];
        this.mZoomIconResIds = new int[4];
        this.mZoomIconResIds[0] = attr.getResourceId(0, -1);
        this.mZoomIconResIds[1] = attr.getResourceId(1, -1);
        this.mZoomIconResIds[2] = attr.getResourceId(2, -1);
        this.mZoomIconResIds[3] = attr.getResourceId(3, -1);
        RelativeLayout.LayoutParams indicatorLayout = new RelativeLayout.LayoutParams(-2, -2);
        addView(this.mPositionIconView, indicatorLayout);
        this.mPositionMargin = attr.getDimensionPixelSize(9, 0);
        this.mBar_margin_left = attr.getDimensionPixelSize(12, 0);
        this.mBar_margin_top = attr.getDimensionPixelSize(11, 0);
        this.mZoomBarLength = attr.getDimensionPixelSize(10, 0);
        this.mIndicatorMargin = attr.getDimensionPixelSize(13, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateVisibility();
        updateBackGroundBarInformation();
        refresh();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVisibility() {
        boolean isVisibleOnMovieRecording = true;
        setVisibility(0);
        if (2 == this.mCameraSetting.getCurrentMode()) {
            List<String> supList = AntiHandBlurController.getInstance().getSupportedValue("movie");
            if (supList != null && supList.contains(AntiHandBlurController.MOVIE_ACTIVE)) {
                isVisibleOnMovieRecording = false;
            }
            if (!isVisibleOnMovieRecording) {
                this.mLeftArrowView.setVisibility(8);
                this.mRightArrowView.setVisibility(8);
                this.mDigitalZoomIconView.setVisibility(8);
                this.mZoomMagnificationTextView.setVisibility(8);
            }
        }
        this.mIsVisibleOnMovieRecording = isVisibleOnMovieRecording;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBackGroundBarInformation() {
        Log.i(TAG, LOG_MSG_UPDATE_BACKGROUND_BAR_INFOMATION);
        this.mZoomMagMaxValue = this.mDigitalZoomController.getMaxDigitalZoomMagnification(null);
        this.mZoomMagMinValue = 100;
        int smartZoomMaxValue = this.mDigitalZoomController.getMaxDigitalZoomMagnification(DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART);
        int clearZoomMaxValue = this.mDigitalZoomController.getMaxDigitalZoomMagnification(DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION);
        int precisionZoomMaxValue = this.mDigitalZoomController.getMaxDigitalZoomMagnification(DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION);
        this.mZoomMag[0] = 100;
        this.mZoomMag[1] = smartZoomMaxValue;
        this.mZoomMag[2] = clearZoomMaxValue;
        this.mZoomMag[3] = precisionZoomMaxValue;
        this.mZoomTypeNum = 0;
        if (this.mZoomMag.length != 0) {
            for (int i = 1; i < this.mZoomMag.length; i++) {
                int magdiff = this.mZoomMag[i] - this.mZoomMag[i - 1];
                if (magdiff != 0) {
                    this.mZoomTypeNum++;
                }
            }
        }
        if (this.mZoomTypeNum == 1) {
            this.mZoomRangeBarView.setImageResource(this.mZoomRangeBarIds[0]);
            return;
        }
        if (this.mZoomTypeNum == 2) {
            this.mZoomRangeBarView.setImageResource(this.mZoomRangeBarIds[1]);
        } else if (this.mZoomTypeNum == 3) {
            this.mZoomRangeBarView.setImageResource(this.mZoomRangeBarIds[2]);
        } else {
            this.mZoomRangeBarView.setImageResource(this.mZoomIconResIds[0]);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDigitalZoomIconView = (ImageView) findViewById(R.id.zoom_mode_icon);
        this.mLeftArrowView = (ImageView) findViewById(R.id.zoom_left_arrow);
        this.mRightArrowView = (ImageView) findViewById(R.id.zoom_right_arrow);
        this.mZoomRangeBarView = (ImageView) findViewById(R.id.zoom_range_bar);
        this.mPositionIconView = (ImageView) findViewById(R.id.zoom_range_position_icon);
        this.mZoomMagnificationTextView = (TextView) findViewById(R.id.zoom_mag_text);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.DigitalZoomSettingView.1
                private final String[] tags = {CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, CameraNotificationManager.MOVIE_REC_STOP};

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.tags;
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                        DigitalZoomSettingView.this.refresh();
                        return;
                    }
                    if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag) || CameraNotificationManager.MOVIE_REC_START_SUCCEEDED.equals(tag) || CameraNotificationManager.MOVIE_REC_STOP.equals(tag)) {
                        DigitalZoomSettingView.this.updateVisibility();
                        DigitalZoomSettingView.this.updateBackGroundBarInformation();
                        DigitalZoomSettingView.this.refresh();
                    }
                }
            };
        }
        return this.mNotificationListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        this.mCurrentZoomMag = this.mDigitalZoomController.getCurrentDigitalZoomMagnification();
        updateDigitalZoomIconAndMagText();
        updatePositionOnZoomBar();
    }

    private void updateDigitalZoomIconAndMagText() {
        if (this.mIsVisibleOnMovieRecording) {
            String digitalZoomStatus = this.mDigitalZoomController.getCurrentDigitalZoomMode();
            if (DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART.equals(digitalZoomStatus)) {
                this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[1]);
            } else if (DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION.equals(digitalZoomStatus)) {
                this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[2]);
            } else if (DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION.equals(digitalZoomStatus)) {
                this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[3]);
            } else if (DigitalZoomController.TAG_DIGITAL_ZOOM_MODE_NOT_SET.equals(digitalZoomStatus)) {
                this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[0]);
            }
            String magText = String.format(this.mFormat, this.mDigitalZoomController.getCurrentDigitalZoomMagnificationText());
            this.mZoomMagnificationTextView.setText(magText);
            if (this.mCurrentZoomMag == this.mZoomMagMinValue) {
                this.mLeftArrowView.setVisibility(4);
                this.mRightArrowView.setVisibility(0);
            } else if (this.mCurrentZoomMag == this.mZoomMagMaxValue) {
                this.mLeftArrowView.setVisibility(0);
                this.mRightArrowView.setVisibility(4);
            } else {
                this.mLeftArrowView.setVisibility(0);
                this.mRightArrowView.setVisibility(0);
            }
        }
    }

    private void updatePositionOnZoomBar() {
        float point = 0.0f;
        if (100 == this.mCurrentZoomMag || this.mZoomTypeNum == 0) {
            point = 0.0f;
        } else {
            int numOfRange = -1;
            int indexID = 0;
            int zoomBarAvailableLength = (this.mZoomBarLength - (this.mPositionMargin * 2)) - this.mIndicatorMargin;
            int lengthPerDividedRange = zoomBarAvailableLength / this.mZoomTypeNum;
            if (lengthPerDividedRange > 0) {
                for (int i = 0; i < this.mZoomMag.length - 1; i++) {
                    if (this.mZoomMag[i] < this.mCurrentZoomMag && this.mCurrentZoomMag <= this.mZoomMag[i + 1]) {
                        indexID = i;
                    }
                }
                if (indexID < this.mZoomMag.length - 1) {
                    for (int i2 = 0; i2 <= indexID; i2++) {
                        if (this.mZoomMag[i2 + 1] - this.mZoomMag[i2] > 0) {
                            numOfRange++;
                        }
                    }
                }
                if (numOfRange >= 0) {
                    int magStepNumPerDividedRange = (this.mZoomMag[indexID + 1] - this.mZoomMag[indexID]) / 10;
                    if (magStepNumPerDividedRange > 0) {
                        float magStepLength = lengthPerDividedRange / magStepNumPerDividedRange;
                        point = (lengthPerDividedRange * numOfRange) + (((this.mCurrentZoomMag - this.mZoomMag[indexID]) / 10) * magStepLength);
                    } else {
                        return;
                    }
                }
            }
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.mPositionIconView.getLayoutParams();
        int org_x = this.mBar_margin_left + ((int) point) + this.mPositionMargin;
        int dst_x = this.mBar_margin_left + ((int) point) + this.mPositionMargin + this.mPositionIconView.getWidth();
        int org_y = this.mBar_margin_top;
        int dst_y = this.mBar_margin_top + this.mPositionIconView.getHeight();
        params.setMargins(org_x, org_y, dst_x, dst_y);
        this.mPositionIconView.setLayoutParams(params);
    }
}
