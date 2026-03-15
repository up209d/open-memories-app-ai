package com.sony.imaging.app.srctrl.playback.contents;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlPlaybackUtil;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;
import com.sony.scalar.graphics.JpegExporter;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.TimeUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/* loaded from: classes.dex */
public class PrepareTransferData {
    private static final int HW_VER_AVIP = 1;
    private static final float MAX_ASPECT_16_9 = 1.8f;
    private static final float MAX_ASPECT_1_1 = 1.02f;
    private static final float MAX_ASPECT_3_2 = 1.52f;
    private static final float MAX_ASPECT_4_3 = 1.35f;
    private static final float MIN_ASPECT_16_9 = 1.75f;
    private static final float MIN_ASPECT_1_1 = 0.98f;
    private static final float MIN_ASPECT_3_2 = 1.48f;
    private static final float MIN_ASPECT_4_3 = 1.31f;
    private static final String MP4_ROOT = "/MP_ROOT/";
    private static final int MSL_BUFFER_SIZE = 2097152;
    private static final String PERIOD = ".";
    private static final int PF_VER_ROTETION_SUPPORTED = 3;
    private static final int SCN_PIXEL_SIZE = 2000000;
    private static final String SEPARATOR = "/";
    private static final String STILL_ROOT = "/DCIM/";
    private static final int VERSION = 1;
    private static final int VGA_16_9_HEIGHT = 360;
    private static final int VGA_16_9_WIDTH = 640;
    private static final int VGA_1_1_HEIGHT = 480;
    private static final int VGA_1_1_WIDTH = 480;
    private static final int VGA_3_2_HEIGHT = 428;
    private static final int VGA_3_2_WIDTH = 640;
    private static final int VGA_4_3_HEIGHT = 480;
    private static final int VGA_4_3_WIDTH = 640;
    private static final int VGA_HEIGHT = 480;
    private static final int VGA_WIDTH = 640;
    private static final String XAVCS_MS_ROOT = "/M4ROOT/";
    private static final String XAVCS_SD_ROOT = "/PRIVATE/M4ROOT/";
    private static final String TAG = PrepareTransferData.class.getName();
    private static long mSize = 0;
    private static final PrepareTransferData instance = new PrepareTransferData();
    private static DSP mDsp = null;
    private static DeviceBuffer mDbuf = null;
    private static int mOrientation = 0;
    private static ResizeType mResizeType = ResizeType.UNKNOWN;
    protected InputStream mIn = null;
    private StringBuffer mFilePath = new StringBuffer();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ResizeType {
        UNKNOWN,
        ORG,
        SCN_MSL,
        THM_MSL,
        SCN_AVINDEX,
        VGA,
        THM_AVINDEX
    }

    public native int beginMSL(String str);

    public native boolean changeRotationAngle(int i, int i2, int i3);

    public native int directBuffer(byte[] bArr, int i, int i2);

    public native void endMSL();

    public native int execResizeImage(boolean z, int i, int i2, String str, int i3, long j);

    public native int getMSLVersion();

    public native int getPixelSize();

    public native int getRotateInfo();

    public native boolean isImageExisting(boolean z);

    static {
        System.loadLibrary("ResizeImage");
    }

    private PrepareTransferData() {
    }

    public static synchronized PrepareTransferData getInstance() {
        PrepareTransferData prepareTransferData;
        synchronized (PrepareTransferData.class) {
            prepareTransferData = instance;
        }
        return prepareTransferData;
    }

    public synchronized long beginResizeVgaImage(String filePath, String uri) {
        long j = 0;
        synchronized (this) {
            if (this.mIn != null || 0 != mSize) {
                Log.i(TAG, "beginResizeImage is error because mIn is not null, or mSize is not zero. ");
            } else if (filePath == null || uri == null) {
                Log.i(TAG, "beginResizeVgaImage is error. unicId = null");
            } else {
                Log.i(TAG, "beginResizeVgaImage uniqId = " + uri);
                if (beginMSL(filePath) == 0) {
                    mOrientation = getRotateInfo();
                    endMSL();
                }
                ContentsManager mgr = ContentsManager.getInstance();
                ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(uri);
                OptimizedImage image = mgr.getOptimizedImageWithoutCache(id, 2);
                this.mIn = getVgaScaledImageImputStream(image);
                mResizeType = ResizeType.VGA;
                if (this.mIn == null) {
                    mSize = 0L;
                } else {
                    try {
                        mSize = this.mIn.available();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mSize = 0L;
                        this.mIn = null;
                    }
                }
                if (this.mIn != null && mOrientation != 0) {
                    this.mIn = setRotationAngle(this.mIn, mOrientation);
                }
                Log.i(TAG, "beginResizeVgaImage size = " + mSize + " bytes");
                j = mSize;
            }
        }
        return j;
    }

    public synchronized long beginResizeImage(String filePath, String type, boolean reEncMode, String uri, boolean isCreateBuff, boolean isRecInfoModified) {
        long j = 0;
        synchronized (this) {
            if (filePath == null || type == null) {
                Log.i(TAG, "beginResizeImage is error because filePath or type are null.");
            } else if (this.mIn != null || 0 != mSize) {
                Log.i(TAG, "beginResizeImage is error because mIn is not null, or mSize is not zero. ");
            } else {
                mResizeType = getResizeType(filePath, type, reEncMode, uri);
                if (ResizeType.ORG.equals(mResizeType)) {
                    this.mIn = getOriginalImage(filePath);
                } else if (ResizeType.SCN_MSL.equals(mResizeType)) {
                    if (getResizeImageByMSL(filePath, true, isCreateBuff, isRecInfoModified) != 0) {
                        mSize = 0L;
                    }
                } else if (ResizeType.SCN_AVINDEX.equals(mResizeType)) {
                    this.mIn = getScreennailByAvindex(uri);
                } else if (ResizeType.THM_MSL.equals(mResizeType)) {
                    if (getResizeImageByMSL(filePath, false, isCreateBuff, isRecInfoModified) != 0) {
                        mSize = 0L;
                    }
                } else if (ResizeType.THM_AVINDEX.equals(mResizeType)) {
                    this.mIn = getThumbnailByAvindex(uri);
                } else {
                    mSize = 0L;
                }
                Log.i(TAG, "beginResizeImage size = " + mSize + " bytes");
                j = mSize;
            }
        }
        return j;
    }

    public synchronized InputStream getResizeImage() {
        Log.i(TAG, "getResieImage start");
        if (mResizeType.equals(ResizeType.SCN_MSL) || mResizeType.equals(ResizeType.THM_MSL)) {
            byte[] data = new byte[(int) mSize];
            int ret = directBuffer(data, (int) mSize, 0);
            if (ret > 0) {
                Log.i(TAG, "call directBuffer");
                this.mIn = new ByteArrayInputStream(data);
            }
        }
        Log.i(TAG, "getResieImage end");
        return this.mIn;
    }

    public synchronized void endResizeImage() {
        Log.i(TAG, "endResizeImage mIn = " + this.mIn);
        if (mDbuf != null) {
            mDbuf.release();
            mDbuf = null;
        }
        if (mDsp != null) {
            mDsp.release();
            mDsp = null;
        }
        try {
            if (this.mIn != null) {
                try {
                    this.mIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    this.mIn = null;
                    mSize = 0L;
                }
            }
            endMSL();
            mResizeType = ResizeType.UNKNOWN;
        } finally {
            this.mIn = null;
            mSize = 0L;
        }
    }

    public int getVersion() {
        Log.i(TAG, "getVersion = 1");
        return 1;
    }

    public int getVersionOfMSL() {
        int version = getMSLVersion();
        Log.i(TAG, "GetVersionOfMSL = " + version);
        return version;
    }

    public String getFilePath(String imageType, String url) {
        ContentsManager mgr = ContentsManager.getInstance();
        ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(url);
        ContentInfo contentInfo = mgr.getContentInfo(id);
        if (contentInfo == null) {
            Log.e(TAG, "getFilePath ContentInfo is null!");
            return null;
        }
        int contType = contentInfo.getInt("content_type");
        this.mFilePath.replace(0, this.mFilePath.length(), Environment.getExternalStorageDirectory().toString());
        String dirName = contentInfo.getString("DCF_TBLDirName");
        String fileName = contentInfo.getString("DCF_TBLFileName");
        switch (contType) {
            case 1:
                boolean bIsExistMPO = 1 == contentInfo.getInt("DCF_TBLStillFileType");
                if (bIsExistMPO) {
                    fileName = fileName.replace(SRCtrlConstants.MPO_FILE_SUFFIX, ".JPG");
                }
                this.mFilePath.append(STILL_ROOT);
                if (fileName != null) {
                    if (SRCtrlConstants.IMAGE_TYPE_ORGJPEG.equals(imageType)) {
                        fileName = fileName.replace(SRCtrlConstants.RAW_FILE_SUFFIX, ".JPG");
                        break;
                    } else if (SRCtrlConstants.IMAGE_TYPE_ORGRAW.equals(imageType)) {
                        fileName = fileName.replace(".JPG", SRCtrlConstants.RAW_FILE_SUFFIX);
                        break;
                    }
                }
                break;
            case 4:
                this.mFilePath.append(MP4_ROOT);
                break;
            case 256:
                String mediaId = AvindexStore.getExternalMediaIds()[0];
                if (mediaId != null) {
                    MediaInfo info = AvindexStore.getMediaInfo(mediaId);
                    int mediaType = info.getMediaType();
                    if (1 == mediaType) {
                        this.mFilePath.append(XAVCS_SD_ROOT);
                        break;
                    } else if (2 == mediaType) {
                        this.mFilePath.append(XAVCS_MS_ROOT);
                        break;
                    }
                }
                break;
        }
        this.mFilePath.append(dirName);
        this.mFilePath.append(SEPARATOR);
        this.mFilePath.append(fileName);
        Log.i(TAG, "getFilePath = " + ((Object) this.mFilePath));
        return this.mFilePath.toString();
    }

    public String getFilePath(String uniqId) {
        return getFilePath(SRCtrlConstants.IMAGE_TYPE_ORG, uniqId);
    }

    private OptimizedImage getVgaScaledImage(OptimizedImage optImage) {
        int scaledWidth;
        int scaledHeight;
        if (optImage == null) {
            return null;
        }
        OptimizedImage scaledOptImage = null;
        int srcWidth = optImage.getWidth();
        int srcHeight = optImage.getHeight();
        Log.d(TAG, "getVgaScaledImage Source: " + srcWidth + "x" + srcHeight);
        ScaleImageFilter filter = new ScaleImageFilter();
        filter.setSource(optImage, true);
        float aspectValue = srcWidth / srcHeight;
        if (MIN_ASPECT_4_3 <= aspectValue && aspectValue <= MAX_ASPECT_4_3) {
            scaledWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            scaledHeight = 480;
        } else if (MIN_ASPECT_3_2 <= aspectValue && aspectValue <= MAX_ASPECT_3_2) {
            scaledWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            scaledHeight = VGA_3_2_HEIGHT;
        } else if (MIN_ASPECT_16_9 <= aspectValue && aspectValue <= MAX_ASPECT_16_9) {
            scaledWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            scaledHeight = VGA_16_9_HEIGHT;
        } else if (MIN_ASPECT_1_1 <= aspectValue && aspectValue <= MAX_ASPECT_1_1) {
            scaledWidth = 480;
            scaledHeight = 480;
        } else if (srcWidth > 640) {
            scaledWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
            scaledHeight = (srcHeight * AppRoot.USER_KEYCODE.WATER_HOUSING) / srcWidth;
        } else {
            scaledWidth = srcWidth;
            scaledHeight = srcHeight;
        }
        filter.setDestSize(scaledWidth, scaledHeight);
        Log.i(TAG, "getVgaScaledImage SetDestSize Width:" + scaledWidth + " Height:" + scaledHeight);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        if (filter.execute()) {
            scaledOptImage = filter.getOutput();
            if (scaledOptImage != null) {
                Log.i(TAG, "getVgaScaledImage scaledOptImage Width:" + scaledOptImage.getWidth() + " Height:" + scaledOptImage.getHeight());
            }
        } else {
            optImage.release();
        }
        filter.clearSources();
        filter.release();
        return scaledOptImage;
    }

    private InputStream getVgaScaledImageImputStream(OptimizedImage optImage) {
        OptimizedImage scaledOptImage;
        if (optImage != null && (scaledOptImage = getVgaScaledImage(optImage)) != null) {
            JpegExporter exporter = new JpegExporter();
            JpegExporter.Options option = new JpegExporter.Options();
            option.quality = 1;
            InputStream vgaScaledImage = exporter.encode(scaledOptImage, option);
            exporter.release();
            scaledOptImage.release();
            return vgaScaledImage;
        }
        return null;
    }

    private ResizeType getResizeType(String filePath, String type, boolean reEncMode, String uri) {
        ResizeType resizeType = ResizeType.UNKNOWN;
        if (uri != null) {
            ContentsManager mgr = ContentsManager.getInstance();
            ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(uri);
            ContentInfo contentInfo = mgr.getContentInfo(id);
            if (contentInfo != null) {
                int contType = contentInfo.getInt("content_type");
                if (4 == contType || 256 == contType) {
                    if (SRCtrlConstants.IMAGE_TYPE_ORG.equals(type)) {
                        File movieFile = new File(filePath);
                        resizeType = movieFile.exists() ? ResizeType.ORG : ResizeType.UNKNOWN;
                    } else if ("Thumbnail".equals(type)) {
                        resizeType = mgr.hasThumbnail(id) ? ResizeType.THM_AVINDEX : ResizeType.UNKNOWN;
                    }
                } else {
                    resizeType = getStillResizeType(filePath, type, reEncMode);
                }
            }
        } else {
            resizeType = getStillResizeType(filePath, type, reEncMode);
        }
        Log.i(TAG, "getResizeType resizeType = " + resizeType);
        return resizeType;
    }

    private ResizeType getStillResizeType(String filePath, String type, boolean reEncMode) {
        ResizeType resizeType;
        ResizeType resizeType2 = ResizeType.UNKNOWN;
        if (SRCtrlConstants.IMAGE_TYPE_ORGJPEG.equals(type) || SRCtrlConstants.IMAGE_TYPE_ORGRAW.equals(type)) {
            File jpegFile = new File(filePath);
            if (jpegFile.exists()) {
                resizeType = ResizeType.ORG;
            } else {
                resizeType = ResizeType.UNKNOWN;
            }
        } else if ("Scn".equals(type)) {
            if (1 == com.sony.imaging.app.util.Environment.getVersionOfHW()) {
                if (!reEncMode) {
                    Log.e(TAG, "AVIP is not supported MSL");
                }
                if (SCN_PIXEL_SIZE > getPixelSize()) {
                    resizeType = ResizeType.ORG;
                } else {
                    resizeType = ResizeType.SCN_AVINDEX;
                }
            } else {
                int errMSL = beginMSL(filePath);
                if (errMSL == 0) {
                    if (isImageExisting(true)) {
                        resizeType = ResizeType.SCN_MSL;
                    } else if (reEncMode) {
                        if (SCN_PIXEL_SIZE > getPixelSize()) {
                            resizeType = ResizeType.ORG;
                        } else {
                            resizeType = ResizeType.SCN_AVINDEX;
                            mOrientation = getRotateInfo();
                        }
                    } else {
                        resizeType = ResizeType.ORG;
                    }
                } else {
                    resizeType = ResizeType.UNKNOWN;
                }
                endMSL();
            }
        } else if ("Thumbnail".equals(type)) {
            if (1 == com.sony.imaging.app.util.Environment.getVersionOfHW()) {
                if (!reEncMode) {
                    Log.e(TAG, "AVIP is not supported MSL");
                }
                resizeType = ResizeType.THM_AVINDEX;
            } else {
                int errMSL2 = beginMSL(filePath);
                if (errMSL2 == 0) {
                    if (isImageExisting(false)) {
                        resizeType = ResizeType.THM_MSL;
                    } else {
                        resizeType = ResizeType.UNKNOWN;
                    }
                } else {
                    resizeType = ResizeType.UNKNOWN;
                }
                endMSL();
            }
        } else {
            resizeType = ResizeType.UNKNOWN;
        }
        Log.i(TAG, "getStillResizeType resizeType = " + resizeType);
        return resizeType;
    }

    private InputStream getOriginalImage(String filePath) {
        try {
            File file = new File(filePath);
            mSize = file.length();
            this.mIn = new FileInputStream(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            mSize = 0L;
            this.mIn = null;
        }
        return this.mIn;
    }

    private int getResizeImageByMSL(String filePath, boolean isScn, boolean isCreateBuff, boolean isRecInfoModified) {
        long startTime = System.currentTimeMillis();
        int mslErr = beginMSL(filePath);
        Log.d(TAG, "called JNI openMSL : " + mslErr);
        if (mslErr == 0) {
            int imgAddress = 0;
            int imgSize = 0;
            if (isCreateBuff) {
                try {
                    Log.d(TAG, "Create buffer");
                    mDsp = DSP.createProcessor("sony-di-dsp");
                    if (mDsp != null) {
                        mDbuf = mDsp.createBuffer((int) 2097152);
                    }
                    if (mDbuf != null) {
                        imgAddress = mDsp.getPropertyAsInt(mDbuf, "memory-address");
                        imgSize = mDsp.getPropertyAsInt(mDbuf, "memory-size");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
            String softwareName = ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME);
            String firmVer = ScalarProperties.getFirmwareVersion();
            String softwareName2 = softwareName + " v" + firmVer;
            long localTime = isRecInfoModified ? getTimeStamp() / 1000 : 0L;
            int dlength = execResizeImage(isScn, imgAddress, imgSize, softwareName2, getTimeDiff(), localTime);
            Log.d(TAG, "called JNI createScreennail : " + dlength);
            if (dlength > 0) {
                mSize = dlength;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(TAG, "getResizeImageByMSL ElapsedTime: " + elapsedTime + "[millis]");
        }
        return mslErr;
    }

    private InputStream getScreennailByAvindex(String uri) {
        byte[] screennail;
        long startTime = System.currentTimeMillis();
        if (uri != null) {
            ContentsManager mgr = ContentsManager.getInstance();
            ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(uri);
            ContentInfo contentInfo = mgr.getContentInfo(id);
            if (contentInfo != null) {
                long imageId = contentInfo.getLong("_id");
                ContentResolver resolver = ContentsManager.getInstance().getContentResolver();
                synchronized (SRCtrlPlaybackUtil.getContentTransferLock()) {
                    screennail = AvindexStore.Images.Media.getScreennail(resolver, AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]), imageId);
                }
                if (screennail != null) {
                    this.mIn = new ByteArrayInputStream(screennail);
                    if (this.mIn != null && mOrientation != 0) {
                        this.mIn = setRotationAngle(this.mIn, mOrientation);
                    }
                    try {
                        mSize = this.mIn.available();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mSize = 0L;
                        this.mIn = null;
                    }
                }
            } else {
                Log.e(TAG, "getScreennailByAvindex : ContentInfo is null!");
                mSize = 0L;
                this.mIn = null;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(TAG, "getScreennailByAvindex ElapsedTime: " + elapsedTime + "[millis]");
        }
        return this.mIn;
    }

    private InputStream getThumbnailByAvindex(String uri) {
        long startTime = System.currentTimeMillis();
        if (uri != null) {
            ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(uri);
            ContentsManager mgr = ContentsManager.getInstance();
            Bitmap bitmap = mgr.getThumbnail(id, (ContentsManager.ThumbnailOption) null);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                this.mIn = new ByteArrayInputStream(bos.toByteArray());
                if (this.mIn == null) {
                    mSize = 0L;
                } else {
                    try {
                        mSize = this.mIn.available();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mSize = 0L;
                        this.mIn = null;
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (0 != elapsedTime) {
            Log.v(TAG, "getThumbnailByAvindex ElapsedTime: " + elapsedTime + "[millis]");
        }
        return this.mIn;
    }

    private long getTimeStamp() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        PlainCalendar pc = TimeUtil.getCurrentCalendar();
        cal.set(pc.year, pc.month - 1, pc.day, pc.hour, pc.minute, pc.second);
        return cal.getTimeInMillis();
    }

    private int getTimeDiff() {
        PlainTimeZone p = TimeUtil.getCurrentTimeZone();
        int diff = p.gmtDiff;
        return diff + p.summerTimeDiff;
    }

    private boolean hasMovieThumbnail(String url) {
        ContentsManager mgr;
        ContentsIdentifier id;
        ContentInfo contentInfo;
        if (url == null || (contentInfo = (mgr = ContentsManager.getInstance()).getContentInfo((id = SRCtrlPlaybackUtil.getContentsIdentifier(url)))) == null) {
            return false;
        }
        int contType = contentInfo.getInt("content_type");
        if ((4 != contType && 256 != contType) || !mgr.hasThumbnail(id)) {
            return false;
        }
        return true;
    }

    private InputStream setRotationAngle(InputStream in, int angle) {
        byte[] data;
        int readSize;
        if (1 == com.sony.imaging.app.util.Environment.getVersionOfHW() || 3 > com.sony.imaging.app.util.Environment.getVersionPfAPI()) {
            return in;
        }
        InputStream cIn = in;
        DSP dsp = null;
        try {
            try {
                dsp = DSP.createProcessor("sony-di-dsp");
                dbuf = dsp != null ? dsp.createBuffer(MSL_BUFFER_SIZE) : null;
                int wroteSize = 0;
                int imgAddress = 0;
                int imgSize = 0;
                if (dbuf != null) {
                    wroteSize = dbuf.write(in);
                    imgAddress = dsp.getPropertyAsInt(dbuf, "memory-address");
                    imgSize = dsp.getPropertyAsInt(dbuf, "memory-size");
                }
                if (changeRotationAngle(imgAddress, imgSize, angle) && (readSize = dbuf.read((data = new byte[wroteSize]))) > 0) {
                    Log.i(TAG, "setRotationAngle size = " + readSize + "mSize = " + mSize);
                    InputStream cIn2 = new ByteArrayInputStream(data);
                    try {
                        in.close();
                        cIn = cIn2;
                    } catch (Exception e) {
                        e = e;
                        cIn = cIn2;
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                        if (dbuf != null) {
                            dbuf.release();
                        }
                        if (dsp != null) {
                            dsp.release();
                        }
                        return cIn;
                    } catch (Throwable th) {
                        th = th;
                        if (dbuf != null) {
                            dbuf.release();
                        }
                        if (dsp != null) {
                            dsp.release();
                        }
                        throw th;
                    }
                }
                if (dbuf != null) {
                    dbuf.release();
                }
                if (dsp != null) {
                    dsp.release();
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        return cIn;
    }
}
