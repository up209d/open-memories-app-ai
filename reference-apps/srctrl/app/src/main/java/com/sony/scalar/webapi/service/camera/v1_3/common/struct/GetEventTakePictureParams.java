package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTakePictureParams {
    public String[] takePictureUrl;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTakePictureParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTakePictureParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "takePictureUrl", src.takePictureUrl);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTakePictureParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTakePictureParams dst = new GetEventTakePictureParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.takePictureUrl = JsonUtil.getStringArray(src, "takePictureUrl");
            return dst;
        }
    }
}
