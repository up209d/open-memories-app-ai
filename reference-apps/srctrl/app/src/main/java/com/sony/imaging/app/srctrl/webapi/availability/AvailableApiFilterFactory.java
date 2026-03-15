package com.sony.imaging.app.srctrl.webapi.availability;

import android.util.Log;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class AvailableApiFilterFactory {
    private static final String tag = AvailableApiFilterFactory.class.getName();
    private static IAvailableApiFilter s_ApiFilter = null;

    /* loaded from: classes.dex */
    public interface IAvailableApiFilter {
        String[] squeezeApiList(String[] strArr);
    }

    /* loaded from: classes.dex */
    private static class DefaultAvailableApiFilter implements IAvailableApiFilter {
        private DefaultAvailableApiFilter() {
        }

        public String[] getAllApiList() {
            return Name.s_DEFAULT_API_LIST;
        }

        @Override // com.sony.imaging.app.srctrl.webapi.availability.AvailableApiFilterFactory.IAvailableApiFilter
        public String[] squeezeApiList(String[] apiList) {
            if (apiList == null || apiList.length == 0) {
                return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
            }
            String[] allApiList = getAllApiList();
            List<String> result = new ArrayList<>();
            for (String api : apiList) {
                int offset = Arrays.binarySearch(allApiList, api, Name.s_COMP);
                if (offset >= 0) {
                    result.add(api);
                }
            }
            return (String[]) result.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
        }
    }

    /* loaded from: classes.dex */
    private static class PreinstallAppAvailableApiFilter extends DefaultAvailableApiFilter {
        private PreinstallAppAvailableApiFilter() {
            super();
        }

        @Override // com.sony.imaging.app.srctrl.webapi.availability.AvailableApiFilterFactory.DefaultAvailableApiFilter
        public String[] getAllApiList() {
            return Name.s_PREINSTALL_API_LIST;
        }
    }

    public static IAvailableApiFilter createApiFilter() {
        if (s_ApiFilter != null) {
            return s_ApiFilter;
        }
        State state = StateController.getInstance().getState();
        if (state == null) {
            Log.e(tag, "Api Filter is not ready...");
            return s_ApiFilter;
        }
        boolean isSystemApp = SRCtrl.isSystemApp(state.getActivity());
        if (isSystemApp) {
            Log.v(tag, "This is a default system app using limited APIs.");
            s_ApiFilter = new PreinstallAppAvailableApiFilter();
        } else {
            Log.v(tag, "This is a downloaded app with full APIs.");
            s_ApiFilter = new DefaultAvailableApiFilter();
        }
        return s_ApiFilter;
    }
}
