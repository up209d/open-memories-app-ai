package com.sony.scalar.webapi.service.camera.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventAELockParams {
    public boolean[] aeLockCandidates;
    public Boolean currentAELock;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventAELockParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventAELockParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putOptional(dst, "currentAELock", src.currentAELock);
            JsonUtil.putRequired(dst, "aeLockCandidates", src.aeLockCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventAELockParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventAELockParams dst = new GetEventAELockParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentAELock = Boolean.valueOf(JsonUtil.getBoolean(src, "currentAELock", false));
            dst.aeLockCandidates = JsonUtil.getBooleanArray(src, "aeLockCandidates");
            return dst;
        }
    }
}
