package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventFocusModeParams {
    public String currentFocusMode;
    public String[] focusModeCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventFocusModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventFocusModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentFocusMode", src.currentFocusMode);
            JsonUtil.putRequired(dst, "focusModeCandidates", src.focusModeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventFocusModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventFocusModeParams dst = new GetEventFocusModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentFocusMode = JsonUtil.getString(src, "currentFocusMode");
            dst.focusModeCandidates = JsonUtil.getStringArray(src, "focusModeCandidates");
            return dst;
        }
    }
}
