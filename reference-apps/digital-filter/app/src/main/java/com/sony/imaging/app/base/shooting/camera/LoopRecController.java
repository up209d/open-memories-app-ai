package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.media.MediaRecorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class LoopRecController extends AbstractController {
    public static final String API_NAME_LOOPREC = "setLoopRec";
    private static final String LOG_BOOLEAN_TAG = ", return:";
    public static final String MAX_DURATION = "LoopRec_MaxDiration";
    public static final String MAX_DURATION_120min = "LoopRec_MaxDiration_120min";
    public static final String MAX_DURATION_20min = "LoopRec_MaxDiration_20min";
    public static final String MAX_DURATION_5min = "LoopRec_MaxDiration_5min";
    public static final String MAX_DURATION_60min = "LoopRec_MaxDiration_60min";
    public static final String MAX_DURATION_UNLIMITED = "LoopRec_MaxDiration_unlimited";
    protected static final int PF_VALUE_120MIN = 120;
    protected static final int PF_VALUE_20MIN = 20;
    protected static final int PF_VALUE_5MIN = 5;
    protected static final int PF_VALUE_60MIN = 60;
    protected static final int PF_VALUE_UNLIMITED = 0;
    protected static LoopRecController mInstance;
    protected CameraSetting mCamSet = CameraSetting.getInstance();
    public static final String TAG = LoopRecController.class.getSimpleName();
    private static StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    protected static SparseArray<String> pf2app = null;
    protected static HashMap<String, Integer> app2pf = null;
    private static final String myName = TAG;

    public static final String getName() {
        return myName;
    }

    protected static void setLoopRecMaxTimeDictionay() {
        app2pf = new HashMap<>();
        pf2app = new SparseArray<>();
        app2pf.put(MAX_DURATION_5min, 5);
        app2pf.put(MAX_DURATION_20min, 20);
        app2pf.put(MAX_DURATION_60min, Integer.valueOf(PF_VALUE_60MIN));
        app2pf.put(MAX_DURATION_120min, Integer.valueOf(PF_VALUE_120MIN));
        app2pf.put(MAX_DURATION_UNLIMITED, 0);
        pf2app.put(5, MAX_DURATION_5min);
        pf2app.put(20, MAX_DURATION_20min);
        pf2app.put(PF_VALUE_60MIN, MAX_DURATION_60min);
        pf2app.put(PF_VALUE_120MIN, MAX_DURATION_120min);
        pf2app.put(0, MAX_DURATION_UNLIMITED);
    }

    protected LoopRecController() {
        setLoopRecMaxTimeDictionay();
        setController(this);
    }

    public static LoopRecController getInstance() {
        if (mInstance == null) {
            mInstance = new LoopRecController();
        }
        return mInstance;
    }

    private static void setController(LoopRecController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        if (MAX_DURATION.equals(tag) && Environment.isLoopRecAPISupported() && 2 == this.mCamSet.getCurrentMode()) {
            StringBuilder strBuilder = sStringBuilder.get();
            strBuilder.replace(0, strBuilder.length(), "setValue:").append(tag).append(", value:").append(value);
            Log.i(TAG, strBuilder.toString());
            int intValue = app2pf.get(value).intValue();
            MediaRecorder.Parameters params = new MediaRecorder.Parameters();
            params.setLoopRecTime(intValue);
            this.mCamSet.setRecorderParameters(params);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        MediaRecorder.Parameters params;
        String value = null;
        if (MAX_DURATION.equals(tag) && getSupportedValue(tag) != null && Environment.isLoopRecAPISupported() && Environment.isLoopRecAPISupported() && 2 == this.mCamSet.getCurrentMode() && (params = this.mCamSet.getRecorderParameters()) != null) {
            int time = params.getLoopRecTime();
            value = pf2app.get(time);
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), "getValue:").append(tag).append(", value:").append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> value = null;
        if (MAX_DURATION.equals(tag) && Environment.isNewBizDeviceActionCam() && Environment.isLoopRecAPISupported() && 2 == this.mCamSet.getCurrentMode()) {
            value = new ArrayList<>();
            List<Integer> supporteds = this.mCamSet.getSupportedRecorderParameters().getSupportedLoopRecTimes();
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
        strBuilder.replace(0, strBuilder.length(), "getSupportedValue:").append(tag).append(", value:").append(value);
        Log.i(TAG, strBuilder.toString());
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = new ArrayList<>();
        List<String> supporteds = getSupportedValue(tag);
        if (supporteds != null) {
            for (String s : supporteds) {
                if (AvailableInfo.isAvailable(API_NAME_LOOPREC, app2pf.get(s))) {
                    availables.add(s);
                }
            }
        }
        if (availables.size() == 0) {
            availables = null;
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), "getAvailableValue:").append(tag).append(", value:").append(availables);
        Log.i(TAG, strBuilder.toString());
        return availables;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean ret = false;
        if (MAX_DURATION.equals(tag) && Environment.isNewBizDeviceActionCam() && Environment.isLoopRecAPISupported() && 2 == this.mCamSet.getCurrentMode()) {
            ret = AvailableInfo.isAvailable(API_NAME_LOOPREC, null);
        }
        StringBuilder strBuilder = sStringBuilder.get();
        strBuilder.replace(0, strBuilder.length(), "isAvailable:").append(tag).append(LOG_BOOLEAN_TAG).append(ret);
        Log.i(TAG, strBuilder.toString());
        return ret;
    }
}
