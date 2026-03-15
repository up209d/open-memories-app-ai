package com.sony.mexi.webapi;

import com.sony.mexi.webapi.GeneralSettings;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GeneralSettingsValue {
    public GeneralSettings[] settings;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GeneralSettingsValue> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GeneralSettingsValue src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "settings", JsonUtil.toJsonArray(src.settings, GeneralSettings.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GeneralSettingsValue fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GeneralSettingsValue dst = new GeneralSettingsValue();
            List<GeneralSettings> tmpListSettings = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "settings"), GeneralSettings.Converter.REF);
            dst.settings = tmpListSettings != null ? (GeneralSettings[]) tmpListSettings.toArray(new GeneralSettings[tmpListSettings.size()]) : null;
            return dst;
        }
    }
}
