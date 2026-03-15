package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventLiveviewStatusParams {
    public Boolean liveviewStatus;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventLiveviewStatusParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventLiveviewStatusParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "liveviewStatus", src.liveviewStatus);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventLiveviewStatusParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventLiveviewStatusParams dst = new GetEventLiveviewStatusParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.liveviewStatus = Boolean.valueOf(JsonUtil.getBoolean(src, "liveviewStatus"));
            return dst;
        }
    }
}
