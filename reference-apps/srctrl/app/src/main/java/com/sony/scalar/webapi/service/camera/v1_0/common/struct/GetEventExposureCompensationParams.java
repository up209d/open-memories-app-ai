package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventExposureCompensationParams {
    public Integer currentExposureCompensation;
    public Integer maxExposureCompensation;
    public Integer minExposureCompensation;
    public Integer stepIndexOfExposureCompensation;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventExposureCompensationParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventExposureCompensationParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentExposureCompensation", src.currentExposureCompensation);
            JsonUtil.putRequired(dst, "maxExposureCompensation", src.maxExposureCompensation);
            JsonUtil.putRequired(dst, "minExposureCompensation", src.minExposureCompensation);
            JsonUtil.putRequired(dst, "stepIndexOfExposureCompensation", src.stepIndexOfExposureCompensation);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventExposureCompensationParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventExposureCompensationParams dst = new GetEventExposureCompensationParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentExposureCompensation = Integer.valueOf(JsonUtil.getInt(src, "currentExposureCompensation"));
            dst.maxExposureCompensation = Integer.valueOf(JsonUtil.getInt(src, "maxExposureCompensation"));
            dst.minExposureCompensation = Integer.valueOf(JsonUtil.getInt(src, "minExposureCompensation"));
            dst.stepIndexOfExposureCompensation = Integer.valueOf(JsonUtil.getInt(src, "stepIndexOfExposureCompensation"));
            return dst;
        }
    }
}
