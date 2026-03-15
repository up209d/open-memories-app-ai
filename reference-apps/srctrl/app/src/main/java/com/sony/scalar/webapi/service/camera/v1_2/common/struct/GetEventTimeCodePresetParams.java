package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.TimeCodePresetCandidateParams;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.TimeCodePresetCurrentParams;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTimeCodePresetParams {
    public TimeCodePresetCandidateParams candidate;
    public TimeCodePresetCurrentParams timeCodePreset;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTimeCodePresetParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTimeCodePresetParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "timeCodePreset", TimeCodePresetCurrentParams.Converter.REF.toJson(src.timeCodePreset));
            JsonUtil.putRequired(dst, "candidate", TimeCodePresetCandidateParams.Converter.REF.toJson(src.candidate));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTimeCodePresetParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTimeCodePresetParams dst = new GetEventTimeCodePresetParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.timeCodePreset = TimeCodePresetCurrentParams.Converter.REF.fromJson(JsonUtil.getObject(src, "timeCodePreset"));
            dst.candidate = TimeCodePresetCandidateParams.Converter.REF.fromJson(JsonUtil.getObject(src, "candidate"));
            return dst;
        }
    }
}
