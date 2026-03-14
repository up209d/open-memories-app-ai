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
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureImageUtil;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.playback.state.PreparingPlayState;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class PreparingLayout extends Layout implements ContentsManager.QueryCallback {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private TextView mTxtVwProcessing = null;
    private DoubleExposureImageUtil mDoubleExposureImageUtil = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = obtainViewFromPool(R.layout.preparing_layout);
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
            getAndProcessFirstOptimizedImage();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
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
            getAndProcessFirstOptimizedImage();
        } else {
            AppLog.exit(this.TAG, "Intial query failed");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void getAndProcessFirstOptimizedImage() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ContentsIdentifier contentsIdentifier = ContentsManager.getInstance().getContentsId();
        if (contentsIdentifier == null) {
            AppLog.info(this.TAG, "Content identifier is null");
            return;
        }
        int firstImageFileNumber = ContentsManager.getInstance().getInt("dcf_file_number");
        int firstImageFolderNumber = ContentsManager.getInstance().getInt("dcf_folder_number");
        AppLog.info(this.TAG, "ContentsIdentifier id: " + contentsIdentifier._id);
        AppLog.info(this.TAG, "ContentsIdentifier data: " + contentsIdentifier.data);
        AppLog.info(this.TAG, "ContentsIdentifier mediaId: " + contentsIdentifier.mediaId);
        AppLog.info(this.TAG, "File Number: " + firstImageFileNumber);
        AppLog.info(this.TAG, "Folder Number: " + firstImageFolderNumber);
        this.mDoubleExposureImageUtil.setFirstImageContentsIdentifier(contentsIdentifier);
        DoubleExposureUtil.getInstance().setFirstImageFileNumber(firstImageFileNumber);
        DoubleExposureUtil.getInstance().setFirstImageFolderNumber(firstImageFolderNumber);
        OptimizedImage optimizedImage = this.mDoubleExposureImageUtil.getReversedOptimizedImage(this.mDoubleExposureImageUtil.getRotatedOptimizedImage(this.mDoubleExposureImageUtil.getScaledOptimizedImage(this.mDoubleExposureImageUtil.getOptimizedImage(contentsIdentifier), true)));
        if (DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus()) {
            transitToShooting(optimizedImage);
        } else if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus()) {
            this.mDoubleExposureImageUtil.storeOptimizedImage(optimizedImage);
            transitToShooting(optimizedImage);
        } else {
            if (optimizedImage != null) {
                optimizedImage.release();
            }
            AppLog.info(this.TAG, "Media is not mounted");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void transitToShooting(OptimizedImage optimizedImage) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (optimizedImage != null) {
            optimizedImage.release();
        }
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!selectedTheme.equalsIgnoreCase("Manual")) {
            DoubleExposureUtil.getInstance().setInitFocusMode(true);
        }
        getHandler().sendEmptyMessage(PreparingPlayState.TRANSIT_TO_SHOOTING);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
