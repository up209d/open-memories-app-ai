package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TimeCodePresetFrameRangeDropFrameParams {
    public Boolean isDropFrame;
    public Integer max;
    public Integer min;
    public Integer step;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TimeCodePresetFrameRangeDropFrameParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TimeCodePresetFrameRangeDropFrameParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "min", src.min);
            JsonUtil.putRequired(dst, "max", src.max);
            JsonUtil.putRequired(dst, "step", src.step);
            JsonUtil.putRequired(dst, "isDropFrame", src.isDropFrame);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public TimeCodePresetFrameRangeDropFrameParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TimeCodePresetFrameRangeDropFrameParams dst = new TimeCodePresetFrameRangeDropFrameParams();
            dst.min = Integer.valueOf(JsonUtil.getInt(src, "min"));
            dst.max = Integer.valueOf(JsonUtil.getInt(src, "max"));
            dst.step = Integer.valueOf(JsonUtil.getInt(src, "step"));
            dst.isDropFrame = Boolean.valueOf(JsonUtil.getBoolean(src, "isDropFrame"));
            return dst;
        }
    }
}
