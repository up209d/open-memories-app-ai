package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventSteadyModeParams {
    public String currentSteadyMode;
    public String[] steadyModeCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventSteadyModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventSteadyModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentSteadyMode", src.currentSteadyMode);
            JsonUtil.putRequired(dst, "steadyModeCandidates", src.steadyModeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventSteadyModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventSteadyModeParams dst = new GetEventSteadyModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentSteadyMode = JsonUtil.getString(src, "currentSteadyMode");
            dst.steadyModeCandidates = JsonUtil.getStringArray(src, "steadyModeCandidates");
            return dst;
        }
    }
}
