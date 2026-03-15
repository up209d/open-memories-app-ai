package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTimeCodeMakeModeParams {
    public String[] candidate;
    public String timeCodeMakeMode;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTimeCodeMakeModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTimeCodeMakeModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "timeCodeMakeMode", src.timeCodeMakeMode);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTimeCodeMakeModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTimeCodeMakeModeParams dst = new GetEventTimeCodeMakeModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.timeCodeMakeMode = JsonUtil.getString(src, "timeCodeMakeMode");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
