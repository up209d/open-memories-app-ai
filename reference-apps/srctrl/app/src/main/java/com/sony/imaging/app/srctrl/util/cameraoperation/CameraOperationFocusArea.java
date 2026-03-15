package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationFocusArea {
    public static final String FOCUS_AREA_FLEX = "flex-spot";
    private static final String TAG = CameraOperationFocusArea.class.getSimpleName();

    public static String getBaseIdStr(String param) {
        if ("flex-spot".equals(param)) {
            return "flex-spot";
        }
        Log.e(TAG, "Unknown focus area parameter name: " + param);
        return null;
    }

    public static String getIdFromBase(String baseId) {
        if ("flex-spot".equals(baseId)) {
            return "flex-spot";
        }
        Log.e(TAG, "Unknown BaseApp focus area parameter name: " + baseId);
        return null;
    }

    public static String[] getIdFromBase(List<String> baseIdList) {
        if (baseIdList == null) {
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        ArrayList<String> ret = new ArrayList<>();
        for (String focusAreaInBase : baseIdList) {
            String mode = getIdFromBase(focusAreaInBase);
            if (mode != null) {
                ret.add(mode);
            }
        }
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }
}
