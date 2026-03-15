package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Polling {
    public Boolean polling;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<Polling> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(Polling src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "polling", src.polling);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public Polling fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            Polling dst = new Polling();
            dst.polling = Boolean.valueOf(JsonUtil.getBoolean(src, "polling"));
            return dst;
        }
    }
}
