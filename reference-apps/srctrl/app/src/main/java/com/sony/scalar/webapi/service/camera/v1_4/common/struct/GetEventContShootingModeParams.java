package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventContShootingModeParams {
    public String[] candidate;
    public String contShootingMode;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventContShootingModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventContShootingModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "contShootingMode", src.contShootingMode);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventContShootingModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventContShootingModeParams dst = new GetEventContShootingModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.contShootingMode = JsonUtil.getString(src, "contShootingMode");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
