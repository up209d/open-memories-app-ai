package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SilentShootingSettingAvailableParams {
    public String[] candidate;
    public String silentShooting;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<SilentShootingSettingAvailableParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(SilentShootingSettingAvailableParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "silentShooting", src.silentShooting);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public SilentShootingSettingAvailableParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            SilentShootingSettingAvailableParams dst = new SilentShootingSettingAvailableParams();
            dst.silentShooting = JsonUtil.getString(src, "silentShooting");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
