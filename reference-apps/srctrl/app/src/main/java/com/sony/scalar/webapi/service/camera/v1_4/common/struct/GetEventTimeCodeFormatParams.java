package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTimeCodeFormatParams {
    public String[] candidate;
    public String timeCodeFormat;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTimeCodeFormatParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTimeCodeFormatParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "timeCodeFormat", src.timeCodeFormat);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTimeCodeFormatParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTimeCodeFormatParams dst = new GetEventTimeCodeFormatParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.timeCodeFormat = JsonUtil.getString(src, "timeCodeFormat");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
