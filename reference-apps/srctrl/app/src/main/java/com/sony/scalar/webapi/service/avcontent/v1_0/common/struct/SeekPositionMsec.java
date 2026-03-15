package com.sony.scalar.webapi.service.avcontent.v1_0.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SeekPositionMsec {
    public Integer positionMsec;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<SeekPositionMsec> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(SeekPositionMsec src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "positionMsec", src.positionMsec);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public SeekPositionMsec fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            SeekPositionMsec dst = new SeekPositionMsec();
            dst.positionMsec = Integer.valueOf(JsonUtil.getInt(src, "positionMsec"));
            return dst;
        }
    }
}
