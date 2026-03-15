package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventAutoPowerOffParams {
    public Integer autoPowerOff;
    public int[] candidate;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventAutoPowerOffParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventAutoPowerOffParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "autoPowerOff", src.autoPowerOff);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventAutoPowerOffParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventAutoPowerOffParams dst = new GetEventAutoPowerOffParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.autoPowerOff = Integer.valueOf(JsonUtil.getInt(src, "autoPowerOff"));
            dst.candidate = JsonUtil.getIntArray(src, "candidate");
            return dst;
        }
    }
}
