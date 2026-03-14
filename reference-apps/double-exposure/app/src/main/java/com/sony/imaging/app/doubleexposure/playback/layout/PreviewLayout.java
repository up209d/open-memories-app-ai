package com.sony.imaging.app.doubleexposure.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.fw.Layout;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class PreviewLayout extends Layout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private OptimizedImageView mOptimizedImageView = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = obtainViewFromPool(R.layout.autoreview_layout);
        }
        this.mOptimizedImageView = this.mCurrentView.findViewById(R.id.opt_img_view);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.mOptimizedImageView != null && ProcessingLayout.sOptimizedImage != null) {
            this.mOptimizedImageView.setOptimizedImage(ProcessingLayout.sOptimizedImage);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (ProcessingLayout.sOptimizedImage != null) {
            ProcessingLayout.sOptimizedImage.release();
            ProcessingLayout.sOptimizedImage = null;
        }
        if (this.mOptimizedImageView != null) {
            this.mOptimizedImageView.release();
            this.mOptimizedImageView = null;
        }
        this.mCurrentView = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        event.getScanCode();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }
}
