package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TouchAFCurrentPositionParams {
    public Boolean set;
    public double[] touchCoordinates;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<TouchAFCurrentPositionParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(TouchAFCurrentPositionParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, Name.PREFIX_SET, src.set);
            JsonUtil.putRequired(dst, "touchCoordinates", src.touchCoordinates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public TouchAFCurrentPositionParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            TouchAFCurrentPositionParams dst = new TouchAFCurrentPositionParams();
            dst.set = Boolean.valueOf(JsonUtil.getBoolean(src, Name.PREFIX_SET));
            dst.touchCoordinates = JsonUtil.getDoubleArray(src, "touchCoordinates");
            return dst;
        }
    }
}
