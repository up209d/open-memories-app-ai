package com.sony.imaging.app.lightshaft.shooting;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;

/* loaded from: classes.dex */
public class ShaftsEffect extends NotificationManager {
    static final int ASPECT_11 = 3;
    static final int ASPECT_169 = 2;
    static final int ASPECT_32 = 1;
    static final int ASPECT_43 = 4;
    static final int ASPECT_UNKNOWN = 0;
    public static final String TAG_CHANGE = "ShaftsEffect.Changed";
    private static int mActiveDevice = 0;
    private static ByteBuffer mBParams = null;
    private static ByteBuffer mBRayTable = null;
    private static Rect mEERect = null;
    private static String mPackageName = null;
    public static int mRotation = 0;
    private static Parameters mSettingParams = null;
    private static ShaftsEffect mTheInstance = new ShaftsEffect();
    private static final int us_resize_count = 2;
    NotificationListener mListener = new ChangeYUVNotifier();

    public static ShaftsEffect getInstance() {
        return mTheInstance;
    }

    public void initialize(String packageName) {
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mBParams = ByteBuffer.allocateDirect(104);
        mBParams.order(ByteOrder.nativeOrder());
        mBRayTable = ByteBuffer.allocateDirect(128);
        mBRayTable.order(ByteOrder.nativeOrder());
        mPackageName = packageName;
        Parameters p = new Parameters();
        p.setEffect(1);
        setParameters(p);
        mEERect = new Rect(0, 0, AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
        DisplayModeObserver disp = DisplayModeObserver.getInstance();
        if (disp == null) {
            mRotation = 4;
        } else {
            mRotation = disp.getActiveDeviceStatus().viewPattern;
        }
        mActiveDevice = 0;
        ByteBuffer params = mBParams;
        params.position(24);
        params.putShort((short) 2);
        params.putShort((short) 0);
        params.putShort((short) 1);
        params.putShort((short) 0);
        params.position(36);
        params.putInt(0);
        params.putInt(0);
        params.putInt(0);
        params.putInt(0);
        params.putInt(0);
        params.position(68);
        params.putShort((short) 0);
        params.putShort((short) 0);
        params.position(85);
        params.putShort((short) 0);
        params.putShort((short) 0);
        params.putShort((short) 0);
    }

    public void terminate() {
        if (this.mListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        }
    }

    /* loaded from: classes.dex */
    public static class Parameters implements Cloneable {
        public static final int ANGEL = 1;
        public static final int BEAM = 4;
        public static final int FLARE = 3;
        public static final int STAR = 2;
        BaseParameters mParams;

        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public Parameters m1clone() {
            Parameters p = null;
            try {
                p = (Parameters) super.clone();
                p.mParams = this.mParams.m0clone();
                return p;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return p;
            }
        }

        public String flatten() {
            String ret = null;
            if (this.mParams instanceof AngelParameters) {
                ret = "Effect=1;" + this.mParams.flatten();
            }
            if (this.mParams instanceof StarParameters) {
                ret = "Effect=2;" + this.mParams.flatten();
            }
            if (this.mParams instanceof FlareParameters) {
                ret = "Effect=3;" + this.mParams.flatten();
            }
            if (this.mParams instanceof BeamParameters) {
                String ret2 = "Effect=4;" + this.mParams.flatten();
                return ret2;
            }
            return ret;
        }

        public void unflatten(String f) {
            BaseParameters params;
            int index = f.indexOf("Effect=");
            if (-1 != index) {
                int start = index + "Effect=".length();
                int e = Integer.parseInt(f.substring(start, start + 1));
                AppLog.info("ShaftEffect", "doItemClickProcessing unflatten = " + e);
                switch (e) {
                    case 1:
                        params = new AngelParameters();
                        break;
                    case 2:
                        params = new StarParameters();
                        break;
                    case 3:
                        params = new FlareParameters();
                        break;
                    case 4:
                        params = new BeamParameters();
                        break;
                    default:
                        throw new InvalidParameterException("Effect " + e + " does not support value");
                }
            } else {
                params = new AngelParameters();
            }
            params.unflatten(f);
            this.mParams = params;
        }

        protected void update(ByteBuffer params) {
            this.mParams.update(params);
        }

        public void setEffect(int e) {
            unflatten("Effect=" + e);
        }

        public int getEffect() {
            if (this.mParams instanceof AngelParameters) {
                return 1;
            }
            if (this.mParams instanceof StarParameters) {
                return 2;
            }
            if (this.mParams instanceof FlareParameters) {
                return 3;
            }
            if (this.mParams instanceof BeamParameters) {
                return 4;
            }
            return -1;
        }

        public void setPoint(Point p) {
            this.mParams.setPoint(p);
        }

        public Point getPoint() {
            return this.mParams.getPoint();
        }

        Point getSAPoint(int width, int height) {
            return this.mParams.getSAPoint(width, height);
        }

        public Rect getLimitPoint() {
            return this.mParams.getLimitPoint();
        }

        public void setOSDPoint(PointF p) {
            this.mParams.setOSDPoint(p);
        }

        public PointF getOSDPoint() {
            return this.mParams.getOSDPoint();
        }

        public RectF getLimitOSDPoint() {
            return this.mParams.getLimitOSDPoint();
        }

        public void setRange(int r) {
            this.mParams.setRange(r);
        }

        public int getRange() {
            return this.mParams.getRange();
        }

        public int getLimitRange() {
            return this.mParams.getLimitRange();
        }

        public float getRangeDegree() {
            return this.mParams.getRangeDegree();
        }

        public void setDirection(int d) {
            this.mParams.setDirection(d);
        }

        public int getDirection() {
            return this.mParams.getDirection();
        }

        public int getLimitDirection() {
            return this.mParams.getLimitDirection();
        }

        public float getDirectionDegree() {
            return this.mParams.getDirectionDegree();
        }

        public void setStrength(int s) {
            this.mParams.setStrength(s);
        }

        public int getStrength() {
            return this.mParams.getStrength();
        }

        public int getLimitStrength() {
            return this.mParams.getLimitStrength();
        }

        public void setLength(int l) {
            this.mParams.setLength(l);
        }

        public int getLength() {
            return this.mParams.getLength();
        }

        public int getLimitLength() {
            return this.mParams.getLimitLength();
        }

        public int getOSDLength() {
            return this.mParams.getOSDLength();
        }

        public int getOSDLength(int l) {
            return this.mParams.getOSDLength(l);
        }

        public void setNumberOfShafts(int n) {
            this.mParams.setNumberOfShafts(n);
        }

        public int getNumberOfShafts() {
            return this.mParams.getNumberOfShafts();
        }

        public int getLimitNumberOfShafts() {
            return this.mParams.getLimitNumberOfShafts();
        }
    }

    public Parameters getParameters() {
        return mSettingParams.m1clone();
    }

    public void setParameters(Parameters p) {
        mSettingParams = p.m1clone();
        mSettingParams.update(mBParams);
        notify(TAG_CHANGE);
    }

    public OptimizedImage run(OptimizedImage original, CameraSequence sequence) {
        Point point;
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        String filePath = "/android/data/lib/" + getPackageName() + "/lib/liblightshafts_top.so";
        dsp.setProgram(filePath);
        DeviceBuffer boot_param = prepare_sa_bootparam(dsp);
        int in_adr = dsp.getPropertyAsInt(original, "memory-address");
        int cwidth = dsp.getPropertyAsInt(original, "image-canvas-width");
        int original_offset = dsp.getPropertyAsInt(original, "image-data-offset");
        int in_adr2 = in_adr + original_offset;
        int width = original.getWidth();
        int height = original.getHeight();
        int out_adr = dsp.getPropertyAsInt(original, "memory-address");
        int copy_offset = dsp.getPropertyAsInt(original, "image-data-offset");
        int out_adr2 = out_adr + copy_offset;
        DeviceBuffer sa_work = dsp.createBuffer(((((((width + 63) * height) >> 4) + 4095) & CustomKeyMgr.STATUS_NOT_EXIST) * 5) + 4096);
        int work_adr = dsp.getPropertyAsInt(sa_work, "memory-address");
        DeviceBuffer ray_table = dsp.createBuffer(128);
        int ray_table_adr = dsp.getPropertyAsInt(ray_table, "memory-address");
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
        Camera.Size s = ((Camera.Parameters) p.first).getPictureSize();
        Parameters param = getParameters();
        Log.i("ShaftsEffect::run", "##### Imager  Size : width    = " + width);
        Log.i("ShaftsEffect::run", "##### Imager  Size : height   = " + height);
        Log.i("ShaftsEffect::run", "##### Picture Size : s.width  = " + s.width);
        Log.i("ShaftsEffect::run", "##### Picture Size : s.height = " + s.height);
        if (width == s.width || height == s.height) {
            Log.i("ShaftsEffect::getAspect", "##### using Full Area for Imager");
            point = param.getSAPoint(s.width, s.height);
            point.x += (width - s.width) / 2;
            point.y += (height - s.height) / 2;
        } else {
            Log.i("ShaftsEffect::getAspect", "##### using Limited Area for Imager");
            point = param.getSAPoint(width, height);
            if (1 != getAspect(s.width, s.height) && 2 == getAspect(s.width, s.height)) {
                point.y = ((int) (((height * 5.0f) / 32.0f) / 2.0f)) + ((int) ((point.y * 27.0f) / 32.0f));
            }
        }
        ByteBuffer bParams = mBParams;
        bParams.position(32);
        bParams.putShort((short) point.x);
        bParams.putShort((short) point.y);
        bParams.rewind();
        bParams.putInt(in_adr2 & 1073741823);
        bParams.putInt(out_adr2 & 1073741823);
        bParams.putInt(work_adr & 1073741823);
        bParams.putInt(ray_table_adr & 1073741823);
        bParams.putShort((short) width);
        bParams.putShort((short) height);
        bParams.putShort((short) cwidth);
        bParams.putShort((short) cwidth);
        DeviceBuffer params = dsp.createBuffer(104);
        params.write(bParams);
        ray_table.write(mBRayTable);
        dsp.setArg(0, boot_param);
        dsp.setArg(1, params);
        dsp.setArg(2, original);
        dsp.setArg(3, original);
        dsp.setArg(4, sa_work);
        dsp.setArg(5, ray_table);
        if (dsp.execute()) {
            Log.i("ShaftsEffect", "sa success");
        } else {
            Log.i("ShaftsEffect", "sa Failed");
        }
        ray_table.release();
        sa_work.release();
        dsp.clearProgram();
        dsp.release();
        boot_param.release();
        params.release();
        return original;
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

    protected ShaftsEffect() {
    }

    private int getpfVersion() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        return pfMajorVersion;
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
            Log.i("TAMA", "onNotify: " + tag);
            DisplayModeObserver disp = DisplayModeObserver.getInstance();
            if (DisplayModeObserver.TAG_YUVLAYOUT_CHANGE.equals(tag)) {
                DisplayManager.VideoRect rect = (DisplayManager.VideoRect) disp.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                Log.i("TAMA", "l: " + rect.pxLeft + " r: " + rect.pxRight + " t: " + rect.pxTop + " b: " + rect.pxBottom);
                ShaftsEffect.mEERect.left = rect.pxLeft;
                ShaftsEffect.mEERect.top = rect.pxTop;
                ShaftsEffect.mEERect.right = rect.pxRight;
                ShaftsEffect.mEERect.bottom = rect.pxBottom;
                Log.i("TAMA", "width: " + ShaftsEffect.mEERect.width() + " height: " + ShaftsEffect.mEERect.height());
                ShaftsEffect.mTheInstance.notify(ShaftsEffect.TAG_CHANGE);
                return;
            }
            if (DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE.equals(tag)) {
                DisplayManager.DeviceStatus status = disp.getActiveDeviceStatus();
                if (status == null) {
                    current2 = 4;
                } else {
                    current2 = disp.getActiveDeviceStatus().viewPattern;
                }
                if (ShaftsEffect.mRotation != current2) {
                    ShaftsEffect.mRotation = current2;
                    ShaftsEffect.mTheInstance.notify(ShaftsEffect.TAG_CHANGE);
                    return;
                }
                return;
            }
            if (DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag) && ShaftsEffect.mActiveDevice != (current = disp.getActiveDevice())) {
                int unused = ShaftsEffect.mActiveDevice = current;
                ShaftsEffect.mTheInstance.notify(ShaftsEffect.TAG_CHANGE);
            }
        }
    }

    private String getPackageName() {
        return mPackageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class BaseParameters implements Cloneable {
        int mDirection;
        int mLength;
        Point mLightSource;
        int mNumberOfShafts;
        int mRange;
        int mStrength;

        abstract short getLengthSAParameterValue(int i);

        public abstract Rect getLimitPoint();

        BaseParameters() {
        }

        public String flatten() {
            StringBuilder f = new StringBuilder();
            f.append("Point=").append(this.mLightSource.x).append(":").append(this.mLightSource.y).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Strength=").append(this.mStrength).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Length=").append(this.mLength).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Range=").append(this.mRange).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("Direction=").append(this.mDirection).append(MovieFormatController.Settings.SEMI_COLON);
            f.append("NumberOfShafts=").append(this.mNumberOfShafts).append(MovieFormatController.Settings.SEMI_COLON);
            return f.toString();
        }

        public void unflatten(String f) {
            String[] dev = f.split(MovieFormatController.Settings.SEMI_COLON, -1);
            for (String str : dev) {
                String[] element = str.split(MovieFormatController.Settings.EQUAL);
                if (!"Point".equals(element[0])) {
                    if (!"Strength".equals(element[0])) {
                        if (!"Length".equals(element[0])) {
                            if (!"Range".equals(element[0])) {
                                if (!"Direction".equals(element[0])) {
                                    if ("NumberOfShafts".equals(element[0])) {
                                        setNumberOfShafts(Integer.valueOf(element[1]).intValue());
                                    }
                                } else {
                                    setDirection(Integer.valueOf(element[1]).intValue());
                                }
                            } else {
                                setRange(Integer.valueOf(element[1]).intValue());
                            }
                        } else {
                            setLength(Integer.valueOf(element[1]).intValue());
                        }
                    } else {
                        setStrength(Integer.valueOf(element[1]).intValue());
                    }
                } else {
                    String[] p = element[1].split(":");
                    setPoint(new Point(Integer.valueOf(p[0]).intValue(), Integer.valueOf(p[1]).intValue()));
                }
            }
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

        public void setPoint(Point p) {
            Rect limit = getLimitPoint();
            if (!limit.contains(p.x, p.y)) {
                throw new InvalidParameterException("Point x: " + p.x + " y: " + p.y + " are over limit!");
            }
            this.mLightSource = new Point(p);
        }

        public Point getPoint() {
            return new Point(this.mLightSource);
        }

        public Point getSAPoint(int width, int height) {
            return new Point((int) ((this.mLightSource.x / 1000.0d) * width), (int) ((this.mLightSource.y / 1000.0d) * height));
        }

        public void setOSDPoint(PointF p) {
            RectF limit = getLimitOSDPoint();
            if (!limit.contains(p.x, p.y)) {
                throw new InvalidParameterException("Point x: " + p.x + " y: " + p.y + " are over limit!");
            }
            int x = (1 == ShaftsEffect.mRotation && ShaftsEffect.mActiveDevice == 0) ? (int) ((((ShaftsEffect.mEERect.width() - p.x) + ShaftsEffect.mEERect.left) * 1000.0d) / ShaftsEffect.mEERect.width()) : (int) (((p.x - ShaftsEffect.mEERect.left) * 1000.0d) / ShaftsEffect.mEERect.width());
            Point point = new Point();
            point.x = x;
            point.y = (int) (((p.y - ShaftsEffect.mEERect.top) * 1000.0d) / ShaftsEffect.mEERect.height());
            Log.i("TAMA", "setPoint: " + point.toString());
            Log.i("TAMA", "setOSDPoint: PointF(" + p.x + LogHelper.MSG_COMMA + p.y + LogHelper.MSG_CLOSE_BRACKET);
            setPoint(point);
        }

        public PointF getOSDPoint() {
            float x = (1 == ShaftsEffect.mRotation && ShaftsEffect.mActiveDevice == 0) ? (((1000.0f - this.mLightSource.x) / 1000.0f) * ShaftsEffect.mEERect.width()) + ShaftsEffect.mEERect.left : ((this.mLightSource.x / 1000.0f) * ShaftsEffect.mEERect.width()) + ShaftsEffect.mEERect.left;
            PointF point = new PointF(x, ((this.mLightSource.y / 1000.0f) * ShaftsEffect.mEERect.height()) + ShaftsEffect.mEERect.top);
            Log.i("TAMA", "getPoint: " + this.mLightSource.toString());
            Log.i("TAMA", "getOSDPoint: PointF(" + point.x + LogHelper.MSG_COMMA + point.y + LogHelper.MSG_CLOSE_BRACKET);
            return point;
        }

        public RectF getLimitOSDPoint() {
            Rect rect = getLimitPoint();
            RectF ret = new RectF(((rect.left / 1000.0f) * ShaftsEffect.mEERect.width()) + ShaftsEffect.mEERect.left, ((rect.top / 1000.0f) * ShaftsEffect.mEERect.height()) + ShaftsEffect.mEERect.top, ((rect.right / 1000.0f) * ShaftsEffect.mEERect.width()) + ShaftsEffect.mEERect.left, ((rect.bottom / 1000.0f) * ShaftsEffect.mEERect.height()) + ShaftsEffect.mEERect.top);
            return ret;
        }

        public void setStrength(int s) {
            if (s >= getLimitStrength()) {
                throw new InvalidParameterException("Strength " + s + " is over limit!");
            }
            this.mStrength = s;
        }

        public int getStrength() {
            return this.mStrength;
        }

        public int getLimitStrength() {
            return 11;
        }

        public void setLength(int l) {
            if (l >= getLimitLength()) {
                throw new InvalidParameterException("Length " + l + " is over limit!");
            }
            if (l < 0) {
                throw new InvalidParameterException("Length " + l + " is under limit!");
            }
            this.mLength = l;
        }

        public int getLength() {
            return this.mLength;
        }

        public int getLimitLength() {
            return 11;
        }

        public int getOSDLength(int l) {
            int v = getLengthSAParameterValue(l);
            if (v < 0) {
                v += 65536;
            }
            float lengthRatio = v / 16384.0f;
            return (int) (ShaftsEffect.mEERect.height() * lengthRatio);
        }

        public int getOSDLength() {
            return getOSDLength(this.mLength);
        }

        public void setRange(int r) {
            if (r >= getLimitRange()) {
                throw new InvalidParameterException("Range " + r + " is over limit!");
            }
            if (r < 0) {
                throw new InvalidParameterException("Range " + r + " is under limit!");
            }
            this.mRange = r;
        }

        public int getRange() {
            return this.mRange;
        }

        public int getLimitRange() {
            return 32;
        }

        public float getRangeDegree() {
            return 11.25f * (this.mRange + 1);
        }

        public void setDirection(int d) {
            if (d < 0) {
                d = getLimitDirection() - 1;
            } else if (d >= getLimitDirection()) {
                d = 0;
            }
            this.mDirection = d;
        }

        public int getDirection() {
            return this.mDirection;
        }

        public int getLimitDirection() {
            return 32;
        }

        public float getDirectionDegree() {
            int direction = 0;
            if (1 == ShaftsEffect.mRotation && ShaftsEffect.mActiveDevice == 0) {
                if (this.mDirection >= 0 && this.mDirection < 8) {
                    direction = 16 - this.mDirection;
                } else if (8 <= this.mDirection && this.mDirection < 16) {
                    direction = 16 - this.mDirection;
                } else if (16 <= this.mDirection && this.mDirection < 24) {
                    direction = 48 - this.mDirection;
                } else if (24 <= this.mDirection && this.mDirection < 32) {
                    direction = 48 - this.mDirection;
                }
            } else {
                direction = this.mDirection;
            }
            return 11.25f * (direction % 32);
        }

        public void setNumberOfShafts(int n) {
            this.mNumberOfShafts = n;
        }

        public int getNumberOfShafts() {
            return this.mNumberOfShafts;
        }

        public int getLimitNumberOfShafts() {
            return 8;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AngelParameters extends BaseParameters {
        private static final short[] us_area_circle = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
        private static final short[] us_area_rad_ratio = {3687, 7004, 10322, 13640, 16957, 20275, 23593, 26911, 30228, -31990, -28672};
        private static final short[] us_flare_gain = {26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26};
        private static final short[] us_ray_gain = {128, 153, 179, 204, 230, 256, 281, 307, 332, 358, 384};
        private static final short[] us_len1_0_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len1_1_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_0_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_1_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_1u_ratio = {26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26};
        private static final short[] us_ray_slope1 = {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128};
        private static final short[] us_ray_intercept1 = {33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33};
        private static final short[] us_ray_slope2 = {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};
        private static final short[] us_ray_intercept2 = {70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70};
        private static final short[] us_ray_offset = {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};

        AngelParameters() {
            this.mLightSource = new Point(500, 100);
            this.mStrength = 5;
            this.mLength = 10;
            this.mRange = 8;
            this.mDirection = 8;
            this.mNumberOfShafts = -1;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public Rect getLimitPoint() {
            return new Rect(-125, -125, 1125, 1125);
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public short getLengthSAParameterValue(int l) {
            return us_area_rad_ratio[l];
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public void update(ByteBuffer params) {
            ShaftsEffect.make_ray_en(ShaftsEffect.mBRayTable, 1, (this.mRange + 1) * 4, this.mDirection * 4);
            params.position(56);
            params.putShort((short) 1);
            params.putShort(us_area_circle[this.mLength]);
            params.putShort((short) 0);
            params.putShort(us_area_rad_ratio[this.mLength]);
            params.putShort((short) 0);
            params.putShort((short) 10);
            params.position(72);
            params.putShort(us_flare_gain[this.mStrength]);
            params.putShort(us_ray_gain[this.mStrength]);
            params.putShort(us_len1_0_ratio[this.mStrength]);
            params.putShort(us_len1_1_ratio[this.mStrength]);
            params.putShort(us_len2_0_ratio[this.mStrength]);
            params.putShort(us_len2_1_ratio[this.mStrength]);
            params.putShort(us_len2_1u_ratio[this.mStrength]);
            params.position(92);
            params.putShort(us_ray_slope1[this.mStrength]);
            params.putShort(us_ray_intercept1[this.mStrength]);
            params.putShort(us_ray_slope2[this.mStrength]);
            params.putShort(us_ray_intercept2[this.mStrength]);
            params.putShort(us_ray_offset[this.mStrength]);
            params.putShort((short) 0);
            super.update(params);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class StarParameters extends BaseParameters {
        private static final short[] us_area_circle = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
        private static final short[] us_area_rad_ratio = {1638, 3113, 4588, 6062, 7537, 9011, 10486, 11960, 13435, 14909, 16384};
        private static final short[] us_flare_gain = {64, 69, 74, 79, 84, 90, 95, 100, 105, 110, 115};
        private static final short[] us_ray_gain = {64, 102, 141, 179, 218, 256, 294, 333, 371, 410, 448};
        private static final short[] us_len1_0_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len1_1_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_0_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_1_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_1u_ratio = {26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26};
        private static final short[] us_ray_slope1 = {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128};
        private static final short[] us_ray_intercept1 = {33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33};
        private static final short[] us_ray_slope2 = {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};
        private static final short[] us_ray_intercept2 = {70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70};
        private static final short[] us_ray_offset = {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};

        StarParameters() {
            this.mLightSource = new Point(500, 500);
            this.mStrength = 5;
            this.mLength = 10;
            this.mRange = -1;
            this.mDirection = 4;
            this.mNumberOfShafts = 4;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public Rect getLimitPoint() {
            return new Rect(0, 0, 1000, 1000);
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public int getLimitRange() {
            return 0;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public void setRange(int r) {
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public short getLengthSAParameterValue(int l) {
            return us_area_rad_ratio[l];
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public void update(ByteBuffer params) {
            ShaftsEffect.make_ray_en(ShaftsEffect.mBRayTable, this.mNumberOfShafts, 2, this.mDirection * 4);
            params.position(56);
            params.putShort((short) 2);
            params.putShort(us_area_circle[this.mLength]);
            params.putShort((short) 0);
            params.putShort(us_area_rad_ratio[this.mLength]);
            params.putShort((short) 0);
            params.putShort((short) 10);
            params.position(72);
            params.putShort(us_flare_gain[this.mStrength]);
            params.putShort(us_ray_gain[this.mStrength]);
            params.putShort(us_len1_0_ratio[this.mStrength]);
            params.putShort(us_len1_1_ratio[this.mStrength]);
            params.putShort(us_len2_0_ratio[this.mStrength]);
            params.putShort(us_len2_1_ratio[this.mStrength]);
            params.putShort(us_len2_1u_ratio[this.mStrength]);
            params.position(92);
            params.putShort(us_ray_slope1[this.mStrength]);
            params.putShort(us_ray_intercept1[this.mStrength]);
            params.putShort(us_ray_slope2[this.mStrength]);
            params.putShort(us_ray_intercept2[this.mStrength]);
            params.putShort(us_ray_offset[this.mStrength]);
            params.putShort((short) 0);
            super.update(params);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FlareParameters extends BaseParameters {
        private static final short[] us_area_circle = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
        private static final short[] us_area_rad_ratio = {1638, 3113, 4588, 6062, 7537, 9011, 10486, 11960, 13435, 14909, 16384};
        private static final short[] us_flare_gain = {13, 19, 26, 32, 38, 45, 51, 58, 64, 70, 77};
        private static final short[] us_ray_gain = {51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51};
        private static final short[] us_len1_0_ratio = {8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
        private static final short[] us_len1_1_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_0_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_1_ratio = {38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38};
        private static final short[] us_len2_1u_ratio = {26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26};
        private static final short[] us_ray_slope1 = {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128};
        private static final short[] us_ray_intercept1 = {33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33};
        private static final short[] us_ray_slope2 = {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};
        private static final short[] us_ray_intercept2 = {70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70};
        private static final short[] us_ray_offset = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        FlareParameters() {
            this.mLightSource = new Point(500, 500);
            this.mStrength = 5;
            this.mLength = 5;
            this.mRange = -1;
            this.mDirection = -1;
            this.mNumberOfShafts = -1;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public Rect getLimitPoint() {
            return new Rect(0, 0, 1000, 1000);
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public int getLimitRange() {
            return 0;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public void setRange(int r) {
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public short getLengthSAParameterValue(int l) {
            return us_area_rad_ratio[l];
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public void update(ByteBuffer params) {
            params.position(56);
            params.putShort((short) 0);
            params.putShort(us_area_circle[this.mLength]);
            params.putShort((short) 1);
            params.putShort(us_area_rad_ratio[this.mLength]);
            params.putShort((short) 0);
            params.putShort((short) 10);
            params.position(72);
            params.putShort(us_flare_gain[this.mStrength]);
            params.putShort(us_ray_gain[this.mStrength]);
            params.putShort(us_len1_0_ratio[this.mStrength]);
            params.putShort(us_len1_1_ratio[this.mStrength]);
            params.putShort(us_len2_0_ratio[this.mStrength]);
            params.putShort(us_len2_1_ratio[this.mStrength]);
            params.putShort(us_len2_1u_ratio[this.mStrength]);
            params.position(92);
            params.putShort(us_ray_slope1[this.mStrength]);
            params.putShort(us_ray_intercept1[this.mStrength]);
            params.putShort(us_ray_slope2[this.mStrength]);
            params.putShort(us_ray_intercept2[this.mStrength]);
            params.putShort(us_ray_offset[this.mStrength]);
            params.putShort((short) 0);
            super.update(params);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class BeamParameters extends BaseParameters {
        private static final short[] us_area_circle = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
        private static final short[] us_area_rad_ratio_normal = {1638, 3113, 4588, 6062, 7537, 9011, 10486, 11960, 13435, 14909, 16384};
        private static final short[] us_area_rad_ratio_hv = {3113, 4588, 6062, 7537, 9011, 10486, 11960, 13435, 14909, 15565, 16384};
        private static final short[] us_flare_gain = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        private static final short[] us_ray_gain_normal = {64, 128, 256, 384, 512, 640, 768, 896, 1024, 1152, 1280};
        private static final short[] us_ray_gain_hv = {64, 90, 128, 230, 333, 410, 486, 563, 768, 1024, 1280};
        private static final short[] us_len1_0_ratio_normal = {38, 51, 64, 77, 90, 102, 115, 128, 141, 154, 256};
        private static final short[] us_len1_0_ratio_hv = {26, 26, 38, 51, 64, 77, 90, 102, 115, 128, 154};
        private static final short[] us_len1_1_ratio = {256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256};
        private static final short[] us_len2_0_ratio_normal = {38, 51, 64, 77, 90, 102, 115, 128, 141, 154, 256};
        private static final short[] us_len2_0_ratio_hv = {26, 26, 38, 51, 64, 77, 90, 102, 115, 128, 154};
        private static final short[] us_len2_1_ratio = {256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256};
        private static final short[] us_len2_1u_ratio = {26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26};
        private static final short[] us_ray_slope1 = {256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256};
        private static final short[] us_ray_intercept1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        private static final short[] us_ray_slope2 = {256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256};
        private static final short[] us_ray_intercept2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        private static final short[] us_ray_offset = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        BeamParameters() {
            this.mLightSource = new Point(500, 500);
            this.mStrength = 5;
            this.mLength = 10;
            this.mRange = 0;
            this.mDirection = 17;
            this.mNumberOfShafts = -1;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public Point getSAPoint(int width, int height) {
            int x = (int) ((this.mLightSource.x / 1000.0d) * width);
            int y = (int) ((this.mLightSource.y / 1000.0d) * height);
            if (x < 4) {
                x = 4;
            }
            if (y < 4) {
                y = 4;
            }
            if (x > width - 5) {
                x = width - 5;
            }
            if (y > height - 5) {
                y = height - 5;
            }
            return new Point(x, y);
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public Rect getLimitPoint() {
            return new Rect(0, 0, 1000, 1000);
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public int getLimitRange() {
            return 7;
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public float getRangeDegree() {
            return 2.8125f * (this.mRange + 1);
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public short getLengthSAParameterValue(int l) {
            return us_area_rad_ratio_normal[l];
        }

        @Override // com.sony.imaging.app.lightshaft.shooting.ShaftsEffect.BaseParameters
        public void update(ByteBuffer params) {
            int range;
            short[] us_area_rad_ratio;
            short[] us_ray_gain;
            short[] us_len1_0_ratio;
            short[] us_len2_0_ratio;
            if (this.mRange == 0 && this.mDirection % 8 == 0) {
                range = 2;
                us_area_rad_ratio = us_area_rad_ratio_hv;
                us_ray_gain = us_ray_gain_hv;
                us_len1_0_ratio = us_len1_0_ratio_hv;
                us_len2_0_ratio = us_len2_0_ratio_hv;
            } else {
                range = this.mRange + 1;
                us_area_rad_ratio = us_area_rad_ratio_normal;
                us_ray_gain = us_ray_gain_normal;
                us_len1_0_ratio = us_len1_0_ratio_normal;
                us_len2_0_ratio = us_len2_0_ratio_normal;
            }
            ShaftsEffect.make_ray_en(ShaftsEffect.mBRayTable, 1, range, this.mDirection * 4);
            params.position(56);
            params.putShort((short) 4);
            params.putShort(us_area_circle[this.mLength]);
            params.putShort((short) 0);
            params.putShort(us_area_rad_ratio[this.mLength]);
            params.putShort((short) 0);
            params.putShort((short) 15);
            params.position(72);
            params.putShort(us_flare_gain[this.mStrength]);
            params.putShort(us_ray_gain[this.mStrength]);
            params.putShort(us_len1_0_ratio[this.mLength]);
            params.putShort(us_len1_1_ratio[this.mStrength]);
            params.putShort(us_len2_0_ratio[this.mLength]);
            params.putShort(us_len2_1_ratio[this.mStrength]);
            params.putShort(us_len2_1u_ratio[this.mStrength]);
            params.position(92);
            params.putShort(us_ray_slope1[this.mStrength]);
            params.putShort(us_ray_intercept1[this.mStrength]);
            params.putShort(us_ray_slope2[this.mStrength]);
            params.putShort(us_ray_intercept2[this.mStrength]);
            params.putShort(us_ray_offset[this.mStrength]);
            params.putShort((short) 0);
            super.update(params);
        }
    }

    private DeviceBuffer prepare_sa_bootparam(DSP dsp) {
        DeviceBuffer bootParam = dsp.createBuffer(60);
        ByteBuffer bootParam_buf = ByteBuffer.allocateDirect(60);
        bootParam_buf.order(ByteOrder.nativeOrder());
        bootParam_buf.putInt(dsp.getPropertyAsInt("program-descriptor"));
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam_buf.put((byte) 1);
        bootParam_buf.put((byte) 1);
        bootParam_buf.put((byte) 1);
        bootParam_buf.put((byte) 1);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.put((byte) 0);
        bootParam_buf.putInt(0);
        bootParam_buf.putInt(0);
        bootParam.write(bootParam_buf);
        return bootParam;
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

    /* JADX INFO: Access modifiers changed from: private */
    public static void make_ray_en(ByteBuffer ray_table, int n, int w, int d) {
        byte[] ray_en = new byte[128];
        for (int i = 0; i < 128; i++) {
            ray_en[i] = 0;
        }
        float s = (float) (128.0d / n);
        for (int i2 = 0; i2 < n; i2++) {
            int k = w / 2;
            int l = (((int) ((i2 * s) + 1.0f)) >> 1) << 1;
            int l2 = l + d;
            for (int j = -k; j < w - k; j++) {
                ray_en[(l2 + j) & 127] = 1;
            }
        }
        ray_table.position(0);
        ray_table.put(ray_en);
    }

    private static void rotate_ray_en(ByteBuffer ray_table, int r) {
        byte[] buf = ray_table.array();
        byte[] ray_en = new byte[128];
        for (int i = 0; i < 128; i++) {
            ray_en[i] = buf[(i - r) & 127];
        }
        ray_table.position(0);
        ray_table.put(ray_en);
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return null;
    }
}
