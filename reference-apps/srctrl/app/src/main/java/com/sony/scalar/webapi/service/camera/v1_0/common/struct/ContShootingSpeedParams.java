package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContShootingSpeedParams {
    public String contShootingSpeed;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContShootingSpeedParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContShootingSpeedParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "contShootingSpeed", src.contShootingSpeed);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContShootingSpeedParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContShootingSpeedParams dst = new ContShootingSpeedParams();
            dst.contShootingSpeed = JsonUtil.getString(src, "contShootingSpeed");
            return dst;
        }
    }
}
