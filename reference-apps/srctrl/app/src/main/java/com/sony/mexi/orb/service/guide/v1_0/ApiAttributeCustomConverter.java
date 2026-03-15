package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiAttribute;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ApiAttributeCustomConverter extends ApiAttribute.Converter {
    public static final ApiAttributeCustomConverter REF = new ApiAttributeCustomConverter();

    public JSONObject toJson(ApiAttribute src, String[] protocols) {
        if (src == null) {
            return null;
        }
        JSONObject dst = new JSONObject();
        JsonUtil.putRequired(dst, "version", src.version);
        if (src.protocols != null && !Arrays.equals(src.protocols, protocols)) {
            JsonUtil.put(dst, "protocols", src.protocols);
        }
        if (src.authLevel != null && !src.authLevel.equals("none")) {
            JsonUtil.put(dst, "authLevel", src.authLevel);
            return dst;
        }
        return dst;
    }

    public JSONArray toJsonArray(ApiAttribute[] src, String[] protocols) {
        if (src == null) {
            return null;
        }
        JSONArray dst = new JSONArray();
        for (ApiAttribute java : src) {
            JsonUtil.put(dst, toJson(java, protocols));
        }
        return dst;
    }
}
