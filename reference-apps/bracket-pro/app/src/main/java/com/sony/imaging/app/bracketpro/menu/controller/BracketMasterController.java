package com.sony.imaging.app.bracketpro.menu.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.bracketpro.BMAperture;
import com.sony.imaging.app.bracketpro.BMShutterSpeed;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class BracketMasterController {
    private static final String FORMAT_BIG_DIGIT = "F%.0f";
    private static final String FORMAT_ONE_DIGIT = "F%.1f";
    private static final String INVALID_APERTURE_STRING = "F--";
    private static final float INVALID_APERTURE_VALUE = 0.0f;
    private static final float NUMBER_100 = 100.0f;
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    float apMaxValue;
    float apMinValue;
    private HashMap<String, Integer> centerCordinatePoint;
    private HashMap<String, Double> centerCordinatePointofSS;
    CameraEx.ShutterSpeedInfo infoSS;
    BMApertureListner mAListner;
    private int mCenterPos;
    private int mFirstPos;
    BMShutterSpeedListner mSSListner;
    private int mSecondPos;
    private static final String TAG = BracketMasterController.class.getName();
    private static BracketMasterController sBracketMaster = null;
    private String mCurrentApertureVal = null;
    private String mCurrentApertureAvailMax = null;
    private String mCurrentApertureAvailMin = null;
    private String mCurrentShutterSpeedvailMax = null;
    private String mCurrentShutterSpeedAvailMin = null;
    private String mCurrentShutterSpeedVal = null;
    ArrayList<String> mApertureValueList = new ArrayList<>();
    public ArrayList<String> mShutterSpeedValueList = new ArrayList<>();
    public int SHUTTER_SPEED_MAX_VALUE = 0;
    public int APERTURE_MAX_VALUE = 0;
    public int SHUTTER_SPEED_MIN_VALUE = 0;
    public int APERTURE_MIN_VALUE = 0;
    public String[] rangeValues = {"5.0", "4.7", "4.3", "4.0", "3.7", "3.3", "3.0", "2.7", "2.3", "2.0", "1.7", "1.3", "1.0", "0.7", "0.3"};
    BMAperture ap = new BMAperture();
    BMShutterSpeed ss = new BMShutterSpeed();
    double shiftXPosition = 8.65d;
    float y_Axis = 5.0f;

    public void setMaxMinShutterSpeedValue() {
        this.SHUTTER_SPEED_MAX_VALUE = this.mShutterSpeedValueList.indexOf(this.mCurrentShutterSpeedvailMax);
        this.SHUTTER_SPEED_MIN_VALUE = this.mShutterSpeedValueList.indexOf(this.mCurrentShutterSpeedAvailMin);
    }

    public void setMaxMinApertureSpeedValue() {
        this.APERTURE_MAX_VALUE = this.mApertureValueList.indexOf(this.mCurrentApertureAvailMax);
        this.APERTURE_MIN_VALUE = this.mApertureValueList.indexOf(this.mCurrentApertureAvailMin);
    }

    public void apertureValues(int mGaugeApertureCurrentLevel) {
        Log.d(TAG, "selected level:" + mGaugeApertureCurrentLevel);
        this.ap.setCenterApertureValue(getCurrentApertureVal());
        this.ap.setCenterSSValue(getCurrentShutterSpeedVal());
        this.ap.setMaxApertureValue(getCenterApertureTrapeziumValue(getCurrentApertureAvailMax()));
        this.ap.setMinApertureValue(getCenterApertureTrapeziumValue(getCurrentApertureAvailMin()));
        this.ap.setmCenterYval(getCenterApertureTrapeziumValue(this.ap.getCenterApertureValue()));
        this.ap.setmCenterXVal(getShutterCenterValuesForTrapezium(this.ap.getCenterSSValue()));
        this.ap.setFirstApertureValue(getFirstApertureValue(this.ap.getCenterApertureValue(), mGaugeApertureCurrentLevel));
        this.ap.setSecondApertureValue(getSecondApertureValue(this.ap.getCenterApertureValue(), mGaugeApertureCurrentLevel));
        this.ap.setmFirstXvalue(getShutterFirstValuesForTrapezium(this.ap.getCenterSSValue(), mGaugeApertureCurrentLevel));
        this.ap.setmSecondXvalue(getShutterSecondValuesForTrapezium(this.ap.getCenterSSValue(), mGaugeApertureCurrentLevel));
        this.ap.setmFirstYvalue(getFirstApertureTrapeziumValue(this.ap.getCenterApertureValue(), mGaugeApertureCurrentLevel));
        this.ap.setmSecondYvalue(getSecondApertureTrapeziumValue(this.ap.getCenterApertureValue(), mGaugeApertureCurrentLevel));
        this.ap.getSecondShift();
        this.ap.getThirdShootingPosition();
    }

    public void ShutterSpeedValues(int mGaugeSSCurrentLevel) {
        this.ss.setCenterApertureValue(getCurrentApertureVal());
        this.ss.setCenterShutterSpeedValue(getCurrentShutterSpeedVal());
        this.ss.setMaxApertureValue(getCenterApertureTrapeziumValue(getCurrentApertureAvailMax()));
        this.ss.setMinApertureValue(getCenterApertureTrapeziumValue(getCurrentApertureAvailMin()));
        this.ss.setMaxSSValue(getShutterCenterValuesForTrapezium(getmCurrentShutterSpeedvailMax()));
        this.ss.setMinSSValue(getShutterCenterValuesForTrapezium(getmCurrentShutterSpeedAvailMin()));
        this.ss.setmCenterYval(getCenterApertureTrapeziumValue(this.ss.getCenterApertureValue()));
        this.ss.setmCenterXVal(getShutterCenterValuesForTrapezium(this.ss.getCenterShutterSpeedValue()));
        this.ss.setFirstShutterSpeedValue1(getFirstShutterSpeedValue(this.ss.getCenterShutterSpeedValue(), mGaugeSSCurrentLevel));
        this.ss.setSecondShutterSpeedValue(getSecondShutterSpeedValue(this.ss.getCenterShutterSpeedValue(), mGaugeSSCurrentLevel));
        this.ss.setmFirstXvalue(getShutterFirstValuesForTrapezium(this.ss.getCenterShutterSpeedValue(), mGaugeSSCurrentLevel));
        this.ss.setmSecondXvalue(getShutterSecondValuesForTrapezium(this.ss.getCenterShutterSpeedValue(), mGaugeSSCurrentLevel));
        this.ss.setmFirstYvalue(getFirstApertureTrapeziumValue(this.ss.getCenterApertureValue(), mGaugeSSCurrentLevel));
        this.ss.setmThirdYvalue(getSecondApertureTrapeziumValue(this.ss.getCenterApertureValue(), mGaugeSSCurrentLevel));
        this.ss.getSecondShift();
        this.ss.getThirdShootingPosition();
    }

    private BracketMasterController() {
        createApertureValuesHashMap();
        createApertureValuesList();
        createShutterSpeedValuesHashMap();
        createShutterSpeedValuesList();
        registerNotificationListeners();
    }

    public static BracketMasterController getInstance() {
        if (sBracketMaster == null) {
            sBracketMaster = new BracketMasterController();
        }
        return sBracketMaster;
    }

    public String getFirstApertureValue(String apertureCenterValue, int mGaugeCurrentLevel) {
        if (apertureCenterValue != null && apertureCenterValue.equalsIgnoreCase("F1.7")) {
            apertureCenterValue = "F1.8";
        }
        int positionMid = this.mApertureValueList.indexOf(apertureCenterValue);
        int pos1 = positionMid - (mGaugeCurrentLevel + 1);
        if (pos1 <= this.APERTURE_MIN_VALUE) {
            pos1 = this.APERTURE_MIN_VALUE;
        }
        try {
            String mAppValue = this.mApertureValueList.get(pos1);
            return mAppValue;
        } catch (Exception e) {
            return INVALID_APERTURE_STRING;
        }
    }

    public String getSecondApertureValue(String apertureCenterValue, int mGaugeCurrentLevel) {
        if (apertureCenterValue != null && apertureCenterValue.equalsIgnoreCase("F1.7")) {
            apertureCenterValue = "F1.8";
        }
        int positionMid = this.mApertureValueList.indexOf(apertureCenterValue);
        int pos2 = positionMid + mGaugeCurrentLevel + 1;
        if (pos2 >= this.APERTURE_MAX_VALUE) {
            pos2 = this.APERTURE_MAX_VALUE;
        }
        try {
            String mAppValue = this.mApertureValueList.get(pos2);
            return mAppValue;
        } catch (Exception e) {
            return INVALID_APERTURE_STRING;
        }
    }

    public int getCenterApertureTrapeziumValue(String apertureCenterValue) {
        if (apertureCenterValue != null) {
            try {
                if (apertureCenterValue.equalsIgnoreCase("F1.7")) {
                    apertureCenterValue = "F1.8";
                }
            } catch (Exception exp) {
                Log.d(TAG, "Exception in getApertureValuesForTrapezium():" + exp.getMessage());
                return 0;
            }
        }
        int CenterCordinateofSS = this.centerCordinatePoint.get(apertureCenterValue).intValue();
        return CenterCordinateofSS;
    }

    public int getFirstApertureTrapeziumValue(String apertureCenterValue, int mGaugeCurrentLevel) {
        Log.d(TAG, "+ getApertureVauesForTrapezium(),centerValue:" + apertureCenterValue);
        if (apertureCenterValue != null) {
            try {
                if (apertureCenterValue.equalsIgnoreCase("F1.7")) {
                    apertureCenterValue = "F1.8";
                }
            } catch (Exception exp) {
                Log.d(TAG, "Exception in getApertureFirstTrapeziumValue():" + exp.getMessage());
                return 0;
            }
        }
        int FirstCordinate = this.centerCordinatePoint.get(getFirstApertureValue(apertureCenterValue, mGaugeCurrentLevel)).intValue();
        return FirstCordinate;
    }

    public int getSecondApertureTrapeziumValue(String apertureCenterValue, int mGaugeCurrentLevel) {
        Log.d(TAG, "+getApertureVauesForTrapezium(),centerValue:" + apertureCenterValue);
        if (apertureCenterValue != null) {
            try {
                if (apertureCenterValue.equalsIgnoreCase("F1.7")) {
                    apertureCenterValue = "F1.8";
                }
            } catch (Exception exp) {
                Log.d(TAG, "Exception in getApertureSecondTrapeziumValue():" + exp.getMessage());
                return 0;
            }
        }
        int secondCordinate = this.centerCordinatePoint.get(getSecondApertureValue(apertureCenterValue, mGaugeCurrentLevel)).intValue();
        return secondCordinate;
    }

    public String getFirstShutterSpeedValue(String ShutterSpeedCenterValue, int mGaugeCurrentLevel) {
        getInstance().setMaxMinShutterSpeedValue();
        int maxposition = this.SHUTTER_SPEED_MAX_VALUE;
        int positionMidofSS = this.mShutterSpeedValueList.indexOf(ShutterSpeedCenterValue);
        int posS1 = positionMidofSS + mGaugeCurrentLevel + 1;
        if (maxposition <= posS1) {
            posS1 = maxposition;
            Log.e("ShutterSpeed Values in if :", "posS1 :" + posS1);
        }
        try {
            this.mShutterSpeedValueList.size();
            String value = this.mShutterSpeedValueList.get(posS1);
            setCenterPos(positionMidofSS);
            setFirstPos(posS1);
            return value;
        } catch (Exception e) {
            return "";
        }
    }

    public String getSecondShutterSpeedValue(String ShutterSpeedCenterValue, int mGaugeCurrentLevel) {
        int positionMidofSS = this.mShutterSpeedValueList.indexOf(ShutterSpeedCenterValue);
        int posS2 = positionMidofSS - (mGaugeCurrentLevel + 1);
        if (posS2 <= this.SHUTTER_SPEED_MIN_VALUE) {
            posS2 = this.SHUTTER_SPEED_MIN_VALUE;
        }
        try {
            setSecondPos(posS2);
            return this.mShutterSpeedValueList.get(posS2);
        } catch (Exception e) {
            return "";
        }
    }

    public void setCenterPos(int position) {
        this.mCenterPos = position;
    }

    public int getCenterPos() {
        return this.mCenterPos;
    }

    public void setSecondPos(int position) {
        this.mSecondPos = position;
    }

    public int getSecondPos() {
        return this.mSecondPos;
    }

    public void setFirstPos(int Position) {
        this.mFirstPos = Position;
    }

    public int getFirstPos() {
        return this.mFirstPos;
    }

    public double getShutterFirstValuesForTrapezium(String shutterSpeedCenterValue, int mGaugeCurrentLevel) {
        try {
            double firstCordinateofSS = this.centerCordinatePointofSS.get(getFirstShutterSpeedValue(shutterSpeedCenterValue, mGaugeCurrentLevel)).doubleValue();
            return firstCordinateofSS;
        } catch (Exception exp) {
            Log.d(TAG, "Exception in getApertureValuesForTrapezium():" + exp.getMessage());
            return 0.0d;
        }
    }

    public double getShutterSecondValuesForTrapezium(String shutterSpeedCenterValue, int mGaugeCurrentLevel) {
        try {
            double secondCordinateofSS = this.centerCordinatePointofSS.get(getSecondShutterSpeedValue(shutterSpeedCenterValue, mGaugeCurrentLevel)).doubleValue();
            return secondCordinateofSS;
        } catch (Exception exp) {
            Log.d(TAG, "Exception in getApertureValuesForTrapezium():" + exp.getMessage());
            return 0.0d;
        }
    }

    public double getShutterCenterValuesForTrapezium(String shutterSpeedCenterValue) {
        try {
            double CenterCordinateofSS = this.centerCordinatePointofSS.get(shutterSpeedCenterValue).doubleValue();
            return CenterCordinateofSS;
        } catch (Exception exp) {
            Log.d(TAG, "Exception in getApertureValuesForTrapezium():" + exp.getMessage());
            return 0.0d;
        }
    }

    public String getCurrentApertureVal() {
        return this.mCurrentApertureVal;
    }

    public String getCurrentShutterSpeedVal() {
        return this.mCurrentShutterSpeedVal;
    }

    public void setCurrentApertureVal(String currentApertureVal) {
        this.mCurrentApertureVal = currentApertureVal;
    }

    public String getCurrentApertureAvailMax() {
        return this.mCurrentApertureAvailMax;
    }

    public void setCurrentApertureAvailMax(String mCurrentApertureAvailMax) {
        this.mCurrentApertureAvailMax = mCurrentApertureAvailMax;
    }

    public String getCurrentApertureAvailMin() {
        return this.mCurrentApertureAvailMin;
    }

    public void setCurrentApertureAvailMin(String mCurrentApertureAvailMin) {
        this.mCurrentApertureAvailMin = mCurrentApertureAvailMin;
    }

    public void setCurrentShutterSpeedVal(String CurrentShutterSpeedVal) {
        Log.d(TAG, "+setCurrentApertureVal(), CurrentShutterSpeedVal:  " + this.mCurrentShutterSpeedVal);
        this.mCurrentShutterSpeedVal = CurrentShutterSpeedVal;
    }

    public String getmCurrentShutterSpeedvailMax() {
        return this.mCurrentShutterSpeedvailMax;
    }

    public String getShutterSpeedvalMax() {
        int positionMidofSS = this.mShutterSpeedValueList.indexOf(this.mCurrentShutterSpeedvailMax);
        if (positionMidofSS <= 0) {
        }
        return this.mShutterSpeedValueList.get(0);
    }

    public void setmCurrentShutterSpeedvailMax(String mCurrentShutterSpeedvailMax) {
        this.mCurrentShutterSpeedvailMax = mCurrentShutterSpeedvailMax;
    }

    public String getmCurrentShutterSpeedAvailMin() {
        return this.mCurrentShutterSpeedAvailMin;
    }

    public String getShutterSpeedvalMin() {
        int positionMidofSS = this.mShutterSpeedValueList.indexOf(this.mCurrentShutterSpeedAvailMin);
        if (positionMidofSS >= this.SHUTTER_SPEED_MAX_VALUE || positionMidofSS <= this.SHUTTER_SPEED_MIN_VALUE) {
            positionMidofSS = this.SHUTTER_SPEED_MAX_VALUE;
        }
        return this.mShutterSpeedValueList.get(positionMidofSS);
    }

    public void setmCurrentShutterSpeedAvailMin(String mCurrentShutterSpeedAvailMin) {
        this.mCurrentShutterSpeedAvailMin = mCurrentShutterSpeedAvailMin;
    }

    /* loaded from: classes.dex */
    public class BMShutterSpeedListner implements NotificationListener {
        private String[] TAGS = {CameraNotificationManager.SHUTTER_SPEED};

        public BMShutterSpeedListner() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(BracketMasterController.TAG, "onNotify() called is BMShutterSpeedListner ");
            BracketMasterController.this.refreshShutterSpeedValues();
        }
    }

    public void refreshShutterSpeedValues() {
        String value;
        String min_value;
        String max_value;
        this.infoSS = CameraSetting.getInstance().getShutterSpeedInfo();
        if (this.infoSS != null) {
            int denominator = this.infoSS.currentShutterSpeed_d;
            int numerator = this.infoSS.currentShutterSpeed_n;
            if (denominator == 0 || denominator == 1) {
                value = "" + numerator + "\"";
            } else {
                value = speedval(numerator, denominator);
            }
            setCurrentShutterSpeedVal(value);
            Log.d("min", "min:" + this.infoSS.currentAvailableMin_d + "   " + this.infoSS.currentAvailableMin_n);
            int minDenominator = this.infoSS.currentAvailableMin_d;
            int minumerator = this.infoSS.currentAvailableMin_n;
            if (minDenominator == 0 || minDenominator == 1) {
                min_value = "" + minumerator + "\"";
            } else {
                min_value = speedval(minumerator, minDenominator);
            }
            setmCurrentShutterSpeedAvailMin(min_value);
            Log.d("max", "max:" + this.infoSS.currentAvailableMin_d + "   " + this.infoSS.currentAvailableMin_n);
            int maxDenominator = this.infoSS.currentAvailableMax_d;
            int maxnumerator = this.infoSS.currentAvailableMax_n;
            if (maxDenominator == 0 || maxDenominator == 1) {
                max_value = "" + maxnumerator + "\"";
            } else {
                max_value = speedval(maxnumerator, maxDenominator);
            }
            setmCurrentShutterSpeedvailMax(max_value);
            Log.d("maxDiadem ...minDiadem...current", "ShootX" + max_value + "...." + min_value + "...." + value);
        }
    }

    public String convertApertureValueFormat(float value) {
        String displayValue;
        if (value == INVALID_APERTURE_VALUE) {
            displayValue = INVALID_APERTURE_STRING;
        } else if (value < THRESHODL_BIG_OR_ONE) {
            displayValue = String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
        } else {
            displayValue = String.format(FORMAT_BIG_DIGIT, Float.valueOf(value));
        }
        return displayValue.replace(',', '.');
    }

    /* loaded from: classes.dex */
    public class BMApertureListner implements NotificationListener {
        private String[] TAGS = {"Aperture"};

        public BMApertureListner() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(BracketMasterController.TAG, "onNotify() called is BMApertureListner ");
            BracketMasterController.this.refreshApertureValues();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    }

    public void refreshApertureValues() {
        CameraEx.ApertureInfo info = CameraSetting.getInstance().getApertureInfo();
        if (info != null) {
            int currentAperture = info.currentAperture;
            Log.d(TAG, "onNotify() BMApertureListner currentAperture:" + currentAperture);
            float aperture = currentAperture / NUMBER_100;
            Log.d(TAG, "onNotify() BMApertureListner>>>>>>>>>>>>>>>>>>aperture:" + aperture);
            this.apMinValue = info.currentAvailableMin / NUMBER_100;
            this.apMaxValue = info.currentAvailableMax / NUMBER_100;
            String convertedAperture = convertApertureValueFormat(aperture);
            setCurrentApertureVal(convertedAperture);
            setCurrentApertureAvailMax(convertApertureValueFormat(this.apMaxValue));
            setCurrentApertureAvailMin(convertApertureValueFormat(this.apMinValue));
            setMaxMinApertureSpeedValue();
            Log.d(TAG, "current Min Max " + this.mCurrentApertureVal + ",    " + this.apMinValue + ",   " + this.apMaxValue);
        }
    }

    public void createApertureValuesHashMap() {
        this.centerCordinatePoint = new HashMap<>();
        this.centerCordinatePoint.put("F1.0", Integer.valueOf(Math.round(this.y_Axis * 40.0f)));
        this.centerCordinatePoint.put("F1.1", Integer.valueOf(Math.round(this.y_Axis * 39.0f)));
        this.centerCordinatePoint.put("F1.3", Integer.valueOf(Math.round(this.y_Axis * 38.0f)));
        this.centerCordinatePoint.put("F1.4", Integer.valueOf(Math.round(this.y_Axis * 37.0f)));
        this.centerCordinatePoint.put("F1.6", Integer.valueOf(Math.round(this.y_Axis * 36.0f)));
        this.centerCordinatePoint.put("F1.8", Integer.valueOf(Math.round(this.y_Axis * 35.0f)));
        this.centerCordinatePoint.put("F2.0", Integer.valueOf(Math.round(this.y_Axis * 34.0f)));
        this.centerCordinatePoint.put("F2.2", Integer.valueOf(Math.round(this.y_Axis * 33.0f)));
        this.centerCordinatePoint.put("F2.5", Integer.valueOf(Math.round(this.y_Axis * 32.0f)));
        this.centerCordinatePoint.put("F2.8", Integer.valueOf(Math.round(this.y_Axis * 31.0f)));
        this.centerCordinatePoint.put("F3.2", Integer.valueOf(Math.round(this.y_Axis * 30.0f)));
        this.centerCordinatePoint.put("F3.5", Integer.valueOf(Math.round(this.y_Axis * 29.0f)));
        this.centerCordinatePoint.put("F4.0", Integer.valueOf(Math.round(this.y_Axis * 28.0f)));
        this.centerCordinatePoint.put("F4.5", Integer.valueOf(Math.round(this.y_Axis * 27.0f)));
        this.centerCordinatePoint.put("F5.0", Integer.valueOf(Math.round(this.y_Axis * 26.0f)));
        this.centerCordinatePoint.put("F5.6", Integer.valueOf(Math.round(this.y_Axis * 25.0f)));
        this.centerCordinatePoint.put("F6.3", Integer.valueOf(Math.round(this.y_Axis * 24.0f)));
        this.centerCordinatePoint.put("F7.1", Integer.valueOf(Math.round(this.y_Axis * 23.0f)));
        this.centerCordinatePoint.put("F8.0", Integer.valueOf(Math.round(this.y_Axis * 22.0f)));
        this.centerCordinatePoint.put("F9.0", Integer.valueOf(Math.round(this.y_Axis * 21.0f)));
        this.centerCordinatePoint.put("F10", Integer.valueOf(Math.round(this.y_Axis * 20.0f)));
        this.centerCordinatePoint.put("F11", Integer.valueOf(Math.round(this.y_Axis * 19.0f)));
        this.centerCordinatePoint.put("F13", Integer.valueOf(Math.round(this.y_Axis * 18.0f)));
        this.centerCordinatePoint.put("F14", Integer.valueOf(Math.round(this.y_Axis * 17.0f)));
        this.centerCordinatePoint.put("F16", Integer.valueOf(Math.round(this.y_Axis * 16.0f)));
        this.centerCordinatePoint.put("F18", Integer.valueOf(Math.round(this.y_Axis * 15.0f)));
        this.centerCordinatePoint.put("F20", Integer.valueOf(Math.round(this.y_Axis * 14.0f)));
        this.centerCordinatePoint.put("F22", Integer.valueOf(Math.round(this.y_Axis * 13.0f)));
        this.centerCordinatePoint.put("F25", Integer.valueOf(Math.round(this.y_Axis * 12.0f)));
        this.centerCordinatePoint.put("F29", Integer.valueOf(Math.round(this.y_Axis * 11.0f)));
        this.centerCordinatePoint.put("F32", Integer.valueOf(Math.round(this.y_Axis * THRESHODL_BIG_OR_ONE)));
        this.centerCordinatePoint.put("F36", Integer.valueOf(Math.round(this.y_Axis * 9.0f)));
        this.centerCordinatePoint.put("F40", Integer.valueOf(Math.round(this.y_Axis * 8.0f)));
        this.centerCordinatePoint.put("F45", Integer.valueOf(Math.round(this.y_Axis * 7.0f)));
        this.centerCordinatePoint.put("F51", Integer.valueOf(Math.round(this.y_Axis * 6.0f)));
        this.centerCordinatePoint.put("F57", Integer.valueOf(Math.round(this.y_Axis * 5.0f)));
        this.centerCordinatePoint.put("F64", Integer.valueOf(Math.round(this.y_Axis * 4.0f)));
        this.centerCordinatePoint.put("F72", Integer.valueOf(Math.round(this.y_Axis * 3.0f)));
        this.centerCordinatePoint.put("F81", Integer.valueOf(Math.round(this.y_Axis * 2.0f)));
        this.centerCordinatePoint.put("F90", Integer.valueOf(Math.round(this.y_Axis)));
    }

    public void createApertureValuesList() {
        this.mApertureValueList.add("F1.0");
        this.mApertureValueList.add("F1.1");
        this.mApertureValueList.add("F1.3");
        this.mApertureValueList.add("F1.4");
        this.mApertureValueList.add("F1.6");
        this.mApertureValueList.add("F1.8");
        this.mApertureValueList.add("F2.0");
        this.mApertureValueList.add("F2.2");
        this.mApertureValueList.add("F2.5");
        this.mApertureValueList.add("F2.8");
        this.mApertureValueList.add("F3.2");
        this.mApertureValueList.add("F3.5");
        this.mApertureValueList.add("F4.0");
        this.mApertureValueList.add("F4.5");
        this.mApertureValueList.add("F5.0");
        this.mApertureValueList.add("F5.6");
        this.mApertureValueList.add("F6.3");
        this.mApertureValueList.add("F7.1");
        this.mApertureValueList.add("F8.0");
        this.mApertureValueList.add("F9.0");
        this.mApertureValueList.add("F10");
        this.mApertureValueList.add("F11");
        this.mApertureValueList.add("F13");
        this.mApertureValueList.add("F14");
        this.mApertureValueList.add("F16");
        this.mApertureValueList.add("F18");
        this.mApertureValueList.add("F20");
        this.mApertureValueList.add("F22");
        this.mApertureValueList.add("F25");
        this.mApertureValueList.add("F29");
        this.mApertureValueList.add("F32");
        this.mApertureValueList.add("F36");
        this.mApertureValueList.add("F40");
        this.mApertureValueList.add("F45");
        this.mApertureValueList.add("F51");
        this.mApertureValueList.add("F57");
        this.mApertureValueList.add("F64");
        this.mApertureValueList.add("F72");
        this.mApertureValueList.add("F81");
        this.mApertureValueList.add("F90");
    }

    public String speedval(int num, int den) {
        float Val = num / den;
        Log.d("Val initial = ", "" + Val);
        float Val2 = Math.round(Val * THRESHODL_BIG_OR_ONE) / THRESHODL_BIG_OR_ONE;
        Log.d("Val new = ", "" + Val2);
        int Value = (int) (NUMBER_100 * Val2);
        switch (Value) {
            case 40:
                return "0.4\"";
            case PictureQualityController.QUALITY_FINE /* 50 */:
                return "0.5\"";
            case 60:
                return "0.6\"";
            case 80:
                return "0.8\"";
            case 130:
                return "1.3\"";
            case 160:
                return "1.6\"";
            case 250:
                return "2.5\"";
            case 320:
                return "3.2\"";
            default:
                String ret = "" + num + "/" + den;
                return ret;
        }
    }

    public void createShutterSpeedValuesHashMap() {
        this.centerCordinatePointofSS = new HashMap<>();
        this.centerCordinatePointofSS.put("30\"", Double.valueOf(0.0d));
        this.centerCordinatePointofSS.put("25\"", Double.valueOf(this.shiftXPosition));
        this.centerCordinatePointofSS.put("20\"", Double.valueOf(this.shiftXPosition * 2.0d));
        this.centerCordinatePointofSS.put("15\"", Double.valueOf(this.shiftXPosition * 3.0d));
        this.centerCordinatePointofSS.put("13\"", Double.valueOf(this.shiftXPosition * 4.0d));
        this.centerCordinatePointofSS.put("10\"", Double.valueOf(this.shiftXPosition * 5.0d));
        this.centerCordinatePointofSS.put("8\"", Double.valueOf(this.shiftXPosition * 6.0d));
        this.centerCordinatePointofSS.put("6\"", Double.valueOf(this.shiftXPosition * 7.0d));
        this.centerCordinatePointofSS.put("5\"", Double.valueOf(this.shiftXPosition * 8.0d));
        this.centerCordinatePointofSS.put("4\"", Double.valueOf(this.shiftXPosition * 9.0d));
        this.centerCordinatePointofSS.put("3.2\"", Double.valueOf(this.shiftXPosition * 10.0d));
        this.centerCordinatePointofSS.put("2.5\"", Double.valueOf(this.shiftXPosition * 11.0d));
        this.centerCordinatePointofSS.put("2\"", Double.valueOf(this.shiftXPosition * 12.0d));
        this.centerCordinatePointofSS.put("1.6\"", Double.valueOf(this.shiftXPosition * 13.0d));
        this.centerCordinatePointofSS.put("1.3\"", Double.valueOf(this.shiftXPosition * 14.0d));
        this.centerCordinatePointofSS.put("1\"", Double.valueOf(this.shiftXPosition * 15.0d));
        this.centerCordinatePointofSS.put("0.8\"", Double.valueOf(this.shiftXPosition * 16.0d));
        this.centerCordinatePointofSS.put("0.6\"", Double.valueOf(this.shiftXPosition * 17.0d));
        this.centerCordinatePointofSS.put("0.5\"", Double.valueOf(this.shiftXPosition * 18.0d));
        this.centerCordinatePointofSS.put("0.4\"", Double.valueOf(this.shiftXPosition * 19.0d));
        this.centerCordinatePointofSS.put("1/3", Double.valueOf(this.shiftXPosition * 20.0d));
        this.centerCordinatePointofSS.put("1/4", Double.valueOf(this.shiftXPosition * 21.0d));
        this.centerCordinatePointofSS.put("1/5", Double.valueOf(this.shiftXPosition * 22.0d));
        this.centerCordinatePointofSS.put("1/6", Double.valueOf(this.shiftXPosition * 23.0d));
        this.centerCordinatePointofSS.put("1/8", Double.valueOf(this.shiftXPosition * 24.0d));
        this.centerCordinatePointofSS.put("1/10", Double.valueOf(this.shiftXPosition * 25.0d));
        this.centerCordinatePointofSS.put("1/13", Double.valueOf(this.shiftXPosition * 26.0d));
        this.centerCordinatePointofSS.put("1/15", Double.valueOf(this.shiftXPosition * 27.0d));
        this.centerCordinatePointofSS.put("1/20", Double.valueOf(this.shiftXPosition * 28.0d));
        this.centerCordinatePointofSS.put("1/25", Double.valueOf(this.shiftXPosition * 29.0d));
        this.centerCordinatePointofSS.put("1/30", Double.valueOf(this.shiftXPosition * 30.0d));
        this.centerCordinatePointofSS.put("1/40", Double.valueOf(this.shiftXPosition * 31.0d));
        this.centerCordinatePointofSS.put("1/50", Double.valueOf(this.shiftXPosition * 32.0d));
        this.centerCordinatePointofSS.put("1/60", Double.valueOf(this.shiftXPosition * 33.0d));
        this.centerCordinatePointofSS.put("1/80", Double.valueOf(this.shiftXPosition * 34.0d));
        this.centerCordinatePointofSS.put("1/100", Double.valueOf(this.shiftXPosition * 35.0d));
        this.centerCordinatePointofSS.put("1/125", Double.valueOf(this.shiftXPosition * 36.0d));
        this.centerCordinatePointofSS.put("1/160", Double.valueOf(this.shiftXPosition * 37.0d));
        this.centerCordinatePointofSS.put("1/200", Double.valueOf(this.shiftXPosition * 38.0d));
        this.centerCordinatePointofSS.put("1/250", Double.valueOf(this.shiftXPosition * 39.0d));
        this.centerCordinatePointofSS.put("1/320", Double.valueOf(this.shiftXPosition * 40.0d));
        this.centerCordinatePointofSS.put("1/400", Double.valueOf(this.shiftXPosition * 41.0d));
        this.centerCordinatePointofSS.put("1/500", Double.valueOf(this.shiftXPosition * 42.0d));
        this.centerCordinatePointofSS.put("1/640", Double.valueOf(this.shiftXPosition * 43.0d));
        this.centerCordinatePointofSS.put("1/800", Double.valueOf(this.shiftXPosition * 44.0d));
        this.centerCordinatePointofSS.put("1/1000", Double.valueOf(this.shiftXPosition * 45.0d));
        this.centerCordinatePointofSS.put("1/1250", Double.valueOf(this.shiftXPosition * 46.0d));
        this.centerCordinatePointofSS.put("1/1600", Double.valueOf(this.shiftXPosition * 47.0d));
        this.centerCordinatePointofSS.put("1/2000", Double.valueOf(this.shiftXPosition * 48.0d));
        this.centerCordinatePointofSS.put("1/2500", Double.valueOf(this.shiftXPosition * 49.0d));
        this.centerCordinatePointofSS.put("1/3200", Double.valueOf(this.shiftXPosition * 50.0d));
        this.centerCordinatePointofSS.put("1/4000", Double.valueOf(this.shiftXPosition * 51.0d));
        this.centerCordinatePointofSS.put("1/5000", Double.valueOf(this.shiftXPosition * 52.0d));
        this.centerCordinatePointofSS.put("1/6400", Double.valueOf(this.shiftXPosition * 53.0d));
        this.centerCordinatePointofSS.put("1/8000", Double.valueOf(this.shiftXPosition * 54.0d));
        this.centerCordinatePointofSS.put("1/10000", Double.valueOf(this.shiftXPosition * 55.0d));
        this.centerCordinatePointofSS.put("1/12800", Double.valueOf(this.shiftXPosition * 56.0d));
        this.centerCordinatePointofSS.put("1/16000", Double.valueOf(this.shiftXPosition * 57.0d));
        this.centerCordinatePointofSS.put("1/20000", Double.valueOf(this.shiftXPosition * 58.0d));
        this.centerCordinatePointofSS.put("1/25600", Double.valueOf(this.shiftXPosition * 59.0d));
        this.centerCordinatePointofSS.put("1/32000", Double.valueOf(this.shiftXPosition * 60.0d));
    }

    public void createShutterSpeedValuesList() {
        this.mShutterSpeedValueList.add("30\"");
        this.mShutterSpeedValueList.add("25\"");
        this.mShutterSpeedValueList.add("20\"");
        this.mShutterSpeedValueList.add("15\"");
        this.mShutterSpeedValueList.add("13\"");
        this.mShutterSpeedValueList.add("10\"");
        this.mShutterSpeedValueList.add("8\"");
        this.mShutterSpeedValueList.add("6\"");
        this.mShutterSpeedValueList.add("5\"");
        this.mShutterSpeedValueList.add("4\"");
        this.mShutterSpeedValueList.add("3.2\"");
        this.mShutterSpeedValueList.add("2.5\"");
        this.mShutterSpeedValueList.add("2\"");
        this.mShutterSpeedValueList.add("1.6\"");
        this.mShutterSpeedValueList.add("1.3\"");
        this.mShutterSpeedValueList.add("1\"");
        this.mShutterSpeedValueList.add("0.8\"");
        this.mShutterSpeedValueList.add("0.6\"");
        this.mShutterSpeedValueList.add("0.5\"");
        this.mShutterSpeedValueList.add("0.4\"");
        this.mShutterSpeedValueList.add("1/3");
        this.mShutterSpeedValueList.add("1/4");
        this.mShutterSpeedValueList.add("1/5");
        this.mShutterSpeedValueList.add("1/6");
        this.mShutterSpeedValueList.add("1/8");
        this.mShutterSpeedValueList.add("1/10");
        this.mShutterSpeedValueList.add("1/13");
        this.mShutterSpeedValueList.add("1/15");
        this.mShutterSpeedValueList.add("1/20");
        this.mShutterSpeedValueList.add("1/25");
        this.mShutterSpeedValueList.add("1/30");
        this.mShutterSpeedValueList.add("1/40");
        this.mShutterSpeedValueList.add("1/50");
        this.mShutterSpeedValueList.add("1/60");
        this.mShutterSpeedValueList.add("1/80");
        this.mShutterSpeedValueList.add("1/100");
        this.mShutterSpeedValueList.add("1/125");
        this.mShutterSpeedValueList.add("1/160");
        this.mShutterSpeedValueList.add("1/200");
        this.mShutterSpeedValueList.add("1/250");
        this.mShutterSpeedValueList.add("1/320");
        this.mShutterSpeedValueList.add("1/400");
        this.mShutterSpeedValueList.add("1/500");
        this.mShutterSpeedValueList.add("1/640");
        this.mShutterSpeedValueList.add("1/800");
        this.mShutterSpeedValueList.add("1/1000");
        this.mShutterSpeedValueList.add("1/1250");
        this.mShutterSpeedValueList.add("1/1600");
        this.mShutterSpeedValueList.add("1/2000");
        this.mShutterSpeedValueList.add("1/2500");
        this.mShutterSpeedValueList.add("1/3200");
        this.mShutterSpeedValueList.add("1/4000");
        this.mShutterSpeedValueList.add("1/5000");
        this.mShutterSpeedValueList.add("1/6400");
        this.mShutterSpeedValueList.add("1/8000");
        this.mShutterSpeedValueList.add("1/10000");
        this.mShutterSpeedValueList.add("1/12800");
        this.mShutterSpeedValueList.add("1/16000");
        this.mShutterSpeedValueList.add("1/20000");
        this.mShutterSpeedValueList.add("1/25600");
        this.mShutterSpeedValueList.add("1/32000");
    }

    public float getXposition(int Y) {
        int y = 37 - (Y / 5);
        float x = ((float) (64.13d / 37.0d)) * y;
        return x;
    }

    public float getxp(double Xvalue, int Y) {
        int y = 35 - (Y / 5);
        double shift = Xvalue / this.shiftXPosition;
        if (shift <= 25.0d || shift > 1.0d) {
            double dob = 56.0d - (2.0d * shift);
            float x = ((float) Xvalue) + ((float) ((dob / 30.0d) * y));
            return x;
        }
        if (shift >= 27.0d || shift <= this.SHUTTER_SPEED_MAX_VALUE) {
            double dob2 = (shift - 27.0d) * 1.8d;
            float x2 = ((float) Xvalue) - ((float) ((dob2 / 30.0d) * y));
            return x2;
        }
        float x3 = (float) Xvalue;
        return x3;
    }

    public float getxpn(double Xvalue, int Y) {
        int y = 35 - (Y / 5);
        double shift = Xvalue / this.shiftXPosition;
        if (shift >= 47.0d || shift <= this.SHUTTER_SPEED_MAX_VALUE) {
            double dob = (shift - 28.0d) * 2.3d;
            float x = ((float) Xvalue) - ((float) ((dob / 30.0d) * y));
            return x;
        }
        float x2 = (float) Xvalue;
        return x2;
    }

    public double getSSDisplacement(double Xvalue) {
        double X1 = (Xvalue / this.shiftXPosition) * 6.65d;
        return X1;
    }

    public void unregisterNotificationListeners() {
        if (this.mAListner != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mAListner);
        }
        if (this.mSSListner != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mSSListner);
        }
        this.mSSListner = null;
        this.mAListner = null;
    }

    public void registerNotificationListeners() {
        if (this.mAListner == null) {
            this.mAListner = new BMApertureListner();
        }
        if (this.mSSListner == null) {
            this.mSSListner = new BMShutterSpeedListner();
        }
        CameraNotificationManager.getInstance().setNotificationListener(this.mAListner);
        CameraNotificationManager.getInstance().setNotificationListener(this.mSSListner);
    }
}
