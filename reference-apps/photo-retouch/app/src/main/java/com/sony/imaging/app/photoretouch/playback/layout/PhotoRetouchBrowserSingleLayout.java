package com.sony.imaging.app.photoretouch.playback.layout;

import android.graphics.Point;
import android.util.Log;
import android.view.ViewGroup;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class PhotoRetouchBrowserSingleLayout extends OptimizedImageLayoutBase {
    private static PhotoRetouchBrowserSingleLayout own;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_singlepb_image_err};

    public PhotoRetouchBrowserSingleLayout() {
        own = this;
    }

    public static void showImage(boolean b) {
        if (b) {
            OptimizedImage i = ImageEditor.getImage();
            own.getOptimizedImageView().setOptimizedImage(i);
        } else {
            own.getOptimizedImageView().setOptimizedImage((OptimizedImage) null);
        }
    }

    public static void updateImage(OptimizedImage i) {
        ImageEditor.setImage(i);
        own.updateOptimizedImage(i);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ContentsManager mgr = ContentsManager.getInstance();
        OptimizedImage image = mgr.getOptimizedImageWithoutCache(mgr.getContentsId(), 1);
        ImageEditor.optImageCount++;
        ImageEditor.setImage(image);
        if (image != null) {
            int optImgHeight = image.getHeight();
            int optImgWidth = image.getWidth();
            Log.d(this.TAG, "====height= " + optImgHeight);
            Log.d(this.TAG, "====width= " + optImgWidth);
            ImageEditor.retrieveAspectRatio(optImgWidth, optImgHeight);
            ContentInfo info = mgr.getContentInfo(mgr.getContentsId());
            int orientationInfo = info.getInt("Orientation");
            ImageEditor.setOrientationInfo(orientationInfo);
        }
        updateOptimizedImage(image);
        showImage(true);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ImageEditor.clearImage();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateVPicDisplay(OptimizedImageView img, ContentsManager mgr) {
        int angle = ImageEditor.getOrientationInfo();
        switch (angle) {
            case 3:
                img.setDisplayRotationAngle(180);
                return;
            case 4:
            case 5:
            case 7:
            default:
                img.setDisplayRotationAngle(0);
                return;
            case 6:
                img.setDisplayRotationAngle(90);
                return;
            case 8:
                img.setDisplayRotationAngle(270);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateOptimizedImageLayoutParam(OptimizedImageView imgView, ContentsManager mgr) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgView.getLayoutParams();
        params.width = AppRoot.USER_KEYCODE.MODE_DIAL_AES;
        params.height = 480;
        params.leftMargin = 122;
        Point p = new Point(params.width >> 1, params.height >> 1);
        imgView.setPivot(p);
        imgView.setLayoutParams(params);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.single_image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateDisplay() {
        updateOptimizedImage(ImageEditor.getImage());
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageOkLayoutResourceId() {
        return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageErrLayoutResourceId() {
        return IMAGE_ERR_LAYOUT_RESOURCE_ID;
    }
}
