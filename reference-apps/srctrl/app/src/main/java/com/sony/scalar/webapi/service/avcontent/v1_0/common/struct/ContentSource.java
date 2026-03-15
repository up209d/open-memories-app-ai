package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentSource {
    public String source;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentSource> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentSource src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "source", src.source);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentSource fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentSource dst = new ContentSource();
            dst.source = JsonUtil.getString(src, "source");
            return dst;
        }
    }
}
