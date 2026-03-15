package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventLiveviewOrientationParams {
    public String liveviewOrientation;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventLiveviewOrientationParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventLiveviewOrientationParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "liveviewOrientation", src.liveviewOrientation);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventLiveviewOrientationParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventLiveviewOrientationParams dst = new GetEventLiveviewOrientationParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.liveviewOrientation = JsonUtil.getString(src, "liveviewOrientation");
            return dst;
        }
    }
}
