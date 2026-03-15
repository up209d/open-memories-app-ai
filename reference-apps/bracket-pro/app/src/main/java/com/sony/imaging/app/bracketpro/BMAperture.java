package com.sony.imaging.app.bracketpro;

import android.util.Log;

/* loaded from: classes.dex */
public class BMAperture {
    private static int shiftSecond;
    private static int shiftThird;
    private static int thirdShootYvalue;
    private static int maxApertureValue = 0;
    private static int minApertureValue = 0;
    private static int mFirstYvalue = 0;
    private static int mCenterYval = 0;
    private static int mSecondYvalue = 0;
    private static double mFirstXvalue = 0.0d;
    private static double mCenterXVal = 0.0d;
    private static double mSecondXvalue = 0.0d;
    private static String centerSSValue = "";
    private static String centerApertureValue = "";
    private static String firstApertureValue = "";
    private static String SecondApertureValue = "";
    private static int secondShootYvalue = 0;
    public static int mGauge = 5;

    public int getmGauge() {
        return mGauge;
    }

    public void setmGauge(int mGauge2) {
        mGauge = mGauge2;
    }

    public int getMaxApertureValue() {
        return maxApertureValue;
    }

    public void setMaxApertureValue(int maxApertureValue2) {
        maxApertureValue = maxApertureValue2;
    }

    public int getMinApertureValue() {
        return minApertureValue;
    }

    public void setMinApertureValue(int minApertureValue2) {
        minApertureValue = minApertureValue2;
    }

    public int getmFirstYvalue() {
        return mFirstYvalue;
    }

    public void setmFirstYvalue(int mFirstYvalue2) {
        mFirstYvalue = mFirstYvalue2;
    }

    public int getmCenterYval() {
        return mCenterYval;
    }

    public void setmCenterYval(int mCenterYval2) {
        mCenterYval = mCenterYval2;
    }

    public int getmSecondYvalue() {
        return mSecondYvalue;
    }

    public void setmSecondYvalue(int mSecondYvalue2) {
        mSecondYvalue = mSecondYvalue2;
    }

    public double getmFirstXvalue() {
        return mFirstXvalue;
    }

    public void setmFirstXvalue(double mFirstXvalue2) {
        mFirstXvalue = mFirstXvalue2;
    }

    public double getmCenterXVal() {
        return mCenterXVal;
    }

    public void setmCenterXVal(double mCenterXVal2) {
        mCenterXVal = mCenterXVal2;
    }

    public double getmSecondXvalue() {
        return mSecondXvalue;
    }

    public void setmSecondXvalue(double mSecondXvalue2) {
        mSecondXvalue = mSecondXvalue2;
    }

    public String getCenterSSValue() {
        return centerSSValue;
    }

    public void setCenterSSValue(String centerSSValue2) {
        centerSSValue = centerSSValue2;
    }

    public String getCenterApertureValue() {
        return centerApertureValue;
    }

    public void setCenterApertureValue(String centerApertureValue2) {
        centerApertureValue = centerApertureValue2;
    }

    public String getFirstApertureValue() {
        return firstApertureValue;
    }

    public void setFirstApertureValue(String firstApertureValue2) {
        firstApertureValue = firstApertureValue2;
    }

    public String getSecondApertureValue() {
        return SecondApertureValue;
    }

    public void setSecondApertureValue(String secondApertureValue) {
        SecondApertureValue = secondApertureValue;
    }

    public int getThirdShootingPosition() {
        shiftThird = 0;
        Log.d("TAG", "handleShutterCallbackAperture()  third value:" + mSecondYvalue);
        if (maxApertureValue >= mSecondYvalue) {
            thirdShootYvalue = maxApertureValue;
            shiftThird = (mCenterYval - maxApertureValue) / 5;
        } else {
            shiftThird = (mCenterYval - mSecondYvalue) / 5;
        }
        return shiftThird - 1;
    }

    public int getSecondShootingPosition() {
        return getSecondShift() + getThirdShootingPosition();
    }

    public int getSecondShift() {
        shiftSecond = 2;
        Log.d("TAG", "handleShutterCallbackAperture()  second  value:" + mFirstYvalue);
        Log.d("TAG", "handleShutterCallbackAperture()  first  value:" + mCenterYval);
        if (minApertureValue <= mFirstYvalue) {
            secondShootYvalue = minApertureValue;
            shiftSecond = (minApertureValue - mCenterYval) / 5;
        } else {
            shiftSecond = (mFirstYvalue - mCenterYval) / 5;
        }
        return shiftSecond - 1;
    }

    public void setShiftThird(int shiftThird2) {
        shiftThird = shiftThird2;
    }

    public void setShiftSecond(int shiftSecond2) {
        shiftSecond = shiftSecond2;
    }
}
