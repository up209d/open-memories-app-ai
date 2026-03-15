package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationAttribute;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationInfo {
    public String name;
    public NotificationAttribute[] versions;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<NotificationInfo> {
        public static final Converter REF = new Converter();

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(NotificationInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "name", src.name);
            JsonUtil.putRequired(dst, "versions", JsonUtil.toJsonArray(src.versions, NotificationAttribute.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public NotificationInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            NotificationInfo dst = new NotificationInfo();
            dst.name = JsonUtil.getString(src, "name");
            List<NotificationAttribute> tmpListVersions = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "versions"), NotificationAttribute.Converter.REF);
            dst.versions = tmpListVersions != null ? (NotificationAttribute[]) tmpListVersions.toArray(new NotificationAttribute[tmpListVersions.size()]) : null;
            return dst;
        }
    }
}
