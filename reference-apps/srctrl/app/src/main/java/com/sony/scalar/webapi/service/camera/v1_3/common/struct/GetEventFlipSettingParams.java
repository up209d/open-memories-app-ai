package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventFlipSettingParams {
    public String[] candidate;
    public String flip;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventFlipSettingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventFlipSettingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "flip", src.flip);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventFlipSettingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventFlipSettingParams dst = new GetEventFlipSettingParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.flip = JsonUtil.getString(src, "flip");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
