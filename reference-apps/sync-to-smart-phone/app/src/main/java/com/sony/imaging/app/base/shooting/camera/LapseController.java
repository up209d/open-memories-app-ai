package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class LapseController extends AbstractController {
    public static final String API_NAME_LAPSE = "setIntervalRecTime";
    private static final String GET_AVAILABLE_VALUE = "getAvailableValue:";
    private static final String GET_SUPPORTED_VALUE = "getSupportedValue:";
    private static final String GET_VALUE = "getValue:";
    public static final String INTERVAL = "Interval";
    public static final String INTERVAL_10S = "Interval-10s";
    public static final String INTERVAL_1S = "Interval-1s";
    public static final String INTERVAL_2S = "Interval-2s";
    public static final String INTERVAL_30S = "Interval-30s";
    public static final String INTERVAL_5S = "Interval-5s";
    public static final String INTERVAL_60S = "Interval-60s";
    private static final String IS_AVAILABLE = "isAvailable:";
    private static final String LOG_BOOLEAN_TAG = ", return:";
    private static final String LOG_VALUE_TAG = ", value:";
    private static final int PF_VERSION_SUPORT_INTERVALREC = 9;
    private static final String SET_VALUE = "setValue:";
    protected static final int S_10S = 10000;
    protected static final int S_1S = 1000;
    protected static final int S_2S = 2000;
    protected static final int S_30S = 30000;
    protected static final int S_5S = 5000;
    protected static final int S_60S = 60000;
    public static final String TAG = "LapseController";
    private static LapseController mInstance;
    protected CameraSetting mCamSet = CameraSetting.getInstance();
    private static StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    protected static SparseArray<String> pf2app = null;
    protected static HashMap<String, Integer> app2pf = null;

    protected static void setIntervalSecDictionay() {
        app2pf = new HashMap<>();
        pf2app = new SparseArray<>();
        app2pf.put(INTERVAL_1S, 1000);
        app2pf.put(INTERVAL_2S, Integer.valueOf(S_2S));
        app2pf.put(INTERVAL_5S, 5000);
        app2pf.put(INTERVAL_10S, Integer.valueOf(S_10S));
        app2pf.put(INTERVAL_30S, Integer.valueOf(S_30S));
        app2pf.put(INTERVAL_60S, Integer.valueOf(S_60S));
        pf2app.put(1000, INTERVAL_1S);
        pf2app.put(S_2S, INTERVAL_2S);
        pf2app.put(5000, INTERVAL_5S);
        pf2app.put(S_10S, INTERVAL_10S);
        pf2app.put(S_30S, INTERVAL_30S);
        pf2app.put(S_60S, INTERVAL_60S);
    }

    protected LapseController() {
        setIntervalSecDictionay();
    }

    public static LapseController getInstance() {
        if (mInstance == null) {
            mInstance = new LapseController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        if ("Interval".equals(tag) && 9 <= Environment.getVersionPfAPI() && 2 != this.mCamSet.getCurrentMode()) {
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), SET_VALUE).append(tag).append(LOG_VALUE_TAG).append(value);
            Log.i(TAG, strBuilder.toString());
            Integer intValue = app2pf.get(value);
            if (intValue != null) {
                int v = intValue.intValue();
                ((CameraEx.ParametersModifier) emptyParam.second).setIntervalRecTime(v);
                this.mCamSet.setParameters(emptyParam);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String value = null;
        if (Environment.isNewBizDeviceActionCam() && 9 <= Environment.getVersionPfAPI()) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
            CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) p.second;
            if ("Interval".equals(tag)) {
                int time = params.getIntervalRecTime();
                value = pf2app.get(time);
            }
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), GET_VALUE).append(tag).append(LOG_VALUE_TAG).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> value = null;
        if ("Interval".equals(tag) && Environment.isNewBizDeviceActionCam() && 9 <= Environment.getVersionPfAPI() && 2 != this.mCamSet.getCurrentMode()) {
            value = new ArrayList<>();
            List<Integer> supporteds = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedIntervalRecTime();
            if (supporteds != null) {
                for (Integer i : supporteds) {
                    String app = pf2app.get(i.intValue());
                    if (app != null) {
                        value.add(app);
                    }
                }
            }
            if (value.size() == 0) {
                value = null;
            }
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), GET_SUPPORTED_VALUE).append(tag).append(LOG_VALUE_TAG).append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue("Interval");
        if (supporteds != null) {
            for (String s : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME_LAPSE, app2pf.get(s))) {
                    availables.add(s);
                }
            }
        }
        if (availables.size() == 0) {
            availables = null;
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), GET_AVAILABLE_VALUE).append(tag).append(LOG_VALUE_TAG).append(availables);
        Log.i(TAG, strBuilder.toString());
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = false;
        if ("Interval".equals(tag) && Environment.isNewBizDeviceActionCam() && 9 <= Environment.getVersionPfAPI() && 2 != this.mCamSet.getCurrentMode()) {
            ret = AvailableInfo.isAvailable(API_NAME_LAPSE, null);
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), IS_AVAILABLE).append(tag).append(LOG_BOOLEAN_TAG).append(ret);
        Log.i(TAG, strBuilder.toString());
        return ret;
    }
}
