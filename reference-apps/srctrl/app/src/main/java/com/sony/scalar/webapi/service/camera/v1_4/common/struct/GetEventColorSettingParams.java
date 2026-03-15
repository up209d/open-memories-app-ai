package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventColorSettingParams {
    public String[] candidate;
    public String colorSetting;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventColorSettingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventColorSettingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "colorSetting", src.colorSetting);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventColorSettingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventColorSettingParams dst = new GetEventColorSettingParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.colorSetting = JsonUtil.getString(src, "colorSetting");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
