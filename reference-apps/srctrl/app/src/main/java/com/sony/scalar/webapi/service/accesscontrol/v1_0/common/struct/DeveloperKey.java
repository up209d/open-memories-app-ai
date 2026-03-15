package com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DeveloperKey {
    public String dg;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<DeveloperKey> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(DeveloperKey src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "dg", src.dg);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public DeveloperKey fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            DeveloperKey dst = new DeveloperKey();
            dst.dg = JsonUtil.getString(src, "dg");
            return dst;
        }
    }
}
