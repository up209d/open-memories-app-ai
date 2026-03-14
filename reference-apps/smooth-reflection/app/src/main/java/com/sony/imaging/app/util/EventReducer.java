package com.sony.imaging.app.util;

import android.os.Handler;
import android.os.Message;

/* loaded from: classes.dex */
public class EventReducer {
    private int count = 0;
    private Handler handler = new Handler() { // from class: com.sony.imaging.app.util.EventReducer.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (EventReducer.access$006(EventReducer.this) == 0) {
                EventReducer.this.run.run();
            }
        }
    };
    private Runnable run;

    static /* synthetic */ int access$006(EventReducer x0) {
        int i = x0.count - 1;
        x0.count = i;
        return i;
    }

    public EventReducer(Runnable run) {
        this.run = run;
    }

    public void push() {
        this.handler.sendEmptyMessage(0);
        this.count++;
    }

    public void clear() {
        this.handler.removeMessages(0);
        this.count = 0;
    }
}
