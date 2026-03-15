package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTrackingFocusParams {
    public String[] candidate;
    public String trackingFocus;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTrackingFocusParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTrackingFocusParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "trackingFocus", src.trackingFocus);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTrackingFocusParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTrackingFocusParams dst = new GetEventTrackingFocusParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.trackingFocus = JsonUtil.getString(src, "trackingFocus");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
