package com.sony.mexi.webapi;

import com.sony.imaging.app.util.IRelativeLayoutGroup;
import com.sony.mexi.webapi.GeneralSettingsCandidate;
import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GeneralSettingsInfo {
    public GeneralSettingsCandidate[] candidate;
    public String currentValue;
    public String deviceUIInfo;
    public Boolean isAvailable;
    public String target;
    public String title;
    public String titleTextID;
    public String type;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<GeneralSettingsInfo> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(GeneralSettingsInfo src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, IRelativeLayoutGroup.NOT_EXCLUSIVE, src.target);
            JsonUtil.putRequired(dst, "currentValue", src.currentValue);
            JsonUtil.putOptional(dst, "deviceUIInfo", src.deviceUIInfo);
            JsonUtil.putOptional(dst, "title", src.title);
            JsonUtil.putOptional(dst, "titleTextID", src.titleTextID);
            JsonUtil.putOptional(dst, "type", src.type);
            JsonUtil.putOptional(dst, "isAvailable", src.isAvailable);
            JsonUtil.putOptional(dst, "candidate", JsonUtil.toJsonArray(src.candidate, GeneralSettingsCandidate.Converter.REF));
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public GeneralSettingsInfo fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            GeneralSettingsInfo dst = new GeneralSettingsInfo();
            dst.target = JsonUtil.getString(src, IRelativeLayoutGroup.NOT_EXCLUSIVE);
            dst.currentValue = JsonUtil.getString(src, "currentValue");
            dst.deviceUIInfo = JsonUtil.getString(src, "deviceUIInfo", "");
            dst.title = JsonUtil.getString(src, "title", "");
            dst.titleTextID = JsonUtil.getString(src, "titleTextID", "");
            dst.type = JsonUtil.getString(src, "type", "");
            dst.isAvailable = Boolean.valueOf(JsonUtil.getBoolean(src, "isAvailable", true));
            List<GeneralSettingsCandidate> tmpListCandidate = JsonUtil.fromJsonArray(JsonUtil.getArray(src, "candidate", null), GeneralSettingsCandidate.Converter.REF);
            dst.candidate = tmpListCandidate != null ? (GeneralSettingsCandidate[]) tmpListCandidate.toArray(new GeneralSettingsCandidate[tmpListCandidate.size()]) : null;
            return dst;
        }
    }
}
