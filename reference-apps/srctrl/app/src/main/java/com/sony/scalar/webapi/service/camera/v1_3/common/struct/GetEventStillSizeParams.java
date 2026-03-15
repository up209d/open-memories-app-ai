package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventStillSizeParams {
    public Boolean checkAvailability;
    public String currentAspect;
    public String currentSize;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventStillSizeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventStillSizeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "checkAvailability", src.checkAvailability);
            JsonUtil.putRequired(dst, "currentAspect", src.currentAspect);
            JsonUtil.putRequired(dst, "currentSize", src.currentSize);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventStillSizeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventStillSizeParams dst = new GetEventStillSizeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.checkAvailability = Boolean.valueOf(JsonUtil.getBoolean(src, "checkAvailability"));
            dst.currentAspect = JsonUtil.getString(src, "currentAspect");
            dst.currentSize = JsonUtil.getString(src, "currentSize");
            return dst;
        }
    }
}
