package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationInfo;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ServiceInfo {
    public ApiInfo[] apis;
    public NotificationInfo[] notifications;
    public String[] protocols;
    public String service;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ServiceInfo> {
        public static final Converter REF = new Converter();

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ServiceInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "service", src.service);
            JsonUtil.putRequired(dst, "protocols", src.protocols);
            JsonUtil.putRequired(dst, "apis", JsonUtil.toJsonArray(src.apis, ApiInfo.Converter.REF));
            JsonUtil.putOptional(dst, "notifications", JsonUtil.toJsonArray(src.notifications, NotificationInfo.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ServiceInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ServiceInfo dst = new ServiceInfo();
            dst.service = JsonUtil.getString(src, "service");
            dst.protocols = JsonUtil.getStringArray(src, "protocols");
            List<ApiInfo> tmpListApis = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "apis"), ApiInfo.Converter.REF);
            dst.apis = tmpListApis == null ? null : (ApiInfo[]) tmpListApis.toArray(new ApiInfo[tmpListApis.size()]);
            List<NotificationInfo> tmpListNotifications = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "notifications", null), NotificationInfo.Converter.REF);
            dst.notifications = tmpListNotifications != null ? (NotificationInfo[]) tmpListNotifications.toArray(new NotificationInfo[tmpListNotifications.size()]) : null;
            return dst;
        }
    }
}
