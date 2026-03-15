package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTriggeredErrorParams {
    public String[] triggeredError;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTriggeredErrorParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTriggeredErrorParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "triggeredError", src.triggeredError);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTriggeredErrorParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTriggeredErrorParams dst = new GetEventTriggeredErrorParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.triggeredError = JsonUtil.getStringArray(src, "triggeredError");
            return dst;
        }
    }
}
