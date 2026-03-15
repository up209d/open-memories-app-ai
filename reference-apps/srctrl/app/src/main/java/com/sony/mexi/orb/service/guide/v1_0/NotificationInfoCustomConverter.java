package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationInfo;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationInfoCustomConverter extends NotificationInfo.Converter {
    public static final NotificationInfoCustomConverter REF = new NotificationInfoCustomConverter();

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationInfo.Converter, com.sony.mexi.webapi.json.JsonConverter
    public JSONObject toJson(NotificationInfo src) {
        if (src == null) {
            return null;
        }
        JSONObject dst = new JSONObject();
        JsonUtil.putRequired(dst, "name", src.name);
        JsonUtil.putRequired(dst, "versions", JsonUtil.toJsonArray(src.versions, NotificationAttributeCustomConverter.REF));
        return dst;
    }
}
