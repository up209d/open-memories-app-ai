package com.sony.imaging.app.graduatedfilter.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BorderView extends View {
    private static final String BASE = "base";
    private static final String BORDER = "main";
    private static final String COMMON = "common";
    private static final String FILTER = "filter";
    private static final float IMAGE_HEIGHT_RATIO = 0.5f;
    private static final int MAIN_LINE_DOT_EDGE_WIDTH = 2;
    private static final int MAIN_LINE_DOT_INTERNAL_WIDTH = 10;
    private static final int MAIN_LINE_DOT_INTEVAL = 30;
    private static final int MAIN_LINE_DOT_WIDTH = 14;
    private static final int MAIN_LINE_ONE_SET = 44;
    private static final String NORMAL = "normal";
    private static final String UNKNOWN = "unknown";
    private static NotificationListener mListener;
    private static GFEffectParameters.Parameters mParams;
    private int EE_height;
    private int EE_width;
    private final String TAG;
    private Bitmap mCenterIcon;
    private DashPathEffect mDashPath;
    private DashPathEffect mDashPathEdge;
    private int mMainColor;
    private String mMode;
    private Paint mPaintCenterDash;
    private Paint mPaintCenterDashEdge;
    private Paint mPaintShading;
    private Paint mPaintShadingEdge;
    private Resources mResources;
    private String mSetting;
    private DashPathEffect mShadingDashPath;
    private DashPathEffect mShadingDashPathEdge;
    private int mSubColor;

    private void prepareResources() {
        this.mDashPath = new DashPathEffect(new float[]{10.0f, 34.0f}, 0.0f);
        this.mDashPathEdge = new DashPathEffect(new float[]{14.0f, 30.0f}, 0.0f);
        this.mShadingDashPath = new DashPathEffect(new float[]{4.0f, 40.0f}, 0.0f);
        this.mShadingDashPathEdge = new DashPathEffect(new float[]{8.0f, 36.0f}, 0.0f);
        this.mPaintCenterDashEdge = new Paint();
        this.mPaintCenterDashEdge.setARGB(BatteryIcon.BATTERY_STATUS_CHARGING, 0, 0, 0);
        this.mPaintCenterDashEdge.setStyle(Paint.Style.STROKE);
        this.mPaintCenterDashEdge.setStrokeWidth(6.0f);
        this.mPaintCenterDashEdge.setAntiAlias(true);
        this.mPaintCenterDashEdge.setPathEffect(this.mDashPathEdge);
        this.mPaintCenterDash = new Paint();
        this.mPaintCenterDash.setColor(this.mMainColor);
        this.mPaintCenterDash.setStyle(Paint.Style.STROKE);
        this.mPaintCenterDash.setStrokeWidth(2.0f);
        this.mPaintCenterDash.setAntiAlias(true);
        this.mPaintCenterDash.setPathEffect(this.mDashPath);
        this.mPaintShadingEdge = new Paint();
        this.mPaintShadingEdge.setARGB(BatteryIcon.BATTERY_STATUS_CHARGING, 0, 0, 0);
        this.mPaintShadingEdge.setStyle(Paint.Style.STROKE);
        this.mPaintShadingEdge.setStrokeWidth(6.0f);
        this.mPaintShadingEdge.setAntiAlias(true);
        this.mPaintShadingEdge.setPathEffect(this.mShadingDashPathEdge);
        this.mPaintShading = new Paint();
        this.mPaintShading.setColor(this.mSubColor);
        this.mPaintShading.setStyle(Paint.Style.STROKE);
        this.mPaintShading.setStrokeWidth(2.0f);
        this.mPaintShading.setAntiAlias(true);
        this.mPaintShading.setPathEffect(this.mShadingDashPath);
    }

    private void setCenterIcons() {
        String setting = this.mSetting;
        if ("unknown".equalsIgnoreCase(this.mSetting)) {
            if (GFCommonUtil.getInstance().isFilterSetting()) {
                setting = FILTER;
            } else {
                setting = BASE;
            }
        }
        if (BASE.equalsIgnoreCase(setting)) {
            if (GFCommonUtil.getInstance().isBaseCameraSettings()) {
                this.mCenterIcon = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_hgf_tricorn_icon);
                return;
            } else {
                this.mCenterIcon = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_hgf_tricorn_icon_selected);
                return;
            }
        }
        this.mCenterIcon = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_hgf_tricorn_icon_selected);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        Rect clipRect = new Rect(videoRect.pxLeft, videoRect.pxTop, videoRect.pxRight, videoRect.pxBottom);
        canvas.clipRect(clipRect);
        canvas.save();
        setCenterIcons();
        mParams = GFEffectParameters.getInstance().getParameters();
        int shadingWidth = getShadingWidth(mParams.getStrength());
        float OSDPoint_x = mParams.getOSDPoint().x;
        float OSDPoint_y = mParams.getOSDPoint().y;
        float degree = Float.valueOf(mParams.getDegree()).floatValue();
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            degree = -degree;
        }
        if ((DisplayModeObserver.getInstance().getActiveDevice() == 0 || DisplayModeObserver.getInstance().getActiveDevice() == 2) && ScalarProperties.getInt("device.panel.aspect") == 169) {
            double rad = (degree * 3.141592653589793d) / 180.0d;
            double hanten = 0.0d;
            if (GFCommonUtil.getInstance().isReversedDisplay()) {
                if ((-degree) > 90.0f && (-degree) <= 270.0f) {
                    hanten = 0.0d + 180.0d;
                }
            } else if (degree > 90.0f && degree <= 270.0f) {
                hanten = 0.0d + 180.0d;
            }
            double target = Math.atan(1.3333333333333333d * Math.tan(rad));
            degree = (float) (((180.0d * target) / 3.141592653589793d) + hanten);
        }
        canvas.rotate(degree, OSDPoint_x, OSDPoint_y);
        int startX = (((((this.EE_width * 4) / MAIN_LINE_ONE_SET) + 1) * MAIN_LINE_ONE_SET) + 29) - 1;
        int disp_x = startX - ((int) OSDPoint_x);
        int disp_y = (int) OSDPoint_y;
        int iconWidth = (this.mCenterIcon.getWidth() / 2) + 0;
        int iconHeight = (this.mCenterIcon.getHeight() / 2) + 0;
        this.mPaintCenterDash.setColor(this.mMainColor);
        canvas.drawLine(-disp_x, disp_y, disp_x, disp_y, this.mPaintCenterDashEdge);
        if ("normal".equalsIgnoreCase(this.mMode)) {
            canvas.drawLine(3 - disp_x, disp_y + shadingWidth, disp_x, disp_y + shadingWidth, this.mPaintShadingEdge);
            canvas.drawLine(3 - disp_x, disp_y - shadingWidth, disp_x, disp_y - shadingWidth, this.mPaintShadingEdge);
            canvas.drawLine((3 - disp_x) + 2, disp_y + shadingWidth, disp_x, disp_y + shadingWidth, this.mPaintShading);
            canvas.drawLine((3 - disp_x) + 2, disp_y - shadingWidth, disp_x, disp_y - shadingWidth, this.mPaintShading);
        }
        canvas.drawLine((-disp_x) + 2, disp_y, disp_x, disp_y, this.mPaintCenterDash);
        canvas.drawBitmap(this.mCenterIcon, OSDPoint_x - iconWidth, OSDPoint_y - iconHeight, this.mPaintCenterDash);
        canvas.restore();
        super.onDraw(canvas);
    }

    private int getShadingWidth(int level) {
        int ratio = (int) ((GFEffectParameters.Parameters.mShadingWidthTable[(GFEffectParameters.Parameters.mShadingWidthTable.length - 1) - level] + 1) * IMAGE_HEIGHT_RATIO);
        int width = ((this.EE_height / 2) * ratio) / 100;
        return width;
    }

    protected void refresh() {
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onAttachedToWindow();
        GFEffectParameters.getInstance().setNotificationListener(mListener);
        DisplayModeObserver.getInstance().setNotificationListener(mListener);
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        this.EE_width = videoRect.pxRight - videoRect.pxLeft;
        this.EE_height = videoRect.pxBottom - videoRect.pxTop;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDetachedFromWindow();
        GFEffectParameters.getInstance().removeNotificationListener(mListener);
        DisplayModeObserver.getInstance().removeNotificationListener(mListener);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void clearParams() {
        this.mDashPath = null;
        this.mDashPathEdge = null;
        this.mMode = null;
        this.mPaintCenterDash = null;
        this.mPaintShading = null;
        if (this.mCenterIcon != null) {
            this.mCenterIcon.recycle();
            this.mCenterIcon = null;
        }
    }

    public BorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.mMode = null;
        this.mDashPath = null;
        this.mDashPathEdge = null;
        this.mShadingDashPath = null;
        this.mShadingDashPathEdge = null;
        this.mCenterIcon = null;
        this.mMainColor = 0;
        this.mSubColor = 0;
        this.mSetting = null;
        clearParams();
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.BorderView);
        this.mMode = attribute.getString(0);
        if (this.mMode == null) {
            this.mMode = "normal";
        }
        this.mMainColor = attribute.getColor(1, -2236963);
        this.mSubColor = attribute.getColor(2, -2236963);
        this.mSetting = attribute.getString(3);
        if (this.mSetting == null) {
            this.mSetting = "unknown";
        }
        this.mResources = context.getResources();
        prepareResources();
        mListener = new NotificationListener() { // from class: com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView.1
            private final String[] TAG = {GFEffectParameters.TAG_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DEVICE_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAG;
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                BorderView.this.EE_width = videoRect.pxRight - videoRect.pxLeft;
                BorderView.this.EE_height = videoRect.pxBottom - videoRect.pxTop;
                BorderView.this.invalidate();
                AppLog.info("BorderView", "onNotify tag = " + tag);
            }
        };
        mParams = null;
    }
}
