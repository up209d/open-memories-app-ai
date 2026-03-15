package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class StreamingURL {
    public String playbackUrl;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<StreamingURL> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(StreamingURL src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "playbackUrl", src.playbackUrl);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public StreamingURL fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            StreamingURL dst = new StreamingURL();
            dst.playbackUrl = JsonUtil.getString(src, "playbackUrl");
            return dst;
        }
    }
}
