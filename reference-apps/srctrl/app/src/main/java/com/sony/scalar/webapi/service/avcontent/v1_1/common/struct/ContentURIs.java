package com.sony.scalar.webapi.service.avcontent.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentURIs {
    public String[] uri;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentURIs> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentURIs src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "uri", src.uri);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentURIs fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentURIs dst = new ContentURIs();
            dst.uri = JsonUtil.getStringArray(src, "uri");
            return dst;
        }
    }
}
