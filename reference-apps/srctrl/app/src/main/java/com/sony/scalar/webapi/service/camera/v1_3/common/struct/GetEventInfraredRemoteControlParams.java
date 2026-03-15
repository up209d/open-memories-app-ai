package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventInfraredRemoteControlParams {
    public String[] candidate;
    public String infraredRemoteControl;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventInfraredRemoteControlParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventInfraredRemoteControlParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "infraredRemoteControl", src.infraredRemoteControl);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventInfraredRemoteControlParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventInfraredRemoteControlParams dst = new GetEventInfraredRemoteControlParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.infraredRemoteControl = JsonUtil.getString(src, "infraredRemoteControl");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
