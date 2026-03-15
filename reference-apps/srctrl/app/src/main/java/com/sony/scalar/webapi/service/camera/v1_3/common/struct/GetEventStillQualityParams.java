package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventStillQualityParams {
    public String[] candidate;
    public String stillQuality;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventStillQualityParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventStillQualityParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "stillQuality", src.stillQuality);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventStillQualityParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventStillQualityParams dst = new GetEventStillQualityParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.stillQuality = JsonUtil.getString(src, "stillQuality");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
