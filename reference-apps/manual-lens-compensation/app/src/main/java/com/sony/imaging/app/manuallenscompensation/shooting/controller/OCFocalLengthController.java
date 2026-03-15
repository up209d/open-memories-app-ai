package com.sony.imaging.app.manuallenscompensation.shooting.controller;

import android.R;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppContext;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class OCFocalLengthController extends AbstractController {
    public static final String FOCAL_LENGTH = "focal_length";
    private ArrayList<String> mSupportedList;
    private static final String TAG = AppLog.getClassName();
    private static OCFocalLengthController sInstance = null;
    static Integer[] mFocalValueArray = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 24, 25, 28, 30, 32, 35, 40, 45, 50, 55, 60, 70, 75, 80, 85, 90, 100, Integer.valueOf(AppRoot.USER_KEYCODE.LEFT), 120, 135, 150, 180, 200, 210, 250, 300, 350, 400, 450, 500, Integer.valueOf(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S), 800, 1000};
    private static ArrayList<Integer> mSupportedFLength = new ArrayList<>(Arrays.asList(mFocalValueArray));
    private final int DEFAULT_MAX_VALUE = 1000;
    int position = 0;

    private OCFocalLengthController() {
        this.mSupportedList = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSupportedList == null) {
            this.mSupportedList = new ArrayList<>();
            this.mSupportedList.add(FOCAL_LENGTH);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static OCFocalLengthController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new OCFocalLengthController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, AppLog.getMethodName());
        LensCompensationParameter mParam = OCUtil.getInstance().getLensParameterObject();
        int focalLength4Disp = 8;
        if (mParam != null) {
            String focalLength = mParam.mFocalLength;
            if (focalLength != null && !"".equalsIgnoreCase(focalLength)) {
                focalLength4Disp = Integer.parseInt(focalLength);
                if (!mSupportedFLength.contains(Integer.valueOf(focalLength4Disp))) {
                    focalLength4Disp = getCameraSupportedFocalLenght(focalLength4Disp);
                }
            } else {
                focalLength4Disp = 8;
            }
        } else {
            AppLog.trace(TAG, AppLog.getMethodName() + LensCompensationParameter.ID_PARAMETER + "  =  " + mParam);
        }
        AppLog.trace(TAG, AppLog.getMethodName() + focalLength4Disp);
        String displayString = String.format(AppContext.getAppContext().getResources().getString(R.string.roamingText1), Integer.valueOf(focalLength4Disp));
        AppLog.exit(TAG, AppLog.getMethodName());
        return displayString;
    }

    public int getCameraSupportedFocalLenght(int focalLength4Disp) {
        int mreturnfocalLength4Disp;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (focalLength4Disp < 8) {
            mreturnfocalLength4Disp = 8;
        } else if (focalLength4Disp >= 1000) {
            mreturnfocalLength4Disp = 1000;
        } else {
            this.position = 0;
            mreturnfocalLength4Disp = searchClosestFValue(focalLength4Disp);
        }
        AppLog.info(TAG, "mreturnfocalLength4Disp=  " + mreturnfocalLength4Disp);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mreturnfocalLength4Disp;
    }

    private int searchClosestFValue(int paramFValue) {
        int value;
        AppLog.enter(TAG, AppLog.getMethodName());
        int right = mSupportedFLength.size() - 1;
        int position = binarySearch(mFocalValueArray, paramFValue, 0, right);
        int fvalue = mSupportedFLength.get(position).intValue();
        int svalue = mSupportedFLength.get(position + 1).intValue();
        AppLog.trace(TAG, AppLog.getMethodName() + "value  =" + paramFValue);
        int firstDiff = paramFValue - fvalue;
        int secondDiff = svalue - paramFValue;
        AppLog.trace(TAG, AppLog.getMethodName() + "firstDiff value  =" + firstDiff);
        AppLog.trace(TAG, AppLog.getMethodName() + "secondDiff value  =" + secondDiff);
        if (firstDiff <= secondDiff) {
            value = fvalue;
        } else {
            value = svalue;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + "value  =" + value);
        AppLog.exit(TAG, AppLog.getMethodName());
        return value;
    }

    private int binarySearch(Integer[] arr, int searchValue, int left, int right) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (right >= left) {
            int mid = (left + right) >>> 1;
            if (searchValue > arr[mid].intValue()) {
                return binarySearch(arr, searchValue, mid + 1, right);
            }
            return searchValue < arr[mid].intValue() ? binarySearch(arr, searchValue, left, mid - 1) : mid;
        }
        return right;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> supportedList = null;
        if (OCUtil.isSupportedByPF()) {
            supportedList = this.mSupportedList;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return supportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> availableValueList = null;
        if (OCUtil.isSupportedByPF()) {
            if (this.mSupportedList == null) {
                this.mSupportedList = new ArrayList<>();
                this.mSupportedList.add(FOCAL_LENGTH);
            }
            availableValueList = this.mSupportedList;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return availableValueList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }
}
