package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.sony.imaging.app.base.shooting.widget.AFViews;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class FocusLayout extends Layout {
    protected ViewGroup mCurrentLayout = null;
    private View mMainView = null;
    private DelayAttachView idleHandler = new DelayAttachView();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DelayAttachView implements MessageQueue.IdleHandler {
        private DelayAttachView() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            if (FocusLayout.this.mMainView == null) {
                FocusLayout.this.mMainView = new AFViews(FocusLayout.this.getActivity(), null);
            }
            FocusLayout.this.mCurrentLayout.addView(FocusLayout.this.mMainView);
            return false;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentLayout == null) {
            this.mCurrentLayout = new FrameLayout(getActivity());
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
