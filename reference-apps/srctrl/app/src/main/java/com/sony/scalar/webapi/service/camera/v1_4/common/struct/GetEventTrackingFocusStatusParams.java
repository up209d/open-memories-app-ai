package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTrackingFocusStatusParams {
    public String trackingFocusStatus;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTrackingFocusStatusParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTrackingFocusStatusParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "trackingFocusStatus", src.trackingFocusStatus);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTrackingFocusStatusParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTrackingFocusStatusParams dst = new GetEventTrackingFocusStatusParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.trackingFocusStatus = JsonUtil.getString(src, "trackingFocusStatus");
            return dst;
        }
    }
}
