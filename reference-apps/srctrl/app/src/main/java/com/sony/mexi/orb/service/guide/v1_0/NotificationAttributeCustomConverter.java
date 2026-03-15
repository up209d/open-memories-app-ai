package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationAttribute;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationAttributeCustomConverter extends NotificationAttribute.Converter {
    public static final NotificationAttributeCustomConverter REF = new NotificationAttributeCustomConverter();

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationAttribute.Converter, com.sony.mexi.webapi.json.JsonConverter
    public JSONObject toJson(NotificationAttribute src) {
        if (src == null) {
            return null;
        }
        JSONObject dst = new JSONObject();
        JsonUtil.putRequired(dst, "version", src.version);
        if (src.authLevel != null && !src.authLevel.equals("none")) {
            JsonUtil.put(dst, "authLevel", src.authLevel);
            return dst;
        }
        return dst;
    }
}
