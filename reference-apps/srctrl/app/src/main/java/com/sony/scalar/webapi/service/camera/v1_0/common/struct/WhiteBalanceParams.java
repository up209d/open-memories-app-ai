package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class WhiteBalanceParams {
    public Integer colorTemperature;
    public String whiteBalanceMode;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<WhiteBalanceParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(WhiteBalanceParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "whiteBalanceMode", src.whiteBalanceMode);
            JsonUtil.putRequired(dst, "colorTemperature", src.colorTemperature);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public WhiteBalanceParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            WhiteBalanceParams dst = new WhiteBalanceParams();
            dst.whiteBalanceMode = JsonUtil.getString(src, "whiteBalanceMode");
            dst.colorTemperature = Integer.valueOf(JsonUtil.getInt(src, "colorTemperature"));
            return dst;
        }
    }
}
