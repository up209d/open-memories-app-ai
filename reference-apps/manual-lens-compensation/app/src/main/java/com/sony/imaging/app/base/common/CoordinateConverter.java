package com.sony.imaging.app.base.common;

import android.util.Pair;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class CoordinateConverter {
    protected static final int OSD_BOTTOM = 480;
    protected static final int OSD_LEFT = 0;
    protected static final int OSD_RIGHT = 640;
    protected static final int OSD_TOP = 0;
    protected static final float SCALAR_HEIGHT = 2000.0f;
    protected static final float SCALAR_WIDTH = 2000.0f;
    protected static final float SCALAR_X_MAX = 1000.0f;
    protected static final float SCALAR_X_MIN = -1000.0f;
    protected static final float SCALAR_Y_MAX = 1000.0f;
    protected static final float SCALAR_Y_MIN = -1000.0f;
    private static final String TAG = "CoordinateConverter";

    public static Pair<Integer, Integer> convertEEtoOSD(Pair<Integer, Integer> eeStartPoint) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvAreaHeight = yuvArea.pxBottom - yuvArea.pxTop;
        float rateX = (((Integer) eeStartPoint.first).intValue() - (-1000.0f)) / 2000.0f;
        float rateY = (((Integer) eeStartPoint.second).intValue() - (-1000.0f)) / 2000.0f;
        int osdX = ((int) (yuvAreaWidth * rateX)) + yuvArea.pxLeft + 0;
        int osdY = ((int) (yuvAreaHeight * rateY)) + yuvArea.pxTop + 0;
        return new Pair<>(Integer.valueOf(osdX), Integer.valueOf(osdY));
    }

    public static int convertEEtoOSDX(int eeStartPointX) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaWidth = yuvArea.pxRight - yuvArea.pxLeft;
        float rateX = (eeStartPointX - (-1000.0f)) / 2000.0f;
        int osdX = ((int) (yuvAreaWidth * rateX)) + yuvArea.pxLeft + 0;
        return osdX;
    }

    public static int convertEEtoOSDY(int eeStartPointY) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaHeight = yuvArea.pxBottom - yuvArea.pxTop;
        float rateY = (eeStartPointY - (-1000.0f)) / 2000.0f;
        int osdY = ((int) (yuvAreaHeight * rateY)) + yuvArea.pxTop + 0;
        return osdY;
    }

    public static Pair<Integer, Integer> convertODStoEE(Pair<Integer, Integer> startPointValueOSD) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvAreaHeight = yuvArea.pxBottom - yuvArea.pxTop;
        float rateX = (((Integer) startPointValueOSD.first).intValue() - (yuvArea.pxLeft + 0)) / yuvAreaWidth;
        float rateY = (((Integer) startPointValueOSD.second).intValue() - (yuvArea.pxTop + 0)) / yuvAreaHeight;
        int eeX = (int) ((rateX * 2000.0f) - 1000.0f);
        int eeY = (int) ((rateY * 2000.0f) - 1000.0f);
        return new Pair<>(Integer.valueOf(eeX), Integer.valueOf(eeY));
    }

    public static int convertODStoEEX(int osdStartPointX) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaWidth = yuvArea.pxRight - yuvArea.pxLeft;
        float rateX = (osdStartPointX - (yuvArea.pxLeft + 0)) / yuvAreaWidth;
        int eeX = (int) ((2000.0f * rateX) - 1000.0f);
        return eeX;
    }

    public static int convertODStoEEY(int osdStartPointY) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaHeight = yuvArea.pxBottom - yuvArea.pxTop;
        float rateY = (osdStartPointY - (yuvArea.pxTop + 0)) / yuvAreaHeight;
        int eeY = (int) ((2000.0f * rateY) - 1000.0f);
        return eeY;
    }
}
