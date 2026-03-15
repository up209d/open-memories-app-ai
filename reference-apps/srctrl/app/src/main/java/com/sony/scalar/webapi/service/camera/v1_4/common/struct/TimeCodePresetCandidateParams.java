package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.TimeCodePresetFrameRangeDropFrameParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.TimeCodePresetHourRangeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.TimeCodePresetMinuteRangeParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.TimeCodePresetSecondRangeParams;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TimeCodePresetCandidateParams {
    public TimeCodePresetFrameRangeDropFrameParams frameRange;
    public TimeCodePresetHourRangeParams hourRange;
    public TimeCodePresetMinuteRangeParams minuteRange;
    public TimeCodePresetSecondRangeParams secondRange;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TimeCodePresetCandidateParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TimeCodePresetCandidateParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "hourRange", TimeCodePresetHourRangeParams.Converter.REF.toJson(src.hourRange));
            JsonUtil.putRequired(dst, "minuteRange", TimeCodePresetMinuteRangeParams.Converter.REF.toJson(src.minuteRange));
            JsonUtil.putRequired(dst, "secondRange", TimeCodePresetSecondRangeParams.Converter.REF.toJson(src.secondRange));
            JsonUtil.putRequired(dst, "frameRange", TimeCodePresetFrameRangeDropFrameParams.Converter.REF.toJson(src.frameRange));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public TimeCodePresetCandidateParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TimeCodePresetCandidateParams dst = new TimeCodePresetCandidateParams();
            dst.hourRange = TimeCodePresetHourRangeParams.Converter.REF.fromJson(JsonUtil.getObject(src, "hourRange"));
            dst.minuteRange = TimeCodePresetMinuteRangeParams.Converter.REF.fromJson(JsonUtil.getObject(src, "minuteRange"));
            dst.secondRange = TimeCodePresetSecondRangeParams.Converter.REF.fromJson(JsonUtil.getObject(src, "secondRange"));
            dst.frameRange = TimeCodePresetFrameRangeDropFrameParams.Converter.REF.fromJson(JsonUtil.getObject(src, "frameRange"));
            return dst;
        }
    }
}
