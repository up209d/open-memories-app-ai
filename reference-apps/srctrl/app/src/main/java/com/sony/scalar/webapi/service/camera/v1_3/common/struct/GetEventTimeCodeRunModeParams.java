package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTimeCodeRunModeParams {
    public String[] candidate;
    public String timeCodeRunMode;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTimeCodeRunModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTimeCodeRunModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "timeCodeRunMode", src.timeCodeRunMode);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTimeCodeRunModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTimeCodeRunModeParams dst = new GetEventTimeCodeRunModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.timeCodeRunMode = JsonUtil.getString(src, "timeCodeRunMode");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
