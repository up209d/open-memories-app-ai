package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventSelfTimerParams {
    public Integer currentSelfTimer;
    public int[] selfTimerCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventSelfTimerParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventSelfTimerParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentSelfTimer", src.currentSelfTimer);
            JsonUtil.putRequired(dst, "selfTimerCandidates", src.selfTimerCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventSelfTimerParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventSelfTimerParams dst = new GetEventSelfTimerParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentSelfTimer = Integer.valueOf(JsonUtil.getInt(src, "currentSelfTimer"));
            dst.selfTimerCandidates = JsonUtil.getIntArray(src, "selfTimerCandidates");
            return dst;
        }
    }
}
