package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventCameraFunctionResultParams {
    public String cameraFunctionResult;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventCameraFunctionResultParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventCameraFunctionResultParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "cameraFunctionResult", src.cameraFunctionResult);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventCameraFunctionResultParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventCameraFunctionResultParams dst = new GetEventCameraFunctionResultParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.cameraFunctionResult = JsonUtil.getString(src, "cameraFunctionResult");
            return dst;
        }
    }
}
