package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventCreativeStyleParams {
    public Boolean checkAvailability;
    public String currentCreativeStyle;
    public Integer currentCreativeStyleContrast;
    public Integer currentCreativeStyleSaturation;
    public Integer currentCreativeStyleSharpness;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventCreativeStyleParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventCreativeStyleParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putOptional(dst, "checkAvailability", src.checkAvailability);
            JsonUtil.putRequired(dst, "currentCreativeStyle", src.currentCreativeStyle);
            JsonUtil.putOptional(dst, "currentCreativeStyleContrast", src.currentCreativeStyleContrast);
            JsonUtil.putOptional(dst, "currentCreativeStyleSaturation", src.currentCreativeStyleSaturation);
            JsonUtil.putOptional(dst, "currentCreativeStyleSharpness", src.currentCreativeStyleSharpness);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventCreativeStyleParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventCreativeStyleParams dst = new GetEventCreativeStyleParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.checkAvailability = Boolean.valueOf(JsonUtil.getBoolean(src, "checkAvailability", false));
            dst.currentCreativeStyle = JsonUtil.getString(src, "currentCreativeStyle");
            dst.currentCreativeStyleContrast = Integer.valueOf(JsonUtil.getInt(src, "currentCreativeStyleContrast", 0));
            dst.currentCreativeStyleSaturation = Integer.valueOf(JsonUtil.getInt(src, "currentCreativeStyleSaturation", 0));
            dst.currentCreativeStyleSharpness = Integer.valueOf(JsonUtil.getInt(src, "currentCreativeStyleSharpness", 0));
            return dst;
        }
    }
}
