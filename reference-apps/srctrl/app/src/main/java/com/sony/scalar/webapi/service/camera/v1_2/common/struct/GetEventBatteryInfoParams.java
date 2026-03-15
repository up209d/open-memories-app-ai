package com.sony.scalar.webapi.service.camera.v1_2.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_2.common.struct.BatteryInfoParams;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventBatteryInfoParams {
    public BatteryInfoParams[] batteryInfo;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventBatteryInfoParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventBatteryInfoParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "batteryInfo", JsonUtil.toJsonArray(src.batteryInfo, BatteryInfoParams.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventBatteryInfoParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventBatteryInfoParams dst = new GetEventBatteryInfoParams();
            dst.type = JsonUtil.getString(src, "type");
            List<BatteryInfoParams> tmpListBatteryInfo = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "batteryInfo"), BatteryInfoParams.Converter.REF);
            dst.batteryInfo = tmpListBatteryInfo != null ? (BatteryInfoParams[]) tmpListBatteryInfo.toArray(new BatteryInfoParams[tmpListBatteryInfo.size()]) : null;
            return dst;
        }
    }
}
