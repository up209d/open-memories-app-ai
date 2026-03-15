package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventBeepModeParams {
    public String[] beepModeCandidates;
    public String currentBeepMode;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventBeepModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventBeepModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentBeepMode", src.currentBeepMode);
            JsonUtil.putRequired(dst, "beepModeCandidates", src.beepModeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventBeepModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventBeepModeParams dst = new GetEventBeepModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentBeepMode = JsonUtil.getString(src, "currentBeepMode");
            dst.beepModeCandidates = JsonUtil.getStringArray(src, "beepModeCandidates");
            return dst;
        }
    }
}
