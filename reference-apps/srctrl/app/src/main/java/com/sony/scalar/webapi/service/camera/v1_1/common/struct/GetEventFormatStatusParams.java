package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventFormatStatusParams {
    public String formatResult;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventFormatStatusParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventFormatStatusParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "formatResult", src.formatResult);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventFormatStatusParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventFormatStatusParams dst = new GetEventFormatStatusParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.formatResult = JsonUtil.getString(src, "formatResult");
            return dst;
        }
    }
}
