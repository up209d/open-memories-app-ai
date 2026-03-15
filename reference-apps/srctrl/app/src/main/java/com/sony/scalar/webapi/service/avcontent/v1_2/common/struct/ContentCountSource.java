package com.sony.scalar.webapi.service.avcontent.v1_2.common.struct;

import com.sony.imaging.app.util.IRelativeLayoutGroup;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentCountSource {
    public String target;
    public String[] type;
    public String uri;
    public String view;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentCountSource> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentCountSource src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "uri", src.uri);
            JsonUtil.putOptional(dst, "type", src.type);
            JsonUtil.putOptional(dst, IRelativeLayoutGroup.NOT_EXCLUSIVE, src.target);
            JsonUtil.putOptional(dst, "view", src.view);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentCountSource fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentCountSource dst = new ContentCountSource();
            dst.uri = JsonUtil.getString(src, "uri");
            dst.type = JsonUtil.getStringArray(src, "type", null);
            dst.target = JsonUtil.getString(src, IRelativeLayoutGroup.NOT_EXCLUSIVE, "");
            dst.view = JsonUtil.getString(src, "view", "");
            return dst;
        }
    }
}
