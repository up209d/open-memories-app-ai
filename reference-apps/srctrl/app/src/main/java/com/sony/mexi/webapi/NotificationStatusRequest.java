package com.sony.mexi.webapi;

import com.sony.mexi.webapi.ApiIdentity;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationStatusRequest {
    public ApiIdentity[] disabled;
    public ApiIdentity[] enabled;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<NotificationStatusRequest> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(NotificationStatusRequest src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putOptional(dst, "enabled", JsonUtil.toJsonArray(src.enabled, ApiIdentity.Converter.REF));
            JsonUtil.putOptional(dst, "disabled", JsonUtil.toJsonArray(src.disabled, ApiIdentity.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public NotificationStatusRequest fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            NotificationStatusRequest dst = new NotificationStatusRequest();
            List<ApiIdentity> tmpListEnabled = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "enabled", null), ApiIdentity.Converter.REF);
            dst.enabled = tmpListEnabled == null ? null : (ApiIdentity[]) tmpListEnabled.toArray(new ApiIdentity[tmpListEnabled.size()]);
            List<ApiIdentity> tmpListDisabled = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "disabled", null), ApiIdentity.Converter.REF);
            dst.disabled = tmpListDisabled != null ? (ApiIdentity[]) tmpListDisabled.toArray(new ApiIdentity[tmpListDisabled.size()]) : null;
            return dst;
        }
    }
}
