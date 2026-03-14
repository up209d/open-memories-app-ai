package com.sony.imaging.app.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class Oscillator {
    public static final int HZ_0_8 = 8;
    public static final int HZ_3_2 = 32;
    private static final int PERIOD = 156;
    private static final int PHASE = 4;
    private static Oscillator own = new Oscillator();
    private Handler mHandler = new Period();
    private int count = 0;
    private boolean highlowFor32 = true;
    private boolean highlowFor08 = true;
    private ArrayList<OnPeriodListener> for32Hz = new ArrayList<>();
    private ArrayList<OnPeriodListener> for08Hz = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface OnPeriodListener {
        void onPeriod(int i, boolean z);
    }

    /* loaded from: classes.dex */
    public static class Handle {
        private int Hz;
        private OnPeriodListener listener;

        public void detach() {
            Oscillator.detach(this.Hz, this.listener);
        }

        private Handle(int Hz, OnPeriodListener listener) {
            this.Hz = Hz;
            this.listener = listener;
        }
    }

    public static Handle attach(int Hz, OnPeriodListener listener) {
        ArrayList<OnPeriodListener> target = own.returnTarget(Hz);
        if (target.contains(listener)) {
            return null;
        }
        target.add(listener);
        own.run();
        Handle ret = new Handle(Hz, listener);
        return ret;
    }

    public static boolean detach(Handle handle) {
        return detach(handle.Hz, handle.listener);
    }

    public static boolean detach(int Hz, OnPeriodListener listener) {
        ArrayList<OnPeriodListener> target = own.returnTarget(Hz);
        switch (Hz) {
            case 8:
                target = own.for08Hz;
                break;
            case 32:
                target = own.for32Hz;
                break;
            default:
                Log.w(own.getClass().getSimpleName(), "This class supports only 3.2Hz and 0.8Hz");
                break;
        }
        boolean ret = target.remove(listener);
        if (own.isEmpty()) {
            own.stop();
        }
        return ret;
    }

    /* loaded from: classes.dex */
    private class Period extends Handler {
        private Period() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Oscillator.this.run();
        }
    }

    private boolean isEmpty() {
        if (this.for32Hz.size() != 0 || this.for08Hz.size() != 0) {
            return false;
        }
        return true;
    }

    private ArrayList<OnPeriodListener> returnTarget(int Hz) {
        switch (Hz) {
            case 8:
                ArrayList<OnPeriodListener> target = this.for08Hz;
                return target;
            case 32:
                ArrayList<OnPeriodListener> target2 = this.for32Hz;
                return target2;
            default:
                Log.w(getClass().getSimpleName(), "This class supports only 3.2Hz and 0.8Hz");
                return null;
        }
    }

    private void start() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessageDelayed(0, 156L);
    }

    private void stop() {
        this.highlowFor32 = true;
        this.highlowFor08 = true;
        this.count = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void run() {
        if (this.count % 4 == 0) {
            this.count = 0;
        }
        kick(32, this.highlowFor32);
        this.highlowFor32 = !this.highlowFor32;
        if (this.count == 0) {
            kick(8, this.highlowFor08);
            this.highlowFor08 = this.highlowFor08 ? false : true;
        }
        this.count++;
        if (!isEmpty()) {
            start();
        }
    }

    private void kick(int Hz, boolean highlow) {
        ArrayList<OnPeriodListener> target = returnTarget(Hz);
        int count = target.size();
        for (int i = 0; i < count; i++) {
            target.get(i).onPeriod(Hz, highlow);
        }
    }
}
