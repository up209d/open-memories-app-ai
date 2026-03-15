package com.sony.mexi.webapi;

import com.sony.mexi.webapi.json.JsonConverter;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NullParams {

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<NullParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(NullParams src) {
            if (src == null) {
                return null;
            }
            return new JSONObject();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public NullParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            return new NullParams();
        }
    }
}
