package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiAttribute;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ApiInfo {
    public String name;
    public ApiAttribute[] versions;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ApiInfo> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ApiInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "name", src.name);
            JsonUtil.putRequired(dst, "versions", JsonUtil.toJsonArray(src.versions, ApiAttribute.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ApiInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ApiInfo dst = new ApiInfo();
            dst.name = JsonUtil.getString(src, "name");
            List<ApiAttribute> tmpListVersions = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "versions"), ApiAttribute.Converter.REF);
            dst.versions = tmpListVersions != null ? (ApiAttribute[]) tmpListVersions.toArray(new ApiAttribute[tmpListVersions.size()]) : null;
            return dst;
        }
    }
}
