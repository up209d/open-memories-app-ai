package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventSceneSelectionParams {
    public String[] candidate;
    public String scene;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventSceneSelectionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventSceneSelectionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "scene", src.scene);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventSceneSelectionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventSceneSelectionParams dst = new GetEventSceneSelectionParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.scene = JsonUtil.getString(src, "scene");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
