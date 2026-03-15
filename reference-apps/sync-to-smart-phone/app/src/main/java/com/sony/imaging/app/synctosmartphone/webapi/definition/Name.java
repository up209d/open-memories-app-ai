package com.sony.imaging.app.synctosmartphone.webapi.definition;

import java.util.Arrays;
import java.util.Comparator;

/* loaded from: classes.dex */
public class Name {
    public static final String[] s_PREINSTALL_API_LIST;
    public static final Comparator<String> s_COMP = new Comparator<String>() { // from class: com.sony.imaging.app.synctosmartphone.webapi.definition.Name.1
        @Override // java.util.Comparator
        public int compare(String l, String r) {
            return l.compareTo(r);
        }
    };
    public static final String GET_VERSIONS = "getVersions";
    public static final String GET_METHOD_TYPES = "getMethodTypes";
    public static final String ACT_PAIRING = "actPairing";
    public static final String NOTIFY_SYNC_STATUS = "notifySyncStatus";
    public static final String[] s_DEFAULT_API_LIST = {GET_VERSIONS, GET_METHOD_TYPES, ACT_PAIRING, NOTIFY_SYNC_STATUS};

    static {
        Arrays.sort(s_DEFAULT_API_LIST);
        s_PREINSTALL_API_LIST = new String[]{ACT_PAIRING, NOTIFY_SYNC_STATUS};
        Arrays.sort(s_PREINSTALL_API_LIST);
    }
}
