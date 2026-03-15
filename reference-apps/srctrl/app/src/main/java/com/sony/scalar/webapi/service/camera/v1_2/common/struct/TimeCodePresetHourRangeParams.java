package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TimeCodePresetHourRangeParams {
    public Integer max;
    public Integer min;
    public Integer step;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TimeCodePresetHourRangeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TimeCodePresetHourRangeParams src) {
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
        public TimeCodePresetHourRangeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TimeCodePresetHourRangeParams dst = new TimeCodePresetHourRangeParams();
            dst.min = Integer.valueOf(JsonUtil.getInt(src, "min"));
            dst.max = Integer.valueOf(JsonUtil.getInt(src, "max"));
            dst.step = Integer.valueOf(JsonUtil.getInt(src, "step"));
            return dst;
        }
    }
}
