package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class StreamingStatus {
    public String factor;
    public String status;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<StreamingStatus> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(StreamingStatus src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "status", src.status);
            JsonUtil.putOptional(dst, "factor", src.factor);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public StreamingStatus fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            StreamingStatus dst = new StreamingStatus();
            dst.status = JsonUtil.getString(src, "status");
            dst.factor = JsonUtil.getString(src, "factor", null);
            return dst;
        }
    }
}
