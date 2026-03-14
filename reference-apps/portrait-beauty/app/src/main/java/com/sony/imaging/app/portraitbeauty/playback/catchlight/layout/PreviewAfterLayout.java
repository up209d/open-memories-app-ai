package com.sony.imaging.app.portraitbeauty.playback.catchlight.layout;

import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.EVFOffLayout;
import com.sony.imaging.app.portraitbeauty.common.ImageEditor;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class PreviewAfterLayout extends EVFOffLayout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private OptimizedImageView mImgVwPreview = null;
    private boolean isCenterKeyPressed = false;
    private OptimizedImage mOptImage = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.preview_layout);
        this.mImgVwPreview = this.mCurrentView.findViewById(R.id.preview_opt_img_view1);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setPrviewAfterImage();
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        closeLayout();
        if (this.mImgVwPreview != null) {
            this.mImgVwPreview.release();
            this.mImgVwPreview.setOptimizedImage((OptimizedImage) null);
            this.mImgVwPreview = null;
        }
        ImageEditor.releaseOptImage(this.mOptImage);
        if (!this.isCenterKeyPressed) {
            ImageEditor.releaseOptImage(CatchLightPlayBackLayout.sSelectedOptimizedImage);
        }
        this.isCenterKeyPressed = false;
        this.mCurrentView = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 232:
                this.isCenterKeyPressed = true;
                return 0;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                this.isCenterKeyPressed = true;
                return 0;
            default:
                return 0;
        }
    }

    private void setPrviewAfterImage() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mImgVwPreview.setOptimizedImage((OptimizedImage) null);
        if (CatchLightPlayBackLayout.sSelectedOptimizedImage != null) {
            AppLog.checkIf(this.TAG, "Preview image created");
            ViewGroup.LayoutParams params = this.mImgVwPreview.getLayoutParams();
            this.mImgVwPreview.setDisplayType(OptimizedImageView.DisplayType.DISPLAY_TYPE_CENTER_INNER);
            Point p = new Point(params.width >> 1, params.height >> 1);
            this.mImgVwPreview.setPivot(p);
            this.mImgVwPreview.setLayoutParams(params);
            this.mImgVwPreview.setVisibility(0);
            this.mImgVwPreview.setOptimizedImage(CatchLightPlayBackLayout.sSelectedOptimizedImage);
            return;
        }
        AppLog.checkIf(this.TAG, "Preview image is null");
    }
}
