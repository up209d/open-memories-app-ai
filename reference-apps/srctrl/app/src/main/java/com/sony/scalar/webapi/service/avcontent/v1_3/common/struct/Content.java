package com.sony.scalar.webapi.service.avcontent.v1_3.common.struct;

import com.sony.mexi.webapi.json.JsonConverter;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.ContentInfo;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Content {
    public ContentInfo content;
    public String contentKind;
    public String createdTime;
    public String fileNo;
    public String folderNo;
    public String isBrowsable;
    public String isPlayable;
    public String isProtected;
    public String[] remotePlayType;
    public String title;
    public String uri;

    /* loaded from: classes.dex */
    public static class Converter implements JsonConverter<Content> {
        public static final Converter REF = new Converter();

        @Override // com.sony.mexi.webapi.json.JsonConverter
        public JSONObject toJson(Content src) {
            if (src == null) {
                return null;
            }
            JSONObject dst = new JSONObject();
            JsonUtil.putRequired(dst, "uri", src.uri);
            JsonUtil.putOptional(dst, "title", src.title);
            JsonUtil.putOptional(dst, "isProtected", src.isProtected);
            JsonUtil.putOptional(dst, "createdTime", src.createdTime);
            JsonUtil.putOptional(dst, "content", ContentInfo.Converter.REF.toJson(src.content));
            JsonUtil.putOptional(dst, "folderNo", src.folderNo);
            JsonUtil.putOptional(dst, "fileNo", src.fileNo);
            JsonUtil.putOptional(dst, "contentKind", src.contentKind);
            JsonUtil.putOptional(dst, "isPlayable", src.isPlayable);
            JsonUtil.putOptional(dst, "isBrowsable", src.isBrowsable);
            JsonUtil.putOptional(dst, "remotePlayType", src.remotePlayType);
            return dst;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.sony.mexi.webapi.json.JsonConverter
        public Content fromJson(JSONObject src) {
            if (src == null) {
                return null;
            }
            Content dst = new Content();
            dst.uri = JsonUtil.getString(src, "uri");
            dst.title = JsonUtil.getString(src, "title", "");
            dst.isProtected = JsonUtil.getString(src, "isProtected", "");
            dst.createdTime = JsonUtil.getString(src, "createdTime", "");
            dst.content = ContentInfo.Converter.REF.fromJson(JsonUtil.getObject(src, "content", null));
            dst.folderNo = JsonUtil.getString(src, "folderNo", "");
            dst.fileNo = JsonUtil.getString(src, "fileNo", "");
            dst.contentKind = JsonUtil.getString(src, "contentKind", "");
            dst.isPlayable = JsonUtil.getString(src, "isPlayable", "");
            dst.isBrowsable = JsonUtil.getString(src, "isBrowsable", "");
            dst.remotePlayType = JsonUtil.getStringArray(src, "remotePlayType", null);
            return dst;
        }
    }
}
