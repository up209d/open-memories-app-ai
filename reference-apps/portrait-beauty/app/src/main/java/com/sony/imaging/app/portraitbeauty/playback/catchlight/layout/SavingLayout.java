package com.sony.imaging.app.portraitbeauty.playback.catchlight.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.ImageEditor;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;

/* loaded from: classes.dex */
public class SavingLayout extends Layout implements ImageEditor.SaveCallback {
    public static boolean sSavingToSInglePlayBack = false;
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private final int REVIEW_DELAY_TIME = 250;
    private RunnableTask mRunnableTask = null;
    private int mImageOrientation = 0;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.saving_layout);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        OptimizedImage image = CatchLightPlayBackLayout.sSelectedOptimizedImage;
        switch (ContentsManager.getInstance().getContentInfo(ContentsManager.getInstance().getContentsId()).getInt("Orientation")) {
            case 3:
                image = ImageEditor.rotateImage(image, RotateImageFilter.ROTATION_DEGREE.DEGREE_180, true);
                this.mImageOrientation = 3;
                break;
            case 4:
            case 5:
            case 7:
            default:
                this.mImageOrientation = 1;
                break;
            case 6:
                image = ImageEditor.rotateImage(image, RotateImageFilter.ROTATION_DEGREE.DEGREE_270, true);
                this.mImageOrientation = 6;
                break;
            case 8:
                image = ImageEditor.rotateImage(image, RotateImageFilter.ROTATION_DEGREE.DEGREE_90, true);
                this.mImageOrientation = 8;
                break;
        }
        ImageEditor.setImage(image);
        ImageEditor.init(getActivity());
        ImageEditor.saveEditedImage(this.mImageOrientation, this);
        CatchLightPlayBackLayout.sSelectedOptimizedImage = image;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ImageEditor.clearImage();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (CatchLightPlayBackLayout.sSelectedOptimizedImage != null) {
            CatchLightPlayBackLayout.sSelectedOptimizedImage.release();
            CatchLightPlayBackLayout.sSelectedOptimizedImage = null;
        }
        if (this.mRunnableTask != null) {
            this.mRunnableTask.removeCallbacks();
            this.mRunnableTask = null;
        }
        closeLayout();
        this.mCurrentView = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        System.gc();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                return -1;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return -1;
    }

    /* loaded from: classes.dex */
    private class RunnableTask {
        private Handler mHandler;
        Runnable mRunnable;

        private RunnableTask() {
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.SavingLayout.RunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    AppLog.enter(SavingLayout.this.TAG, AppLog.getMethodName());
                    AppLog.info(SavingLayout.this.TAG, "Transiting to shooting or continuousshot playback list");
                    SavingLayout.this.getHandler().sendEmptyMessage(101);
                    RunnableTask.this.removeCallbacks();
                    AppLog.exit(SavingLayout.this.TAG, AppLog.getMethodName());
                }
            };
        }

        public void execute() {
            AppLog.enter(SavingLayout.this.TAG, AppLog.getMethodName());
            this.mHandler.postDelayed(this.mRunnable, 250L);
            AppLog.exit(SavingLayout.this.TAG, AppLog.getMethodName());
        }

        public void removeCallbacks() {
            AppLog.enter(SavingLayout.this.TAG, AppLog.getMethodName());
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mRunnable = null;
            AppLog.exit(SavingLayout.this.TAG, AppLog.getMethodName());
        }
    }

    private OptimizedImage resizeToOriginalImage() {
        ContentsManager contentManager = ContentsManager.getInstance();
        ContentsIdentifier contentsIdentifier = contentManager.getContentsId();
        OptimizedImage optImage = ContentsManager.getInstance().getOptimizedImageWithoutCache(contentsIdentifier, 1);
        if (optImage != null) {
            int width = optImage.getWidth();
            int height = optImage.getHeight();
            ImageEditor.releaseOptImage(optImage);
            if (CatchLightPlayBackLayout.sSelectedOptimizedImage != null) {
                ScaleImageFilter scaleFilter = new ScaleImageFilter();
                scaleFilter.setSource(CatchLightPlayBackLayout.sSelectedOptimizedImage, true);
                scaleFilter.setDestSize(width, height);
                if (scaleFilter.execute()) {
                    CatchLightPlayBackLayout.sSelectedOptimizedImage = scaleFilter.getOutput();
                    Log.d(this.TAG, "resizeOriginalImage() resize1_execute");
                } else {
                    Log.e(this.TAG, "resizeOriginalImage() Could not resize!!! ");
                }
                scaleFilter.clearSources();
                scaleFilter.release();
            }
        }
        return CatchLightPlayBackLayout.sSelectedOptimizedImage;
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.ImageEditor.SaveCallback
    public void onSuccess() {
        if (this.mRunnableTask == null) {
            this.mRunnableTask = new RunnableTask();
        }
        this.mRunnableTask.execute();
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.ImageEditor.SaveCallback
    public void onFail() {
        getHandler().sendEmptyMessage(101);
    }
}
