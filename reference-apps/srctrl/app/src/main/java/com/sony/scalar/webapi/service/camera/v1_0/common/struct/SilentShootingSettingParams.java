package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SilentShootingSettingParams {
    public String silentShooting;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<SilentShootingSettingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(SilentShootingSettingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "silentShooting", src.silentShooting);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public SilentShootingSettingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            SilentShootingSettingParams dst = new SilentShootingSettingParams();
            dst.silentShooting = JsonUtil.getString(src, "silentShooting");
            return dst;
        }
    }
}
