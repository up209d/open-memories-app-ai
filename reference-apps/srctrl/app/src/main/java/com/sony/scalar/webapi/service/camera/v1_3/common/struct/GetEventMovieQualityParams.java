package com.sony.scalar.webapi.service.camera.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GetEventMovieQualityParams {
    public String currentMovieQuality;
    public String[] movieQualityCandidates;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GetEventMovieQualityParams> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GetEventMovieQualityParams src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "type", src.type);
            JsonUtil.putRequired(dst, "currentMovieQuality", src.currentMovieQuality);
            JsonUtil.putRequired(dst, "movieQualityCandidates", src.movieQualityCandidates);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GetEventMovieQualityParams fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GetEventMovieQualityParams dst = new GetEventMovieQualityParams();
            dst.type = JsonUtil.getString(src, "type");
            dst.currentMovieQuality = JsonUtil.getString(src, "currentMovieQuality");
            dst.movieQualityCandidates = JsonUtil.getStringArray(src, "movieQualityCandidates");
            return dst;
        }
    }
}
