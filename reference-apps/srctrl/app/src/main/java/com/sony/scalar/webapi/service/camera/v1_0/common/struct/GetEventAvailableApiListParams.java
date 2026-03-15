package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventAvailableApiListParams {
    public String[] names;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventAvailableApiListParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventAvailableApiListParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "names", src.names);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventAvailableApiListParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventAvailableApiListParams dst = new GetEventAvailableApiListParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.names = JsonUtil.getStringArray(src, "names");
            return dst;
        }
    }
}
