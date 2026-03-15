package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventZoomSettingParams {
    public String[] candidate;
    public String type;
    public String zoom;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventZoomSettingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventZoomSettingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "zoom", src.zoom);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventZoomSettingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventZoomSettingParams dst = new GetEventZoomSettingParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.zoom = JsonUtil.getString(src, "zoom");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
