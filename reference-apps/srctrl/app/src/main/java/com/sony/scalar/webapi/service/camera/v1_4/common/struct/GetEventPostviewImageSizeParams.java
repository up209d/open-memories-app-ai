package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventPostviewImageSizeParams {
    public String currentPostviewImageSize;
    public String[] postviewImageSizeCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventPostviewImageSizeParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventPostviewImageSizeParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentPostviewImageSize", src.currentPostviewImageSize);
            JsonUtil.putRequired(dst, "postviewImageSizeCandidates", src.postviewImageSizeCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventPostviewImageSizeParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventPostviewImageSizeParams dst = new GetEventPostviewImageSizeParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentPostviewImageSize = JsonUtil.getString(src, "currentPostviewImageSize");
            dst.postviewImageSizeCandidates = JsonUtil.getStringArray(src, "postviewImageSizeCandidates");
            return dst;
        }
    }
}
