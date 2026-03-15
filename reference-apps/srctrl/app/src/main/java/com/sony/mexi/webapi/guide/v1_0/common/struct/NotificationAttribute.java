package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationAttribute {
    public String authLevel;
    public String version;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<NotificationAttribute> {
        public static final Converter REF = new Converter();

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(NotificationAttribute src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "version", src.version);
            JsonUtil.putOptional(dst, "authLevel", src.authLevel);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public NotificationAttribute fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            NotificationAttribute dst = new NotificationAttribute();
            dst.version = JsonUtil.getString(src, "version");
            dst.authLevel = JsonUtil.getString(src, "authLevel", "none");
            return dst;
        }
    }
}
