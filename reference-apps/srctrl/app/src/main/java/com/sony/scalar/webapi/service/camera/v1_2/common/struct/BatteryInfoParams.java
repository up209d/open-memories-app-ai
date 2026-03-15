package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class BatteryInfoParams {
    public String additionalStatus;
    public String batteryID;
    public String description;
    public Integer levelDenom;
    public Integer levelNumer;
    public String status;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<BatteryInfoParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(BatteryInfoParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "batteryID", src.batteryID);
            JsonUtil.putRequired(dst, "status", src.status);
            JsonUtil.putRequired(dst, "additionalStatus", src.additionalStatus);
            JsonUtil.putRequired(dst, "levelNumer", src.levelNumer);
            JsonUtil.putRequired(dst, "levelDenom", src.levelDenom);
            JsonUtil.putRequired(dst, "description", src.description);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public BatteryInfoParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            BatteryInfoParams dst = new BatteryInfoParams();
            dst.batteryID = JsonUtil.getString(src, "batteryID");
            dst.status = JsonUtil.getString(src, "status");
            dst.additionalStatus = JsonUtil.getString(src, "additionalStatus");
            dst.levelNumer = Integer.valueOf(JsonUtil.getInt(src, "levelNumer"));
            dst.levelDenom = Integer.valueOf(JsonUtil.getInt(src, "levelDenom"));
            dst.description = JsonUtil.getString(src, "description");
            return dst;
        }
    }
}
