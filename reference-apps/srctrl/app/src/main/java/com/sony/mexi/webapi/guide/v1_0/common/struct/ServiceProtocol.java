package com.sony.mexi.webapi.guide.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ServiceProtocol {
    public String[] protocols;
    public String serviceName;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<ServiceProtocol> {
        public static final Converter REF = new Converter();

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(ServiceProtocol src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "serviceName", src.serviceName);
            JsonUtil.putRequired(dst, "protocols", src.protocols);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public ServiceProtocol fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            ServiceProtocol dst = new ServiceProtocol();
            dst.serviceName = JsonUtil.getString(src, "serviceName");
            dst.protocols = JsonUtil.getStringArray(src, "protocols");
            return dst;
        }
    }
}
