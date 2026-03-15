package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventUserBitPresetParams {
    public String type;
    public String userBitPreset;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventUserBitPresetParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventUserBitPresetParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "userBitPreset", src.userBitPreset);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventUserBitPresetParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventUserBitPresetParams dst = new GetEventUserBitPresetParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.userBitPreset = JsonUtil.getString(src, "userBitPreset");
            return dst;
        }
    }
}
