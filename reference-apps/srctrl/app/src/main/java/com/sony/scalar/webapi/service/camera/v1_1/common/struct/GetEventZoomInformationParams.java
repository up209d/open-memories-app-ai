package com.sony.scalar.webapi.service.camera.v1_1.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventZoomInformationParams {
    public String type;
    public Integer zoomIndexCurrentBox;
    public Integer zoomNumberBox;
    public Integer zoomPosition;
    public Integer zoomPositionCurrentBox;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventZoomInformationParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventZoomInformationParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "zoomPosition", src.zoomPosition);
            JsonUtil.putRequired(dst, "zoomNumberBox", src.zoomNumberBox);
            JsonUtil.putRequired(dst, "zoomIndexCurrentBox", src.zoomIndexCurrentBox);
            JsonUtil.putRequired(dst, "zoomPositionCurrentBox", src.zoomPositionCurrentBox);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventZoomInformationParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventZoomInformationParams dst = new GetEventZoomInformationParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.zoomPosition = Integer.valueOf(JsonUtil.getInt(src, "zoomPosition"));
            dst.zoomNumberBox = Integer.valueOf(JsonUtil.getInt(src, "zoomNumberBox"));
            dst.zoomIndexCurrentBox = Integer.valueOf(JsonUtil.getInt(src, "zoomIndexCurrentBox"));
            dst.zoomPositionCurrentBox = Integer.valueOf(JsonUtil.getInt(src, "zoomPositionCurrentBox"));
            return dst;
        }
    }
}
