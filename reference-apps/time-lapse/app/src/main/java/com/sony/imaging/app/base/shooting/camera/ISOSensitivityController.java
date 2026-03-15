package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class ISOSensitivityController extends AbstractController {
    private static final String API_NAME = "setISOSensitivity";
    private static final int BASE_VALUE_FOR_EV_STEP = 50;
    private static final int EXPANDED_ISO_MENU_VALUE = 0;
    private static final int EXPANDED_ISO_PF_VALUE = 1;
    public static final String ISO_100 = "100";
    public static final String ISO_1000 = "1000";
    public static final String ISO_10000 = "10000";
    public static final String ISO_100Expanded = "100Expanded";
    private static final int ISO_100Expanded_VALUE = 100;
    public static final String ISO_102400 = "102400";
    public static final String ISO_125 = "125";
    public static final String ISO_1250 = "1250";
    public static final String ISO_12800 = "12800";
    public static final String ISO_128000 = "128000";
    public static final String ISO_160 = "160";
    public static final String ISO_1600 = "1600";
    public static final String ISO_16000 = "16000";
    public static final String ISO_160000 = "160000";
    public static final String ISO_200 = "200";
    public static final String ISO_2000 = "2000";
    public static final String ISO_20000 = "20000";
    public static final String ISO_204800 = "204800";
    public static final String ISO_250 = "250";
    public static final String ISO_2500 = "2500";
    public static final String ISO_25600 = "25600";
    public static final String ISO_256000 = "256000";
    public static final String ISO_320 = "320";
    public static final String ISO_3200 = "3200";
    public static final String ISO_32000 = "32000";
    public static final String ISO_320000 = "320000";
    public static final String ISO_400 = "400";
    public static final String ISO_4000 = "4000";
    public static final String ISO_40000 = "40000";
    public static final String ISO_409600 = "409600";
    public static final String ISO_50 = "50";
    public static final String ISO_500 = "500";
    public static final String ISO_5000 = "5000";
    public static final String ISO_50Expanded = "50Expanded";
    private static final int ISO_50Expanded_VALUE = 50;
    public static final String ISO_51200 = "51200";
    public static final String ISO_64 = "64";
    public static final String ISO_640 = "640";
    public static final String ISO_6400 = "6400";
    public static final String ISO_64000 = "64000";
    public static final String ISO_64Expanded = "64Expanded";
    private static final int ISO_64Expanded_VALUE = 64;
    public static final String ISO_80 = "80";
    public static final String ISO_800 = "800";
    public static final String ISO_8000 = "8000";
    public static final String ISO_80000 = "80000";
    public static final String ISO_80Expanded = "80Expanded";
    private static final int ISO_80Expanded_VALUE = 80;
    public static final String ISO_AUTO = "0";
    private static final int ISO_AUTO_VALUE = 0;
    private static final String LOG_MSG_GETISOSENSITIVITY = "getISOSensitivity = ";
    private static final String LOG_MSG_GETSUPPORTEDEXPANDEDISOSENSITIVITIES = "getSupportedExpandedISOSensitivities = ";
    private static final String LOG_MSG_GETSUPPORTEDISOSENSITIVITIES = "getSupportedISOSensitivities = ";
    private static final String LOG_MSG_ILLEGALVALUE = "Illegal value is set. ";
    private static final String LOG_MSG_SETISOSENSITIVITY = "setISOSensitivity = ";
    public static final String MENU_ITEM_ID_ISO = "Iso";
    public static final String PF_VALUE_ISO = "PfValue";
    private static final int POWER_FOR_1EV = 2;
    private static final String TAG = "ISOSensitivityController";
    private static ISOSensitivityController mInstance;
    private static List<String> mSupportedISOSensitivities;
    private CameraSetting mCameraSetting;
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static HashMap<String, String> mISO_Expanded = null;
    protected static List<String> mSupportedISOByBase = null;
    protected static List<String> mSupportedISO_ExpandedByBase = null;
    private static final String myName = ISOSensitivityController.class.getSimpleName();

    public static final String getName() {
        return myName;
    }

    public static ISOSensitivityController getInstance() {
        if (mInstance == null) {
            new ISOSensitivityController();
        }
        return mInstance;
    }

    private static void setController(ISOSensitivityController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected ISOSensitivityController() {
        this.mCameraSetting = null;
        this.mCameraSetting = CameraSetting.getInstance();
        setController(this);
        createSupportedValueByBase();
    }

    protected static void createSupportedValueByBase() {
        mSupportedISOByBase = new ArrayList();
        mSupportedISOByBase.add("0");
        mSupportedISOByBase.add(ISO_80);
        mSupportedISOByBase.add("100");
        mSupportedISOByBase.add(ISO_125);
        mSupportedISOByBase.add(ISO_160);
        mSupportedISOByBase.add(ISO_200);
        mSupportedISOByBase.add(ISO_250);
        mSupportedISOByBase.add(ISO_320);
        mSupportedISOByBase.add(ISO_400);
        mSupportedISOByBase.add(ISO_500);
        mSupportedISOByBase.add(ISO_640);
        mSupportedISOByBase.add(ISO_800);
        mSupportedISOByBase.add(ISO_1000);
        mSupportedISOByBase.add(ISO_1250);
        mSupportedISOByBase.add("1600");
        mSupportedISOByBase.add(ISO_2000);
        mSupportedISOByBase.add(ISO_2500);
        mSupportedISOByBase.add(ISO_3200);
        mSupportedISOByBase.add(ISO_4000);
        mSupportedISOByBase.add(ISO_5000);
        mSupportedISOByBase.add(ISO_6400);
        mSupportedISOByBase.add(ISO_8000);
        mSupportedISOByBase.add(ISO_10000);
        mSupportedISOByBase.add(ISO_12800);
        mSupportedISOByBase.add(ISO_16000);
        mSupportedISOByBase.add(ISO_20000);
        mSupportedISOByBase.add(ISO_25600);
        mSupportedISOByBase.add(ISO_32000);
        mSupportedISOByBase.add(ISO_40000);
        mSupportedISOByBase.add(ISO_51200);
        mSupportedISOByBase.add(ISO_64000);
        mSupportedISOByBase.add(ISO_80000);
        mSupportedISOByBase.add(ISO_102400);
        mSupportedISOByBase.add(ISO_128000);
        mSupportedISOByBase.add(ISO_160000);
        mSupportedISOByBase.add(ISO_204800);
        mSupportedISOByBase.add(ISO_256000);
        mSupportedISOByBase.add(ISO_320000);
        mSupportedISOByBase.add(ISO_409600);
        mSupportedISO_ExpandedByBase = new ArrayList();
        mSupportedISO_ExpandedByBase.add(Integer.toString(50));
        mSupportedISO_ExpandedByBase.add(Integer.toString(ISO_64Expanded_VALUE));
        mSupportedISO_ExpandedByBase.add(Integer.toString(ISO_80Expanded_VALUE));
        mSupportedISO_ExpandedByBase.add(Integer.toString(100));
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraReopened() {
        Log.i(TAG, "onCameraReopened");
        unavailableMultiShootNR();
        mSupportedISOSensitivities = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
        mISO_Expanded = createSupportedExpandedIso(this.mCameraSetting.getSupportedParameters());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        String expandedIso;
        if (itemId == null) {
            itemId = PF_VALUE_ISO;
        }
        try {
            if (MENU_ITEM_ID_ISO.equals(itemId) && (expandedIso = getExpandedIsoValue(1, value)) != null) {
                value = expandedIso;
            }
            setIsoValue(value);
        } catch (NumberFormatException e) {
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_ILLEGALVALUE).append(value);
            Log.e(TAG, strBuilder.toString());
        }
    }

    private void setIsoValue(String value) {
        Log.i(TAG, "setIsoValue value=" + value);
        int raw = Integer.parseInt(value);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getEmptyParameters();
        ((CameraEx.ParametersModifier) params.second).setISOSensitivity(raw);
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_SETISOSENSITIVITY).append(raw);
        Log.i(TAG, strBuilder.toString());
        CameraSetting.getInstance().setParameters(params);
    }

    public String getValue() {
        return getValue(PF_VALUE_ISO);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        String expandedIso;
        if (itemId == null) {
            itemId = PF_VALUE_ISO;
        }
        String value = getIsoValue();
        if (MENU_ITEM_ID_ISO.equals(itemId) && (expandedIso = getExpandedIsoValue(0, value)) != null) {
            return expandedIso;
        }
        return value;
    }

    private String getIsoValue() {
        int raw = 0;
        if (mSupportedISOSensitivities != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getParameters();
            raw = ((CameraEx.ParametersModifier) params.second).getISOSensitivity();
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETISOSENSITIVITY).append(raw);
        Log.i(TAG, strBuilder.toString());
        String value = Integer.toString(raw);
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String itemId) {
        String expandedIsoValue;
        if (itemId == null) {
            itemId = PF_VALUE_ISO;
        }
        if (mSupportedISOSensitivities == null) {
            mSupportedISOSensitivities = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
        }
        if (mISO_Expanded == null) {
            mISO_Expanded = createSupportedExpandedIso(this.mCameraSetting.getSupportedParameters());
        }
        List<String> ret = null;
        List<String> list = mSupportedISOSensitivities;
        if (list != null && list.size() > 0) {
            if (0 == 0) {
                ret = new ArrayList<>();
            }
            for (String v : list) {
                if (mISO_Expanded != null && mISO_Expanded.containsKey(v)) {
                    if (mSupportedISO_ExpandedByBase != null && !mSupportedISO_ExpandedByBase.contains(v)) {
                    }
                    if (MENU_ITEM_ID_ISO.equals(itemId)) {
                        v = expandedIsoValue;
                    }
                    ret.add(v);
                } else {
                    if (mSupportedISOByBase != null && !mSupportedISOByBase.contains(v)) {
                    }
                    if (MENU_ITEM_ID_ISO.equals(itemId) && mISO_Expanded != null && mISO_Expanded.size() > 0 && (expandedIsoValue = getExpandedIsoValue(0, v)) != null) {
                        v = expandedIsoValue;
                    }
                    ret.add(v);
                }
            }
        }
        return ret;
    }

    private static List<String> createSupportedValueArray(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        List<Integer> supported;
        List<String> list = new ArrayList<>();
        if (isMultiShootNR()) {
            supported = ((CameraEx.ParametersModifier) params.second).getSupportedISOSensitivitiesForMultiShootNR();
        } else {
            supported = ((CameraEx.ParametersModifier) params.second).getSupportedISOSensitivities();
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDISOSENSITIVITIES).append(supported);
        Log.i(TAG, strBuilder.toString());
        if (supported != null) {
            for (Integer i : supported) {
                list.add(Integer.toString(i.intValue()));
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:12:0x0052. Please report as an issue. */
    private static HashMap<String, String> createSupportedExpandedIso(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        HashMap<String, String> list = null;
        if (CameraSetting.getPfApiVersion() >= 2) {
            List<Integer> supported = null;
            if (!isMultiShootNR()) {
                supported = ((CameraEx.ParametersModifier) params.second).getSupportedExpandedISOSensitivities();
            }
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_GETSUPPORTEDEXPANDEDISOSENSITIVITIES).append(supported);
            Log.i(TAG, strBuilder.toString());
            if (supported != null) {
                list = new HashMap<>();
                for (Integer i : supported) {
                    String value = null;
                    switch (i.intValue()) {
                        case PictureQualityController.QUALITY_FINE /* 50 */:
                            value = ISO_50Expanded;
                            break;
                        case ISO_64Expanded_VALUE /* 64 */:
                            value = ISO_64Expanded;
                            break;
                        case ISO_80Expanded_VALUE /* 80 */:
                            value = ISO_80Expanded;
                            break;
                        case 100:
                            value = ISO_100Expanded;
                            break;
                    }
                    if (value != null) {
                        list.put(Integer.toString(i.intValue()), value);
                    }
                }
            }
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String itemId) {
        if (itemId == null) {
            itemId = PF_VALUE_ISO;
        }
        List<String> availables = null;
        if (MENU_ITEM_ID_ISO.equals(itemId) || PF_VALUE_ISO.equals(itemId)) {
            availables = getAvailableIsoValue(itemId);
        }
        if (availables == null || availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    private List<String> getAvailableIsoValue(String itemId) {
        String expandedIso;
        List<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(PF_VALUE_ISO);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        if (supporteds != null && supporteds != null) {
            for (String v : supporteds) {
                if (AvailableInfo.isAvailable((Camera.Parameters) params.first, (CameraEx.ParametersModifier) params.second, API_NAME, v)) {
                    if (MENU_ITEM_ID_ISO.equals(itemId) && (expandedIso = getExpandedIsoValue(0, v)) != null) {
                        v = expandedIso;
                    }
                    availables.add(v);
                }
            }
        }
        return availables;
    }

    public void increment() {
        List<String> availables = getAvailableValue(PF_VALUE_ISO);
        String nowValue = getValue(PF_VALUE_ISO);
        int nowIndex = availables.indexOf(nowValue);
        if (nowIndex < 0) {
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_ILLEGALVALUE).append(nowValue);
            Log.e(TAG, strBuilder.toString());
        } else {
            int newIndex = Math.min(nowIndex + 1, availables.size() - 1);
            setValue(PF_VALUE_ISO, availables.get(newIndex));
        }
    }

    public void decrement() {
        List<String> availables = getAvailableValue(PF_VALUE_ISO);
        String nowValue = getValue(PF_VALUE_ISO);
        int nowIndex = availables.indexOf(nowValue);
        if (nowIndex < 0) {
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_ILLEGALVALUE).append(nowValue);
            Log.e(TAG, strBuilder.toString());
        } else {
            int newIndex = Math.max(nowIndex - 1, 0);
            setValue(PF_VALUE_ISO, availables.get(newIndex));
        }
    }

    public String getAutoValue() {
        return getAutoValue(PF_VALUE_ISO);
    }

    public String getAutoValue(String itemId) {
        if (itemId == null) {
            itemId = PF_VALUE_ISO;
        }
        int raw = CameraSetting.getInstance().getISOSensitivityAuto();
        if (MENU_ITEM_ID_ISO.equals(itemId)) {
            String expandedIso = getExpandedIsoValue(0, Integer.toString(raw));
            if (expandedIso != null) {
                return expandedIso;
            }
            String value = Integer.toString(raw);
            return value;
        }
        String value2 = Integer.toString(raw);
        return value2;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        return AvailableInfo.isAvailable((Camera.Parameters) params.first, (CameraEx.ParametersModifier) params.second, API_NAME, null);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        boolean b = isUnavailableAPISceneFactor(API_NAME, null);
        return b;
    }

    public boolean isSupported1EVStepValueOrSpecial(String value) {
        try {
            List<String> supported = getSupportedValue(MENU_ITEM_ID_ISO);
            if (!supported.contains(value)) {
                return false;
            }
            String expandedIso = getExpandedIsoValue(1, value);
            if (expandedIso != null) {
                value = expandedIso;
            }
            int raw = Integer.parseInt(value);
            if (raw == 0) {
                return true;
            }
            for (int step = 50; raw >= step; step *= 2) {
                if (raw == step) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), LOG_MSG_ILLEGALVALUE).append(value);
            Log.e(TAG, strBuilder.toString());
            return false;
        }
    }

    private String getExpandedIsoValue(int getType, String value) {
        String ret = null;
        if (getType != 0 && getType != 1) {
            return null;
        }
        if (mISO_Expanded == null) {
            mISO_Expanded = createSupportedExpandedIso(this.mCameraSetting.getSupportedParameters());
        }
        if (mISO_Expanded != null && mISO_Expanded.size() > 0) {
            Iterator i$ = mISO_Expanded.entrySet().iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Map.Entry<String, String> entry = i$.next();
                if (getType == 0) {
                    if (entry.getKey().equals(value)) {
                        ret = entry.getValue();
                        break;
                    }
                } else if (entry.getValue().equals(value)) {
                    ret = entry.getKey();
                    break;
                }
            }
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        Log.i(TAG, "onCameraSet");
        unavailableMultiShootNR();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        if (mSupportedISOSensitivities == null) {
            mSupportedISOSensitivities = createSupportedValueArray(this.mCameraSetting.getSupportedParameters());
        }
        if (mISO_Expanded == null) {
            mISO_Expanded = createSupportedExpandedIso(this.mCameraSetting.getSupportedParameters());
        }
        if (mSupportedISOSensitivities != null || mISO_Expanded != null) {
            String value = getIsoValue();
            boolean isSupportedByBase = true;
            if (mISO_Expanded != null && mISO_Expanded.containsKey(value)) {
                if (mSupportedISO_ExpandedByBase == null || !mSupportedISO_ExpandedByBase.contains(value)) {
                    isSupportedByBase = false;
                }
            } else if (mSupportedISOByBase == null || !mSupportedISOByBase.contains(value)) {
                isSupportedByBase = false;
            }
            if (!isSupportedByBase) {
                List<String> availables = getAvailableValue(MENU_ITEM_ID_ISO);
                if (availables != null) {
                    if (availables.contains("0")) {
                        value = "0";
                    } else {
                        Iterator i$ = mSupportedISOByBase.iterator();
                        while (true) {
                            if (!i$.hasNext()) {
                                break;
                            }
                            String supportedValue = i$.next();
                            if (availables.contains(supportedValue)) {
                                value = supportedValue;
                                break;
                            }
                        }
                    }
                }
                setIsoValue(value);
            }
        }
    }

    protected static boolean isMultiShootNR() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        if (isMovieMode()) {
            return false;
        }
        boolean ret = ((CameraEx.ParametersModifier) params.second).getMultiShootNRMode();
        return ret;
    }

    protected static boolean isMovieMode() {
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        return true;
    }

    protected void unavailableMultiShootNR() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = this.mCameraSetting.getSupportedParameters();
        List<Integer> supportedForMultiShootNR = ((CameraEx.ParametersModifier) params.second).getSupportedISOSensitivitiesForMultiShootNR();
        if (supportedForMultiShootNR != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) p.second).setMultiShootNRMode(false);
            this.mCameraSetting.setParametersDirect(p);
        }
    }
}
