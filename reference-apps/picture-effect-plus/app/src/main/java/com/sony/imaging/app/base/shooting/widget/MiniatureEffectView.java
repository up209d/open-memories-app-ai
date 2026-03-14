package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MiniatureEffectView extends SimulatingEERelativeLayout {
    protected static final double BASE_HEIGHT = 1000.0d;
    protected static final double BASE_WIDTH = 1000.0d;
    private static final int GRADATION_COLOR;
    private static final String IMAGER32_STILL11 = "imager32_still11";
    private static final String IMAGER32_STILL169 = "imager32_still169";
    private static final String IMAGER32_STILL32 = "imager32_still32";
    private static final String IMAGER32_STILL43 = "imager32_still43";
    private static final String IMAGER32_STILL_COMMON = "imager32_still_common";
    private static final String IMAGER43_STILL11 = "imager43_still11";
    private static final String IMAGER43_STILL169 = "imager43_still169";
    private static final String IMAGER43_STILL32 = "imager43_still32";
    private static final String IMAGER43_STILL43 = "imager43_still43";
    private static final String IMAGER43_STILL_COMMON = "imager43_still_common";
    protected static final int INVERT = 1;
    private static final String LOG_ONCHANGE_VIEWPATTERN = "onChangeViewPattern";
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER32_STILL11;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER32_STILL169;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER32_STILL32 = new HashMap<>();
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER32_STILL43;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER32_STILL_COMMON;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER43_STILL11;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER43_STILL169;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER43_STILL32;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER43_STILL43;
    private static final HashMap<String, Rect> MINIATURE_AREA_IMAGER43_STILL_COMMON;
    private static final HashMap<String, HashMap<String, Rect>> MINIATURE_FOCUS_AREAS;
    private static final String NOT_DISPLAY = "notdisplay";
    private static final String NOT_SUPPORTED_AREA = "invalid";
    protected static final int NO_INVERT = 0;
    protected static final int START_X = 0;
    protected static final int START_Y = 0;
    private static final String TAG = "MiniatureEffectView";
    private final CameraNotificationManager mCameraNotifier;
    private NotificationListener mDisplayModeListener;
    protected final DisplayModeObserver mDisplayModeNotifier;
    private ImageView mGradationArea1;
    private ImageView mGradationArea2;
    private RelativeLayout.LayoutParams mGradationAreaLayout1;
    private RelativeLayout.LayoutParams mGradationAreaLayout2;
    protected String mImagerAspect;
    protected int mInvertH;
    NotificationListener mPictureEffectChangeListener;

    static {
        MINIATURE_AREA_IMAGER32_STILL32.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 173, 1000, 425));
        MINIATURE_AREA_IMAGER32_STILL32.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 374, 1000, AppRoot.USER_KEYCODE.PROJECTOR));
        MINIATURE_AREA_IMAGER32_STILL32.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 827));
        MINIATURE_AREA_IMAGER32_STILL32.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER32_STILL32.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER32_STILL32.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER32_STILL169 = new HashMap<>();
        MINIATURE_AREA_IMAGER32_STILL169.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 111, 1000, 411));
        MINIATURE_AREA_IMAGER32_STILL169.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 350, 1000, 650));
        MINIATURE_AREA_IMAGER32_STILL169.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1000, 889));
        MINIATURE_AREA_IMAGER32_STILL169.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER32_STILL169.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER32_STILL169.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER32_STILL43 = new HashMap<>();
        MINIATURE_AREA_IMAGER32_STILL43.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 173, 1000, 425));
        MINIATURE_AREA_IMAGER32_STILL43.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 374, 1000, AppRoot.USER_KEYCODE.PROJECTOR));
        MINIATURE_AREA_IMAGER32_STILL43.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 827));
        MINIATURE_AREA_IMAGER32_STILL43.put(PictureEffectController.MINIATURE_LEFT, new Rect(135, 0, 416, 1000));
        MINIATURE_AREA_IMAGER32_STILL43.put(PictureEffectController.MINIATURE_VCENTER, new Rect(360, 0, AppRoot.USER_KEYCODE.WATER_HOUSING, 1000));
        MINIATURE_AREA_IMAGER32_STILL43.put(PictureEffectController.MINIATURE_RIGHT, new Rect(584, 0, 865, 1000));
        MINIATURE_AREA_IMAGER32_STILL11 = new HashMap<>();
        MINIATURE_AREA_IMAGER32_STILL11.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 173, 1000, 425));
        MINIATURE_AREA_IMAGER32_STILL11.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 374, 1000, AppRoot.USER_KEYCODE.PROJECTOR));
        MINIATURE_AREA_IMAGER32_STILL11.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 827));
        MINIATURE_AREA_IMAGER32_STILL11.put(PictureEffectController.MINIATURE_LEFT, new Rect(19, 0, 393, 1000));
        MINIATURE_AREA_IMAGER32_STILL11.put(PictureEffectController.MINIATURE_VCENTER, new Rect(313, 0, 687, 1000));
        MINIATURE_AREA_IMAGER32_STILL11.put(PictureEffectController.MINIATURE_RIGHT, new Rect(AppRoot.USER_KEYCODE.FEL, 0, 986, 1000));
        MINIATURE_AREA_IMAGER32_STILL_COMMON = new HashMap<>();
        MINIATURE_AREA_IMAGER32_STILL_COMMON.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 175, 1000, 425));
        MINIATURE_AREA_IMAGER32_STILL_COMMON.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 375, 1000, AppRoot.USER_KEYCODE.PEAKING));
        MINIATURE_AREA_IMAGER32_STILL_COMMON.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 825));
        MINIATURE_AREA_IMAGER32_STILL_COMMON.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER32_STILL_COMMON.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER32_STILL_COMMON.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER43_STILL_COMMON = new HashMap<>();
        MINIATURE_AREA_IMAGER43_STILL_COMMON.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 175, 1000, 425));
        MINIATURE_AREA_IMAGER43_STILL_COMMON.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 375, 1000, AppRoot.USER_KEYCODE.PEAKING));
        MINIATURE_AREA_IMAGER43_STILL_COMMON.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 825));
        MINIATURE_AREA_IMAGER43_STILL_COMMON.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER43_STILL_COMMON.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER43_STILL_COMMON.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER43_STILL32 = new HashMap<>();
        MINIATURE_AREA_IMAGER43_STILL32.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 136, 1000, 416));
        MINIATURE_AREA_IMAGER43_STILL32.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 360, 1000, AppRoot.USER_KEYCODE.WATER_HOUSING));
        MINIATURE_AREA_IMAGER43_STILL32.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 584, 1000, 864));
        MINIATURE_AREA_IMAGER43_STILL32.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER43_STILL32.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER43_STILL32.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER43_STILL169 = new HashMap<>();
        MINIATURE_AREA_IMAGER43_STILL169.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 67, 1000, 400));
        MINIATURE_AREA_IMAGER43_STILL169.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 333, 1000, 667));
        MINIATURE_AREA_IMAGER43_STILL169.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S, 1000, 933));
        MINIATURE_AREA_IMAGER43_STILL169.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER43_STILL169.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER43_STILL169.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER43_STILL43 = new HashMap<>();
        MINIATURE_AREA_IMAGER43_STILL43.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 175, 1000, 425));
        MINIATURE_AREA_IMAGER43_STILL43.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 375, 1000, AppRoot.USER_KEYCODE.PEAKING));
        MINIATURE_AREA_IMAGER43_STILL43.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 825));
        MINIATURE_AREA_IMAGER43_STILL43.put(PictureEffectController.MINIATURE_LEFT, new Rect(175, 0, 425, 1000));
        MINIATURE_AREA_IMAGER43_STILL43.put(PictureEffectController.MINIATURE_VCENTER, new Rect(375, 0, AppRoot.USER_KEYCODE.PEAKING, 1000));
        MINIATURE_AREA_IMAGER43_STILL43.put(PictureEffectController.MINIATURE_RIGHT, new Rect(575, 0, 825, 1000));
        MINIATURE_AREA_IMAGER43_STILL11 = new HashMap<>();
        MINIATURE_AREA_IMAGER43_STILL11.put(PictureEffectController.MINIATURE_UPPER, new Rect(0, 175, 1000, 425));
        MINIATURE_AREA_IMAGER43_STILL11.put(PictureEffectController.MINIATURE_HCENTER, new Rect(0, 375, 1000, AppRoot.USER_KEYCODE.PEAKING));
        MINIATURE_AREA_IMAGER43_STILL11.put(PictureEffectController.MINIATURE_LOWER, new Rect(0, 575, 1000, 825));
        MINIATURE_AREA_IMAGER43_STILL11.put(PictureEffectController.MINIATURE_LEFT, new Rect(67, 0, 400, 1000));
        MINIATURE_AREA_IMAGER43_STILL11.put(PictureEffectController.MINIATURE_VCENTER, new Rect(333, 0, 667, 1000));
        MINIATURE_AREA_IMAGER43_STILL11.put(PictureEffectController.MINIATURE_RIGHT, new Rect(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S, 0, 933, 1000));
        MINIATURE_FOCUS_AREAS = new HashMap<>();
        MINIATURE_FOCUS_AREAS.put(IMAGER32_STILL32, MINIATURE_AREA_IMAGER32_STILL32);
        MINIATURE_FOCUS_AREAS.put(IMAGER32_STILL169, MINIATURE_AREA_IMAGER32_STILL169);
        MINIATURE_FOCUS_AREAS.put(IMAGER32_STILL43, MINIATURE_AREA_IMAGER32_STILL43);
        MINIATURE_FOCUS_AREAS.put(IMAGER32_STILL11, MINIATURE_AREA_IMAGER32_STILL11);
        MINIATURE_FOCUS_AREAS.put(IMAGER32_STILL_COMMON, MINIATURE_AREA_IMAGER32_STILL_COMMON);
        MINIATURE_FOCUS_AREAS.put(IMAGER43_STILL_COMMON, MINIATURE_AREA_IMAGER43_STILL_COMMON);
        MINIATURE_FOCUS_AREAS.put(IMAGER43_STILL32, MINIATURE_AREA_IMAGER43_STILL32);
        MINIATURE_FOCUS_AREAS.put(IMAGER43_STILL169, MINIATURE_AREA_IMAGER43_STILL169);
        MINIATURE_FOCUS_AREAS.put(IMAGER43_STILL43, MINIATURE_AREA_IMAGER43_STILL43);
        MINIATURE_FOCUS_AREAS.put(IMAGER43_STILL11, MINIATURE_AREA_IMAGER43_STILL11);
        GRADATION_COLOR = Color.argb(128, 204, 204, 204);
    }

    public MiniatureEffectView(Context context) {
        this(context, null);
    }

    public MiniatureEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInvertH = 0;
        this.mPictureEffectChangeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.MiniatureEffectView.1
            private final String[] TAGS = {CameraNotificationManager.PICTURE_EFFECT_CHANGE, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.MINIATURE_SHADED_REGION_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                MiniatureEffectView.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAGS;
            }
        };
        this.mDisplayModeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.MiniatureEffectView.2
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                MiniatureEffectView.this.onChangeViewPattern();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return new String[]{DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE};
            }
        };
        this.mCameraNotifier = CameraNotificationManager.getInstance();
        this.mGradationArea1 = new ImageView(context);
        this.mGradationArea2 = new ImageView(context);
        this.mGradationArea1.setBackgroundColor(GRADATION_COLOR);
        this.mGradationArea2.setBackgroundColor(GRADATION_COLOR);
        this.mGradationAreaLayout1 = new RelativeLayout.LayoutParams(-1, -1);
        this.mGradationAreaLayout2 = new RelativeLayout.LayoutParams(-1, -1);
        this.mGradationAreaLayout1.addRule(9);
        this.mGradationAreaLayout1.addRule(10);
        this.mGradationAreaLayout2.addRule(9);
        this.mGradationAreaLayout2.addRule(10);
        addView(this.mGradationArea1);
        addView(this.mGradationArea2);
        this.mDisplayModeNotifier = DisplayModeObserver.getInstance();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        addCameraNotificationListener(this.mPictureEffectChangeListener);
        this.mDisplayModeNotifier.setNotificationListener(this.mDisplayModeListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCameraNotificationListener(this.mPictureEffectChangeListener);
        this.mDisplayModeNotifier.removeNotificationListener(this.mDisplayModeListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addCameraNotificationListener(NotificationListener listener) {
        if (this.mCameraNotifier != null) {
            this.mCameraNotifier.setNotificationListener(listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeCameraNotificationListener(NotificationListener listener) {
        if (this.mCameraNotifier != null) {
            this.mCameraNotifier.removeNotificationListener(listener);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout
    public void setLayoutSize(Rect rect) {
        super.setLayoutSize(rect);
        refresh();
    }

    private void clear() {
        setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refresh() {
        String pos = getFocusPosition();
        String area = getDisplayArea();
        if (NOT_SUPPORTED_AREA.equals(area)) {
            clear();
            return;
        }
        if (NOT_DISPLAY.equals(pos)) {
            clear();
            return;
        }
        if (1 <= CameraSetting.getPfApiVersion() && DigitalZoomController.getInstance().isDigitalZoomStatus()) {
            setVisibility(4);
            return;
        }
        if (this.mInvertH == 1) {
            if (PictureEffectController.MINIATURE_RIGHT.equals(pos)) {
                pos = PictureEffectController.MINIATURE_LEFT;
            } else if (PictureEffectController.MINIATURE_LEFT.equals(pos)) {
                pos = PictureEffectController.MINIATURE_RIGHT;
            }
        }
        setVisibility(0);
        HashMap<String, Rect> areas = MINIATURE_FOCUS_AREAS.get(area);
        Rect baseRect = areas.get(pos);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        int eeLeft = params.leftMargin;
        int eeTop = params.topMargin;
        int eeRight = params.leftMargin + params.width;
        int eeBottom = params.topMargin + params.height;
        double eeWidth = params.width;
        double eeHeight = params.height;
        Rect yuvRect = new Rect(eeLeft, eeTop, eeRight, eeBottom);
        double miniatureLeft = (baseRect.left * eeWidth) / 1000.0d;
        double miniatureTop = (baseRect.top * eeHeight) / 1000.0d;
        double miniatureRight = (baseRect.right * eeWidth) / 1000.0d;
        double miniatureBottom = (baseRect.bottom * eeHeight) / 1000.0d;
        Rect eeMiniatureRect = new Rect((int) miniatureLeft, (int) miniatureTop, (int) miniatureRight, (int) miniatureBottom);
        if (PictureEffectController.MINIATURE_UPPER.equals(pos) || PictureEffectController.MINIATURE_HCENTER.equals(pos) || PictureEffectController.MINIATURE_LOWER.equals(pos)) {
            this.mGradationAreaLayout1.leftMargin = eeMiniatureRect.left;
            this.mGradationAreaLayout1.topMargin = 0;
            this.mGradationAreaLayout1.width = eeMiniatureRect.right;
            this.mGradationAreaLayout1.height = eeMiniatureRect.top;
            this.mGradationAreaLayout2.leftMargin = eeMiniatureRect.left;
            this.mGradationAreaLayout2.topMargin = eeMiniatureRect.bottom;
            this.mGradationAreaLayout2.width = eeMiniatureRect.right;
            this.mGradationAreaLayout2.height = (yuvRect.bottom - eeMiniatureRect.bottom) - yuvRect.top;
        } else {
            this.mGradationAreaLayout1.leftMargin = 0;
            this.mGradationAreaLayout1.topMargin = eeMiniatureRect.top;
            this.mGradationAreaLayout1.width = eeMiniatureRect.left;
            this.mGradationAreaLayout1.height = eeMiniatureRect.bottom;
            this.mGradationAreaLayout2.leftMargin = eeMiniatureRect.right;
            this.mGradationAreaLayout2.topMargin = eeMiniatureRect.top;
            this.mGradationAreaLayout2.width = (yuvRect.right - eeMiniatureRect.right) - yuvRect.left;
            this.mGradationAreaLayout2.height = eeMiniatureRect.bottom;
        }
        updateViewLayout(this.mGradationArea1, this.mGradationAreaLayout1);
        updateViewLayout(this.mGradationArea2, this.mGradationAreaLayout2);
    }

    protected String getFocusPosition() {
        String miniature = getMiniatureSetting();
        if (NOT_DISPLAY.equals(miniature)) {
            return miniature;
        }
        if ("auto".equals(miniature)) {
            miniature = NOT_DISPLAY;
        }
        return miniature;
    }

    private String getMiniatureSetting() {
        PictureEffectController pec = PictureEffectController.getInstance();
        String effect = pec.getValue(PictureEffectController.PICTUREEFFECT);
        if (!PictureEffectController.MODE_MINIATURE.equals(effect)) {
            return NOT_DISPLAY;
        }
        String miniature = pec.getValue(PictureEffectController.MODE_MINIATURE);
        return miniature;
    }

    private int getImagerAspect() {
        if (3 > CameraSetting.getPfApiVersion()) {
            return 32;
        }
        int ret = ScalarProperties.getInt("device.imager.aspect");
        return ret;
    }

    private String getDisplayArea() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        String pfValue = ((CameraEx.ParametersModifier) params.second).getImageAspectRatio();
        PictureEffectController pictureEffectController = PictureEffectController.getInstance();
        if (2 <= CameraSetting.getPfApiVersion()) {
            int imagerAspect = getImagerAspect();
            if (pictureEffectController.isMiniatureForSpecialSequence()) {
                if (imagerAspect == 32) {
                    if ("3:2".equals(pfValue)) {
                        return IMAGER32_STILL32;
                    }
                    if ("16:9".equals(pfValue)) {
                        return IMAGER32_STILL169;
                    }
                    if ("4:3".equals(pfValue)) {
                        return IMAGER32_STILL43;
                    }
                    if (!"1:1".equals(pfValue)) {
                        return NOT_SUPPORTED_AREA;
                    }
                    return IMAGER32_STILL11;
                }
                if (imagerAspect == 43) {
                    if ("3:2".equals(pfValue)) {
                        return IMAGER43_STILL32;
                    }
                    if ("16:9".equals(pfValue)) {
                        return IMAGER43_STILL169;
                    }
                    if ("4:3".equals(pfValue)) {
                        return IMAGER43_STILL43;
                    }
                    if (!"1:1".equals(pfValue)) {
                        return NOT_SUPPORTED_AREA;
                    }
                    return IMAGER43_STILL11;
                }
                if (imagerAspect == 169 || imagerAspect != 11) {
                }
                return NOT_SUPPORTED_AREA;
            }
            if (imagerAspect == 32) {
                return IMAGER32_STILL_COMMON;
            }
            if (imagerAspect == 43) {
                return IMAGER43_STILL_COMMON;
            }
            if (imagerAspect == 169 || imagerAspect == 11) {
            }
            return NOT_SUPPORTED_AREA;
        }
        if ("3:2".equals(pfValue)) {
            return IMAGER32_STILL32;
        }
        if ("16:9".equals(pfValue)) {
            return IMAGER32_STILL169;
        }
        if ("4:3".equals(pfValue)) {
            return IMAGER32_STILL43;
        }
        if (!"1:1".equals(pfValue)) {
            return NOT_SUPPORTED_AREA;
        }
        return IMAGER32_STILL11;
    }

    protected void onChangeViewPattern() {
        Log.d(TAG, LOG_ONCHANGE_VIEWPATTERN);
        updateMiniatureArea();
    }

    private void updateMiniatureArea() {
        int device = this.mDisplayModeNotifier.getActiveDevice();
        DisplayManager.DeviceStatus deviceStatus = this.mDisplayModeNotifier.getActiveDeviceStatus();
        if (deviceStatus == null) {
            this.mInvertH = 0;
        } else {
            int panelViewPattern = deviceStatus.viewPattern;
            if (device == 0) {
                if (panelViewPattern == 1) {
                    this.mInvertH = 1;
                } else if (panelViewPattern == 0) {
                    this.mInvertH = 0;
                }
            }
        }
        refresh();
    }
}
