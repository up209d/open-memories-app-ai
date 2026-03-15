package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContShootingSpeedSupportedParams {
    public String[] candidate;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContShootingSpeedSupportedParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContShootingSpeedSupportedParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContShootingSpeedSupportedParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContShootingSpeedSupportedParams dst = new ContShootingSpeedSupportedParams();
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
