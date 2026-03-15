package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventAudioRecordingParams {
    public String audioRecording;
    public String[] candidate;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventAudioRecordingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventAudioRecordingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "audioRecording", src.audioRecording);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventAudioRecordingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventAudioRecordingParams dst = new GetEventAudioRecordingParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.audioRecording = JsonUtil.getString(src, "audioRecording");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
