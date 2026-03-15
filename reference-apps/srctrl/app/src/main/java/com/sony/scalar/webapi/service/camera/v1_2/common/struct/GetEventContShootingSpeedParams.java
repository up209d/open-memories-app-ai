package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventContShootingSpeedParams {
    public String[] candidate;
    public String contShootingSpeed;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventContShootingSpeedParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventContShootingSpeedParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "contShootingSpeed", src.contShootingSpeed);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventContShootingSpeedParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventContShootingSpeedParams dst = new GetEventContShootingSpeedParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.contShootingSpeed = JsonUtil.getString(src, "contShootingSpeed");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
