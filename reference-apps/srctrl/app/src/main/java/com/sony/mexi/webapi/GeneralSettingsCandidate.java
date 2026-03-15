package com.sony.mexi.webapi;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GeneralSettingsCandidate {
    public Boolean isAvailable;
    public Double max;
    public Double min;
    public Double step;
    public String title;
    public String titleTextID;
    public String value;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GeneralSettingsCandidate> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GeneralSettingsCandidate src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putOptional(dst, "title", src.title);
            JsonUtil.putOptional(dst, "titleTextID", src.titleTextID);
            JsonUtil.putOptional(dst, "value", src.value);
            JsonUtil.putOptional(dst, "isAvailable", src.isAvailable);
            JsonUtil.putOptional(dst, "max", src.max);
            JsonUtil.putOptional(dst, "min", src.min);
            JsonUtil.putOptional(dst, "step", src.step);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GeneralSettingsCandidate fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GeneralSettingsCandidate dst = new GeneralSettingsCandidate();
            dst.title = JsonUtil.getString(src, "title", "");
            dst.titleTextID = JsonUtil.getString(src, "titleTextID", "");
            dst.value = JsonUtil.getString(src, "value", "");
            dst.isAvailable = Boolean.valueOf(JsonUtil.getBoolean(src, "isAvailable", true));
            dst.max = Double.valueOf(JsonUtil.getDouble(src, "max", -1.0d));
            dst.min = Double.valueOf(JsonUtil.getDouble(src, "min", -1.0d));
            dst.step = Double.valueOf(JsonUtil.getDouble(src, "step", -1.0d));
            return dst;
        }
    }
}
