package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventSilentShootingSettingParams {
    public String[] candidate;
    public String silentShooting;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventSilentShootingSettingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventSilentShootingSettingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "silentShooting", src.silentShooting);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventSilentShootingSettingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventSilentShootingSettingParams dst = new GetEventSilentShootingSettingParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.silentShooting = JsonUtil.getString(src, "silentShooting");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
