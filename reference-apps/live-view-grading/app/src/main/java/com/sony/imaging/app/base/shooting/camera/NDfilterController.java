package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class NDfilterController extends AbstractController {
    private static final String API_NAME = "setNDFilter";
    public static final String ITEM_ID_NDFILTER = "NDfilter";
    private static final String LOG_INVALID_TAG = ": Invalid tag. ";
    private static final String LOG_MSG_GETNDFILTER = "getNDFilter = ";
    private static final String LOG_MSG_GETSUPPORTEDNDFILTERS = "getSupportedNDFilters = ";
    private static final String LOG_MSG_SETNDFILTER = "setNDFilter = ";
    public static final String ND_FILTER_AUTO = "auto";
    public static final String ND_FILTER_OFF = "off";
    public static final String ND_FILTER_ON = "on";
    public static final String STATUS_ID_NDFILTER = "NDfilterStatus";
    private static final String TAG = "NDfilterController";
    private static NDfilterController mInstance;
    CameraSetting mCamSet = CameraSetting.getInstance();
    private static final String myName = NDfilterController.class.getSimpleName();
    private static List<String> mSupportedNDFilter = null;

    public static final String getName() {
        return myName;
    }

    public static NDfilterController getInstance() {
        if (mInstance == null) {
            new NDfilterController();
        }
        return mInstance;
    }

    private static void setController(NDfilterController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected NDfilterController() {
        setController(this);
    }

    private List<String> createSupportedValueArray() {
        if (6 > CameraSetting.getPfApiVersion()) {
            return null;
        }
        List<String> supParams = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedNDFilters();
        return supParams;
    }

    public List<String> getSupportedValueList() {
        if (mSupportedNDFilter == null) {
            mSupportedNDFilter = createSupportedValueArray();
        }
        List<String> supList = mSupportedNDFilter;
        return supList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParam.second).setNDFilter(value);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_SETNDFILTER).append(value);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        this.mCamSet.setParameters(emptyParam);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        String pfValue;
        if (STATUS_ID_NDFILTER.equals(tag)) {
            if (CameraSetting.mNDFilterStatus) {
                pfValue = "on";
            } else {
                pfValue = "off";
            }
        } else {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> param = this.mCamSet.getParameters();
            pfValue = ((CameraEx.ParametersModifier) param.second).getNDFilter();
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETNDFILTER).append(pfValue);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return pfValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        new ArrayList();
        List<String> supList = getSupportedValueList();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETSUPPORTEDNDFILTERS).append(supList);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return supList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        ArrayList<String> ret = new ArrayList<>();
        List<String> supList = getSupportedValueList();
        if (supList != null) {
            AvailableInfo.update();
            for (String tmp : supList) {
                if (AvailableInfo.isAvailable(API_NAME, tmp)) {
                    ret.add(tmp);
                }
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        AvailableInfo.update();
        boolean ret = AvailableInfo.isAvailable(API_NAME, null);
        return ret;
    }
}
