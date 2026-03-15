package com.sony.scalar.webapi.service.avcontent.v1_3.common.struct;

import com.sony.imaging.app.util.IRelativeLayoutGroup;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ContentListSource {
    public Integer cnt;
    public String sort;
    public Integer stIdx;
    public String target;
    public String[] type;
    public String uri;
    public String view;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ContentListSource> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ContentListSource src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "uri", src.uri);
            JsonUtil.putOptional(dst, "stIdx", src.stIdx);
            JsonUtil.putOptional(dst, "cnt", src.cnt);
            JsonUtil.putOptional(dst, "type", src.type);
            JsonUtil.putOptional(dst, IRelativeLayoutGroup.NOT_EXCLUSIVE, src.target);
            JsonUtil.putOptional(dst, "view", src.view);
            JsonUtil.putOptional(dst, "sort", src.sort);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ContentListSource fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ContentListSource dst = new ContentListSource();
            dst.uri = JsonUtil.getString(src, "uri");
            dst.stIdx = Integer.valueOf(JsonUtil.getInt(src, "stIdx", 0));
            dst.cnt = Integer.valueOf(JsonUtil.getInt(src, "cnt", 50));
            dst.type = JsonUtil.getStringArray(src, "type", null);
            dst.target = JsonUtil.getString(src, IRelativeLayoutGroup.NOT_EXCLUSIVE, "");
            dst.view = JsonUtil.getString(src, "view", "");
            dst.sort = JsonUtil.getString(src, "sort", "");
            return dst;
        }
    }
}
