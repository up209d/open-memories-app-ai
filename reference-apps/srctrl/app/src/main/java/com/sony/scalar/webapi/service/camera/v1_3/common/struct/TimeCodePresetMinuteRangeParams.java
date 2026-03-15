package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TimeCodePresetMinuteRangeParams {
    public Integer max;
    public Integer min;
    public Integer step;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TimeCodePresetMinuteRangeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TimeCodePresetMinuteRangeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "min", src.min);
            JsonUtil.putRequired(dst, "max", src.max);
            JsonUtil.putRequired(dst, "step", src.step);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public TimeCodePresetMinuteRangeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TimeCodePresetMinuteRangeParams dst = new TimeCodePresetMinuteRangeParams();
            dst.min = Integer.valueOf(JsonUtil.getInt(src, "min"));
            dst.max = Integer.valueOf(JsonUtil.getInt(src, "max"));
            dst.step = Integer.valueOf(JsonUtil.getInt(src, "step"));
            return dst;
        }
    }
}
