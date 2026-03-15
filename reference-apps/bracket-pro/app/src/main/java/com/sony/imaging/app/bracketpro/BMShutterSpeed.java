package com.sony.imaging.app.bracketpro;

import android.util.Log;

/* loaded from: classes.dex */
public class BMShutterSpeed {
    private static String SecondShutterSpeedValue;
    private static String centerApertureValue;
    private static String centerShutterSpeedValue;
    private static String firstShutterSpeedValue1;
    public static int mGaureSS;
    private static int maxApertureValue;
    private static double maxSSValue;
    private static int minApertureValue;
    private static double minSSValue;
    private static int shiftSecond;
    private static int shiftThird;
    int secondShootYvalue = 0;
    int thirdShootYvalue = 0;
    private static int mFirstYvalue = 0;
    private static int mCenterYval = 0;
    private static int mThirdYvalue = 0;
    private static double mCenterXVal = 0.0d;
    private static double mFirstXvalue = 0.0d;
    private static double mSecondXvalue = 0.0d;

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

    public int getmThirdYvalue() {
        return mThirdYvalue;
    }

    public void setmThirdYvalue(int mSecondYvalue) {
        mThirdYvalue = mSecondYvalue;
    }

    public double getmCenterXVal() {
        return mCenterXVal;
    }

    public void setmCenterXVal(double mCenterXVal2) {
        mCenterXVal = mCenterXVal2;
    }

    public double getmFirstXvalue() {
        return mFirstXvalue;
    }

    public void setmFirstXvalue(double mFirstXvalue2) {
        mFirstXvalue = mFirstXvalue2;
    }

    public double getmSecondXvalue() {
        return mSecondXvalue;
    }

    public void setmSecondXvalue(double mSecondXvalue2) {
        mSecondXvalue = mSecondXvalue2;
    }

    public String getFirstShutterSpeedValue1() {
        return firstShutterSpeedValue1;
    }

    public void setFirstShutterSpeedValue1(String firstShutterSpeedValue12) {
        firstShutterSpeedValue1 = firstShutterSpeedValue12;
    }

    public String getCenterShutterSpeedValue() {
        return centerShutterSpeedValue;
    }

    public void setCenterShutterSpeedValue(String centerShutterSpeedValue2) {
        centerShutterSpeedValue = centerShutterSpeedValue2;
    }

    public String getSecondShutterSpeedValue() {
        return SecondShutterSpeedValue;
    }

    public void setSecondShutterSpeedValue(String secondShutterSpeedValue) {
        SecondShutterSpeedValue = secondShutterSpeedValue;
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

    public double getMaxSSValue() {
        return maxSSValue;
    }

    public void setMaxSSValue(double maxSSValue2) {
        maxSSValue = maxSSValue2;
    }

    public double getMinSSValue() {
        return minSSValue;
    }

    public void setMinSSValue(double minSSValue2) {
        minSSValue = minSSValue2;
    }

    public String getCenterApertureValue() {
        return centerApertureValue;
    }

    public void setCenterApertureValue(String centerApertureValue2) {
        centerApertureValue = centerApertureValue2;
    }

    public int getThirdShootingPosition() {
        Log.d("min sec", "ShootX" + minSSValue + "......." + mSecondXvalue);
        if (mSecondXvalue <= minSSValue) {
            mSecondXvalue = minSSValue;
        }
        if (8.0d >= mSecondXvalue) {
            shiftThird = (int) (getmCenterXVal() / 8.65d);
        } else if (mSecondXvalue >= 449.0d) {
            shiftSecond = (int) ((450.0d - mCenterXVal) / 8.65d);
        } else {
            shiftThird = (int) ((mCenterXVal - mSecondXvalue) / 8.6d);
        }
        return shiftThird;
    }

    public int getSecondShootingPosition() {
        return getSecondShift() + getThirdShootingPosition();
    }

    public int getSecondShift() {
        Log.d("max third", "ShootX" + maxSSValue + "......." + mFirstXvalue);
        if (mFirstXvalue >= maxSSValue) {
            mFirstXvalue = maxSSValue;
        }
        if (8.0d >= mFirstXvalue) {
            shiftSecond = (int) (getmCenterXVal() / 8.65d);
        } else if (mFirstXvalue >= 449.0d) {
            shiftSecond = (int) ((450.0d - mCenterXVal) / 8.65d);
        } else {
            double shiftSecond1 = (mFirstXvalue - mCenterXVal) / 8.6d;
            shiftSecond = (int) shiftSecond1;
        }
        return shiftSecond;
    }

    public void setShiftThird(int shiftThird2) {
        shiftThird = shiftThird2;
    }

    public void setShiftSecond(int shiftSecond2) {
        shiftSecond = shiftSecond2;
    }
}
