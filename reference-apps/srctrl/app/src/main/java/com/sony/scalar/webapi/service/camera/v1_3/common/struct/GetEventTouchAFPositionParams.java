package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventTouchAFPositionParams {
    public Boolean currentSet;
    public double[] currentTouchCoordinates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventTouchAFPositionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventTouchAFPositionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentSet", src.currentSet);
            JsonUtil.putRequired(dst, "currentTouchCoordinates", src.currentTouchCoordinates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventTouchAFPositionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventTouchAFPositionParams dst = new GetEventTouchAFPositionParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentSet = Boolean.valueOf(JsonUtil.getBoolean(src, "currentSet"));
            dst.currentTouchCoordinates = JsonUtil.getDoubleArray(src, "currentTouchCoordinates");
            return dst;
        }
    }
}
