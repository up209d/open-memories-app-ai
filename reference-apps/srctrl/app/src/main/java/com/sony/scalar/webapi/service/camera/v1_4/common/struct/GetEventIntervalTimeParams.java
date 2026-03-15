package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventIntervalTimeParams {
    public String[] candidate;
    public String intervalTimeSec;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventIntervalTimeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventIntervalTimeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "intervalTimeSec", src.intervalTimeSec);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventIntervalTimeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventIntervalTimeParams dst = new GetEventIntervalTimeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.intervalTimeSec = JsonUtil.getString(src, "intervalTimeSec");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
