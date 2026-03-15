package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventFNumberParams {
    public String currentFNumber;
    public String[] fNumberCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventFNumberParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventFNumberParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentFNumber", src.currentFNumber);
            JsonUtil.putRequired(dst, "fNumberCandidates", src.fNumberCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventFNumberParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventFNumberParams dst = new GetEventFNumberParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentFNumber = JsonUtil.getString(src, "currentFNumber");
            dst.fNumberCandidates = JsonUtil.getStringArray(src, "fNumberCandidates");
            return dst;
        }
    }
}
