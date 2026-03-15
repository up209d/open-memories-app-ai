package com.sony.scalar.webapi.service.avcontent.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Original {
    public String fileName;
    public String stillObject;
    public String url;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<Original> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(Original src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putOptional(dst, "url", src.url);
            JsonUtil.putOptional(dst, "fileName", src.fileName);
            JsonUtil.putOptional(dst, "stillObject", src.stillObject);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public Original fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            Original dst = new Original();
            dst.url = JsonUtil.getString(src, "url", "");
            dst.fileName = JsonUtil.getString(src, "fileName", "");
            dst.stillObject = JsonUtil.getString(src, "stillObject", "");
            return dst;
        }
    }
}
