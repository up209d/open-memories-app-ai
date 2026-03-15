package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventPictureEffectParams {
    public Boolean checkAvailability;
    public String currentPictureEffect;
    public String currentPictureEffectOption;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventPictureEffectParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventPictureEffectParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putOptional(dst, "checkAvailability", src.checkAvailability);
            JsonUtil.putRequired(dst, "currentPictureEffect", src.currentPictureEffect);
            JsonUtil.putRequired(dst, "currentPictureEffectOption", src.currentPictureEffectOption);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventPictureEffectParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventPictureEffectParams dst = new GetEventPictureEffectParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.checkAvailability = Boolean.valueOf(JsonUtil.getBoolean(src, "checkAvailability", false));
            dst.currentPictureEffect = JsonUtil.getString(src, "currentPictureEffect");
            dst.currentPictureEffectOption = JsonUtil.getString(src, "currentPictureEffectOption");
            return dst;
        }
    }
}
