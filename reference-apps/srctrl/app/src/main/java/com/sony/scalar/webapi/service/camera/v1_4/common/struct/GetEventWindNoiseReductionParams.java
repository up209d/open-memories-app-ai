package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventWindNoiseReductionParams {
    public String[] candidate;
    public String type;
    public String windNoiseReduction;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventWindNoiseReductionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventWindNoiseReductionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "windNoiseReduction", src.windNoiseReduction);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventWindNoiseReductionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventWindNoiseReductionParams dst = new GetEventWindNoiseReductionParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.windNoiseReduction = JsonUtil.getString(src, "windNoiseReduction");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
