package com.sony.mexi.orb.service.avcontent.v1_3;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.avcontent.v1_3.GetContentListCallback;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.Content;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.ContentListSource;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class SmartRemoteControlAvContentServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.3";

    public abstract void getContentList(ContentListSource contentListSource, GetContentListCallback getContentListCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class GetContentListCallbackImpl extends OrbAbstractClientCallbacks implements GetContentListCallback {
        OrbAbstractClient client;

        GetContentListCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_3.GetContentListCallback
        public void returnCb(Content[] contentList) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(contentList, Content.Converter.REF));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetContentList extends OrbAbstractMethod {
        private MethodGetContentList() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"uri\":\"string\", \"title\":\"string\", \"isProtected\":\"string\", \"createdTime\":\"string\", \"content\":\"ContentInfo\", \"folderNo\":\"string\", \"fileNo\":\"string\", \"contentKind\":\"string\", \"isPlayable\":\"string\", \"isBrowsable\":\"string\", \"remotePlayType\":\"string*\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"uri\":\"string\", \"stIdx\":\"int\", \"cnt\":\"int\", \"type\":\"string*\", \"target\":\"string\", \"view\":\"string\", \"sort\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                GetContentListCallbackImpl callbacks = new GetContentListCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.getContentList(ContentListSource.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public String getAccessLevel() {
            return "private";
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final void init(OrbServiceProvider service) {
        addMethod(Name.GET_CONTENT_LIST, new MethodGetContentList());
    }
}
