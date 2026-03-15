package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContShootingUrlParams {
    public String postviewUrl;
    public String thumbnailUrl;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContShootingUrlParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContShootingUrlParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "thumbnailUrl", src.thumbnailUrl);
            JsonUtil.putRequired(dst, "postviewUrl", src.postviewUrl);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContShootingUrlParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContShootingUrlParams dst = new ContShootingUrlParams();
            dst.thumbnailUrl = JsonUtil.getString(src, "thumbnailUrl");
            dst.postviewUrl = JsonUtil.getString(src, "postviewUrl");
            return dst;
        }
    }
}
