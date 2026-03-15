package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventFlashModeParams {
    public String currentFlashMode;
    public String[] flashModeCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventFlashModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventFlashModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentFlashMode", src.currentFlashMode);
            JsonUtil.putRequired(dst, "flashModeCandidates", src.flashModeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventFlashModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventFlashModeParams dst = new GetEventFlashModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentFlashMode = JsonUtil.getString(src, "currentFlashMode");
            dst.flashModeCandidates = JsonUtil.getStringArray(src, "flashModeCandidates");
            return dst;
        }
    }
}
