package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventIsoSpeedRateParams {
    public String currentIsoSpeedRate;
    public String[] isoSpeedRateCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventIsoSpeedRateParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventIsoSpeedRateParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentIsoSpeedRate", src.currentIsoSpeedRate);
            JsonUtil.putRequired(dst, "isoSpeedRateCandidates", src.isoSpeedRateCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventIsoSpeedRateParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventIsoSpeedRateParams dst = new GetEventIsoSpeedRateParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentIsoSpeedRate = JsonUtil.getString(src, "currentIsoSpeedRate");
            dst.isoSpeedRateCandidates = JsonUtil.getStringArray(src, "isoSpeedRateCandidates");
            return dst;
        }
    }
}
