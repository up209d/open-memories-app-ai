package com.sony.scalar.webapi.service.camera.v1_4.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventMovieFileFormatParams {
    public String[] candidate;
    public String movieFileFormat;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventMovieFileFormatParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventMovieFileFormatParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "movieFileFormat", src.movieFileFormat);
            JsonUtil.putRequired(dst, "candidate", src.candidate);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventMovieFileFormatParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventMovieFileFormatParams dst = new GetEventMovieFileFormatParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.movieFileFormat = JsonUtil.getString(src, "movieFileFormat");
            dst.candidate = JsonUtil.getStringArray(src, "candidate");
            return dst;
        }
    }
}
