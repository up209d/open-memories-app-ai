package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class LiveviewFrameInfoParams {
    public Boolean frameInfo;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<LiveviewFrameInfoParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(LiveviewFrameInfoParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "frameInfo", src.frameInfo);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public LiveviewFrameInfoParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            LiveviewFrameInfoParams dst = new LiveviewFrameInfoParams();
            dst.frameInfo = Boolean.valueOf(JsonUtil.getBoolean(src, "frameInfo"));
            return dst;
        }
    }
}
