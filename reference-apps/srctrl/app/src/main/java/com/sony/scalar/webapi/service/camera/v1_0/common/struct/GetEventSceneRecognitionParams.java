package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventSceneRecognitionParams {
    public String motionRecognition;
    public String sceneRecognition;
    public String steadyRecognition;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventSceneRecognitionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventSceneRecognitionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "sceneRecognition", src.sceneRecognition);
            JsonUtil.putRequired(dst, "steadyRecognition", src.steadyRecognition);
            JsonUtil.putRequired(dst, "motionRecognition", src.motionRecognition);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventSceneRecognitionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventSceneRecognitionParams dst = new GetEventSceneRecognitionParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.sceneRecognition = JsonUtil.getString(src, "sceneRecognition");
            dst.steadyRecognition = JsonUtil.getString(src, "steadyRecognition");
            dst.motionRecognition = JsonUtil.getString(src, "motionRecognition");
            return dst;
        }
    }
}
