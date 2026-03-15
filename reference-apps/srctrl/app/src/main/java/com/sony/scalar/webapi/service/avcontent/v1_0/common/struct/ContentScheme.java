package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentScheme {
    public String scheme;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentScheme> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentScheme src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "scheme", src.scheme);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentScheme fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentScheme dst = new ContentScheme();
            dst.scheme = JsonUtil.getString(src, "scheme");
            return dst;
        }
    }
}
