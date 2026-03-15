package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventBracketShootModeParams {
    public Boolean checkAvailability;
    public String currentBracketShootMode;
    public String currentBracketShootModeOption;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventBracketShootModeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventBracketShootModeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putOptional(dst, "checkAvailability", src.checkAvailability);
            JsonUtil.putRequired(dst, "currentBracketShootMode", src.currentBracketShootMode);
            JsonUtil.putRequired(dst, "currentBracketShootModeOption", src.currentBracketShootModeOption);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventBracketShootModeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventBracketShootModeParams dst = new GetEventBracketShootModeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.checkAvailability = Boolean.valueOf(JsonUtil.getBoolean(src, "checkAvailability", false));
            dst.currentBracketShootMode = JsonUtil.getString(src, "currentBracketShootMode");
            dst.currentBracketShootModeOption = JsonUtil.getString(src, "currentBracketShootModeOption");
            return dst;
        }
    }
}
