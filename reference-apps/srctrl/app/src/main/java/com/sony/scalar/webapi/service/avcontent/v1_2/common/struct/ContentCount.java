package com.sony.scalar.webapi.service.avcontent.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentCount {
    public Integer count;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentCount> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentCount src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "count", src.count);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentCount fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentCount dst = new ContentCount();
            dst.count = Integer.valueOf(JsonUtil.getInt(src, "count"));
            return dst;
        }
    }
}
