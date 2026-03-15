package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ZoomSettingParams {
    public String zoom;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ZoomSettingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ZoomSettingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "zoom", src.zoom);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ZoomSettingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ZoomSettingParams dst = new ZoomSettingParams();
            dst.zoom = JsonUtil.getString(src, "zoom");
            return dst;
        }
    }
}
