package com.sony.imaging.app.digitalfilter.shooting;

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
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.fw.AppRoot;
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
    public static final String BASEWBMODE = "BaseWBmode";
    private static final String COLORFILTER = "ColorFilter";
    private static final String CREATIVESTYLE = "CreativeStyle";
    private static final String CREATIVESTYLEOPTION = "CreativeStyleOption";
    private static final String DEGREE = "Degree";
    private static final String DEGREE2 = "Degree2";
    private static final String DRO = "DRO";
    private static final String EXPMODE = "ExpMode";
    private static final String FILTERAPERTURE = "FilterAperture";
    private static final String FILTEREXPOSURECOMP = "FilterExposureComp";
    private static final String FILTERISO = "FilterIso";
    private static final String FILTERSSDENOMINATOR = "FilterSsDenominator";
    private static final String FILTERSSNUMERATOR = "FilterSsNumerator";
    public static final String FILTERWBMODE = "FilterWBmode";
    private static final String FLASHCOMP = "FlashComp";
    private static final String FLASHMODE = "FlashMode";
    private static final String LAYER3APERTURE = "Layer3Aperture";
    private static final String LAYER3EXPOSURECOMP = "Layer3ExposureComp";
    private static final String LAYER3ISO = "Layer3Iso";
    private static final String LAYER3SSDENOMINATOR = "Layer3SsDenominator";
    private static final String LAYER3SSNUMERATOR = "Layer3SsNumerator";
    public static final String LAYER3WBMODE = "Layer3WBmode";
    private static final String METERINGMODE = "MeteringMode";
    private static final String POINT = "Point";
    private static final String POINT2 = "Point2";
    private static final String STRENGTH = "Strength";
    private static final String STRENGTH2 = "Strength2";
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
        public static final int BASE_SETTING = 0;
        public static final int CUSTOM1 = 3;
        public static final int CUSTOM2 = 4;
        public static final int FILTER_SETTING = 1;
        public static final int LAYER3_SETTING = 2;
        public static final String OFF = "OFF";
        public static final String ON = "ON";
        public static final int REVERSE = 5;
        public static final int SKYBLUE = 0;
        public static final int STANDARD = 2;
        public static final int STRIPE = 6;
        public static final int SUNSET = 1;
        static BaseParameters mParams;
        public static final int[] mShadingWidthTable = {IntervalRecExecutor.INTVL_REC_INITIALIZED, 190, 180, 170, 160, 150, 140, 130, 120, DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED, 100, 95, 90, 85, 80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5};
        public static int[] mSAStrength = new int[mShadingWidthTable.length];
        public static int[] mSAStrength2 = new int[mShadingWidthTable.length];
        public static final String[] DEFAULT_EXP_MODE = {"Aperture", "Aperture", "Aperture", "Aperture", "Aperture", "Aperture", "Aperture"};
        public static final List<Point> DEFAULT_CENTER = Arrays.asList(new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 500), new Point(500, 666));
        public static final int[] DEFAULT_DEGREE = {0, 0, 0, 0, 0, 0, 0};
        public static final int[] DEFAULT_STRENGTH = {14, 14, 14, 14, 14, 14, 14};
        public static final List<Point> DEFAULT_CENTER2 = Arrays.asList(new Point(500, 250), new Point(500, 250), new Point(500, 250), new Point(500, 250), new Point(500, 250), new Point(500, 250), new Point(500, 333));
        public static final int[] DEFAULT_DEGREE2 = {0, 0, 0, 0, 0, 0, 0};
        public static final int[] DEFAULT_STRENGTH2 = {14, 14, 14, 14, 14, 14, 14};
        public static final String[] DEFAULT_FLASHMODE = {FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC, FlashController.FRONTSYNC};
        public static final String[] DEFAULT_FLASHCOMP = {ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO};
        public static final String[] DEFAULT_METERINGMODE = {FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI, FocusAreaController.MULTI};
        public static final String[] DEFAULT_DROMODE = {"off", "off", "off", "off", "off", "off", "off"};
        public static final String[] DEFAULT_CREATIVESTYLE = {"standard", "sunset", "standard", "standard", "standard", "standard", "standard"};
        public static final String[] DEFAULT_CREATIVESTYLEOPTION = {"0/0/0", "0/1/0", "0/0/0", "0/0/0", "0/0/0", "0/0/0", "0/0/0"};
        public static final String[] DEFAULT_BASE_ISO = {"100", "100", "100", "100", "100", "100", "100"};
        public static final String[] DEFAULT_BASE_EXPCOMP = {"6", "2", "3", "3", "3", "3", ISOSensitivityController.ISO_AUTO};
        public static final int[] DEFAULT_BASE_APERTURE = {1600, 800, 800, 800, 800, 800, 800};
        public static final List<Pair> DEFAULT_BASE_SS = Arrays.asList(new Pair(1, 60), new Pair(1, 1), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250));
        public static final String[] DEFAULT_BASE_WB = {WhiteBalanceController.COLOR_TEMP, WhiteBalanceController.COLOR_TEMP, "auto", "auto", "auto", "auto", "auto"};
        public static final String[] DEFAULT_BASE_WB_OPTION = {"0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500"};
        public static final String[] DEFAULT_FILTER_ISO = {"100", "100", "100", "100", "100", "100", "100"};
        public static final String[] DEFAULT_FILTER_EXPCOMP = {"-3", "-2", "-6", "-6", "-6", "-6", ISOSensitivityController.ISO_AUTO};
        public static final int[] DEFAULT_FILTER_APERTURE = {1600, 800, 800, 800, 800, 800, 800};
        public static final List<Pair> DEFAULT_FILTER_SS = Arrays.asList(new Pair(1, 60), new Pair(1, 1), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250));
        public static final String[] DEFAULT_FILTER_WB = {WhiteBalanceController.COLOR_TEMP, WhiteBalanceController.COLOR_TEMP, "auto", "auto", "auto", "auto", "auto"};
        public static final String[] DEFAULT_FILTER_WB_OPTION = {"0/0/4000", "0/0/9000", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500"};
        public static final String[] DEFAULT_LAYER3_ISO = {"100", "100", "100", "100", "100", "100", "100"};
        public static final String[] DEFAULT_LAYER3_EXPCOMP = {ISOSensitivityController.ISO_AUTO, ISOSensitivityController.ISO_AUTO, "-3", "-3", "-3", "-3", "3"};
        public static final int[] DEFAULT_LAYER3_APERTURE = {1600, 800, 800, 800, 800, 800, 800};
        public static final List<Pair> DEFAULT_LAYER3_SS = Arrays.asList(new Pair(1, 60), new Pair(1, 1), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250), new Pair(1, 250));
        public static final String[] DEFAULT_LAYER3_WB = {WhiteBalanceController.COLOR_TEMP, WhiteBalanceController.COLOR_TEMP, "auto", "auto", "auto", "auto", "auto"};
        public static final String[] DEFAULT_LAYER3_WB_OPTION = {"0/0/4000", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500", "0/0/5500"};

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
            if (mParams instanceof ReverseParameters) {
                String ret6 = "Effect=5;" + mParams.flatten();
                return ret6;
            }
            if (!(mParams instanceof StripeParameters)) {
                return null;
            }
            String ret7 = "Effect=6;" + mParams.flatten();
            return ret7;
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
                        params = new ReverseParameters();
                        break;
                    case 6:
                        params = new StripeParameters();
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
            } else if (mParams instanceof ReverseParameters) {
                ret = "Effect=5;" + mParams.getDefaultFlatten(5);
            } else if (mParams instanceof StripeParameters) {
                ret = "Effect=6;" + mParams.getDefaultFlatten(6);
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
            if (mParams instanceof ReverseParameters) {
                return 5;
            }
            if (mParams instanceof StripeParameters) {
                return 6;
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

        public void setPoint2(Point p) {
            mParams.setPoint2(p);
        }

        public Point getPoint() {
            return mParams.getPoint();
        }

        public Point getPoint2() {
            return mParams.getPoint2();
        }

        public Point getSAPoint(int width, int height) {
            return mParams.getSAPoint(width, height);
        }

        public Point getSAPoint2(int width, int height) {
            return mParams.getSAPoint2(width, height);
        }

        public Point getSAPoint(int rawWidth, int rawHeight, String aspectRatioOfPictureImage) {
            return mParams.getSAPoint(rawWidth, rawHeight, aspectRatioOfPictureImage);
        }

        public Point getSAPoint2(int rawWidth, int rawHeight, String aspectRatioOfPictureImage) {
            return mParams.getSAPoint2(rawWidth, rawHeight, aspectRatioOfPictureImage);
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

        public void setOSDPoint2(PointF p) {
            RectF limit = getLimitOSDPoint();
            if (limit.contains(p.x, p.y)) {
                Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
                mParams.setOSDPoint2(p);
                return;
            }
            Log.i(GFEffectParameters.TAG, "Point x: " + p.x + " y: " + p.y + " are over limit!, limit : " + limit);
            p.x = Math.max(p.x, limit.left);
            p.x = Math.min(p.x, limit.right);
            p.y = Math.max(p.y, limit.top);
            p.y = Math.min(p.y, limit.bottom);
            Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
            mParams.setOSDPoint2(p);
        }

        public PointF getOSDPoint() {
            return mParams.getOSDPoint();
        }

        public PointF getOSDPoint2() {
            return mParams.getOSDPoint2();
        }

        public PointF getOSDPoint(Point centerPoint) {
            return mParams.getOSDPoint(centerPoint);
        }

        public RectF getLimitOSDPoint() {
            return mParams.getLimitOSDPoint();
        }

        public PointF getOSDSAPoint(int width, int height) {
            return mParams.getOSDSAPoint(width, height);
        }

        public PointF getOSDSAPoint2(int width, int height) {
            return mParams.getOSDSAPoint2(width, height);
        }

        public void setDegree(int d) {
            mParams.setDegree(d);
        }

        public void setDegree2(int d) {
            mParams.setDegree2(d);
        }

        public int getDegree() {
            return mParams.getDegree();
        }

        public int getDegree2() {
            return mParams.getDegree2();
        }

        public int getSADegree() {
            return 360 - mParams.getDegree();
        }

        public int getSADegree2() {
            return 360 - mParams.getDegree2();
        }

        public void setStrength(int s) {
            mParams.setStrength(s);
        }

        public void setStrength2(int s) {
            mParams.setStrength2(s);
        }

        public int getStrength() {
            return mParams.getStrength();
        }

        public int getStrength2() {
            return mParams.getStrength2();
        }

        public static void createStrengthArray() {
            for (int i = 0; i < mShadingWidthTable.length; i++) {
                int[] iArr = mSAStrength;
                int[] iArr2 = mSAStrength2;
                int i2 = BASE_COEF / mShadingWidthTable[(mShadingWidthTable.length - 1) - i];
                iArr2[i] = i2;
                iArr[i] = i2;
            }
        }

        public int getSAStrength() {
            return mSAStrength[mParams.getStrength()];
        }

        public int getSAStrength2() {
            return mSAStrength2[mParams.getStrength2()];
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

        public void setISO(int layer, String i) {
            if (layer == 0) {
                mParams.setBaseISO(i);
            } else if (layer == 1) {
                mParams.setFilterISO(i);
            } else if (layer == 2) {
                mParams.setLayer3ISO(i);
            }
        }

        public String getISO(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isISOLink(layer);
            if (isLink) {
                String theme = GFThemeController.getInstance().getValue();
                return GFBackUpKey.getInstance().getCommonISO(theme);
            }
            if (layer == 0) {
                return mParams.getBaseISO();
            }
            if (layer == 1) {
                return mParams.getFilterISO();
            }
            if (layer != 2) {
                Log.e(GFEffectParameters.TAG, "Layer(" + layer + ") is not found.");
                return null;
            }
            return mParams.getLayer3ISO();
        }

        public void setExposureComp(int layer, String e) {
            if (layer == 0) {
                mParams.setBaseExposureComp(e);
            } else if (layer == 1) {
                mParams.setFilterExposureComp(e);
            } else if (layer == 2) {
                mParams.setLayer3ExposureComp(e);
            }
        }

        public String getExposureComp(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isExpCompLink(layer);
            if (isLink) {
                String theme = GFThemeController.getInstance().getValue();
                return GFBackUpKey.getInstance().getCommonExpComp(theme);
            }
            if (layer == 0) {
                return mParams.getBaseExposureComp();
            }
            if (layer == 1) {
                return mParams.getFilterExposureComp();
            }
            if (layer != 2) {
                Log.e(GFEffectParameters.TAG, "Layer(" + layer + ") is not found.");
                return null;
            }
            return mParams.getLayer3ExposureComp();
        }

        public void setAperture(int layer, int a) {
            if (layer == 0) {
                mParams.setBaseAperture(a);
            } else if (layer == 1) {
                mParams.setFilterAperture(a);
            } else if (layer == 2) {
                mParams.setLayer3Aperture(a);
            }
        }

        public int getAperture(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isApertureLink(layer);
            if (isLink) {
                String theme = GFThemeController.getInstance().getValue();
                return GFBackUpKey.getInstance().getCommonAperture(theme);
            }
            if (layer == 0) {
                return mParams.getBaseAperture();
            }
            if (layer == 1) {
                return mParams.getFilterAperture();
            }
            if (layer != 2) {
                Log.e(GFEffectParameters.TAG, "Layer(" + layer + ") is not found.");
                return 0;
            }
            return mParams.getLayer3Aperture();
        }

        public void setSSNumerator(int layer, int s) {
            if (layer == 0) {
                mParams.setBaseSSNumerator(s);
            } else if (layer == 1) {
                mParams.setFilterSSNumerator(s);
            } else if (layer == 2) {
                mParams.setLayer3SSNumerator(s);
            }
        }

        public int getSSNumerator(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isSSLink(layer);
            if (isLink) {
                String theme = GFThemeController.getInstance().getValue();
                return GFBackUpKey.getInstance().getCommonSsNumerator(theme);
            }
            if (layer == 0) {
                return mParams.getBaseSSNumerator();
            }
            if (layer == 1) {
                return mParams.getFilterSSNumerator();
            }
            return mParams.getLayer3SSNumerator();
        }

        public void setSSDenominator(int layer, int s) {
            if (layer == 0) {
                mParams.setBaseSSDenominator(s);
            } else if (layer == 1) {
                mParams.setFilterSSDenominator(s);
            } else if (layer == 2) {
                mParams.setLayer3SSDenominator(s);
            }
        }

        public int getSSDenominator(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isSSLink(layer);
            if (isLink) {
                String theme = GFThemeController.getInstance().getValue();
                return GFBackUpKey.getInstance().getCommonSsDenominator(theme);
            }
            if (layer == 0) {
                return mParams.getBaseSSDenominator();
            }
            if (layer == 1) {
                return mParams.getFilterSSDenominator();
            }
            return mParams.getLayer3SSDenominator();
        }

        public void setWBMode(int layer, String w) {
            if (layer == 0) {
                mParams.setBaseWBMode(w);
            } else if (layer == 1) {
                mParams.setFilterWBMode(w);
            } else if (layer == 2) {
                mParams.setLayer3WBMode(w);
            }
        }

        public String getRawWBMode(int layer) {
            if (layer == 0) {
                return mParams.getBaseWBMode();
            }
            if (layer == 1) {
                return mParams.getFilterWBMode();
            }
            if (layer == 2) {
                return mParams.getLayer3WBMode();
            }
            return null;
        }

        public String getWBMode(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isWBLink(layer);
            if (isLink) {
                String theme = GFThemeController.getInstance().getValue();
                return GFBackUpKey.getInstance().getCommonWB(theme);
            }
            if (layer == 0) {
                return mParams.getBaseWBMode();
            }
            if (layer == 1) {
                return mParams.getFilterWBMode();
            }
            if (layer == 2) {
                return mParams.getLayer3WBMode();
            }
            return null;
        }

        public String getWBOption(int layer) {
            boolean isLink = GFLinkAreaController.getInstance().isWBLink(layer);
            String theme = GFThemeController.getInstance().getValue();
            if (isLink) {
                return GFBackUpKey.getInstance().getCommonWBOption(theme);
            }
            if (layer == 0) {
                return GFBackUpKey.getInstance().getWBOption(mParams.getBaseWBMode(), 0, theme);
            }
            if (layer == 1) {
                return GFBackUpKey.getInstance().getWBOption(mParams.getFilterWBMode(), 1, theme);
            }
            return GFBackUpKey.getInstance().getWBOption(mParams.getLayer3WBMode(), 2, theme);
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
        Point mCenterPoint;
        Point mCenterPoint2;
        String mColorFilter;
        String mCreativeStyle;
        String mCreativeStyleOption;
        String mDRO;
        int mDegree;
        int mDegree2;
        String mExpMode;
        int mFilterAperture;
        String mFilterExposureComp;
        String mFilterIso;
        int mFilterSsDenominator;
        int mFilterSsNumerator;
        String mFilterWBMode;
        String mFlashComp;
        String mFlashMode;
        int mLayer3Aperture;
        String mLayer3ExposureComp;
        String mLayer3Iso;
        int mLayer3SsDenominator;
        int mLayer3SsNumerator;
        String mLayer3WBMode;
        String mMeteringMode;
        int mStrength;
        int mStrength2;

        public abstract Rect getLimitPoint();

        BaseParameters() {
        }

        public String flatten() {
            StringBuilder f = new StringBuilder();
            f.append(GFEffectParameters.EXPMODE).append(MovieFormatController.Settings.EQUAL).append(this.mExpMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.POINT).append(MovieFormatController.Settings.EQUAL).append(this.mCenterPoint.x).append(":").append(this.mCenterPoint.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.DEGREE).append(MovieFormatController.Settings.EQUAL).append(this.mDegree).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.STRENGTH).append(MovieFormatController.Settings.EQUAL).append(this.mStrength).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.POINT2).append(MovieFormatController.Settings.EQUAL).append(this.mCenterPoint2.x).append(":").append(this.mCenterPoint2.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.DEGREE2).append(MovieFormatController.Settings.EQUAL).append(this.mDegree2).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.STRENGTH2).append(MovieFormatController.Settings.EQUAL).append(this.mStrength2).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FlashMode").append(MovieFormatController.Settings.EQUAL).append(this.mFlashMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FLASHCOMP).append(MovieFormatController.Settings.EQUAL).append(this.mFlashComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("MeteringMode").append(MovieFormatController.Settings.EQUAL).append(this.mMeteringMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.DRO).append(MovieFormatController.Settings.EQUAL).append(this.mDRO).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("CreativeStyle").append(MovieFormatController.Settings.EQUAL).append(this.mCreativeStyle).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.CREATIVESTYLEOPTION).append(MovieFormatController.Settings.EQUAL).append(this.mCreativeStyleOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEISO).append(MovieFormatController.Settings.EQUAL).append(this.mBaseIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEEXPOSURECOMP).append(MovieFormatController.Settings.EQUAL).append(this.mBaseExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEAPERTURE).append(MovieFormatController.Settings.EQUAL).append(this.mBaseAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASESSNUMERATOR).append(MovieFormatController.Settings.EQUAL).append(this.mBaseSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASESSDENOMINATOR).append(MovieFormatController.Settings.EQUAL).append(this.mBaseSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.BASEWBMODE).append(MovieFormatController.Settings.EQUAL).append(this.mBaseWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERISO).append(MovieFormatController.Settings.EQUAL).append(this.mFilterIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTEREXPOSURECOMP).append(MovieFormatController.Settings.EQUAL).append(this.mFilterExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERAPERTURE).append(MovieFormatController.Settings.EQUAL).append(this.mFilterAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERSSNUMERATOR).append(MovieFormatController.Settings.EQUAL).append(this.mFilterSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERSSDENOMINATOR).append(MovieFormatController.Settings.EQUAL).append(this.mFilterSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.FILTERWBMODE).append(MovieFormatController.Settings.EQUAL).append(this.mFilterWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.LAYER3ISO).append(MovieFormatController.Settings.EQUAL).append(this.mLayer3Iso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.LAYER3EXPOSURECOMP).append(MovieFormatController.Settings.EQUAL).append(this.mLayer3ExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.LAYER3APERTURE).append(MovieFormatController.Settings.EQUAL).append(this.mLayer3Aperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.LAYER3SSNUMERATOR).append(MovieFormatController.Settings.EQUAL).append(this.mLayer3SsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.LAYER3SSDENOMINATOR).append(MovieFormatController.Settings.EQUAL).append(this.mLayer3SsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append(GFEffectParameters.LAYER3WBMODE).append(MovieFormatController.Settings.EQUAL).append(this.mLayer3WBMode).append(MovieFormatController.Settings.SEMI_COLON);
            return f.toString();
        }

        public void unflatten(String f) {
            String[] dev = f.split(MovieFormatController.Settings.SEMI_COLON, -1);
            this.mColorFilter = null;
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
                } else if (GFEffectParameters.POINT2.equals(element[0])) {
                    String[] p2 = element[1].split(":");
                    setPoint2(new Point(Integer.valueOf(p2[0]).intValue(), Integer.valueOf(p2[1]).intValue()));
                } else if (GFEffectParameters.DEGREE2.equals(element[0])) {
                    setDegree2(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.STRENGTH2.equals(element[0])) {
                    setStrength2(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.COLORFILTER.equals(element[0])) {
                    this.mColorFilter = element[1];
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
                } else if (GFEffectParameters.LAYER3ISO.equals(element[0])) {
                    setLayer3ISO(element[1]);
                } else if (GFEffectParameters.LAYER3EXPOSURECOMP.equals(element[0])) {
                    setLayer3ExposureComp(element[1]);
                } else if (GFEffectParameters.LAYER3APERTURE.equals(element[0])) {
                    setLayer3Aperture(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.LAYER3SSNUMERATOR.equals(element[0])) {
                    setLayer3SSNumerator(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.LAYER3SSDENOMINATOR.equals(element[0])) {
                    setLayer3SSDenominator(Integer.valueOf(element[1]).intValue());
                } else if (GFEffectParameters.LAYER3WBMODE.equals(element[0])) {
                    setLayer3WBMode(element[1]);
                }
            }
            if (this.mColorFilter != null) {
            }
        }

        public void setDefaultValues(int theme) {
            this.mExpMode = Parameters.DEFAULT_EXP_MODE[theme];
            this.mCenterPoint = Parameters.DEFAULT_CENTER.get(theme);
            this.mDegree = Parameters.DEFAULT_DEGREE[theme];
            this.mStrength = Parameters.DEFAULT_STRENGTH[theme];
            this.mCenterPoint2 = Parameters.DEFAULT_CENTER2.get(theme);
            this.mDegree2 = Parameters.DEFAULT_DEGREE2[theme];
            this.mStrength2 = Parameters.DEFAULT_STRENGTH2[theme];
            this.mFlashMode = Parameters.DEFAULT_FLASHMODE[theme];
            this.mFlashComp = Parameters.DEFAULT_FLASHCOMP[theme];
            this.mMeteringMode = Parameters.DEFAULT_METERINGMODE[theme];
            this.mDRO = Parameters.DEFAULT_DROMODE[theme];
            this.mCreativeStyle = Parameters.DEFAULT_CREATIVESTYLE[theme];
            this.mCreativeStyleOption = Parameters.DEFAULT_CREATIVESTYLEOPTION[theme];
            this.mBaseIso = Parameters.DEFAULT_BASE_ISO[theme];
            this.mBaseExposureComp = Parameters.DEFAULT_BASE_EXPCOMP[theme];
            this.mBaseAperture = Parameters.DEFAULT_BASE_APERTURE[theme];
            this.mBaseSsNumerator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).first).intValue();
            this.mBaseSsDenominator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).second).intValue();
            this.mBaseWBMode = Parameters.DEFAULT_BASE_WB[theme];
            this.mFilterIso = Parameters.DEFAULT_FILTER_ISO[theme];
            this.mFilterExposureComp = Parameters.DEFAULT_FILTER_EXPCOMP[theme];
            this.mFilterAperture = Parameters.DEFAULT_FILTER_APERTURE[theme];
            this.mFilterSsNumerator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).first).intValue();
            this.mFilterSsDenominator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).second).intValue();
            this.mFilterWBMode = Parameters.DEFAULT_FILTER_WB[theme];
            this.mLayer3Iso = Parameters.DEFAULT_LAYER3_ISO[theme];
            this.mLayer3ExposureComp = Parameters.DEFAULT_LAYER3_EXPCOMP[theme];
            this.mLayer3Aperture = Parameters.DEFAULT_LAYER3_APERTURE[theme];
            this.mLayer3SsNumerator = ((Integer) Parameters.DEFAULT_LAYER3_SS.get(theme).first).intValue();
            this.mLayer3SsDenominator = ((Integer) Parameters.DEFAULT_LAYER3_SS.get(theme).second).intValue();
            this.mLayer3WBMode = Parameters.DEFAULT_LAYER3_WB[theme];
            if (theme == 0) {
                if (!GFCommonUtil.getInstance().isRX10() && GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 1100;
                    this.mFilterAperture = 1100;
                    this.mLayer3Aperture = 1100;
                    this.mBaseSsNumerator = 1;
                    this.mBaseSsDenominator = 160;
                    this.mFilterSsNumerator = 1;
                    this.mFilterSsDenominator = 160;
                    this.mLayer3SsNumerator = 1;
                    this.mLayer3SsDenominator = 160;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 1) {
                if (GFCommonUtil.getInstance().isRX10()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseSsNumerator = 1;
                    this.mBaseSsDenominator = 2;
                    this.mFilterSsNumerator = 1;
                    this.mFilterSsDenominator = 2;
                    this.mLayer3SsNumerator = 1;
                    this.mLayer3SsDenominator = 2;
                    return;
                }
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseSsNumerator = 10;
                    this.mBaseSsDenominator = 25;
                    this.mFilterSsNumerator = 10;
                    this.mFilterSsDenominator = 25;
                    this.mLayer3SsNumerator = 10;
                    this.mLayer3SsDenominator = 25;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 2) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 3) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 4) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 5) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                    return;
                }
                return;
            }
            if (theme == 6) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    this.mBaseAperture = 560;
                    this.mFilterAperture = 560;
                    this.mLayer3Aperture = 560;
                    this.mBaseIso = ISOSensitivityController.ISO_125;
                    this.mFilterIso = ISOSensitivityController.ISO_125;
                    this.mLayer3Iso = ISOSensitivityController.ISO_125;
                }
                if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    this.mBaseWBMode = "auto";
                    this.mFilterWBMode = "auto";
                    this.mLayer3WBMode = "auto";
                    return;
                }
                this.mFilterWBMode = WhiteBalanceController.COLOR_TEMP;
            }
        }

        public String getDefaultFlatten(int theme) {
            String ExpMode = Parameters.DEFAULT_EXP_MODE[theme];
            Point CenterPoint = Parameters.DEFAULT_CENTER.get(theme);
            int Degree = Parameters.DEFAULT_DEGREE[theme];
            int Strength = Parameters.DEFAULT_STRENGTH[theme];
            Point CenterPoint2 = Parameters.DEFAULT_CENTER2.get(theme);
            int Degree2 = Parameters.DEFAULT_DEGREE2[theme];
            int Strength2 = Parameters.DEFAULT_STRENGTH2[theme];
            String FlashMode = Parameters.DEFAULT_FLASHMODE[theme];
            String FlashComp = Parameters.DEFAULT_FLASHCOMP[theme];
            String MeteringMode = Parameters.DEFAULT_METERINGMODE[theme];
            String DRO = Parameters.DEFAULT_DROMODE[theme];
            String CreativeStyle = Parameters.DEFAULT_CREATIVESTYLE[theme];
            String CreativeStyleOption = Parameters.DEFAULT_CREATIVESTYLEOPTION[theme];
            String BaseIso = Parameters.DEFAULT_BASE_ISO[theme];
            String BaseExposureComp = Parameters.DEFAULT_BASE_EXPCOMP[theme];
            int BaseAperture = Parameters.DEFAULT_BASE_APERTURE[theme];
            int BaseSsNumerator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).first).intValue();
            int BaseSsDenominator = ((Integer) Parameters.DEFAULT_BASE_SS.get(theme).second).intValue();
            String BaseWBMode = Parameters.DEFAULT_BASE_WB[theme];
            String FilterIso = Parameters.DEFAULT_FILTER_ISO[theme];
            String FilterExposureComp = Parameters.DEFAULT_FILTER_EXPCOMP[theme];
            int FilterAperture = Parameters.DEFAULT_FILTER_APERTURE[theme];
            int FilterSsNumerator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).first).intValue();
            int FilterSsDenominator = ((Integer) Parameters.DEFAULT_FILTER_SS.get(theme).second).intValue();
            String FilterWBMode = Parameters.DEFAULT_FILTER_WB[theme];
            String Layer3Iso = Parameters.DEFAULT_LAYER3_ISO[theme];
            String Layer3ExposureComp = Parameters.DEFAULT_LAYER3_EXPCOMP[theme];
            int Layer3Aperture = Parameters.DEFAULT_LAYER3_APERTURE[theme];
            int Layer3SsNumerator = ((Integer) Parameters.DEFAULT_LAYER3_SS.get(theme).first).intValue();
            int Layer3SsDenominator = ((Integer) Parameters.DEFAULT_LAYER3_SS.get(theme).second).intValue();
            String Layer3WBMode = Parameters.DEFAULT_LAYER3_WB[theme];
            if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
                BaseWBMode = WhiteBalanceController.COLOR_TEMP;
                FilterWBMode = WhiteBalanceController.COLOR_TEMP;
                Layer3WBMode = WhiteBalanceController.COLOR_TEMP;
            }
            if (theme == 0) {
                if (!GFCommonUtil.getInstance().isRX10() && GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 1100;
                    FilterAperture = 1100;
                    Layer3Aperture = 1100;
                    BaseSsNumerator = 1;
                    BaseSsDenominator = 160;
                    FilterSsNumerator = 1;
                    FilterSsDenominator = 160;
                    Layer3SsNumerator = 1;
                    Layer3SsDenominator = 160;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 1) {
                if (GFCommonUtil.getInstance().isRX10()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseSsNumerator = 1;
                    BaseSsDenominator = 2;
                    FilterSsNumerator = 1;
                    FilterSsDenominator = 2;
                    Layer3SsNumerator = 1;
                    Layer3SsDenominator = 2;
                } else if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseSsNumerator = 10;
                    BaseSsDenominator = 25;
                    FilterSsNumerator = 10;
                    FilterSsDenominator = 25;
                    Layer3SsNumerator = 10;
                    Layer3SsDenominator = 25;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 2) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 3) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 4) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 5) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
            } else if (theme == 6) {
                if (GFCommonUtil.getInstance().isRX100()) {
                    BaseAperture = 560;
                    FilterAperture = 560;
                    Layer3Aperture = 560;
                    BaseIso = ISOSensitivityController.ISO_125;
                    FilterIso = ISOSensitivityController.ISO_125;
                    Layer3Iso = ISOSensitivityController.ISO_125;
                }
                if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    BaseWBMode = "auto";
                    FilterWBMode = "auto";
                    Layer3WBMode = "auto";
                } else {
                    FilterWBMode = WhiteBalanceController.COLOR_TEMP;
                }
            }
            StringBuilder f = new StringBuilder();
            f.append("ExpMode=").append(ExpMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Point=").append(CenterPoint.x).append(":").append(CenterPoint.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Degree=").append(Degree).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Strength=").append(Strength).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Point2=").append(CenterPoint2.x).append(":").append(CenterPoint2.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Degree2=").append(Degree2).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Strength2=").append(Strength2).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FlashMode=").append(FlashMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FlashComp=").append(FlashComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("MeteringMode=").append(MeteringMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("DRO=").append(DRO).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("CreativeStyle=").append(CreativeStyle).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("CreativeStyleOption=").append(CreativeStyleOption).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseIso=").append(BaseIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseExposureComp=").append(BaseExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseAperture=").append(BaseAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseSsNumerator=").append(BaseSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseSsDenominator=").append(BaseSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("BaseWBmode=").append(BaseWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterIso=").append(FilterIso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterExposureComp=").append(FilterExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterAperture=").append(FilterAperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterSsNumerator=").append(FilterSsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterSsDenominator=").append(FilterSsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("FilterWBmode=").append(FilterWBMode).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Layer3Iso=").append(Layer3Iso).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Layer3ExposureComp=").append(Layer3ExposureComp).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Layer3Aperture=").append(Layer3Aperture).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Layer3SsNumerator=").append(Layer3SsNumerator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Layer3SsDenominator=").append(Layer3SsDenominator).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Layer3WBmode=").append(Layer3WBMode).append(MovieFormatController.Settings.SEMI_COLON);
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

        public void setPoint2(Point p) {
            Rect limit = getLimitPoint();
            p.x = Math.max(p.x, limit.left);
            p.x = Math.min(p.x, limit.right);
            p.y = Math.max(p.y, limit.top);
            p.y = Math.min(p.y, limit.bottom);
            Log.i(GFEffectParameters.TAG, "set point.x = " + p.x + " point.y = " + p.y);
            this.mCenterPoint2 = new Point(p);
        }

        public Point getPoint2() {
            return new Point(this.mCenterPoint2);
        }

        public Point getSAPoint2(int width, int height) {
            return new Point((int) ((this.mCenterPoint2.x / 1000.0d) * width), (int) ((this.mCenterPoint2.y / 1000.0d) * height));
        }

        public Point getSAPoint2(int rawWidth, int rawHeight, String aspectRatioOfPictureImage) {
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
            Point point = GFEffectParameters.getInstance().getParameters().getSAPoint2(width, height);
            if (isCroppedTopBottom) {
                point.y += (rawHeight - height) / 2;
            } else {
                point.x += (rawWidth - width) / 2;
            }
            return point;
        }

        public void setOSDPoint2(PointF p) {
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
            setPoint2(point);
        }

        public PointF getOSDPoint2() {
            float x = (1 == GFEffectParameters.mRotation && GFEffectParameters.mActiveDevice == 0) ? (((1000.0f - this.mCenterPoint2.x) / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left : ((this.mCenterPoint2.x / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left;
            PointF point = new PointF(x, ((this.mCenterPoint2.y / 1000.0f) * GFEffectParameters.mEERect.height()) + GFEffectParameters.mEERect.top);
            return point;
        }

        public PointF getOSDSAPoint2(int width, int height) {
            PointF ret = new PointF(((getOSDPoint2().x - GFEffectParameters.mEERect.left) / GFEffectParameters.mEERect.width()) * width, ((getOSDPoint2().y - GFEffectParameters.mEERect.top) / GFEffectParameters.mEERect.height()) * height);
            return ret;
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

        public PointF getOSDPoint(Point centerPoint) {
            float x = (1 == GFEffectParameters.mRotation && GFEffectParameters.mActiveDevice == 0) ? (((1000.0f - centerPoint.x) / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left : ((centerPoint.x / 1000.0f) * GFEffectParameters.mEERect.width()) + GFEffectParameters.mEERect.left;
            PointF point = new PointF(x, ((centerPoint.y / 1000.0f) * GFEffectParameters.mEERect.height()) + GFEffectParameters.mEERect.top);
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

        public void setDegree2(int d) {
            this.mDegree2 = d;
        }

        public int getDegree2() {
            return this.mDegree2;
        }

        public void setStrength(int s) {
            this.mStrength = s;
        }

        public int getStrength() {
            return this.mStrength;
        }

        public void setStrength2(int s) {
            this.mStrength2 = s;
        }

        public int getStrength2() {
            return this.mStrength2;
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
            return this.mFilterWBMode;
        }

        public void setLayer3ISO(String i) {
            this.mLayer3Iso = i;
        }

        public String getLayer3ISO() {
            return this.mLayer3Iso;
        }

        public void setLayer3ExposureComp(String e) {
            this.mLayer3ExposureComp = e;
        }

        public String getLayer3ExposureComp() {
            return this.mLayer3ExposureComp;
        }

        public void setLayer3Aperture(int a) {
            this.mLayer3Aperture = a;
        }

        public int getLayer3Aperture() {
            return this.mLayer3Aperture;
        }

        public void setLayer3SSNumerator(int s) {
            this.mLayer3SsNumerator = s;
        }

        public int getLayer3SSNumerator() {
            return this.mLayer3SsNumerator;
        }

        public void setLayer3SSDenominator(int s) {
            this.mLayer3SsDenominator = s;
        }

        public int getLayer3SSDenominator() {
            return this.mLayer3SsDenominator;
        }

        public void setLayer3WBMode(String w) {
            this.mLayer3WBMode = w;
        }

        public String getLayer3WBMode() {
            return this.mLayer3WBMode;
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

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
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

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
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

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
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

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
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

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ReverseParameters extends BaseParameters {
        ReverseParameters() {
            setDefaultValues(5);
        }

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class StripeParameters extends BaseParameters {
        StripeParameters() {
            setDefaultValues(6);
        }

        @Override // com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters.BaseParameters
        public Rect getLimitPoint() {
            if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            }
            return new Rect(3, 5, 997, 995);
        }
    }

    static void writeFile(String filename, DeviceBuffer data) {
        int size;
        byte[] buf = new byte[GFConstants.JPEG_MAX_SIZE];
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
