package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TouchAFPositionParams {
    public Boolean AFResult;
    public String AFType;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TouchAFPositionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TouchAFPositionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "AFResult", src.AFResult);
            JsonUtil.putRequired(dst, "AFType", src.AFType);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public TouchAFPositionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TouchAFPositionParams dst = new TouchAFPositionParams();
            dst.AFResult = Boolean.valueOf(JsonUtil.getBoolean(src, "AFResult"));
            dst.AFType = JsonUtil.getString(src, "AFType");
            return dst;
        }
    }
}
