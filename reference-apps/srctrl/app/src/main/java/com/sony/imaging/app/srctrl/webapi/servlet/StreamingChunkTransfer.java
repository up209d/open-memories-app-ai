package com.sony.imaging.app.srctrl.webapi.servlet;

import android.util.Log;
import com.sony.imaging.app.srctrl.streaming.StreamingLoader;
import com.sony.mexi.orb.service.http.GenericHttpRequestHandler;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class StreamingChunkTransfer extends GenericHttpRequestHandler {
    private static final String TAG = StreamingChunkTransfer.class.getSimpleName();
    private static final long serialVersionUID = 1;
    private volatile long latestSendingTime = -1;
    private StreamingChunkTransfer sendingMutex;

    public StreamingChunkTransfer() {
        this.sendingMutex = null;
        this.sendingMutex = this;
    }

    public void notifyGetScalarInfraIsKilled() {
        Log.v(TAG, "GetScalarInfra is killed.");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public void handleGet(HttpRequest req, HttpResponse res) throws IOException {
        Log.v(TAG, "Accepted HTTP GET for Playback Streaming");
        try {
            OutputStream stream_ = res.getOutputStream();
            StreamingLoader.setStreamingChunkTransferInstance(this.sendingMutex);
            while (true) {
                try {
                    StreamingLoader.PlayInfoData playInfoData = StreamingLoader.getPlayInfoData();
                    if (playInfoData != null) {
                        try {
                            this.latestSendingTime = System.currentTimeMillis();
                            stream_.write(playInfoData.headerData, 0, playInfoData.headerDataSize);
                            stream_.write(playInfoData.infoData, 0, playInfoData.infoDataSize);
                            long blockingTime = System.currentTimeMillis() - this.latestSendingTime;
                            if (blockingTime > 90) {
                                Log.v(TAG, "PlayInfoData sending was blocked " + blockingTime + " [msec]");
                            }
                            this.latestSendingTime = -1L;
                        } catch (IOException e) {
                            Log.e(TAG, "PlayInfoData Write Error. : " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                    }
                    try {
                        StreamingLoader.StreamData streamData = StreamingLoader.getStreamData();
                        while (streamData == null) {
                            try {
                                Thread.sleep(30L);
                            } catch (InterruptedException e2) {
                                Log.e(TAG, "sleep() InterruptedException.");
                            }
                            streamData = StreamingLoader.getStreamData();
                        }
                        try {
                            this.latestSendingTime = System.currentTimeMillis();
                            stream_.write(streamData.headerData, 0, streamData.headerDataSize);
                            stream_.write(streamData.jpegData, 0, streamData.jpegDataAndPaddingSize);
                            long blockingTime2 = System.currentTimeMillis() - this.latestSendingTime;
                            if (blockingTime2 > 90) {
                                Log.v(TAG, "Jpeg sending was blocked " + blockingTime2 + " [msec]");
                            }
                            this.latestSendingTime = -1L;
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            return;
                        }
                    } catch (IllegalStateException e4) {
                        Log.e(TAG, "Get StreamingData Error. : " + e4.getMessage());
                    }
                } catch (IllegalStateException e5) {
                    Log.e(TAG, "Get PlayInfoData Error. : " + e5.getMessage());
                }
            }
        } finally {
            res.end();
            StreamingLoader.setStreamingChunkTransferInstance(null);
            Log.e(TAG, "sender finish");
        }
    }
}
