package com.sony.mexi.webapi;

import com.sony.imaging.app.util.IRelativeLayoutGroup;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GeneralSettingsTarget {
    public String target;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GeneralSettingsTarget> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GeneralSettingsTarget src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putOptional(dst, IRelativeLayoutGroup.NOT_EXCLUSIVE, src.target);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GeneralSettingsTarget fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GeneralSettingsTarget dst = new GeneralSettingsTarget();
            dst.target = JsonUtil.getString(src, IRelativeLayoutGroup.NOT_EXCLUSIVE, "");
            return dst;
        }
    }
}
