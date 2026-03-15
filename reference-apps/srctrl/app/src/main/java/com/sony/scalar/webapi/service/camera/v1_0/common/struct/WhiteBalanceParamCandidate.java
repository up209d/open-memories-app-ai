package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class WhiteBalanceParamCandidate {
    public int[] colorTemperatureRange;
    public String whiteBalanceMode;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<WhiteBalanceParamCandidate> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(WhiteBalanceParamCandidate src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "whiteBalanceMode", src.whiteBalanceMode);
            JsonUtil.putRequired(dst, "colorTemperatureRange", src.colorTemperatureRange);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public WhiteBalanceParamCandidate fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            WhiteBalanceParamCandidate dst = new WhiteBalanceParamCandidate();
            dst.whiteBalanceMode = JsonUtil.getString(src, "whiteBalanceMode");
            dst.colorTemperatureRange = JsonUtil.getIntArray(src, "colorTemperatureRange");
            return dst;
        }
    }
}
