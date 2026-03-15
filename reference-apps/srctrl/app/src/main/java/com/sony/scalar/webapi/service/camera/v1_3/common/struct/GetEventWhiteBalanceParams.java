package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventWhiteBalanceParams {
    public Boolean checkAvailability;
    public Integer currentColorTemperature;
    public String currentWhiteBalanceMode;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventWhiteBalanceParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventWhiteBalanceParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "checkAvailability", src.checkAvailability);
            JsonUtil.putRequired(dst, "currentWhiteBalanceMode", src.currentWhiteBalanceMode);
            JsonUtil.putRequired(dst, "currentColorTemperature", src.currentColorTemperature);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventWhiteBalanceParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventWhiteBalanceParams dst = new GetEventWhiteBalanceParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.checkAvailability = Boolean.valueOf(JsonUtil.getBoolean(src, "checkAvailability"));
            dst.currentWhiteBalanceMode = JsonUtil.getString(src, "currentWhiteBalanceMode");
            dst.currentColorTemperature = Integer.valueOf(JsonUtil.getInt(src, "currentColorTemperature"));
            return dst;
        }
    }
}
