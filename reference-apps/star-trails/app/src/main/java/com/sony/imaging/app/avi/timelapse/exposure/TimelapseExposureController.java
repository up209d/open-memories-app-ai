package com.sony.imaging.app.avi.timelapse.exposure;

import android.annotation.SuppressLint;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.util.ScalarCalendar;
import com.sony.scalar.hardware.CameraEx;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class TimelapseExposureController {
    private static final String VERSION = "2014/11/14";
    private static final int sContLevelHistoryMAX = 3;
    static final int sSeepUpCounterGain1 = 16;
    private static final int sTargetErrorHistoryMAX = 64;
    private static String TAG = "TLEX";
    private static String mContString = "";
    private METRING_STATE mMetringState = METRING_STATE.CREATED;
    public Options mOptions = null;
    private FileWriter mFw = null;
    private long mPrevTime = 0;
    private long mCurrentTime = 0;
    private boolean mAveragedContLevelOn = false;
    private int mContLevelAverageCounter = 0;
    private int mContLevelSum = 0;
    private int mPrevContLevel = 0;
    private int mContLevelRetryCounter = 0;
    private LinkedList<Integer> mTargetErrorHistory = null;
    private LinkedList<Double> mStillContLevelHistoryDF = null;
    private LinkedList<Double> mTimelapseContLevelHistoryDF = null;
    private TimelapseExposureControllerCallback mTimelapseExposureControllerCallback = null;
    private int mTargetErrorMax = 128;
    private int mSeepUpCounter = 0;
    private int mSpeedUpThresh = 42;
    private int mSeepUpCounterMax = 16;
    private long mStartTime = 0;
    private ExposureLevelCallback mExposureLevelCallback = new ExposureLevelCallback();

    /* loaded from: classes.dex */
    protected enum METRING_STATE {
        CREATED,
        OPENED,
        STARTING,
        STARTED,
        PAUSING,
        PAUSED,
        RESUMED,
        STOPPING,
        STOPPED,
        CLOSED
    }

    /* loaded from: classes.dex */
    public static class Options {
        public static final int AUTO_EXPOSURE_TRACKING_HIGH = 500;
        public static final int AUTO_EXPOSURE_TRACKING_LOCK = 0;
        public static final int AUTO_EXPOSURE_TRACKING_LOW = 100;
        public static final int AUTO_EXPOSURE_TRACKING_MID = 300;
        public static final int AUTO_EXPOSURE_TRACKING_NO_SMOOTHING = 999;
        public static final int FPS_24P = 24;
        public static final int FPS_30P = 30;
        public static final int FPS_STILL = 0;
        public static final int INTERVAL_PRIORITY_OFF = 0;
        public static final int INTERVAL_PRIORITY_ON = 1;
        public static final int LONG_EXPOSURE_NR_OFF = 0;
        public static final int LONG_EXPOSURE_NR_ON = 1;
        public static final int LONG_EXPOSURE_NR_UNKNOWN = 2;
        public int intervalTimeMillis = -1;
        public int longExposureNR = -1;
        public int fps = -1;
        public int shootingNum = -1;
        public int intervalPriority = -1;
        public int autoExposureTracking = -1;
        public int debugMode = 0;

        @SuppressLint({"SdCardPath"})
        public String debugFileName = "/mnt/sdcard/tlex.txt";
    }

    /* loaded from: classes.dex */
    public interface TimelapseExposureControllerCallback {
        void onPauseMeteringDone(CameraEx cameraEx);

        void onStartMeteringDone(CameraEx cameraEx);

        void onStopMeteringDone(CameraEx cameraEx);
    }

    static /* synthetic */ int access$1012(TimelapseExposureController x0, int x1) {
        int i = x0.mContLevelSum + x1;
        x0.mContLevelSum = i;
        return i;
    }

    static /* synthetic */ int access$1108(TimelapseExposureController x0) {
        int i = x0.mContLevelAverageCounter;
        x0.mContLevelAverageCounter = i + 1;
        return i;
    }

    static /* synthetic */ int access$1210(TimelapseExposureController x0) {
        int i = x0.mContLevelRetryCounter;
        x0.mContLevelRetryCounter = i - 1;
        return i;
    }

    public TimelapseExposureController() {
        Log.i(TAG, "T-LEX Lib Ver. 2014/11/14");
    }

    public synchronized void open(CameraEx cameraEx, TimelapseExposureControllerCallback tlexcb, Options options) {
        Log.i(TAG, "open");
        this.mOptions = options;
        this.mTargetErrorHistory = new LinkedList<>();
        this.mStillContLevelHistoryDF = new LinkedList<>();
        this.mTimelapseContLevelHistoryDF = new LinkedList<>();
        this.mSeepUpCounter = 0;
        this.mStartTime = 0L;
        setCurrentTime();
        updatePrevTime();
        Log.i(TAG, "autoExposureTracking=" + this.mOptions.autoExposureTracking);
        Log.i(TAG, "debugFileName=" + this.mOptions.debugFileName);
        setTimelapseExposureControllerCallback(tlexcb);
        cameraEx.setProperExposureLevelCallback(this.mExposureLevelCallback);
        if (this.mOptions.autoExposureTracking == 500) {
            this.mAveragedContLevelOn = false;
        } else if (this.mOptions.autoExposureTracking == 300) {
            this.mAveragedContLevelOn = true;
        } else {
            this.mAveragedContLevelOn = true;
        }
    }

    public synchronized void startMetering(CameraEx cameraEx) {
        Log.d(TAG, "startMetering");
        this.mMetringState = METRING_STATE.STARTING;
        cameraEx.setProperExposureLevelCallback(this.mExposureLevelCallback);
        cameraEx.getProperExposureLevel();
    }

    public synchronized void pauseMetering(CameraEx cameraEx) {
        Log.d(TAG, "pauseMetering");
        this.mMetringState = METRING_STATE.PAUSING;
    }

    public synchronized void resumeMetering(CameraEx cameraEx) {
        Log.d(TAG, "resumeMetering");
        cameraEx.setForceExposureLevel(0);
        cameraEx.getProperExposureLevel();
        this.mMetringState = METRING_STATE.RESUMED;
    }

    public synchronized void stopMetering(CameraEx cameraEx) {
        Log.d(TAG, "stopMetering");
        this.mMetringState = METRING_STATE.STOPPING;
        cameraEx.setForceExposureLevel(0);
        cameraEx.setProperExposureLevelCallback((CameraEx.ProperExposureLevelCallback) null);
        this.mMetringState = METRING_STATE.STOPPED;
        this.mTimelapseExposureControllerCallback.onStopMeteringDone(cameraEx);
    }

    public synchronized void close() {
        Log.d(TAG, "close");
        if (this.mTargetErrorHistory != null) {
            this.mTargetErrorHistory.clear();
            this.mTargetErrorHistory = null;
        }
        if (this.mStillContLevelHistoryDF != null) {
            this.mStillContLevelHistoryDF.clear();
            this.mStillContLevelHistoryDF = null;
        }
        if (this.mTimelapseContLevelHistoryDF != null) {
            this.mTimelapseContLevelHistoryDF.clear();
            this.mTimelapseContLevelHistoryDF = null;
        }
        if (this.mFw != null) {
            try {
                this.mFw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mFw = null;
            this.mOptions.debugFileName = "/mnt/sdcard/tlex.txt";
            this.mStartTime = 0L;
        }
        this.mMetringState = METRING_STATE.CLOSED;
    }

    private void setTimelapseExposureControllerCallback(TimelapseExposureControllerCallback callback) {
        this.mTimelapseExposureControllerCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentTime() {
        this.mCurrentTime = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePrevTime() {
        this.mPrevTime = this.mCurrentTime;
    }

    private int getRealIntervalTimeSec() {
        long intervalTime = (this.mCurrentTime - this.mPrevTime) / 1000;
        if (intervalTime > 60) {
            intervalTime = 60;
        } else if (intervalTime < 1) {
            intervalTime = 1;
        }
        return (int) intervalTime;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContLevelHistory(double stillContLevel, double timelapseContLevel) {
        this.mStillContLevelHistoryDF.addFirst(Double.valueOf(stillContLevel));
        if (this.mStillContLevelHistoryDF.size() > 3) {
            this.mStillContLevelHistoryDF.removeLast();
        }
        this.mTimelapseContLevelHistoryDF.addFirst(Double.valueOf(timelapseContLevel));
        if (this.mTimelapseContLevelHistoryDF.size() > 3) {
            this.mTimelapseContLevelHistoryDF.removeLast();
        }
    }

    private int getMeanTargetError(int num) {
        int num2 = Math.min(num, this.mTargetErrorHistory.size());
        if (num2 == 0) {
            return 0;
        }
        int sum = 0;
        for (int n = 0; n < num2; n++) {
            sum += this.mTargetErrorHistory.get(n).intValue();
        }
        return sum / num2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calcNextContLevel(int contLevel, CameraEx cameraEx) {
        int intelligentContLevel = contLevel;
        setCurrentTime();
        int times = getLongIntervalCompensationTimes();
        for (int n = 0; n < times; n++) {
            intelligentContLevel = updateContLevelByIIRfilter(contLevel);
        }
        recordLog2(contLevel, intelligentContLevel);
        updatePrevTime();
        return intelligentContLevel;
    }

    private double coring2(double value, double thresh0, double thresh1) {
        if (Math.abs(value) <= thresh0) {
            return 0.0d;
        }
        if (Math.abs(value) <= thresh1) {
            double absValue = ((Math.abs(value) - thresh0) * thresh1) / (thresh1 - thresh0);
            return value > 0.0d ? absValue : -absValue;
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTargetErrorHistory(int targetError) {
        int limitedTargetError;
        if (this.mOptions.autoExposureTracking == 100) {
            this.mTargetErrorMax = 256;
        } else {
            this.mTargetErrorMax = 128;
        }
        if (targetError > this.mTargetErrorMax) {
            limitedTargetError = this.mTargetErrorMax;
        } else {
            limitedTargetError = targetError < (-this.mTargetErrorMax) ? -this.mTargetErrorMax : targetError;
        }
        this.mTargetErrorHistory.addFirst(Integer.valueOf(limitedTargetError));
        if (this.mTargetErrorHistory.size() > sTargetErrorHistoryMAX) {
            this.mTargetErrorHistory.removeLast();
        }
    }

    private void updateSpeedUpCounter() {
        int targetErrorWindow;
        int i = 0;
        if (this.mOptions.autoExposureTracking == 500) {
            this.mSeepUpCounter = 0;
            return;
        }
        if (this.mOptions.autoExposureTracking == 100) {
            targetErrorWindow = sTargetErrorHistoryMAX;
            this.mSeepUpCounterMax = 48;
            this.mSpeedUpThresh = sTargetErrorHistoryMAX;
        } else {
            targetErrorWindow = 16;
            this.mSeepUpCounterMax = 16;
            this.mSpeedUpThresh = 42;
        }
        int me = getMeanTargetError(targetErrorWindow);
        if (Math.abs(me) > this.mSpeedUpThresh) {
            this.mSeepUpCounter++;
        } else {
            this.mSeepUpCounter--;
        }
        if (this.mOptions.autoExposureTracking == 100) {
            if (Math.abs(me) > this.mSpeedUpThresh * 2) {
                this.mSeepUpCounter++;
            }
            int me2 = getMeanTargetError(2);
            if (Math.abs(me2) > this.mSpeedUpThresh * 3) {
                this.mSeepUpCounter += 5;
            }
        }
        if (this.mSeepUpCounter >= 0) {
            i = this.mSeepUpCounter > this.mSeepUpCounterMax ? this.mSeepUpCounterMax : this.mSeepUpCounter;
        }
        this.mSeepUpCounter = i;
    }

    private double getSpeedUpRatio() {
        int seepUpCounterLimited = 16;
        if (this.mSeepUpCounter < 0) {
            seepUpCounterLimited = 0;
        } else if (this.mSeepUpCounter <= 16) {
            seepUpCounterLimited = this.mSeepUpCounter;
        }
        return seepUpCounterLimited / 16.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ButterworthA1A2 {
        public double A1;
        public double A2;

        private ButterworthA1A2() {
            this.A1 = -1.7734375d;
            this.A2 = 0.796875d;
        }
    }

    private ButterworthA1A2 calcButterworthA1A2(double speedUpRatio) {
        double weakLPF_FeedBackGain;
        double weakLPF_FeedForwardGain;
        double strongLPF_FeedBackGain;
        double strongLPF_FeedForwardGain;
        double strongLPF_weight;
        ButterworthA1A2 result = new ButterworthA1A2();
        double speedDownRatio = 1.0d - speedUpRatio;
        if (this.mOptions.autoExposureTracking == 300) {
            weakLPF_FeedBackGain = 0.0d;
            weakLPF_FeedForwardGain = 0.27734375d;
            strongLPF_FeedBackGain = 0.5d;
            strongLPF_FeedForwardGain = 0.046875d;
            strongLPF_weight = Math.pow(speedDownRatio, 1.0d);
        } else {
            weakLPF_FeedBackGain = 0.75d;
            weakLPF_FeedForwardGain = 0.0234375d;
            strongLPF_FeedBackGain = 0.875d;
            strongLPF_FeedForwardGain = 0.00390625d;
            strongLPF_weight = Math.pow(speedDownRatio, 0.25d);
        }
        double weakLPF_weight = 1.0d - strongLPF_weight;
        double FeedBackGain = (weakLPF_FeedBackGain * weakLPF_weight) + (strongLPF_FeedBackGain * strongLPF_weight);
        double FeedForwardGain = (weakLPF_FeedForwardGain * weakLPF_weight) + (strongLPF_FeedForwardGain * strongLPF_weight);
        result.A1 = (-1.0d) - FeedBackGain;
        result.A2 = FeedBackGain + FeedForwardGain;
        return result;
    }

    private int updateContLevelByIIRfilter(int stillContLevel) {
        double A1;
        double A2;
        double stillContLevelDouble = stillContLevel / 256.0d;
        double X1 = this.mStillContLevelHistoryDF.get(0).doubleValue();
        double X2 = this.mStillContLevelHistoryDF.get(1).doubleValue();
        double Y1 = this.mTimelapseContLevelHistoryDF.get(0).doubleValue();
        double Y2 = this.mTimelapseContLevelHistoryDF.get(1).doubleValue();
        if (this.mOptions.autoExposureTracking == 500) {
            A1 = (-1.0d) - 0.0d;
            A2 = 0.0d + 0.27734375d;
        } else if (this.mOptions.autoExposureTracking == 300) {
            ButterworthA1A2 coeff = calcButterworthA1A2(getSpeedUpRatio());
            A1 = coeff.A1;
            A2 = coeff.A2;
        } else {
            ButterworthA1A2 coeff2 = calcButterworthA1A2(getSpeedUpRatio());
            A1 = coeff2.A1;
            A2 = coeff2.A2;
        }
        double ffGain = 1.0d + A1 + A2;
        double B0 = ffGain / 4.0d;
        double B1 = ffGain / 2.0d;
        double B2 = ffGain / 4.0d;
        double filterdContLevelDouble = getIIRfilterResult(stillContLevelDouble, X1, X2, Y1, Y2, B0, B1, B2, A1, A2);
        if (this.mOptions.autoExposureTracking == 500) {
            double targetError = stillContLevelDouble - filterdContLevelDouble;
            filterdContLevelDouble += coring2(targetError, 0.6666666666666666d, 1.6666666666666667d);
            updateContLevelHistory(stillContLevelDouble, filterdContLevelDouble);
            updateContLevelHistory(stillContLevelDouble, filterdContLevelDouble);
            updateContLevelHistory(stillContLevelDouble, filterdContLevelDouble);
        }
        int filterdContLevelInt = (int) ((256.0d * filterdContLevelDouble) + 0.5d);
        updateContLevelHistory(stillContLevelDouble, filterdContLevelDouble);
        updateTargetErrorHistory(stillContLevel - filterdContLevelInt);
        updateSpeedUpCounter();
        return filterdContLevelInt;
    }

    private double getIIRfilterResult(double X0, double X1, double X2, double Y1, double Y2, double B0, double B1, double B2, double A1, double A2) {
        double result = ((((B0 * X0) + (B1 * X1)) + (B2 * X2)) - (A1 * Y1)) - (A2 * Y2);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordLog2(int contLevel, int intelligentContLevel) {
        int targetError = contLevel - intelligentContLevel;
        float fTargetError = targetError / 256.0f;
        float fBv = ((contLevel - 16384) / 256.0f) - 5.0f;
        float fIntelligentBv = ((intelligentContLevel - 16384) / 256.0f) - 5.0f;
        ScalarCalendar cal = new ScalarCalendar();
        int year = cal.get(1);
        int month = cal.get(2) + 1;
        int date = cal.get(5);
        int hour = cal.get(11);
        int minute = cal.get(12);
        int second = cal.get(13);
        String datestring = String.format("%4d/%02d/%02d_%02d:%02d:%02d", Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
        cal.clear();
        if (this.mStartTime == 0) {
            this.mStartTime = this.mCurrentTime;
        }
        long elapsedTime = this.mCurrentTime - this.mStartTime;
        mContString = datestring + " \t" + String.format("%12.4f", Float.valueOf(((float) elapsedTime) / 1000.0f)) + " \t" + String.format("0x%04X", Integer.valueOf(contLevel)) + " \t" + String.format("%+6.4f", Float.valueOf(fTargetError)) + " \t" + String.format("%+6.4f", Float.valueOf(fBv)) + " \t" + String.format("%+6.4f", Float.valueOf(fIntelligentBv)) + " \t" + String.format("%+2d", Integer.valueOf(this.mSeepUpCounter)) + "\n";
        Log.w(TAG, mContString);
        if (this.mOptions.debugMode != 0) {
            if (this.mFw == null) {
                String fname = this.mOptions.debugFileName;
                Log.d(TAG, "debugFileName=" + fname);
                try {
                    this.mFw = new FileWriter(fname, true);
                    this.mFw.write("--------------------------------------------------------------------------------------\n");
                    this.mFw.write(datestring + "\n");
                    this.mFw.write("--------------------------------------------------------------------------------------\n");
                    this.mFw.write(String.format("DATE\tElapsedTime\tContLevel\tfTargetError\tfBv\tfIntelligentBv\tmSeepUpCounter\n", new Object[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                this.mFw.write(mContString);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* loaded from: classes.dex */
    private class ExposureLevelCallback implements CameraEx.ProperExposureLevelCallback {
        private ExposureLevelCallback() {
        }

        public synchronized void onControlLevelNotify(int level, CameraEx cameraEx) {
            if (TimelapseExposureController.this.mOptions.debugMode != 0) {
                Log.v(TimelapseExposureController.TAG, "onControlLevelNotify(" + String.format("0x%04x", Integer.valueOf(level)) + LogHelper.MSG_CLOSE_BRACKET);
            }
            if (TimelapseExposureController.this.mMetringState.equals(METRING_STATE.STARTING)) {
                TimelapseExposureController.this.updateContLevelHistory(level / 256.0d, level / 256.0d);
                TimelapseExposureController.this.updateContLevelHistory(level / 256.0d, level / 256.0d);
                TimelapseExposureController.this.updateContLevelHistory(level / 256.0d, level / 256.0d);
                for (int n = 0; n < 32; n++) {
                    TimelapseExposureController.this.updateTargetErrorHistory(0);
                }
                TimelapseExposureController.this.setCurrentTime();
                TimelapseExposureController.this.updatePrevTime();
                TimelapseExposureController.this.mMetringState = METRING_STATE.STARTED;
                TimelapseExposureController.this.mTimelapseExposureControllerCallback.onStartMeteringDone(cameraEx);
                cameraEx.getProperExposureLevel();
                TimelapseExposureController.this.mPrevContLevel = level;
            } else if (TimelapseExposureController.this.mMetringState.equals(METRING_STATE.STARTED)) {
                TimelapseExposureController.this.mContLevelSum = 0;
                TimelapseExposureController.this.mContLevelAverageCounter = 0;
                TimelapseExposureController.this.updateContLevelHistory(level / 256.0d, level / 256.0d);
                TimelapseExposureController.this.setCurrentTime();
                TimelapseExposureController.this.updatePrevTime();
                cameraEx.getProperExposureLevel();
                TimelapseExposureController.this.mPrevContLevel = level;
            } else if (!TimelapseExposureController.this.mMetringState.equals(METRING_STATE.RESUMED)) {
                if (TimelapseExposureController.this.mMetringState.equals(METRING_STATE.PAUSING)) {
                    if (TimelapseExposureController.this.mOptions.autoExposureTracking == 500) {
                        if (TimelapseExposureController.this.mContLevelAverageCounter == 0 && Math.abs(TimelapseExposureController.this.mPrevContLevel - level) > 512) {
                            TimelapseExposureController.this.mContLevelRetryCounter = 3;
                        }
                        if (TimelapseExposureController.this.mContLevelAverageCounter < 7 && TimelapseExposureController.this.mContLevelRetryCounter > 0) {
                            if (Math.abs(TimelapseExposureController.this.mPrevContLevel - level) > 512) {
                                TimelapseExposureController.this.mContLevelRetryCounter = 3;
                            } else {
                                TimelapseExposureController.access$1210(TimelapseExposureController.this);
                            }
                            if (TimelapseExposureController.this.mContLevelRetryCounter > 0) {
                                TimelapseExposureController.access$1012(TimelapseExposureController.this, level);
                                TimelapseExposureController.access$1108(TimelapseExposureController.this);
                                TimelapseExposureController.this.mPrevContLevel = level;
                                TimelapseExposureController.this.recordLog2(level, 0);
                                cameraEx.setForceExposureLevel(level);
                                cameraEx.getProperExposureLevel();
                            }
                        }
                    }
                    TimelapseExposureController.this.mPrevContLevel = level;
                    if (TimelapseExposureController.this.mAveragedContLevelOn) {
                        TimelapseExposureController.access$1012(TimelapseExposureController.this, level);
                        TimelapseExposureController.access$1108(TimelapseExposureController.this);
                        Log.v(TimelapseExposureController.TAG, "cont[" + String.format("%2d", Integer.valueOf(TimelapseExposureController.this.mContLevelAverageCounter)) + "]: " + level);
                        level = (TimelapseExposureController.this.mContLevelSum + (TimelapseExposureController.this.mContLevelAverageCounter >> 1)) / TimelapseExposureController.this.mContLevelAverageCounter;
                        Log.d(TimelapseExposureController.TAG, "average : " + level);
                    }
                    long t0 = System.nanoTime();
                    int level2 = TimelapseExposureController.this.calcNextContLevel(level, cameraEx);
                    long t1 = System.nanoTime();
                    Log.d(TimelapseExposureController.TAG, "calcNextContLevel Time: " + (t1 - t0) + " ns");
                    Log.d(TimelapseExposureController.TAG, "cameraEx.setForceExposureLevel(" + String.format("0x%04x", Integer.valueOf(level2)) + LogHelper.MSG_CLOSE_BRACKET);
                    cameraEx.setForceExposureLevel(level2);
                    TimelapseExposureController.this.wait2V();
                    TimelapseExposureController.this.mMetringState = METRING_STATE.PAUSED;
                    TimelapseExposureController.this.mTimelapseExposureControllerCallback.onPauseMeteringDone(cameraEx);
                    TimelapseExposureController.this.mContLevelAverageCounter = 0;
                    TimelapseExposureController.this.mContLevelSum = 0;
                    TimelapseExposureController.this.mContLevelRetryCounter = 0;
                }
            } else {
                TimelapseExposureController.access$1012(TimelapseExposureController.this, level);
                TimelapseExposureController.access$1108(TimelapseExposureController.this);
                TimelapseExposureController.this.mPrevContLevel = level;
                cameraEx.getProperExposureLevel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void wait2V() {
        try {
            Thread.sleep(34L);
        } catch (Exception e) {
        }
    }

    private int getLongIntervalCompensationTimes() {
        int intervalTimeSec = getRealIntervalTimeSec();
        if (this.mOptions.autoExposureTracking == 300) {
            if (intervalTimeSec <= 17) {
                return 1;
            }
            if (intervalTimeSec <= 35) {
                return 2;
            }
            return 3;
        }
        if (this.mOptions.autoExposureTracking != 100 || intervalTimeSec <= 17) {
            return 1;
        }
        if (intervalTimeSec <= 25) {
            return 2;
        }
        if (intervalTimeSec <= 35) {
            return 3;
        }
        if (intervalTimeSec <= 45) {
            return 4;
        }
        if (intervalTimeSec <= 55) {
            return 5;
        }
        return 6;
    }
}
