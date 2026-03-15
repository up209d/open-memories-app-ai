package com.sony.scalar.webapi.service.avcontent.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.Original;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentInfo {
    public String largeUrl;
    public Original[] original;
    public String smallUrl;
    public String thumbnailUrl;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentInfo> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putOptional(dst, "original", JsonUtil.toJsonArray(src.original, Original.Converter.REF));
            JsonUtil.putOptional(dst, "thumbnailUrl", src.thumbnailUrl);
            JsonUtil.putOptional(dst, "largeUrl", src.largeUrl);
            JsonUtil.putOptional(dst, "smallUrl", src.smallUrl);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentInfo dst = new ContentInfo();
            List<Original> tmpListOriginal = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "original", null), Original.Converter.REF);
            dst.original = tmpListOriginal != null ? (Original[]) tmpListOriginal.toArray(new Original[tmpListOriginal.size()]) : null;
            dst.thumbnailUrl = JsonUtil.getString(src, "thumbnailUrl", "");
            dst.largeUrl = JsonUtil.getString(src, "largeUrl", "");
            dst.smallUrl = JsonUtil.getString(src, "smallUrl", "");
            return dst;
        }
    }
}
