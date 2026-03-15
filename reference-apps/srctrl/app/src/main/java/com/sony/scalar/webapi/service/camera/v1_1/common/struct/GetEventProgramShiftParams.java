package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventProgramShiftParams {
    public Boolean isShifted;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventProgramShiftParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventProgramShiftParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "isShifted", src.isShifted);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventProgramShiftParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventProgramShiftParams dst = new GetEventProgramShiftParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.isShifted = Boolean.valueOf(JsonUtil.getBoolean(src, "isShifted"));
            return dst;
        }
    }
}
