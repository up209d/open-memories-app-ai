package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventExposureModeParams {
    public String currentExposureMode;
    public String[] exposureModeCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventExposureModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventExposureModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentExposureMode", src.currentExposureMode);
            JsonUtil.putRequired(dst, "exposureModeCandidates", src.exposureModeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventExposureModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventExposureModeParams dst = new GetEventExposureModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentExposureMode = JsonUtil.getString(src, "currentExposureMode");
            dst.exposureModeCandidates = JsonUtil.getStringArray(src, "exposureModeCandidates");
            return dst;
        }
    }
}
