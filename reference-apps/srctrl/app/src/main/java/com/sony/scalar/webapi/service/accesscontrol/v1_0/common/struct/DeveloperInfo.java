package com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DeveloperInfo {
    public String developerID;
    public String developerName;
    public String methods;
    public String sg;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<DeveloperInfo> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(DeveloperInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "developerName", src.developerName);
            JsonUtil.putRequired(dst, "developerID", src.developerID);
            JsonUtil.putRequired(dst, "sg", src.sg);
            JsonUtil.putRequired(dst, "methods", src.methods);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public DeveloperInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            DeveloperInfo dst = new DeveloperInfo();
            dst.developerName = JsonUtil.getString(src, "developerName");
            dst.developerID = JsonUtil.getString(src, "developerID");
            dst.sg = JsonUtil.getString(src, "sg");
            dst.methods = JsonUtil.getString(src, "methods");
            return dst;
        }
    }
}
