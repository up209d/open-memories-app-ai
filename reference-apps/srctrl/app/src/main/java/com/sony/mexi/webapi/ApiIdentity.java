package com.sony.mexi.webapi;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ApiIdentity {
    public String name;
    public String version;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ApiIdentity> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ApiIdentity src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "name", src.name);
            JsonUtil.putRequired(dst, "version", src.version);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ApiIdentity fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ApiIdentity dst = new ApiIdentity();
            dst.name = JsonUtil.getString(src, "name");
            dst.version = JsonUtil.getString(src, "version");
            return dst;
        }
    }
}
