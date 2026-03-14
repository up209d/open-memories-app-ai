package com.sony.imaging.app.digitalfilter.shooting.widget;

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
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerShadingLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustmentLayout;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BorderView extends View {
    private static final float IMAGE_HEIGHT_RATIO = 0.5f;
    private static final int MAIN_LINE_DOT_EDGE_WIDTH = 1;
    private static final int MAIN_LINE_DOT_INTERNAL_WIDTH = 14;
    private static final int MAIN_LINE_DOT_INTEVAL = 28;
    private static final int MAIN_LINE_DOT_WIDTH = 16;
    private static final int MAIN_LINE_ONE_SET = 44;
    private static final String NORMAL = "normal";
    private static final String UNKNOWN = "unknown";
    private static NotificationListener mListener;
    private static Paint mPaintCenterDash;
    private static Paint mPaintCenterDash1;
    private static Paint mPaintCenterDashEdge;
    private static Paint mPaintCenterDashEdge1;
    private static Paint mPaintShading;
    private static Paint mPaintShadingEdge;
    private static NotificationListener mSkyLandListener;
    private int EE_height;
    private int EE_width;
    private Bitmap mCenterIcon;
    private Bitmap mLandNormal;
    private Bitmap mLowerSelected;
    private int mMainColor;
    private String mMode;
    private Resources mResources;
    private String mSetting;
    private Bitmap mSkyNormal;
    private int mSubColor;
    private Bitmap mUpperSelected;
    private static final String TAG = AppLog.getClassName();
    private static int WHITE_DOT = -2236963;
    private static int GRAY_DOT = -6710887;
    private static int CINNABAR_DOT = -30720;
    private static DashPathEffect mDashPath = null;
    private static DashPathEffect mDashPathEdge = null;
    private static DashPathEffect mDashPath1 = null;
    private static DashPathEffect mDashPathEdge1 = null;
    private static DashPathEffect mShadingDashPath = null;
    private static DashPathEffect mShadingDashPathEdge = null;

    private void prepareResources() {
        mDashPath = new DashPathEffect(new float[]{14.0f, 30.0f}, 0.0f);
        mDashPathEdge = new DashPathEffect(new float[]{16.0f, 28.0f}, 0.0f);
        mDashPath1 = new DashPathEffect(new float[]{14.0f, 30.0f}, 0.0f);
        mDashPathEdge1 = new DashPathEffect(new float[]{16.0f, 28.0f}, 0.0f);
        mShadingDashPath = new DashPathEffect(new float[]{6.0f, 38.0f}, 0.0f);
        mShadingDashPathEdge = new DashPathEffect(new float[]{8.0f, 36.0f}, 0.0f);
        mPaintCenterDashEdge = new Paint();
        mPaintCenterDashEdge.setARGB(153, 0, 0, 0);
        mPaintCenterDashEdge.setStyle(Paint.Style.STROKE);
        mPaintCenterDashEdge.setStrokeWidth(6.0f);
        mPaintCenterDashEdge.setAntiAlias(true);
        mPaintCenterDashEdge.setPathEffect(mDashPathEdge);
        mPaintCenterDash = new Paint();
        mPaintCenterDash.setColor(this.mMainColor);
        mPaintCenterDash.setStyle(Paint.Style.STROKE);
        mPaintCenterDash.setStrokeWidth(2.0f);
        mPaintCenterDash.setAntiAlias(true);
        mPaintCenterDash.setPathEffect(mDashPath);
        mPaintCenterDashEdge1 = new Paint();
        mPaintCenterDashEdge1.setARGB(153, 0, 0, 0);
        mPaintCenterDashEdge1.setStyle(Paint.Style.STROKE);
        mPaintCenterDashEdge1.setStrokeWidth(6.0f);
        mPaintCenterDashEdge1.setAntiAlias(true);
        mPaintCenterDashEdge1.setPathEffect(mDashPathEdge1);
        mPaintCenterDash1 = new Paint();
        mPaintCenterDash1.setColor(GRAY_DOT);
        mPaintCenterDash1.setStyle(Paint.Style.STROKE);
        mPaintCenterDash1.setStrokeWidth(2.0f);
        mPaintCenterDash1.setAntiAlias(true);
        mPaintCenterDash1.setPathEffect(mDashPath1);
        mPaintShadingEdge = new Paint();
        mPaintShadingEdge.setARGB(153, 0, 0, 0);
        mPaintShadingEdge.setStyle(Paint.Style.STROKE);
        mPaintShadingEdge.setStrokeWidth(6.0f);
        mPaintShadingEdge.setAntiAlias(true);
        mPaintShadingEdge.setPathEffect(mShadingDashPathEdge);
        mPaintShading = new Paint();
        mPaintShading.setColor(this.mSubColor);
        mPaintShading.setStyle(Paint.Style.STROKE);
        mPaintShading.setStrokeWidth(2.0f);
        mPaintShading.setAntiAlias(true);
        mPaintShading.setPathEffect(mShadingDashPath);
        this.mLowerSelected = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_skyhdr_boundary_center_land_selected);
        this.mLandNormal = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_skyhdr_boundary_center_land_normal);
        this.mUpperSelected = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_skyhdr_boundary_center_sky_selected);
        this.mSkyNormal = BitmapFactory.decodeResource(this.mResources, R.drawable.p_16_dd_parts_skyhdr_boundary_center_sky_normal);
    }

    private void setCenterIcons() {
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            if (GFCommonUtil.getInstance().isLand()) {
                this.mCenterIcon = this.mLowerSelected;
                return;
            } else {
                this.mCenterIcon = this.mUpperSelected;
                return;
            }
        }
        if (GFCommonUtil.getInstance().isAdjustmentSetting()) {
            if (GFAdjustmentLayout.getFocusPosition() <= 1) {
                this.mCenterIcon = this.mUpperSelected;
                return;
            } else {
                this.mCenterIcon = this.mSkyNormal;
                return;
            }
        }
        if (GFCommonUtil.getInstance().getCameraSettingsLayerDuringShots() == -1) {
            if (GFEEAreaController.getInstance().isLand()) {
                this.mCenterIcon = this.mLandNormal;
                return;
            } else {
                this.mCenterIcon = this.mSkyNormal;
                return;
            }
        }
        if (GFCommonUtil.getInstance().getCameraSettingsLayerDuringShots() == 0) {
            this.mCenterIcon = this.mLandNormal;
            GFCommonUtil.getInstance().setBorderId(0);
            return;
        }
        this.mCenterIcon = this.mSkyNormal;
        if (GFCommonUtil.getInstance().getCameraSettingsLayerDuringShots() == 2) {
            GFCommonUtil.getInstance().setBorderId(1);
        } else {
            GFCommonUtil.getInstance().setBorderId(0);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.save();
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        Rect clipRect = new Rect(videoRect.pxLeft, videoRect.pxTop, videoRect.pxRight, videoRect.pxBottom);
        canvas.clipRect(clipRect);
        setCenterIcons();
        int shadingWidth = getShadingWidth(GFCommonUtil.getInstance().getStrength());
        float OSDPoint_x = GFCommonUtil.getInstance().getOSDPoint().x;
        float OSDPoint_y = GFCommonUtil.getInstance().getOSDPoint().y;
        float degree = Float.valueOf(GFCommonUtil.getInstance().getDegree()).floatValue();
        boolean isLayer3Disable = GFCommonUtil.getInstance().isLayer3() && GFFilterSetController.getInstance().getValue().equals(GFFilterSetController.TWO_AREAS);
        if (isLayer3Disable) {
            shadingWidth = getShadingWidth(GFCommonUtil.getInstance().getStrength(0));
            OSDPoint_x = GFCommonUtil.getInstance().getOSDPoint(0).x;
            OSDPoint_y = GFCommonUtil.getInstance().getOSDPoint(0).y;
            degree = Float.valueOf(GFCommonUtil.getInstance().getDegree(0)).floatValue();
        }
        float OSDPoint1_x = 0.0f;
        float OSDPoint1_y = 0.0f;
        float degree1 = 0.0f;
        if (GFFilterSetController.getInstance().need3rdShooting() && !isLayer3Disable) {
            if (GFCommonUtil.getInstance().getBorderId() == 0) {
                OSDPoint1_x = GFCommonUtil.getInstance().getOSDPoint(1).x;
                OSDPoint1_y = GFCommonUtil.getInstance().getOSDPoint(1).y;
                degree1 = Float.valueOf(GFCommonUtil.getInstance().getDegree(1)).floatValue();
            } else if (GFCommonUtil.getInstance().getBorderId() == 1) {
                OSDPoint1_x = GFCommonUtil.getInstance().getOSDPoint(0).x;
                OSDPoint1_y = GFCommonUtil.getInstance().getOSDPoint(0).y;
                degree1 = Float.valueOf(GFCommonUtil.getInstance().getDegree(0)).floatValue();
            }
        }
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            degree = -degree;
            degree1 = -degree1;
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
            if (GFFilterSetController.getInstance().need3rdShooting()) {
                double rad2 = (degree1 * 3.141592653589793d) / 180.0d;
                double hanten2 = 0.0d;
                if (GFCommonUtil.getInstance().isReversedDisplay()) {
                    if ((-degree1) > 90.0f && (-degree1) <= 270.0f) {
                        hanten2 = 0.0d + 180.0d;
                    }
                } else if (degree1 > 90.0f && degree1 <= 270.0f) {
                    hanten2 = 0.0d + 180.0d;
                }
                double target2 = Math.atan(1.3333333333333333d * Math.tan(rad2));
                degree1 = (float) (((180.0d * target2) / 3.141592653589793d) + hanten2);
            }
        }
        canvas.rotate(degree, OSDPoint_x, OSDPoint_y);
        int startX = ((((this.EE_width * 4) / MAIN_LINE_ONE_SET) + 1) * MAIN_LINE_ONE_SET) + 7;
        int disp_x = startX - ((int) OSDPoint_x);
        int disp_y = (int) OSDPoint_y;
        int iconWidth = (this.mCenterIcon.getWidth() / 2) + 0;
        int iconHeight = (this.mCenterIcon.getHeight() / 2) + 0;
        if ((GFCommonUtil.getInstance().isLayerSetting() && GFSettingMenuLayout.getFocusPosition() <= 1) || (GFCommonUtil.getInstance().isAdjustmentSetting() && GFAdjustmentLayout.getFocusPosition() == 0)) {
            mPaintCenterDash.setColor(CINNABAR_DOT);
        } else {
            mPaintCenterDash.setColor(this.mMainColor);
        }
        if ((GFCommonUtil.getInstance().isLayerSetting() && GFSettingMenuLayout.getFocusPosition() == 2) || (GFCommonUtil.getInstance().isAdjustmentSetting() && GFAdjustmentLayout.getFocusPosition() == 1)) {
            mPaintShading.setColor(CINNABAR_DOT);
        } else {
            mPaintShading.setColor(this.mSubColor);
        }
        canvas.drawLine(-disp_x, disp_y, disp_x, disp_y, mPaintCenterDashEdge);
        if ("normal".equals(this.mMode)) {
            canvas.drawLine(3 - disp_x, disp_y + shadingWidth, disp_x, disp_y + shadingWidth, mPaintShadingEdge);
            canvas.drawLine(3 - disp_x, disp_y - shadingWidth, disp_x, disp_y - shadingWidth, mPaintShadingEdge);
            canvas.drawLine((3 - disp_x) + 1, disp_y + shadingWidth, disp_x, disp_y + shadingWidth, mPaintShading);
            canvas.drawLine((3 - disp_x) + 1, disp_y - shadingWidth, disp_x, disp_y - shadingWidth, mPaintShading);
        }
        canvas.drawLine((-disp_x) + 1, disp_y, disp_x, disp_y, mPaintCenterDash);
        if (!isLayer3Disable) {
            canvas.drawBitmap(this.mCenterIcon, OSDPoint_x - iconWidth, OSDPoint_y - iconHeight, mPaintCenterDash);
        }
        canvas.restore();
        if (GFFilterSetController.getInstance().need3rdShooting() && !isLayer3Disable) {
            canvas.save();
            canvas.clipRect(clipRect);
            canvas.rotate(degree1, OSDPoint1_x, OSDPoint1_y);
            int disp_x2 = startX - ((int) OSDPoint1_x);
            int disp_y2 = (int) OSDPoint1_y;
            canvas.drawLine(-disp_x2, disp_y2, disp_x2, disp_y2, mPaintCenterDashEdge1);
            canvas.drawLine((-disp_x2) + 1, disp_y2, disp_x2, disp_y2, mPaintCenterDash1);
            if (GFShadingMenuLayout.isShadingLink || GFSetting15LayerShadingLayout.isShadingLink) {
                if (GFCommonUtil.getInstance().getBorderId() == 0) {
                    shadingWidth = getShadingWidth(GFCommonUtil.getInstance().getStrength(1));
                } else if (GFCommonUtil.getInstance().getBorderId() == 1) {
                    shadingWidth = getShadingWidth(GFCommonUtil.getInstance().getStrength(0));
                }
                mPaintShading.setColor(GRAY_DOT);
                canvas.drawLine(3 - disp_x2, disp_y2 + shadingWidth, disp_x2, disp_y2 + shadingWidth, mPaintShadingEdge);
                canvas.drawLine(3 - disp_x2, disp_y2 - shadingWidth, disp_x2, disp_y2 - shadingWidth, mPaintShadingEdge);
                canvas.drawLine((3 - disp_x2) + 1, disp_y2 + shadingWidth, disp_x2, disp_y2 + shadingWidth, mPaintShading);
                canvas.drawLine((3 - disp_x2) + 1, disp_y2 - shadingWidth, disp_x2, disp_y2 - shadingWidth, mPaintShading);
            }
            canvas.restore();
        }
    }

    private int getShadingWidth(int level) {
        int ratio = (int) ((GFEffectParameters.Parameters.mShadingWidthTable[(GFEffectParameters.Parameters.mShadingWidthTable.length - 1) - level] + 1) * IMAGE_HEIGHT_RATIO);
        int width = ((this.EE_height / 2) * ratio) / 100;
        return width;
    }

    @Override // android.view.View
    public void invalidate() {
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        this.EE_width = videoRect.pxRight - videoRect.pxLeft;
        this.EE_height = videoRect.pxBottom - videoRect.pxTop;
        super.invalidate();
    }

    protected void refresh() {
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onAttachedToWindow();
        CameraNotificationManager.getInstance().setNotificationListener(mListener);
        GFEffectParameters.getInstance().setNotificationListener(mListener);
        DisplayModeObserver.getInstance().setNotificationListener(mListener);
        CameraNotificationManager.getInstance().setNotificationListener(mSkyLandListener);
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        this.EE_width = videoRect.pxRight - videoRect.pxLeft;
        this.EE_height = videoRect.pxBottom - videoRect.pxTop;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onDetachedFromWindow();
        CameraNotificationManager.getInstance().removeNotificationListener(mListener);
        GFEffectParameters.getInstance().removeNotificationListener(mListener);
        DisplayModeObserver.getInstance().removeNotificationListener(mListener);
        CameraNotificationManager.getInstance().removeNotificationListener(mSkyLandListener);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void clearParams() {
        this.mMode = null;
        mDashPath = null;
        mDashPathEdge = null;
        mDashPath1 = null;
        mDashPathEdge1 = null;
        mShadingDashPath = null;
        mShadingDashPathEdge = null;
        mPaintCenterDash = null;
        mPaintCenterDashEdge = null;
        mPaintCenterDash1 = null;
        mPaintCenterDashEdge1 = null;
        mPaintShading = null;
        mPaintShadingEdge = null;
        if (this.mLowerSelected != null) {
            this.mLowerSelected.recycle();
            this.mLowerSelected = null;
        }
        if (this.mLandNormal != null) {
            this.mLandNormal.recycle();
            this.mLandNormal = null;
        }
        if (this.mUpperSelected != null) {
            this.mUpperSelected.recycle();
            this.mUpperSelected = null;
        }
        if (this.mSkyNormal != null) {
            this.mSkyNormal.recycle();
            this.mSkyNormal = null;
        }
    }

    public BorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMode = null;
        this.mCenterIcon = null;
        this.mMainColor = 0;
        this.mSubColor = 0;
        this.mSetting = null;
        this.mLowerSelected = null;
        this.mLandNormal = null;
        this.mUpperSelected = null;
        this.mSkyNormal = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        clearParams();
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.BorderView);
        this.mMode = attribute.getString(0);
        if (this.mMode == null) {
            this.mMode = "normal";
        }
        this.mMainColor = attribute.getColor(1, WHITE_DOT);
        this.mSubColor = attribute.getColor(2, GRAY_DOT);
        this.mSetting = attribute.getString(3);
        attribute.recycle();
        if (this.mSetting == null) {
            this.mSetting = "unknown";
        }
        this.mResources = context.getResources();
        prepareResources();
        mSkyLandListener = new NotificationListener() { // from class: com.sony.imaging.app.digitalfilter.shooting.widget.BorderView.1
            private final String[] TAG = {GFConstants.CAMERA_SETTING_CHANGED};

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAG;
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                if (GFConstants.CAMERA_SETTING_CHANGED.equalsIgnoreCase(tag)) {
                    BorderView.this.invalidate();
                }
            }
        };
        mListener = new NotificationListener() { // from class: com.sony.imaging.app.digitalfilter.shooting.widget.BorderView.2
            private final String[] TAG = {DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, GFEffectParameters.TAG_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DEVICE_CHANGE, GFConstants.TAG_GYROSCOPE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAG;
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                BorderView.this.EE_width = videoRect.pxRight - videoRect.pxLeft;
                BorderView.this.EE_height = videoRect.pxBottom - videoRect.pxTop;
                BorderView.this.invalidate(videoRect.pxLeft, videoRect.pxTop, videoRect.pxRight, videoRect.pxBottom);
            }
        };
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
