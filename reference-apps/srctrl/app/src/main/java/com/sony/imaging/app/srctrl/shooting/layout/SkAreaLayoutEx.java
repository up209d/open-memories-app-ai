package com.sony.imaging.app.srctrl.shooting.layout;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;

/* loaded from: classes.dex */
public class SkAreaLayoutEx extends Layout {
    protected ViewGroup mCurrentLayout = null;
    protected View mMainView = null;
    protected DelayAttachView idleHandler = new DelayAttachView();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DelayAttachView implements MessageQueue.IdleHandler {
        protected DelayAttachView() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            if (SkAreaLayoutEx.this.mMainView == null) {
                SkAreaLayoutEx.this.mMainView = SkAreaLayoutEx.this.obtainViewFromPool(R.layout.shooting_main_skarea_ex);
            }
            SkAreaLayoutEx.this.mCurrentLayout.addView(SkAreaLayoutEx.this.mMainView);
            return false;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentLayout == null) {
            this.mCurrentLayout = new RelativeLayout(getActivity());
        }
        Looper.myQueue().addIdleHandler(this.idleHandler);
        return this.mCurrentLayout;
    }

    private void detachView() {
        Looper.myQueue().removeIdleHandler(this.idleHandler);
        if (this.mCurrentLayout != null) {
            this.mCurrentLayout.removeView(this.mMainView);
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        detachView();
    }
}
