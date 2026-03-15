package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventFocusStatusParams {
    public String focusStatus;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventFocusStatusParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventFocusStatusParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "focusStatus", src.focusStatus);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventFocusStatusParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventFocusStatusParams dst = new GetEventFocusStatusParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.focusStatus = JsonUtil.getString(src, "focusStatus");
            return dst;
        }
    }
}
