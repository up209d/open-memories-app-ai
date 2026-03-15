package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventNumberOfShotsParams {
    public Integer numberOfShots;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventNumberOfShotsParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventNumberOfShotsParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "numberOfShots", src.numberOfShots);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventNumberOfShotsParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventNumberOfShotsParams dst = new GetEventNumberOfShotsParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.numberOfShots = Integer.valueOf(JsonUtil.getInt(src, "numberOfShots"));
            return dst;
        }
    }
}
