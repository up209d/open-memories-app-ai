package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContShootingModeParams {
    public String contShootingMode;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContShootingModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContShootingModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "contShootingMode", src.contShootingMode);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContShootingModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContShootingModeParams dst = new ContShootingModeParams();
            dst.contShootingMode = JsonUtil.getString(src, "contShootingMode");
            return dst;
        }
    }
}
