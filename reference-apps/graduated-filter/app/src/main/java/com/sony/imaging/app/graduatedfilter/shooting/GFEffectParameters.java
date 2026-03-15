package com.sony.imaging.app.graduatedfilter.shooting;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class GFEffectParameters extends NotificationManager {
    private static final int ASPECT_11 = 3;
    private static final int ASPECT_169 = 2;
    private static final int ASPECT_32 = 1;
    private static final int ASPECT_43 = 4;
    private static final int ASPECT_UNKNOWN = 0;
    private static final String BASEAPERTURE = "BaseAperture";
    private static final String BASEEXPOSURECOMP = "BaseExposureComp";
    private static final String BASEISO = "BaseIso";
    private static final String BASESSDENOMINATOR = "BaseSsDenominator";
    private static final String BASESSNUMERATOR = "BaseSsNumerator";
    private static final String BASEWBMODE = "BaseWBmode";
    private static final String BASEWBOPTION = "BaseWBOption";
    private static final String COLORFILTER = "ColorFilter";
    private static final String CREATIVESTYLE = "CreativeStyle";
    private static final String CREATIVESTYLEOPTION = "CreativeStyleOption";
    private static final String DEGREE = "Degree";
    private static final String DRO = "DRO";
    private static final String EXPMODE = "ExpMode";
    private static final String FACEDETECTION = "FaceDetection";
    private static final String FILTERAPERTURE = "FilterAperture";
    private static final String FILTEREXPOSURECOMP = "FilterExposureComp";
    private static final String FILTERISO = "FilterIso";
    private static final String FILTERSSDENOMINATOR = "FilterSsDenominator";
    private static final String FILTERSSNUMERATOR = "FilterSsNumerator";
    private static final String FILTERWBB = "FilterWbB";
    private static final String FILTERWBMODE = "FilterWBmode";
    private static final String FILTERWBOPTION = "FilterWBOption";
    private static final String FILTERWBR = "FilterWbR";
    private static final String FLASHCOMP = "FlashComp";
    private static final String FLASHMODE = "FlashMode";
    private static final String METERINGMODE = "MeteringMode";
    private static final String NDFILTER = "NDFilter";
    private static final String POINT = "Point";
    private static final String STRENGTH = "Strength";
    public static final String TAG_CHANGE = "GradationEffect.Changed";
    private static int mActiveDevice;
    private static Rect mEERect;
    public static int mRotation;
    private static Parameters mSettingParams;
    NotificationListener mListener = new ChangeYUVNotifier();
    private static final String TAG = AppLog.getClassName();
    private static GFEffectParameters mInstance = null;

    public static GFEffectParameters getInstance() {
        if (mInstance == null) {
            mInstance = new GFEffectParameters();
        }
        return mInstance;
    }

    public void initialize() {
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        Parameters p = new Parameters();
        p.setEffect(0);
        setParameters(p);
        mEERect = new Rect(0, 0, AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
        DisplayModeObserver disp = DisplayModeObserver.getInstance();
        if (disp == null) {
            mRotation = 4;
        } else if (disp.getActiveDeviceStatus() == null) {
            mRotation = 4;
        } else {
            mRotation = disp.getActiveDeviceStatus().viewPattern;
        }
        mActiveDevice = 0;
        createStrengthArray();
    }

    private void createStrengthArray() {
        Parameters.createStrengthArray();
    }

    public void terminate() {
        if (this.mListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        }
    }

    /* loaded from: classes.dex */
    public static class Parameters implements Cloneable {
        private static final int BASE_COEF = 25600;
        public static final boolean BASE_SETTING = true;
        public static final int CUSTOM1 = 3;
        public static final int CUSTOM2 = 4;
        public static final int CUSTOM3 = 5;
        public static final boolean FILTER_SETTING = false;
        public static final int NO_WB_SHIFT = 0;
        public static final String OFF = "OFF";
        public static final String ON = "ON";
        public static final int SKY_BLUE = 0;
        public static final int STANDARD = 2;
        public static final int SUNSET = 1;
        static BaseParameters mParams;
        public static final int[] mShadingWidthTable = {IntervalRecExecutor.INTVL_REC_INITIALIZED, 190, 180, 170, 160, 150, 140, 130, 120, DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED, 100, 95, 90, 85, 80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5};
        public static int[] mSAStrength = new int[mShadingWidthTable.length];
        public static final String[] DEFAULT_EXP_MODE = {"Aperture", "Aperture", "Aperture", "Aperture", "Aperture", "Aperture", "Aperture"};
        public static final List<Point> DEFAULT_CENTER = Arrays.asList(new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500));
        public static final int[] DEFAULT_DEGREE = {0, 0, 0, 0, 0, 0, 0};
        public static final int[] DEFAULT_STRENGTH = {14, 14, 14, 14, 14, 14, 14};
        public static final String[] DEFAULT_NDFILTER = {"ON", "ON", "ON", "ON", "ON", "ON", "ON"};
        public static final String[] DEFAULT_COLORFILTER = {"ON", "ON", "OFF", "OFF", "OFF", "ON", "ON"};
        public static final String[] DEFAULT_FLASHMODE = {FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC};
        public static final String[] DEFAULT_FLASHCOMP = {ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO};
        public static final String[] DEFAULT_METERINGMODE = {FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI};
        public static final String[] DEFAULT_DROMODE = {"off", "off", "off", "off", "off", "off", "off"};
        public static final String[] DEFAULT_CREATIVESTYLE = {"standard", "sunset", "standard", "standard", "standard", "standard", "standard"};
        public static final String[] DEFAULT_CREATIVESTYLEOPTION = {"0/0/0", "0/1/0", "0/0/0", "0/0/0", "0/0/0", "0/0/0", "0/0/0"};
        public static final String[] DEFAULT_FACEDETECTION = {"off", "off", "off", "off", "off", "off", "off"};
        public static final String[] DEFAULT_BASE_ISO = {ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO};
        public static final String[] DEFAULT_BASE_EXPCOMP = {"6", "2", "3", "3", "3", "3", "3"};
        public static final int[] DEFAULT_BASE_APERTURE = {1600, 800, 800, 560, 560, 560, 560};
        public static final List<Pair> DEFAULT_BASE_SS = Arrays.asList(new Pair(1, 60), new Pair(1, 1), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250));
        public static final String[] DEFAULT_BASE_WB = {WhiteBalanceController.COLOR_TEMP, WhiteBalanceController.COLOR_TEMP, "auto", "auto", "auto", "auto", "auto"};
        public static final String[] DEFAULT_BASE_WB_OPTION = {"0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500"};
        public static final String[] DEFAULT_FILTER_ISO = {ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_100, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO};
        public static final String[] DEFAULT_FILTER_EXPCOMP = {"-3", "-2", "-6", "-6", "-6", "-6", "-6"};
        public static final int[] DEFAULT_FILTER_APERTURE = {1600, 800, 800, 560, 560, 560, 560};
        public static final List<Pair> DEFAULT_FILTER_SS = Arrays.asList(new Pair(1, 500), new Pair(10, 25), new Pair(1, 2000), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250));
        public static final String[] DEFAULT_FILTER_WB = {WhiteBalanceController.COLOR_TEMP, WhiteBalanceController.COLOR_TEMP, GFWhiteBalanceController.SAME, GFWhiteBalanceController.SAME, GFWhiteBalanceController.SAME, "auto", "auto"};
        public static final String[] DEFAULT_FILTER_WB_OPTION = {"0/0/4000", "0/0/9000", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500"};
        public static final int[] DEFAULT_FILTER_WB_R = {0, 0, 0, 0, 0, 0, 0};
        public static final int[] DEFAULT_FILTER_WB_B = {0, 0, 0, 0, 0, 0, 0};

        public static void setDefaultValues(int theme) {
        }

        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public Parameters m1clone() {
            Parameters p = null;
            try {
                p = (Parameters) super.clone();
                mParams = mParams.m0clone();
                return p;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return p;
            }
        }

        public String flatten() {
            if (mParams instanceof SkyBlueParameters) {
                String ret = "Effect=0;" + mParams.flatten();
                return ret;
            }
            if (mParams instanceof SunsetParameters) {
                String ret2 = "Effect=1;" + mParams.flatten();
                return ret2;
            }
            if (mParams instanceof StandardParameters) {
                String ret3 = "Effect=2;" + mParams.flatten();
                return ret3;
            }
            if (mParams instanceof Custom1Parameters) {
                String ret4 = "Effect=3;" + mParams.flatten();
                return ret4;
            }
            if (mParams instanceof Custom2Parameters) {
                String ret5 = "Effect=4;" + mParams.flatten();
                return ret5;
            }
            if (!(mParams instanceof Custom3Parameters)) {
                return null;
            }
            String ret6 = "Effect=5;" + mParams.flatten();
            return ret6;
        }

        public void unflatten(String f) {
            BaseParameters params;
            int index = f.indexOf("Effect=");
            if (-1 != index) {
                int start = index + "Effect=".length();
                int e = Integer.parseInt(f.substring(start, start + 1));
                Log.i(GFEffectParameters.TAG, "doItemClickProcessing unflatten = " + e);
                switch (e) {
                    case 0:
                        params = new SkyBlueParameters();
                        break;
                    case 1:
                        params = new SunsetParameters();
                        break;
                    case 2:
                        params = new StandardParameters();
                        break;
                    case 3:
                        params = new Custom1Parameters();
                        break;
                    case 4:
                        params = new Custom2Parameters();
                        break;
                    case 5:
                        params = new Custom3Parameters();
                        break;
                    default:
                        throw new InvalidParameterException("Effect " + e + " does not support value");
                }
            } else {
                params = new SkyBlueParameters();
            }
            params.unflatten(f);
            mParams = params;
        }

        public String getDefaultFlatten() {
            String ret = null;
            if (mParams instanceof SkyBlueParameters) {
                ret = "Effect=0;" + mParams.getDefaultFlatten(0);
            } else if (mParams instanceof SunsetParameters) {
                ret = "Effect=1;" + mParams.getDefaultFlatten(1);
            } else if (mParams instanceof StandardParameters) {
                ret = "Effect=2;" + mParams.getDefaultFlatten(2);
            } else if (mParams instanceof Custom1Parameters) {
                ret = "Effect=3;" + mParams.getDefaultFlatten(3);
            } else if (mParams instanceof Custom2Parameters) {
                ret = "Effect=4;" + mParams.getDefaultFlatten(4);
            } else if (mParams instanceof Custom3Parameters) {
                ret = "Effect=5;" + mParams.getDefaultFlatten(5);
            }
            AppLog.info(GFEffectParameters.TAG, "Default Params: " + ret);
            return ret;
        }

        protected void update(ByteBuffer params) {
            mParams.update(params);
        }

        public void setEffect(int e) {
            unflatten("Effect=" + e);
        }

        public static int getEffect() {
            if (mParams instanceof SkyBlueParameters) {
                return 0;
            }
            if (mParams instanceof SunsetParameters) {
                return 1;
            }
            if (mParams instanceof StandardParameters) {
                return 2;
            }
            if (mParams instanceof Custom1Parameters) {
                return 3;
            }
            if (mParams instanceof Custom2Parameters) {
                return 4;
            }
            if (mParams instanceof Custom3Parameters) {
                return 5;
            }
            return -1;
        }

        public void setExpMode(String mode) {
            mParams.setExpMode(mode);
        }

        public String getExpMode() {
            return mParams.getExpMode();
        }

        public void setPoint(Point p) {
            mParams.setPoint(p);
        }

        public Point getPoint() {
            return mParams.getPoint();
        }

        public Point getSAPoint(int width, int height) {
            return mParams.getSAPoint(width, height);
        }

        public Point getSAPoint(int rawWidth, int rawHeight, String aspectRatioOfPictureImage) {
            return mParams.getSAPoint(rawWidth, rawHeight, aspectRatioOfPictureImage);
        }

        public Rect getLimitPoint() {
            return mParams.getLimitPoint();
        }

        public void setOSDPoint(PointF p) {
            RectF limit = getLimitOSDPoint();
            if (limit.contains(p.x, p.y)) {
                Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
                mParams.setOSDPoint(p);
                return;
            }
            Log.i(GFEffectParameters.TAG, "Point x: " + p.x + " y: " + p.y + " are over limit!, limit : " + limit);
            p.x = Math.max(p.x, limit.left);
            p.x = Math.min(p.x, limit.right);
            p.y = Math.max(p.y, limit.top);
            p.y = Math.min(p.y, limit.bottom);
            Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
            mParams.setOSDPoint(p);
        }

        public PointF getOSDPoint() {
            return mParams.getOSDPoint();
        }

        public RectF getLimitOSDPoint() {
            return mParams.getLimitOSDPoint();
        }

        public PointF getOSDSAPoint(int width, int height) {
            return mParams.getOSDSAPoint(width, height);
        }

        public void setDegree(int d) {
            Log.i(GFEffectParameters.TAG, "set " + d + "degree");
            mParams.setDegree(d);
        }

        public int getDegree() {
            return mParams.getDegree();
        }

        public int getSADegree() {
            return 360 - mParams.getDegree();
        }

        public void setStrength(int s) {
            mParams.setStrength(s);
        }

        public int getStrength() {
            return mParams.getStrength();
        }

        public static void createStrengthArray() {
            for (int i = 0; i < mShadingWidthTable.length; i++) {
                mSAStrength[i] = BASE_COEF / mShadingWidthTable[(mShadingWidthTable.length - 1) - i];
            }
        }

        public int getSAStrength() {
            Log.i(GFEffectParameters.TAG, "level = " + mParams.getStrength() + " parameter = " + mSAStrength[mParams.getStrength()]);
            return mSAStrength[mParams.getStrength()];
        }

        public void setNDFilter(String n) {
            mParams.setNDFilter(n);
        }

        public String getNDFilter() {
            return mParams.getNDFilter();
        }

        public void setColorFilter(String c) {
            mParams.setColorFilter(c);
        }

        public String getColorFilter() {
            return mParams.getColorFilter();
        }

        public void setFlashMode(String f) {
            mParams.setFlashMode(f);
        }

        public String getFlashMode() {
            return mParams.getFlashMode();
        }

        public void setFlashComp(String f) {
            mParams.setFlashComp(f);
        }

        public String getFlashComp() {
            return mParams.getFlashComp();
        }

        public void setMeteringMode(String f) {
            mParams.setMeteringMode(f);
        }

        public String getMeteringMode() {
            return mParams.getMeteringMode();
        }

        public void setDRO(String d) {
            mParams.setDRO(d);
        }

        public String getDRO() {
            return mParams.getDRO();
        }

        public void setCreativeStyle(String c) {
            mParams.setCreativeStyle(c);
        }

        public String getCreativeStyle() {
            return mParams.getCreativeStyle();
        }

        public void setCreativeStyleOption(String c) {
            mParams.setCreativeStyleOption(c);
        }

        public String getCreativeStyleOption() {
            return mParams.getCreativeStyleOption();
        }

        public void setFaceDetection(String f) {
            mParams.setFaceDetection(f);
        }

        public String getFaceDetection() {
            return mParams.getFaceDetection();
        }

        public void setISO(boolean isBaseSetting, String i) {
            if (isBaseSetting) {
                mParams.setBaseISO(i);
            } else {
                mParams.setFilterISO(i);
            }
        }

        public String getISO(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getNDFilter())) ? mParams.getBaseISO() : mParams.getFilterISO();
        }

        public void setExposureComp(boolean isBaseSetting, String e) {
            if (isBaseSetting) {
                mParams.setBaseExposureComp(e);
            } else {
                mParams.setFilterExposureComp(e);
            }
        }

        public String getExposureComp(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getNDFilter())) ? mParams.getBaseExposureComp() : mParams.getFilterExposureComp();
        }

        public void setAperture(boolean isBaseSetting, int a) {
            if (isBaseSetting) {
                mParams.setBaseAperture(a);
            } else {
                mParams.setFilterAperture(a);
            }
        }

        public int getAperture(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getNDFilter())) ? mParams.getBaseAperture() : mParams.getFilterAperture();
        }

        public void setSSNumerator(boolean isBaseSetting, int s) {
            if (isBaseSetting) {
                mParams.setBaseSSNumerator(s);
            } else {
                mParams.setFilterSSNumerator(s);
            }
        }

        public int getSSNumerator(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getNDFilter())) ? mParams.getBaseSSNumerator() : mParams.getFilterSSNumerator();
        }

        public void setSSDenominator(boolean isBaseSetting, int s) {
            if (isBaseSetting) {
                mParams.setBaseSSDenominator(s);
            } else {
                mParams.setFilterSSDenominator(s);
            }
        }

        public int getSSDenominator(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getNDFilter())) ? mParams.getBaseSSDenominator() : mParams.getFilterSSDenominator();
        }

        public void setWBMode(boolean isBaseSetting, String w) {
            if (isBaseSetting) {
                mParams.setBaseWBMode(w);
            } else {
                mParams.setFilterWBMode(w);
            }
        }

        public String getWBMode(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getColorFilter())) ? mParams.getBaseWBMode() : mParams.getFilterWBMode();
        }

        public String getWBOption(boolean isBaseSetting) {
            return (isBaseSetting || "OFF".equalsIgnoreCase(mParams.getColorFilter())) ? GFBackUpKey.getInstance().getWBOption(mParams.getBaseWBMode(), true, getEffect()) : GFBackUpKey.getInstance().getWBOption(mParams.getFilterWBMode(), false, getEffect());
        }

        public void setWbGM(int l) {
            mParams.setFilterWbR(l);
        }

        public int getWbGM() {
            return mParams.getFilterWbR();
        }

        public void setWbAB(int l) {
            mParams.setFilterWbB(l);
        }

        public int getWbAB() {
            return mParams.getFilterWbB();
        }
    }

    public Parameters getParameters() {
        return mSettingParams.m1clone();
    }

    public void setParameters(Parameters p) {
        mSettingParams = p.m1clone();
        notify(TAG_CHANGE);
    }

    static int getAspect(int width, int height) {
        float aspectValue = width / height;
        Log.i("ShaftsEffect::getAspect", "##### aspectValue = " + aspectValue);
        if (aspectValue >= 0.9f && aspectValue < 1.1f) {
            Log.i("ShaftsEffect::getAspect", "##### ASPECT_11");
            return 3;
        }
        if (aspectValue >= 1.3f && aspectValue < 1.4f) {
            Log.i("ShaftsEffect::getAspect", "##### ASPECT_43");
            return 4;
        }
        if (aspectValue >= 1.4f && aspectValue < 1.6f) {
            Log.i("ShaftsEffect::getAspect", "##### ASPECT_32");
            return 1;
        }
        if (aspectValue >= 1.7f && aspectValue < 1.8f) {
            Log.i("ShaftsEffect::getAspect", "##### ASPECT_169");
            return 2;
        }
        Log.i("ShaftsEffect::getAspect", "##### ASPECT_UNKNOWN");
        return 0;
    }

    protected GFEffectParameters() {
    }

    /* loaded from: classes.dex */
    static class ChangeYUVNotifier implements NotificationListener {
        private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

        ChangeYUVNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int current;
            int current2;
            DisplayModeObserver disp = DisplayModeObserver.getInstance();
            if (DisplayModeObserver.TAG_YUVLAYOUT_CHANGE.equals(tag)) {
                DisplayManager.VideoRect rect = (DisplayManager.VideoRect) disp.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                GFEffectParameters.mEERect.left = rect.pxLeft;
                GFEffectParameters.mEERect.top = rect.pxTop;
                GFEffectParameters.mEERect.right = rect.pxRight;
                GFEffectParameters.mEERect.bottom = rect.pxBottom;
                GFEffectParameters.mInstance.notify(GFEffectParameters.TAG_CHANGE);
                return;
            }
            if (DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE.equals(tag)) {
                DisplayManager.DeviceStatus status = disp.getActiveDeviceStatus();
                if (status == null) {
                    current2 = 4;
                } else {
                    current2 = disp.getActiveDeviceStatus().viewPattern;
                }
                if (GFEffectParameters.mRotation != current2) {
                    GFEffectParameters.mRotation = current2;
                    GFEffectParameters.mInstance.notify(GFEffectParameters.TAG_CHANGE);
                    return;
                }
                return;
            }
            if (DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag) && GFEffectParameters.mActiveDevice != (current = disp.getActiveDevice())) {
                int unused = GFEffectParameters.mActiveDevice = current;
                GFEffectParameters.mInstance.notify(GFEffectParameters.TAG_CHANGE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class BaseParameters implements Cloneable {
        int mBaseAperture;
        String mBaseExposureComp;
        String mBaseIso;
        int mBaseSsDenominator;
        int mBaseSsNumerator;
        String mBaseWBMode;
        String mBaseWBOption;
        Point mCenterPoint;
        String mColorFilter;
        String mCreativeStyle;
        String mCreativeStyleOption;
        String mDRO;
        int mDegree;
        String mExpMode;
        String mFaceDetection;
        int mFilterAperture;
        String mFilterExposureComp;
        String mFilterIso;
        int mFilterSsDenominator;
        int mFilterSsNumerator;
        String mFilterWBMode;
        String mFilterWBOption;
        int mFilterWbB;
        int mFilterWbR;
        String mFlashComp;
        String mFlashMode;
        String mMeteringMode;
        String mNDFilter;
        int mStrength;

        public abstract Rect getLimitPoint();

        BaseParameters() {
        }

        public String flatten() {
            StringBuilder f = new StringBuilder();
            f.append(GFEffectParameters.EXPMODE).append(MovieFormatController.Settings.EQUAL).append(this.mExpMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.POINT).append(MovieFormatController.Settings.EQUAL).append(this.mCenterPoint.x).append(":").append(this.mCenterPoint.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.DEGREE).append(MovieFormatController.Settings.EQUAL).append(this.mDegree).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.STRENGTH).append(MovieFormatController.Settings.EQUAL).append(this.mStrength).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.NDFILTER).append(MovieFormatController.Settings.EQUAL).append(this.mNDFilter).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.COLORFILTER).append(MovieFormatController.Settings.EQUAL).append(this.mColorFilter).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FlashMode").append(MovieFormatController.Settings.EQUAL).append(this.mFlashMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FLASHCOMP).append(MovieFormatController.Settings.EQUAL).append(this.mFlashComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("MeteringMode").append(MovieFormatController.Settings.EQUAL).append(this.mMeteringMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.DRO).append(MovieFormatController.Settings.EQUAL).append(this.mDRO).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("CreativeStyle").append(MovieFormatController.Settings.EQUAL).append(this.mCreativeStyle).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.CREATIVESTYLEOPTION).append(MovieFormatController.Settings.EQUAL).append(this.mCreativeStyleOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FaceDetection").append(MovieFormatController.Settings.EQUAL).append(this.mFaceDetection).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEISO).append(MovieFormatController.Settings.EQUAL).append(this.mBaseIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEEXPOSURECOMP).append(MovieFormatController.Settings.EQUAL).append(this.mBaseExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEAPERTURE).append(MovieFormatController.Settings.EQUAL).append(this.mBaseAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASESSNUMERATOR).append(MovieFormatController.Settings.EQUAL).append(this.mBaseSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASESSDENOMINATOR).append(MovieFormatController.Settings.EQUAL).append(this.mBaseSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEWBMODE).append(MovieFormatController.Settings.EQUAL).append(this.mBaseWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEWBOPTION).append(MovieFormatController.Settings.EQUAL).append(this.mBaseWBOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERISO).append(MovieFormatController.Settings.EQUAL).append(this.mFilterIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTEREXPOSURECOMP).append(MovieFormatController.Settings.EQUAL).append(this.mFilterExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERAPERTURE).append(MovieFormatController.Settings.EQUAL).append(this.mFilterAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERSSNUMERATOR).append(MovieFormatController.Settings.EQUAL).append(this.mFilterSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERSSDENOMINATOR).append(MovieFormatController.Settings.EQUAL).append(this.mFilterSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERWBMODE).append(MovieFormatController.Settings.EQUAL).append(this.mFilterWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERWBOPTION).append(MovieFormatController.Settings.EQUAL).append(this.mFilterWBOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERWBR).append(MovieFormatController.Settings.EQUAL).append(this.mFilterWbR).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERWBB).append(MovieFormatController.Settings.EQUAL).append(this.mFilterWbB).append(MovieFormatController.Settings.SEMI_COLON);
            return f.toString();
        }

        public void unflatten(String f) {
            String[] dev = f.split(MovieFormatController.Settings.SEMI_COLON, -1);
            for (String str : dev) {
                String[] element = str.split(MovieFormatController.Settings.EQUAL);
                if (GFEffectParameters.EXPMODE.equals(element[0])) {
                    setExpMode(element[1]);
                } else if (GFEffectParameters.POINT.equals(element[0])) {
                    String[] p = element[1].split(":");
                    setPoint(new Point(Integer.valueOf(p[0]).intValue(), Integer.valueOf(p[1]).intValue()));
                } else if (GFEffectParameters.DEGREE.equals(element[0])) {
                    setDegree(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.STRENGTH.equals(element[0])) {
                    setStrength(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.NDFILTER.equals(element[0])) {
                    setNDFilter(element[1]);
                } else if (GFEffectParameters.COLORFILTER.equals(element[0])) {
                    setColorFilter(element[1]);
                } else if ("FlashMode".equals(element[0])) {
                    setFlashMode(element[1]);
                } else if (GFEffectParameters.FLASHCOMP.equals(element[0])) {
                    setFlashComp(element[1]);
                } else if ("MeteringMode".equals(element[0])) {
                    setMeteringMode(element[1]);
                } else if (GFEffectParameters.DRO.equals(element[0])) {
                    setDRO(element[1]);
                } else if ("CreativeStyle".equals(element[0])) {
                    setCreativeStyle(element[1]);
                } else if (GFEffectParameters.CREATIVESTYLEOPTION.equals(element[0])) {
                    setCreativeStyleOption(element[1]);
                } else if ("FaceDetection".equals(element[0])) {
                    setFaceDetection(element[1]);
                } else if (GFEffectParameters.BASEISO.equals(element[0])) {
                    setBaseISO(element[1]);
                } else if (GFEffectParameters.BASEEXPOSURECOMP.equals(element[0])) {
                    setBaseExposureComp(element[1]);
                } else if (GFEffectParameters.BASEAPERTURE.equals(element[0])) {
                    setBaseAperture(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.BASESSNUMERATOR.equals(element[0])) {
                    setBaseSSNumerator(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.BASESSDENOMINATOR.equals(element[0])) {
                    setBaseSSDenominator(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.BASEWBMODE.equals(element[0])) {
                    setBaseWBMode(element[1]);
                } else if (GFEffectParameters.BASEWBOPTION.equals(element[0])) {
                    setBaseWBOption(element[1]);
                } else if (GFEffectParameters.FILTERISO.equals(element[0])) {
                    setFilterISO(element[1]);
                } else if (GFEffectParameters.FILTEREXPOSURECOMP.equals(element[0])) {
                    setFilterExposureComp(element[1]);
                } else if (GFEffectParameters.FILTERAPERTURE.equals(element[0])) {
                    setFilterAperture(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.FILTERSSNUMERATOR.equals(element[0])) {
                    setFilterSSNumerator(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.FILTERSSDENOMINATOR.equals(element[0])) {
                    setFilterSSDenominator(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.FILTERWBMODE.equals(element[0])) {
                    setFilterWBMode(element[1]);
                } else if (GFEffectParameters.FILTERWBOPTION.equals(element[0])) {
                    setFilterWBOption(element[1]);
                } else if (GFEffectParameters.FILTERWBR.equals(element[0])) {
                    setFilterWbR(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.FILTERWBB.equals(element[0])) {
                    setFilterWbB(Integer.valueOf(element[1]).intValue());
                }
            }
        }

        public void setDefaultValues(int theme) {
            this.mExpMode = Parameters.DEFAULT_EXP_MODE[theme];
            this.mCenterPoint = Parameters.DEFAULT_CENTER.get(theme);
            this.mDegree = Parameters.DEFAULT_DEGREE[theme];
            this.mStrength = Parameters.DEFAULT_STRENGTH[theme];
            this.mNDFilter = Parameters.DEFAULT_NDFILTER[theme];
            this.mColorFilter = Parameters.DEFAULT_COLORFILTER[theme];
            this.mFlashMode = Parameters.DEFAULT_FLASHMODE[theme];
            this.mFlashComp = Parameters.DEFAULT_FLASHCOMP[theme];
            this.mMeteringMode = Parameters.DEFAULT_METERINGMODE[theme];
            this.mDRO = Parameters.DEFAULT_DROMODE[theme];
            this.mCreativeStyle = Parameters.DEFAULT_CREATIVESTYLE[theme];
            this.mCreativeStyleOption = Parameters.DEFAULT_CREATIVESTYLEOPTION[theme];
            this.mFaceDetection = Parameters.DEFAULT_FACEDETECTION[theme];
            this.mBaseIso = Parameters.DEFAULT_BASE_ISO[theme];
            this.mBaseExposureComp = Parameters.DEFAULT_BASE_EXPCOMP[theme];
            this.mBaseAperture = Parameters.DEFAULT_BASE_APERTURE[theme];
            this.mBaseSsNumerator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).first).intValue();
            this.mBaseSsDenominator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).second).intValue();
            this.mBaseWBMode = Parameters.DEFAULT_BASE_WB[theme];
            this.mBaseWBOption = Parameters.DEFAULT_BASE_WB_OPTION[theme];
            this.mFilterIso = Parameters.DEFAULT_FILTER_ISO[theme];
            this.mFilterExposureComp = Parameters.DEFAULT_FILTER_EXPCOMP[theme];
            this.mFilterAperture = Parameters.DEFAULT_FILTER_APERTURE[theme];
            this.mFilterSsNumerator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).first).intValue();
            this.mFilterSsDenominator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).second).intValue();
            this.mFilterWBMode = Parameters.DEFAULT_FILTER_WB[theme];
            this.mFilterWBOption = Parameters.DEFAULT_FILTER_WB_OPTION[theme];
            this.mFilterWbR = Parameters.DEFAULT_FILTER_WB_R[theme];
            this.mFilterWbB = Parameters.DEFAULT_FILTER_WB_B[theme];
            if (theme == 0) {
                if (!GFCommonUtil.getInstance().isRX10() && GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 1100;
                    this.mFilterAperture = 1100;
                    this.mBaseSsNumerator = 1;
                    this.mBaseSsDenominator = 160;
                    this.mFilterSsNumerator = 1;
                    this.mFilterSsDenominator = 1250;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 1) {
                if (GFCommonUtil.getInstance().isRX10()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mBaseSsNumerator = 1;
                    this.mBaseSsDenominator = 2;
                    this.mFilterSsNumerator = 1;
                    this.mFilterSsDenominator = 5;
                    return;
                }
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mBaseSsNumerator = 10;
                    this.mBaseSsDenominator = 25;
                    this.mFilterSsNumerator = 1;
                    this.mFilterSsDenominator = 6;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 2) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 3) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 4 && GFCommonUtil.getInstance().isRX100()) {
                this.mBaseIso = ISOSensitivityController.ISO_125;
                this.mFilterIso = ISOSensitivityController.ISO_125;
            }
        }

        public String getDefaultFlatten(int theme) {
            String ExpMode = Parameters.DEFAULT_EXP_MODE[theme];
            Point CenterPoint = Parameters.DEFAULT_CENTER.get(theme);
            int Degree = Parameters.DEFAULT_DEGREE[theme];
            int Strength = Parameters.DEFAULT_STRENGTH[theme];
            String NDFilter = Parameters.DEFAULT_NDFILTER[theme];
            String ColorFilter = Parameters.DEFAULT_COLORFILTER[theme];
            String FlashMode = Parameters.DEFAULT_FLASHMODE[theme];
            String FlashComp = Parameters.DEFAULT_FLASHCOMP[theme];
            String MeteringMode = Parameters.DEFAULT_METERINGMODE[theme];
            String DRO = Parameters.DEFAULT_DROMODE[theme];
            String CreativeStyle = Parameters.DEFAULT_CREATIVESTYLE[theme];
            String CreativeStyleOption = Parameters.DEFAULT_CREATIVESTYLEOPTION[theme];
            String FaceDetection = Parameters.DEFAULT_FACEDETECTION[theme];
            String BaseIso = Parameters.DEFAULT_BASE_ISO[theme];
            String BaseExposureComp = Parameters.DEFAULT_BASE_EXPCOMP[theme];
            int BaseAperture = Parameters.DEFAULT_BASE_APERTURE[theme];
            int BaseSsNumerator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).first).intValue();
            int BaseSsDenominator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).second).intValue();
            String BaseWBMode = Parameters.DEFAULT_BASE_WB[theme];
            String BaseWBOption = Parameters.DEFAULT_BASE_WB_OPTION[theme];
            String FilterIso = Parameters.DEFAULT_FILTER_ISO[theme];
            String FilterExposureComp = Parameters.DEFAULT_FILTER_EXPCOMP[theme];
            int FilterAperture = Parameters.DEFAULT_FILTER_APERTURE[theme];
            int FilterSsNumerator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).first).intValue();
            int FilterSsDenominator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).second).intValue();
            String FilterWBMode = Parameters.DEFAULT_FILTER_WB[theme];
            String FilterWBOption = Parameters.DEFAULT_FILTER_WB_OPTION[theme];
            int FilterWbR = Parameters.DEFAULT_FILTER_WB_R[theme];
            int FilterWbB = Parameters.DEFAULT_FILTER_WB_B[theme];
            if (theme == 0) {
                if (!GFCommonUtil.getInstance().isRX10() && GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 1100;
                    FilterAperture = 1100;
                    BaseSsNumerator = 1;
                    BaseSsDenominator = 160;
                    FilterSsNumerator = 1;
                    FilterSsDenominator = 1250;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 1) {
                if (GFCommonUtil.getInstance().isRX10()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    BaseSsNumerator = 1;
                    BaseSsDenominator = 2;
                    FilterSsNumerator = 1;
                    FilterSsDenominator = 5;
                } else if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    BaseSsNumerator = 10;
                    BaseSsDenominator = 25;
                    FilterSsNumerator = 1;
                    FilterSsDenominator = 6;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 2) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 3) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 4 && GFCommonUtil.getInstance().isRX100()) {
                BaseIso = ISOSensitivityController.ISO_125;
                FilterIso = ISOSensitivityController.ISO_125;
            }
            StringBuilder f = new StringBuilder();
            f.append("ExpMode=").append(ExpMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Point=").append(CenterPoint.x).append(":").append(CenterPoint.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Degree=").append(Degree).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Strength=").append(Strength).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("NDFilter=").append(NDFilter).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("ColorFilter=").append(ColorFilter).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FlashMode=").append(FlashMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FlashComp=").append(FlashComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("MeteringMode=").append(MeteringMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("DRO=").append(DRO).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("CreativeStyle=").append(CreativeStyle).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("CreativeStyleOption=").append(CreativeStyleOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FaceDetection=").append(FaceDetection).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseIso=").append(BaseIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseExposureComp=").append(BaseExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseAperture=").append(BaseAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseSsNumerator=").append(BaseSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseSsDenominator=").append(BaseSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseWBmode=").append(BaseWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseWBOption=").append(BaseWBOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterIso=").append(FilterIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterExposureComp=").append(FilterExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterAperture=").append(FilterAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterSsNumerator=").append(FilterSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterSsDenominator=").append(FilterSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterWBmode=").append(FilterWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterWBOption=").append(FilterWBOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterWbR=").append(FilterWbR).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterWbB=").append(FilterWbB).append(MovieFormatController.Settings.SEMI_COLON);
            return f.toString();
        }

        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public BaseParameters m0clone() {
            try {
                BaseParameters p = (BaseParameters) super.clone();
                return p;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void update(ByteBuffer params) {
        }

        public void setExpMode(String mode) {
            this.mExpMode = mode;
        }

        public String getExpMode() {
            return this.mExpMode;
        }

        public void setPoint(Point p) {
            Rect limit = getLimitPoint();
            p.x = Math.max(p.x, limit.left);
            p.x = Math.min(p.x, limit.right);
            p.y = Math.max(p.y, limit.top);
            p.y = Math.min(p.y, limit.bottom);
            Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
            this.mCenterPoint = new Point(p);
        }

        public Point getPoint() {
            return new Point(this.mCenterPoint);
        }

        public Point getSAPoint(int width, int height) {
            return new Point((int) ((this.mCenterPoint.x / 1000.0d) * width), (int) ((this.mCenterPoint.y / 1000.0d) * height));
        }

        public Point getSAPoint(int rawWidth, int rawHeight, String aspectRatioOfPictureImage) {
            float imageAspectRatio;
            boolean isCroppedTopBottom;
            int height;
            int width;
            float inputAspectRatio = rawHeight / rawWidth;
            if (aspectRatioOfPictureImage.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                imageAspectRatio = 1.0f;
                isCroppedTopBottom = 1.0f < inputAspectRatio;
            } else if (aspectRatioOfPictureImage.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                imageAspectRatio = 0.6666667f;
                isCroppedTopBottom = 0.6666667f < inputAspectRatio;
            } else if (aspectRatioOfPictureImage.equalsIgnoreCase(PictureSizeController.ASPECT_16_9)) {
                imageAspectRatio = 0.5625f;
                isCroppedTopBottom = 0.5625f < inputAspectRatio;
            } else {
                imageAspectRatio = 0.75f;
                isCroppedTopBottom = 0.75f < inputAspectRatio;
            }
            if (isCroppedTopBottom) {
                width = rawWidth;
                height = (int) (rawWidth * imageAspectRatio);
            } else {
                height = rawHeight;
                width = (int) (rawHeight / imageAspectRatio);
            }
            Point point = GFEffectParameters.getInstance().getParameters().getSAPoint(width, height);
            if (isCroppedTopBottom) {
                point.y += (rawHeight - height) / 2;
            } else {
                point.x += (rawWidth - width) / 2;
            }
            return point;
        }

        public void setOSDPoint(PointF p) {
            RectF limit = getLimitOSDPoint();
            Log.i(GFEffectParameters.TAG, "Point x: " + p.x + " y: " + p.y + " are over limit!, limit : " + limit);
            p.x = Math.max(p.x, limit.left);
            p.x = Math.min(p.x, limit.right);
            p.y = Math.max(p.y, limit.top);
            p.y = Math.min(p.y, limit.bottom);
            Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
            int x = (1 == GFEffectParameters.mRotation && GFEffectParameters.mActiveDevice == 0) ? (int) ((((GFEffectParameters.mEERect.width() - p.x) + GFEffectParameters.mEERect.left) * 1000.0d) / GFEffectParameters.mEERect.width()) : (int) (((p.x - GFEffectParameters.mEERect.left) * 1000.0d) / GFEffectParameters.mEERect.width());
            Point point = new Point();
            point.x = x;
            point.y = (int) (((p.y - GFEffectParameters.mEERect.top) * 1000.0d) / GFEffectParameters.mEERect.height());
            setPoint(point);
        }

        public PointF getOSDPoint() {
            float x = (1 == GFEffectParameters.mRotation && GFEffectParameters.mActiveDevice == 0) ? (((1000.0f - this.mCenterPoint.x) / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left : ((this.mCenterPoint.x / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left;
            PointF point = new PointF(x, ((this.mCenterPoint.y / 1000.0f) * GFEffectParameters.mEERect.height()) + GFEffectParameters.mEERect.top);
            return point;
        }

        public RectF getLimitOSDPoint() {
            Rect rect = getLimitPoint();
            RectF ret = new RectF(((rect.left / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left, ((rect.top / 1000.0f) * GFEffectParameters.mEERect.height()) + GFEffectParameters.mEERect.top, ((rect.right / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left, ((rect.bottom / 1000.0f) * GFEffectParameters.mEERect.height()) + GFEffectParameters.mEERect.top);
            return ret;
        }

        public PointF getOSDSAPoint(int width, int height) {
            PointF ret = new PointF(((getOSDPoint().x - GFEffectParameters.mEERect.left) / GFEffectParameters.mEERect.width()) * width, ((getOSDPoint().y - GFEffectParameters.mEERect.top) / GFEffectParameters.mEERect.height()) * height);
            return ret;
        }

        public void setDegree(int d) {
            this.mDegree = d;
        }

        public int getDegree() {
            return this.mDegree;
        }

        public void setStrength(int s) {
            this.mStrength = s;
        }

        public int getStrength() {
            return this.mStrength;
        }

        public void setNDFilter(String n) {
            this.mNDFilter = n;
        }

        public String getNDFilter() {
            return this.mNDFilter;
        }

        public void setColorFilter(String c) {
            this.mColorFilter = c;
        }

        public String getColorFilter() {
            return this.mColorFilter;
        }

        public void setFlashMode(String f) {
            this.mFlashMode = f;
        }

        public String getFlashMode() {
            return this.mFlashMode;
        }

        public void setFlashComp(String f) {
            this.mFlashComp = f;
        }

        public String getFlashComp() {
            return this.mFlashComp;
        }

        public void setMeteringMode(String m) {
            this.mMeteringMode = m;
        }

        public String getMeteringMode() {
            return this.mMeteringMode;
        }

        public void setDRO(String d) {
            this.mDRO = d;
        }

        public String getDRO() {
            return this.mDRO;
        }

        public void setCreativeStyle(String c) {
            this.mCreativeStyle = c;
        }

        public String getCreativeStyle() {
            return this.mCreativeStyle;
        }

        public void setCreativeStyleOption(String c) {
            this.mCreativeStyleOption = c;
        }

        public String getCreativeStyleOption() {
            return this.mCreativeStyleOption;
        }

        public void setFaceDetection(String f) {
            this.mFaceDetection = f;
        }

        public String getFaceDetection() {
            return this.mFaceDetection;
        }

        public void setBaseISO(String i) {
            this.mBaseIso = i;
        }

        public String getBaseISO() {
            return this.mBaseIso;
        }

        public void setBaseExposureComp(String e) {
            this.mBaseExposureComp = e;
        }

        public String getBaseExposureComp() {
            return this.mBaseExposureComp;
        }

        public void setBaseAperture(int a) {
            this.mBaseAperture = a;
        }

        public int getBaseAperture() {
            return this.mBaseAperture;
        }

        public void setBaseSSNumerator(int s) {
            this.mBaseSsNumerator = s;
        }

        public int getBaseSSNumerator() {
            return this.mBaseSsNumerator;
        }

        public void setBaseSSDenominator(int s) {
            this.mBaseSsDenominator = s;
        }

        public int getBaseSSDenominator() {
            return this.mBaseSsDenominator;
        }

        public void setBaseWBMode(String w) {
            this.mBaseWBMode = w;
        }

        public String getBaseWBMode() {
            return this.mBaseWBMode;
        }

        public void setBaseWBOption(String w) {
            this.mBaseWBOption = w;
        }

        public void setFilterISO(String i) {
            this.mFilterIso = i;
        }

        public String getFilterISO() {
            return this.mFilterIso;
        }

        public void setFilterExposureComp(String e) {
            this.mFilterExposureComp = e;
        }

        public String getFilterExposureComp() {
            return this.mFilterExposureComp;
        }

        public void setFilterAperture(int a) {
            this.mFilterAperture = a;
        }

        public int getFilterAperture() {
            return this.mFilterAperture;
        }

        public void setFilterSSNumerator(int s) {
            this.mFilterSsNumerator = s;
        }

        public int getFilterSSNumerator() {
            return this.mFilterSsNumerator;
        }

        public void setFilterSSDenominator(int s) {
            this.mFilterSsDenominator = s;
        }

        public int getFilterSSDenominator() {
            return this.mFilterSsDenominator;
        }

        public void setFilterWBMode(String w) {
            this.mFilterWBMode = w;
        }

        public String getFilterWBMode() {
            return "OFF".equalsIgnoreCase(this.mColorFilter) ? this.mBaseWBMode : this.mFilterWBMode;
        }

        public void setFilterWBOption(String w) {
            this.mFilterWBOption = w;
        }

        public void setFilterWbR(int l) {
            this.mFilterWbR = l;
        }

        public int getFilterWbR() {
            return this.mFilterWbR;
        }

        public void setFilterWbB(int l) {
            this.mFilterWbB = l;
        }

        public int getFilterWbB() {
            return this.mFilterWbB;
        }

        public int getCustomWhiteBalanceNum(String itemId) {
            if (itemId.equals(WhiteBalanceController.CUSTOM) || itemId.equals("custom1")) {
                return 1;
            }
            if (itemId.equals("custom2")) {
                return 2;
            }
            if (!itemId.equals("custom3")) {
                return 1;
            }
            return 3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SkyBlueParameters extends BaseParameters {
        SkyBlueParameters() {
            setDefaultValues(0);
        }

        @Override // com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SunsetParameters extends BaseParameters {
        SunsetParameters() {
            setDefaultValues(1);
        }

        @Override // com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class StandardParameters extends BaseParameters {
        StandardParameters() {
            setDefaultValues(2);
        }

        @Override // com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Custom1Parameters extends BaseParameters {
        Custom1Parameters() {
            setDefaultValues(3);
        }

        @Override // com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Custom2Parameters extends BaseParameters {
        Custom2Parameters() {
            setDefaultValues(4);
        }

        @Override // com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Custom3Parameters extends BaseParameters {
        Custom3Parameters() {
            setDefaultValues(5);
        }

        @Override // com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    static void writeFile(String filename, DeviceBuffer data) {
        int size;
        byte[] buf = new byte[1048576];
        int offset = 0;
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + filename);
            file.delete();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            do {
                size = data.read(buf, 0, buf.length, offset);
                if (-1 == size) {
                    break;
                }
                offset += size;
                fos.write(buf, 0, size);
            } while (size == buf.length);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return null;
    }
}
