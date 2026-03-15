package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceProtocol;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ServiceProtocolCustomConverter extends ServiceProtocol.Converter {
    public static final ServiceProtocolCustomConverter REF = new ServiceProtocolCustomConverter();

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceProtocol.Converter, com.sony.mexi.webapi.json.JsonConverter
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
    @Override // com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceProtocol.Converter, com.sony.mexi.webapi.json.JsonConverter
    public ServiceProtocol fromJson(JSONObject src) {
        if (src == null) {
            return null;
        }
        ServiceProtocol dst = new ServiceProtocol();
        dst.serviceName = JsonUtil.getString(src, "relativeUrl");
        dst.protocols = JsonUtil.getStringArray(src, "protocols");
        return dst;
    }

    public ServiceProtocol fromInfoJson(JSONObject src) {
        if (src == null) {
            return null;
        }
        ServiceProtocol dst = new ServiceProtocol();
        dst.serviceName = JsonUtil.getString(src, "service", null);
        dst.protocols = JsonUtil.getStringArray(src, "protocols", null);
        return dst;
    }

    public List<ServiceProtocol> fromInfoJsonArray(JSONArray src) {
        if (src == null) {
            return null;
        }
        List<ServiceProtocol> dst = new ArrayList<>();
        for (int i = 0; i < src.length(); i++) {
            ServiceProtocol val = fromInfoJson(JsonUtil.getObject(src, i));
            if (val != null) {
                dst.add(val);
            }
        }
        return dst;
    }
}
