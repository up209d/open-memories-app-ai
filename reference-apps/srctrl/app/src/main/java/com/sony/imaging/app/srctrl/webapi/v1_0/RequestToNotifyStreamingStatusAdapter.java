package com.sony.imaging.app.srctrl.webapi.v1_0;

import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingStatus;

/* loaded from: classes.dex */
public class RequestToNotifyStreamingStatusAdapter {
    private static final String TAG = RequestToNotifyStreamingStatusAdapter.class.getSimpleName();
    private static RequestToNotifyStreamingStatusAdapter mrequestToNotifyAdapter = null;

    private RequestToNotifyStreamingStatusAdapter() {
    }

    public static RequestToNotifyStreamingStatusAdapter getInstance() {
        if (mrequestToNotifyAdapter == null) {
            mrequestToNotifyAdapter = new RequestToNotifyStreamingStatusAdapter();
        }
        return mrequestToNotifyAdapter;
    }

    public StreamingStatus getStreamingStatus(boolean isPolling) {
        StreamingStatus params = ParamsGenerator.getStreamingStatus(isPolling);
        if (params == null) {
            return null;
        }
        StreamingStatus ret = new StreamingStatus();
        ret.status = params.status;
        ret.factor = params.factor;
        return ret;
    }
}
