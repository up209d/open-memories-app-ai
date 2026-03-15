package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventCameraFunctionParams {
    public String[] cameraFunctionCandidates;
    public String currentCameraFunction;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventCameraFunctionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventCameraFunctionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentCameraFunction", src.currentCameraFunction);
            JsonUtil.putRequired(dst, "cameraFunctionCandidates", src.cameraFunctionCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventCameraFunctionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventCameraFunctionParams dst = new GetEventCameraFunctionParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentCameraFunction = JsonUtil.getString(src, "currentCameraFunction");
            dst.cameraFunctionCandidates = JsonUtil.getStringArray(src, "cameraFunctionCandidates");
            return dst;
        }
    }
}
