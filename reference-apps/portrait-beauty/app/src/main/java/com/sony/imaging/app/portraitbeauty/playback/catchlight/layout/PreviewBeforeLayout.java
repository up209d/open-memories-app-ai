package com.sony.imaging.app.portraitbeauty.playback.catchlight.layout;

import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.EVFOffLayout;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class PreviewBeforeLayout extends EVFOffLayout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private OptimizedImage mOptimizedImage = null;
    private OptimizedImageView mImgVwPreview = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.preview_before_layout);
        this.mImgVwPreview = this.mCurrentView.findViewById(R.id.preview_opt_img_view);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ContentsIdentifier contentsIdentifier = ContentsManager.getInstance().getContentsId();
        this.mOptimizedImage = ContentsManager.getInstance().getOptimizedImageWithoutCache(contentsIdentifier, 1);
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
        if (this.mOptimizedImage != null) {
            this.mOptimizedImage.release();
            this.mOptimizedImage = null;
        }
        this.mCurrentView = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                return 0;
            default:
                return 0;
        }
    }

    private void setPrviewAfterImage() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mOptimizedImage != null) {
            AppLog.checkIf(this.TAG, "Preview image created");
            ViewGroup.LayoutParams params = this.mImgVwPreview.getLayoutParams();
            this.mImgVwPreview.setDisplayType(OptimizedImageView.DisplayType.DISPLAY_TYPE_CENTER_INNER);
            Point p = new Point(params.width >> 1, params.height >> 1);
            this.mImgVwPreview.setPivot(p);
            this.mImgVwPreview.setLayoutParams(params);
            ContentsManager contentManager = ContentsManager.getInstance();
            ContentInfo info = contentManager.getContentInfo(contentManager.getContentsId());
            int angle = info.getInt("Orientation");
            switch (angle) {
                case 3:
                    this.mImgVwPreview.setDisplayRotationAngle(180);
                    break;
                case 4:
                case 5:
                case 7:
                default:
                    this.mImgVwPreview.setDisplayRotationAngle(0);
                    break;
                case 6:
                    this.mImgVwPreview.setDisplayRotationAngle(90);
                    break;
                case 8:
                    this.mImgVwPreview.setDisplayRotationAngle(270);
                    break;
            }
            this.mImgVwPreview.setOptimizedImage(this.mOptimizedImage);
            return;
        }
        AppLog.checkIf(this.TAG, "Preview image is null");
    }
}
