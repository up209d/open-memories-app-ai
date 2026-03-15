package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.BulbShootingUrlParams;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventBulbShootingParams {
    public BulbShootingUrlParams[] bulbShootingUrl;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventBulbShootingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventBulbShootingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "bulbShootingUrl", JsonUtil.toJsonArray(src.bulbShootingUrl, BulbShootingUrlParams.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventBulbShootingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventBulbShootingParams dst = new GetEventBulbShootingParams();
            dst.type = JsonUtil.getString(src, "type");
            List<BulbShootingUrlParams> tmpListBulbShootingUrl = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "bulbShootingUrl"), BulbShootingUrlParams.Converter.REF);
            dst.bulbShootingUrl = tmpListBulbShootingUrl != null ? (BulbShootingUrlParams[]) tmpListBulbShootingUrl.toArray(new BulbShootingUrlParams[tmpListBulbShootingUrl.size()]) : null;
            return dst;
        }
    }
}
