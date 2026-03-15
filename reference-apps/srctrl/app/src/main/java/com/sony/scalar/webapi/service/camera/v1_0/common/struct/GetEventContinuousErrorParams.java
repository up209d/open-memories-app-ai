package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventContinuousErrorParams {
    public String continuousError;
    public Boolean isContinued;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventContinuousErrorParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventContinuousErrorParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "continuousError", src.continuousError);
            JsonUtil.putRequired(dst, "isContinued", src.isContinued);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventContinuousErrorParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventContinuousErrorParams dst = new GetEventContinuousErrorParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.continuousError = JsonUtil.getString(src, "continuousError");
            dst.isContinued = Boolean.valueOf(JsonUtil.getBoolean(src, "isContinued"));
            return dst;
        }
    }
}
