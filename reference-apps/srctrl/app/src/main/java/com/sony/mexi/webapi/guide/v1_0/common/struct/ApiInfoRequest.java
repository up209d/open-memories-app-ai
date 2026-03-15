package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ApiInfoRequest {
    public String[] services;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ApiInfoRequest> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ApiInfoRequest src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putOptional(dst, "services", src.services);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ApiInfoRequest fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ApiInfoRequest dst = new ApiInfoRequest();
            dst.services = JsonUtil.getStringArray(src, "services", null);
            return dst;
        }
    }
}
