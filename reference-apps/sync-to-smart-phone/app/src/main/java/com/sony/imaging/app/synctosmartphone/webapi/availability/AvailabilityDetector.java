package com.sony.imaging.app.synctosmartphone.webapi.availability;

import com.sony.imaging.app.synctosmartphone.webapi.definition.Name;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class AvailabilityDetector {
    private static final String[] PUBLIC_API = {"guide/getServiceProtocols", "guide/getVersions", "guide/getMethodTypes", "contentSync/getVersions", "contentSync/getMethodTypes", "accessControl/getVersions", "accessControl/getMethodTypes", "accessControl/actEnableMethods"};

    static {
        Arrays.sort(PUBLIC_API);
    }

    public static ArrayList<String> getPrivateApiList() {
        ArrayList<String> privateApiList = new ArrayList<>();
        String[] arr$ = Name.s_DEFAULT_API_LIST;
        for (String api : arr$) {
            String apiValue = "contentSync/" + api;
            int result = Arrays.binarySearch(PUBLIC_API, apiValue, Name.s_COMP);
            if (result < 0) {
                privateApiList.add(apiValue);
            }
        }
        return privateApiList;
    }
}
