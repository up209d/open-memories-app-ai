package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class StreamingContentInfo {
    public String remotePlayType;
    public String uri;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<StreamingContentInfo> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(StreamingContentInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "uri", src.uri);
            JsonUtil.putOptional(dst, "remotePlayType", src.remotePlayType);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public StreamingContentInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            StreamingContentInfo dst = new StreamingContentInfo();
            dst.uri = JsonUtil.getString(src, "uri");
            dst.remotePlayType = JsonUtil.getString(src, "remotePlayType", null);
            return dst;
        }
    }
}
