package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ApiAttribute {
    public String authLevel;
    public String[] protocols;
    public String version;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ApiAttribute> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ApiAttribute src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "version", src.version);
            JsonUtil.putOptional(dst, "protocols", src.protocols);
            JsonUtil.putOptional(dst, "authLevel", src.authLevel);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ApiAttribute fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ApiAttribute dst = new ApiAttribute();
            dst.version = JsonUtil.getString(src, "version");
            dst.protocols = JsonUtil.getStringArray(src, "protocols", null);
            dst.authLevel = JsonUtil.getString(src, "authLevel", "none");
            return dst;
        }
    }
}
