package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TimeCodePresetCurrentParams {
    public Integer frame;
    public Integer hour;
    public Integer minute;
    public Integer second;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TimeCodePresetCurrentParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TimeCodePresetCurrentParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "hour", src.hour);
            JsonUtil.putRequired(dst, "minute", src.minute);
            JsonUtil.putRequired(dst, "second", src.second);
            JsonUtil.putRequired(dst, "frame", src.frame);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public TimeCodePresetCurrentParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TimeCodePresetCurrentParams dst = new TimeCodePresetCurrentParams();
            dst.hour = Integer.valueOf(JsonUtil.getInt(src, "hour"));
            dst.minute = Integer.valueOf(JsonUtil.getInt(src, "minute"));
            dst.second = Integer.valueOf(JsonUtil.getInt(src, "second"));
            dst.frame = Integer.valueOf(JsonUtil.getInt(src, "frame"));
            return dst;
        }
    }
}
