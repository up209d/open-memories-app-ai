package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventUserBitTimeRecParams {
    public String[] candidate;
    public String type;
    public String userBitTimeRec;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventUserBitTimeRecParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventUserBitTimeRecParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "userBitTimeRec", src.userBitTimeRec);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventUserBitTimeRecParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventUserBitTimeRecParams dst = new GetEventUserBitTimeRecParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.userBitTimeRec = JsonUtil.getString(src, "userBitTimeRec");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
