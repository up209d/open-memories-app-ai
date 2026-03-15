package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventShootModeParams {
    public String currentShootMode;
    public String[] shootModeCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventShootModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventShootModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentShootMode", src.currentShootMode);
            JsonUtil.putRequired(dst, "shootModeCandidates", src.shootModeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventShootModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventShootModeParams dst = new GetEventShootModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentShootMode = JsonUtil.getString(src, "currentShootMode");
            dst.shootModeCandidates = JsonUtil.getStringArray(src, "shootModeCandidates");
            return dst;
        }
    }
}
