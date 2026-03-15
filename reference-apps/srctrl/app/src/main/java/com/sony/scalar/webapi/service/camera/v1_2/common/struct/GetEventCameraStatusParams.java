package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventCameraStatusParams {
    public String cameraStatus;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventCameraStatusParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventCameraStatusParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "cameraStatus", src.cameraStatus);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventCameraStatusParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventCameraStatusParams dst = new GetEventCameraStatusParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.cameraStatus = JsonUtil.getString(src, "cameraStatus");
            return dst;
        }
    }
}
