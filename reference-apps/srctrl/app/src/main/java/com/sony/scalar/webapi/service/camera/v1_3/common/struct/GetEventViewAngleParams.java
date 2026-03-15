package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventViewAngleParams {
    public Integer currentViewAngle;
    public String type;
    public int[] viewAngleCandidates;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventViewAngleParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventViewAngleParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentViewAngle", src.currentViewAngle);
            JsonUtil.putRequired(dst, "viewAngleCandidates", src.viewAngleCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventViewAngleParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventViewAngleParams dst = new GetEventViewAngleParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentViewAngle = Integer.valueOf(JsonUtil.getInt(src, "currentViewAngle"));
            dst.viewAngleCandidates = JsonUtil.getIntArray(src, "viewAngleCandidates");
            return dst;
        }
    }
}
