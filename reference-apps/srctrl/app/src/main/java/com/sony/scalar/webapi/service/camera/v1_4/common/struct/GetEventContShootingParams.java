package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.ContShootingUrlParams;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventContShootingParams {
    public ContShootingUrlParams[] contShootingUrl;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventContShootingParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventContShootingParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "contShootingUrl", JsonUtil.toJsonArray(src.contShootingUrl, ContShootingUrlParams.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventContShootingParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventContShootingParams dst = new GetEventContShootingParams();
            dst.type = JsonUtil.getString(src, "type");
            List<ContShootingUrlParams> tmpListContShootingUrl = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "contShootingUrl"), ContShootingUrlParams.Converter.REF);
            dst.contShootingUrl = tmpListContShootingUrl != null ? (ContShootingUrlParams[]) tmpListContShootingUrl.toArray(new ContShootingUrlParams[tmpListContShootingUrl.size()]) : null;
            return dst;
        }
    }
}
