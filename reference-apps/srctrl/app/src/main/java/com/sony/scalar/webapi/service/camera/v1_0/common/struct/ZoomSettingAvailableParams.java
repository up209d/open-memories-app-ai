package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ZoomSettingAvailableParams {
    public String[] candidate;
    public String zoom;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ZoomSettingAvailableParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ZoomSettingAvailableParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "zoom", src.zoom);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ZoomSettingAvailableParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ZoomSettingAvailableParams dst = new ZoomSettingAvailableParams();
            dst.zoom = JsonUtil.getString(src, "zoom");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
