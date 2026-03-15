package com.sony.imaging.app.srctrl.liveview;

import android.util.Log;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class LiveviewContainer {
    private static final int LIVEVIEW_COMPRESS_RATE_FOR_DEFAULT = 15;
    private static final int LIVEVIEW_COMPRESS_RATE_FOR_DEFAULT_VGA = 15;
    private static final int LIVEVIEW_COMPRESS_RATE_FOR_LARGE = 8;
    private static final int LIVEVIEW_COMPRESS_RATE_FOR_LARGE_VGA = 5;
    private static final int LIVEVIEW_GENERATING_FRAMERATE = 30000;
    private static final int LIVEVIEW_MAXIMUM_SIZE_IN_KB_FOR_DEFAULT = 100;
    private static final int LIVEVIEW_MAXIMUM_SIZE_IN_KB_FOR_DEFAULT_VGA = 100;
    private static final int LIVEVIEW_MAXIMUM_SIZE_IN_KB_FOR_LARGE = 100;
    private static final int LIVEVIEW_MAXIMUM_SIZE_IN_KB_FOR_LARGE_VGA = 100;
    public static final int PANEL_EE_SIZE_INVALID = -1;
    public static final int PANEL_EE_SIZE_VGA = 2;
    public static final int PANEL_EE_SIZE_XGA = 1;
    public static final String s_DEFAULT_LIVEVIEW_SIZE = "M";
    public static final String s_LARGE_LIVEVIEW_SIZE = "L";
    private static final Map<String, Integer> supportedLiveviewSizeVGA;
    private int compressRate;
    private int frameRate;
    private boolean isSupportedPictureSizeAspect1_1 = false;
    private int maxFileSize;
    private String width;
    private static final String TAG = LiveviewContainer.class.getSimpleName();
    private static LiveviewContainer sState = new LiveviewContainer();
    private static final Map<String, Integer> supportedLiveviewSize = new HashMap(2);

    static {
        supportedLiveviewSize.put(s_DEFAULT_LIVEVIEW_SIZE, Integer.valueOf(AppRoot.USER_KEYCODE.WATER_HOUSING));
        supportedLiveviewSize.put(s_LARGE_LIVEVIEW_SIZE, 1024);
        supportedLiveviewSizeVGA = new HashMap(2);
        supportedLiveviewSizeVGA.put(s_DEFAULT_LIVEVIEW_SIZE, Integer.valueOf(AppRoot.USER_KEYCODE.WATER_HOUSING));
        supportedLiveviewSizeVGA.put(s_LARGE_LIVEVIEW_SIZE, Integer.valueOf(AppRoot.USER_KEYCODE.WATER_HOUSING));
    }

    public static LiveviewContainer getInstance() {
        return sState;
    }

    public LiveviewContainer() {
        initState();
    }

    public void initState() {
        this.frameRate = 30000;
        this.width = s_DEFAULT_LIVEVIEW_SIZE;
        this.compressRate = 15;
        this.maxFileSize = 100;
        this.isSupportedPictureSizeAspect1_1 = hasPictureSizeAspect1_1();
    }

    public boolean setFrameRate(int framerate) {
        this.frameRate = framerate;
        return true;
    }

    public int getFrameRate() {
        return this.frameRate;
    }

    public int getMaxFileSize() {
        if (this.isSupportedPictureSizeAspect1_1) {
            if (this.width.equals(s_DEFAULT_LIVEVIEW_SIZE)) {
                this.maxFileSize = 100;
            } else if (this.width.equals(s_LARGE_LIVEVIEW_SIZE)) {
                this.maxFileSize = 100;
            }
        } else if (this.width.equals(s_DEFAULT_LIVEVIEW_SIZE)) {
            this.maxFileSize = 100;
        } else if (this.width.equals(s_LARGE_LIVEVIEW_SIZE)) {
            this.maxFileSize = 100;
        }
        Log.v(TAG, "MaxFileSize size=" + this.maxFileSize);
        return this.maxFileSize;
    }

    public boolean setLiveviewSize(String size) {
        if (!s_DEFAULT_LIVEVIEW_SIZE.equals(size) && !s_LARGE_LIVEVIEW_SIZE.equals(size)) {
            return false;
        }
        this.width = size;
        return true;
    }

    public int getWidth() {
        int retWidth;
        if (this.isSupportedPictureSizeAspect1_1) {
            retWidth = supportedLiveviewSizeVGA.get(this.width).intValue();
        } else {
            retWidth = supportedLiveviewSize.get(this.width).intValue();
        }
        Log.v(TAG, "Width=" + retWidth);
        return retWidth;
    }

    public String getLiveviewSize() {
        return this.width;
    }

    public int getCompressRate() {
        if (this.isSupportedPictureSizeAspect1_1) {
            if (this.width.equals(s_DEFAULT_LIVEVIEW_SIZE)) {
                this.compressRate = 15;
            } else if (this.width.equals(s_LARGE_LIVEVIEW_SIZE)) {
                this.compressRate = 5;
            }
        } else if (this.width.equals(s_DEFAULT_LIVEVIEW_SIZE)) {
            this.compressRate = 15;
        } else if (this.width.equals(s_LARGE_LIVEVIEW_SIZE)) {
            this.compressRate = 8;
        }
        Log.v(TAG, "CompressRate=" + this.compressRate);
        return this.compressRate;
    }

    public String[] getAvailableLiveviewSize() {
        return getSupportedLiveviewSize();
    }

    public String[] getSupportedLiveviewSize() {
        return (String[]) supportedLiveviewSize.keySet().toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public boolean hasPictureSizeAspect1_1() {
        boolean ret = false;
        List<ScalarProperties.PictureSize> picSizeList = ScalarProperties.getSupportedPictureSizes();
        for (int i = 9; i < picSizeList.size() && i <= 11; i++) {
            if (picSizeList.get(i).width > 0) {
                Log.v(TAG, "getSupportedPictureSizes() inculdes aspect 1:1.");
                ret = true;
            }
        }
        return ret;
    }
}
