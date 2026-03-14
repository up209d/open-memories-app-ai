package com.sony.imaging.app.doubleexposure.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureImageUtil;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.playback.state.PreviewPlayState;
import com.sony.imaging.app.fw.Layout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class ProcessingLayout extends Layout implements ContentsManager.QueryCallback {
    public static OptimizedImage sOptimizedImage = null;
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private TextView mTxtVwProcessing = null;
    private DoubleExposureImageUtil mDoubleExposureImageUtil = null;
    private DESA mDESA = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = obtainViewFromPool(R.layout.processing_layout);
        }
        this.mTxtVwProcessing = (TextView) this.mCurrentView.findViewById(R.id.processing_text);
        if (this.mTxtVwProcessing != null) {
            this.mTxtVwProcessing.setText(android.R.string.deleteText);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        this.mDoubleExposureImageUtil = DoubleExposureImageUtil.getInstance();
        ContentsManager contentsManager = ContentsManager.getInstance();
        if (!contentsManager.isInitialQueryDone()) {
            AppLog.info(this.TAG, "Intial query not done");
            contentsManager.startInitialQuery(this, false);
        } else {
            AppLog.info(this.TAG, "Intial query done");
            getAndProcessSecondOptimizedImage();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mDESA != null) {
            this.mDESA.terminate();
            this.mDESA = null;
        }
        this.mTxtVwProcessing = null;
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

    @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.QueryCallback
    public void onCallback(boolean result) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (true == result) {
            AppLog.exit(this.TAG, "Intial query succeeded");
            getAndProcessSecondOptimizedImage();
        } else {
            AppLog.exit(this.TAG, "Intial query failed");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void getAndProcessSecondOptimizedImage() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ContentsIdentifier contentsIdentifier = ContentsManager.getInstance().getContentsId();
        if (contentsIdentifier == null) {
            AppLog.info(this.TAG, "Content identifier is null");
            return;
        }
        AppLog.info(this.TAG, "ContentsIdentifier id: " + contentsIdentifier._id);
        AppLog.info(this.TAG, "ContentsIdentifier mediaId: " + contentsIdentifier.mediaId);
        AppLog.info(this.TAG, "ContentsIdentifier data: " + contentsIdentifier.data);
        OptimizedImage secondOptimizedImage = this.mDoubleExposureImageUtil.getOptimizedImage(contentsIdentifier);
        int orientation = ContentsManager.getInstance().getContentInfo(contentsIdentifier).getInt("Orientation");
        OptimizedImage firstOptimizedImage = this.mDoubleExposureImageUtil.getOptimizedImage(this.mDoubleExposureImageUtil.getFirstImageContentsIdentifier());
        if (firstOptimizedImage.getWidth() != secondOptimizedImage.getWidth() || firstOptimizedImage.getHeight() != secondOptimizedImage.getHeight()) {
            firstOptimizedImage = this.mDoubleExposureImageUtil.getScaledOptimizedImage(firstOptimizedImage, true, secondOptimizedImage.getWidth(), secondOptimizedImage.getHeight());
        }
        if (firstOptimizedImage != null) {
            firstOptimizedImage = this.mDoubleExposureImageUtil.getRotatedOptimizedImage(firstOptimizedImage);
        }
        if (firstOptimizedImage != null) {
            firstOptimizedImage = this.mDoubleExposureImageUtil.getReversedOptimizedImage(firstOptimizedImage);
        }
        OptimizedImage outOptimizedImage = null;
        if (secondOptimizedImage != null) {
            outOptimizedImage = this.mDoubleExposureImageUtil.copyOptimizedImage(secondOptimizedImage);
        }
        this.mDESA = DESA.getInstance();
        this.mDESA.setPackageName(AppContext.getAppContext().getPackageName());
        this.mDESA.intialize();
        this.mDESA.setSFRMode(0);
        int blendMode = DoubleExposureUtil.getInstance().getBlendingMode();
        this.mDESA.setBlendMode(blendMode);
        if (secondOptimizedImage != null && firstOptimizedImage != null && outOptimizedImage != null) {
            this.mDESA.execute(secondOptimizedImage, firstOptimizedImage, outOptimizedImage);
            if (secondOptimizedImage != null) {
                secondOptimizedImage.release();
                secondOptimizedImage = null;
            }
            if (firstOptimizedImage != null) {
                firstOptimizedImage.release();
                firstOptimizedImage = null;
            }
            sOptimizedImage = outOptimizedImage;
            if (sOptimizedImage != null) {
                this.mDoubleExposureImageUtil.saveImage(sOptimizedImage);
            }
            if (1 != orientation && 65535 != orientation) {
                this.mDoubleExposureImageUtil.changeImageOrientation(orientation);
            }
        }
        if (secondOptimizedImage != null) {
            secondOptimizedImage.release();
        }
        if (firstOptimizedImage != null) {
            firstOptimizedImage.release();
        }
        getHandler().sendEmptyMessage(PreviewPlayState.TRANSIT_TO_PREVIEWSTATE);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
