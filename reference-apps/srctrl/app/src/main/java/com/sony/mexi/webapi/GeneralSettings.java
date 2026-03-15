package com.sony.mexi.webapi;

import com.sony.imaging.app.util.IRelativeLayoutGroup;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GeneralSettings {
    public String target;
    public String value;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GeneralSettings> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GeneralSettings src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, IRelativeLayoutGroup.NOT_EXCLUSIVE, src.target);
            JsonUtil.putRequired(dst, "value", src.value);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GeneralSettings fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GeneralSettings dst = new GeneralSettings();
            dst.target = JsonUtil.getString(src, IRelativeLayoutGroup.NOT_EXCLUSIVE);
            dst.value = JsonUtil.getString(src, "value");
            return dst;
        }
    }
}
