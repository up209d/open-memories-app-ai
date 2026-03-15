package com.sony.mexi.webapi;

import com.sony.mexi.webapi.ApiIdentity;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationStatusResponse {
    public ApiIdentity[] disabled;
    public ApiIdentity[] enabled;
    public ApiIdentity[] rejected;
    public ApiIdentity[] unsupported;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<NotificationStatusResponse> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(NotificationStatusResponse src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "enabled", JsonUtil.toJsonArray(src.enabled, ApiIdentity.Converter.REF));
            JsonUtil.putRequired(dst, "disabled", JsonUtil.toJsonArray(src.disabled, ApiIdentity.Converter.REF));
            JsonUtil.putOptional(dst, "rejected", JsonUtil.toJsonArray(src.rejected, ApiIdentity.Converter.REF));
            JsonUtil.putOptional(dst, "unsupported", JsonUtil.toJsonArray(src.unsupported, ApiIdentity.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public NotificationStatusResponse fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            NotificationStatusResponse dst = new NotificationStatusResponse();
            List<ApiIdentity> tmpListEnabled = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "enabled"), ApiIdentity.Converter.REF);
            dst.enabled = tmpListEnabled == null ? null : (ApiIdentity[]) tmpListEnabled.toArray(new ApiIdentity[tmpListEnabled.size()]);
            List<ApiIdentity> tmpListDisabled = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "disabled"), ApiIdentity.Converter.REF);
            dst.disabled = tmpListDisabled == null ? null : (ApiIdentity[]) tmpListDisabled.toArray(new ApiIdentity[tmpListDisabled.size()]);
            List<ApiIdentity> tmpListRejected = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "rejected", null), ApiIdentity.Converter.REF);
            dst.rejected = tmpListRejected == null ? null : (ApiIdentity[]) tmpListRejected.toArray(new ApiIdentity[tmpListRejected.size()]);
            List<ApiIdentity> tmpListUnsupported = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "unsupported", null), ApiIdentity.Converter.REF);
            dst.unsupported = tmpListUnsupported != null ? (ApiIdentity[]) tmpListUnsupported.toArray(new ApiIdentity[tmpListUnsupported.size()]) : null;
            return dst;
        }
    }
}
