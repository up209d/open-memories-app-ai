package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceInfo;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ServiceInfoCustomConverter extends ServiceInfo.Converter {
    public static final ServiceInfoCustomConverter REF = new ServiceInfoCustomConverter();

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceInfo.Converter, com.sony.mexi.webapi.json.JsonConverter
    public JSONObject toJson(ServiceInfo src) {
        if (src == null) {
            return null;
        }
        JSONObject dst = new JSONObject();
        JsonUtil.putRequired(dst, "service", src.service);
        JsonUtil.putRequired(dst, "protocols", src.protocols);
        JsonUtil.putRequired(dst, "apis", ApiInfoCustomConverter.REF.toJsonArray(src.apis, src.protocols));
        if (src.notifications != null && src.notifications.length != 0) {
            JsonUtil.putOptional(dst, "notifications", JsonUtil.toJsonArray(src.notifications, NotificationInfoCustomConverter.REF));
            return dst;
        }
        return dst;
    }
}
