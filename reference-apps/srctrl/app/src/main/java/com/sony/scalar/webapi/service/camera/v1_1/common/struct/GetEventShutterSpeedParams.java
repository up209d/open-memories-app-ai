package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventShutterSpeedParams {
    public String currentShutterSpeed;
    public String[] shutterSpeedCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventShutterSpeedParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventShutterSpeedParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentShutterSpeed", src.currentShutterSpeed);
            JsonUtil.putRequired(dst, "shutterSpeedCandidates", src.shutterSpeedCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventShutterSpeedParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventShutterSpeedParams dst = new GetEventShutterSpeedParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentShutterSpeed = JsonUtil.getString(src, "currentShutterSpeed");
            dst.shutterSpeedCandidates = JsonUtil.getStringArray(src, "shutterSpeedCandidates");
            return dst;
        }
    }
}
