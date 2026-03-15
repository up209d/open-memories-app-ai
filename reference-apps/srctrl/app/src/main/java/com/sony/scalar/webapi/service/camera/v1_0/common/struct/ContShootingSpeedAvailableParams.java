package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContShootingSpeedAvailableParams {
    public String[] candidate;
    public String contShootingSpeed;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContShootingSpeedAvailableParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContShootingSpeedAvailableParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "contShootingSpeed", src.contShootingSpeed);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContShootingSpeedAvailableParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContShootingSpeedAvailableParams dst = new ContShootingSpeedAvailableParams();
            dst.contShootingSpeed = JsonUtil.getString(src, "contShootingSpeed");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
