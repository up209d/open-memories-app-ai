package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventRecordingTimeParams {
    public Integer recordingTime;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventRecordingTimeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventRecordingTimeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "recordingTime", src.recordingTime);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventRecordingTimeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventRecordingTimeParams dst = new GetEventRecordingTimeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.recordingTime = Integer.valueOf(JsonUtil.getInt(src, "recordingTime"));
            return dst;
        }
    }
}
