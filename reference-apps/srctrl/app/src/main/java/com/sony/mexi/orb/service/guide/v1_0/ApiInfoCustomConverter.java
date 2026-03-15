package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfo;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ApiInfoCustomConverter extends ApiInfo.Converter {
    public static final ApiInfoCustomConverter REF = new ApiInfoCustomConverter();

    public JSONObject toJson(ApiInfo src, String[] protocols) {
        if (src == null) {
            return null;
        }
        JSONObject dst = new JSONObject();
        JsonUtil.putRequired(dst, "name", src.name);
        JsonUtil.putRequired(dst, "versions", ApiAttributeCustomConverter.REF.toJsonArray(src.versions, protocols));
        return dst;
    }

    public JSONArray toJsonArray(ApiInfo[] src, String[] protocols) {
        if (src == null) {
            return null;
        }
        JSONArray dst = new JSONArray();
        for (ApiInfo java : src) {
            JsonUtil.put(dst, toJson(java, protocols));
        }
        return dst;
    }
}
