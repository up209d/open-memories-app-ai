package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventLoopRecTimeParams {
    public String[] candidate;
    public String loopRecTime;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventLoopRecTimeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventLoopRecTimeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "loopRecTime", src.loopRecTime);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventLoopRecTimeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventLoopRecTimeParams dst = new GetEventLoopRecTimeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.loopRecTime = JsonUtil.getString(src, "loopRecTime");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
