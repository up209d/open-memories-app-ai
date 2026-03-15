package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventStorageInformationParams {
    public Integer numberOfRecordableImages;
    public Boolean recordTarget;
    public Integer recordableTime;
    public String storageDescription;
    public String storageID;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventStorageInformationParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventStorageInformationParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "storageID", src.storageID);
            JsonUtil.putRequired(dst, "recordTarget", src.recordTarget);
            JsonUtil.putRequired(dst, "numberOfRecordableImages", src.numberOfRecordableImages);
            JsonUtil.putRequired(dst, "recordableTime", src.recordableTime);
            JsonUtil.putRequired(dst, "storageDescription", src.storageDescription);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventStorageInformationParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventStorageInformationParams dst = new GetEventStorageInformationParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.storageID = JsonUtil.getString(src, "storageID");
            dst.recordTarget = Boolean.valueOf(JsonUtil.getBoolean(src, "recordTarget"));
            dst.numberOfRecordableImages = Integer.valueOf(JsonUtil.getInt(src, "numberOfRecordableImages"));
            dst.recordableTime = Integer.valueOf(JsonUtil.getInt(src, "recordableTime"));
            dst.storageDescription = JsonUtil.getString(src, "storageDescription");
            return dst;
        }
    }
}
