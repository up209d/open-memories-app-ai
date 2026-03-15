package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventBulbCapturingTimeParams {
    public Integer bulbCapturingTime;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventBulbCapturingTimeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventBulbCapturingTimeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "bulbCapturingTime", src.bulbCapturingTime);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventBulbCapturingTimeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventBulbCapturingTimeParams dst = new GetEventBulbCapturingTimeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.bulbCapturingTime = Integer.valueOf(JsonUtil.getInt(src, "bulbCapturingTime"));
            return dst;
        }
    }
}
