package com.sony.imaging.app.doubleexposure.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.caution.DoubleExposureInfo;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureImageUtil;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.OptimizedImage;
import java.util.List;

/* loaded from: classes.dex */
public class DoubleExposureBrowserSingleLayout extends BrowserSingleLayout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private int mCautionId = 0;
    private TextView mTxtVwOk = null;
    private TextView mTxtVwCancel = null;
    private String mImageAspectRatio = null;
    private DoubleExposureUtil mDoubleExposureUtil = null;

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        if (this.mCurrentView != null) {
            this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(getFooterGuideResource());
        }
        if (this.mDoubleExposureUtil.isImageSelection()) {
            AppLog.info(this.TAG, "Footer Guide need to display.");
            if (1 == Environment.getVersionOfHW()) {
                if (this.mFooterGuide != null) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_MLE_FOOTER_GUIDE_4_SK));
                }
            } else if (this.mFooterGuide != null) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), android.R.string.autofill_phone_prefix_separator_re));
            }
        } else {
            AppLog.info(this.TAG, "Footer Guide need not to display.");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        CautionUtilityClass.getInstance().disapperTrigger(this.mCautionId);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(this.mCautionId, null);
        this.mDoubleExposureUtil = null;
        this.mImageAspectRatio = null;
        this.mCautionId = 0;
        this.mTxtVwOk = null;
        this.mTxtVwCancel = null;
        this.mFooterGuide = null;
        this.mView = null;
        this.mCurrentView = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retValue = 1;
        if (this.mDoubleExposureUtil.isImageSelection()) {
            transitionIndexPb();
        } else {
            retValue = super.pushedMenuKey();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retValue;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retValue = 1;
        if (this.mDoubleExposureUtil.isImageSelection()) {
            transitionIndexPb();
        } else {
            retValue = super.pushedPlayIndexKey();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retValue;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retValue = 1;
        if (this.mDoubleExposureUtil.isImageSelection()) {
            checkAspectRatioAndImageSize();
            if (this.mCautionId != 0) {
                displayCaution(this.mCautionId);
            } else {
                transitToPrepairingState();
            }
        } else {
            retValue = super.pushedCenterKey();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retValue;
    }

    private void checkAspectRatioAndImageSize() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCautionId = 0;
        ContentsIdentifier contentsIdentifier = ContentsManager.getInstance().getContentsId();
        if (contentsIdentifier == null) {
            AppLog.info(this.TAG, "Content identifier is null");
            return;
        }
        AppLog.info(this.TAG, "ContentsIdentifier id: " + contentsIdentifier._id);
        AppLog.info(this.TAG, "ContentsIdentifier mediaId: " + contentsIdentifier.mediaId);
        AppLog.info(this.TAG, "ContentsIdentifier data: " + contentsIdentifier.data);
        OptimizedImage optimizedImage = DoubleExposureImageUtil.getInstance().getOptimizedImage(contentsIdentifier);
        ContentInfo info = ContentsManager.getInstance().getContentInfo(contentsIdentifier);
        int contentType = info.getInt("ContentType");
        if (optimizedImage != null) {
            int imageWidth = optimizedImage.getWidth();
            int imageHeight = optimizedImage.getHeight();
            optimizedImage.release();
            if (contentType != 5) {
                this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                return;
            }
            this.mDoubleExposureUtil.setFirstImageAspectRatio(null);
            String imageAspectRatio = this.mDoubleExposureUtil.getAspectRatio(imageWidth, imageHeight);
            AppLog.info(this.TAG, "First Image Aspect Ratio : " + imageAspectRatio);
            String imageSize = this.mDoubleExposureUtil.getPictureSize(imageAspectRatio, imageWidth, imageHeight);
            AppLog.info(this.TAG, "First Image Size : " + imageSize);
            String cameraAspectRatio = this.mDoubleExposureUtil.getCameraSettingsAspectRatio();
            AppLog.info(this.TAG, "Camera Settings Aspect Ratio : " + cameraAspectRatio);
            String cameraImageSize = this.mDoubleExposureUtil.getCameraSettingsImageSize();
            AppLog.info(this.TAG, "Camera Settings Image Size : " + cameraImageSize);
            List<String> supportedPictureAspects = this.mDoubleExposureUtil.getSupportedAspectRatio();
            List<String> supportedPictureSizes = this.mDoubleExposureUtil.getSupportedImageSize();
            if (imageAspectRatio != null && cameraAspectRatio != null && imageAspectRatio.equalsIgnoreCase(cameraAspectRatio)) {
                if (imageSize == null) {
                    AppLog.info(this.TAG, "Display Caution ID26");
                    this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                }
            } else if (supportedPictureAspects != null && supportedPictureAspects.contains(imageAspectRatio)) {
                if (imageSize != null && cameraImageSize != null && imageSize.equalsIgnoreCase(cameraImageSize)) {
                    AppLog.info(this.TAG, "Display Caution ID27");
                    this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_ASPECT_RATIO_CHANGING;
                } else if (supportedPictureSizes != null && supportedPictureSizes.contains(imageSize)) {
                    AppLog.info(this.TAG, "Display Caution ID27");
                    this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_ASPECT_RATIO_CHANGING;
                } else {
                    AppLog.info(this.TAG, "Display Caution ID26");
                    this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                }
            } else {
                AppLog.info(this.TAG, "Display Caution ID26");
                this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
            }
            this.mImageAspectRatio = imageAspectRatio;
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return;
        }
        AppLog.info(this.TAG, "Optimized image is null");
        this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitToPrepairingState() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mDoubleExposureUtil.setFirstImageTransition(DoubleExposureConstant.FIRST_IMAGE_TRANSIT_PLAYBACK);
        getHandler().sendEmptyMessage(100);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void displayCaution(final int cautionId) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.doubleexposure.playback.layout.DoubleExposureBrowserSingleLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                AppLog.enter(DoubleExposureBrowserSingleLayout.this.TAG, AppLog.getMethodName());
                int retValue = -1;
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.UP /* 103 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                    case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                        if (131227 == cautionId) {
                            DoubleExposureBrowserSingleLayout.this.toggleButtonSelection();
                            retValue = 1;
                            break;
                        }
                        break;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        if (131227 == cautionId) {
                            DoubleExposureBrowserSingleLayout.this.mTxtVwOk = DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.getButtonOk();
                            DoubleExposureBrowserSingleLayout.this.mTxtVwCancel = DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.getButtonCancel();
                            if (DoubleExposureBrowserSingleLayout.this.mTxtVwOk == null || !DoubleExposureBrowserSingleLayout.this.mTxtVwOk.isSelected()) {
                                if (DoubleExposureBrowserSingleLayout.this.mTxtVwCancel == null || !DoubleExposureBrowserSingleLayout.this.mTxtVwCancel.isSelected()) {
                                    AppLog.info(DoubleExposureBrowserSingleLayout.this.TAG, "Pressed CENTER and both are null.");
                                } else {
                                    AppLog.info(DoubleExposureBrowserSingleLayout.this.TAG, "Pressed CANCLE");
                                    CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                                    DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.setFirstImageAspectRatio(null);
                                }
                            } else {
                                AppLog.info(DoubleExposureBrowserSingleLayout.this.TAG, "Pressed OK");
                                CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                                DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.setFirstImageAspectRatio(DoubleExposureBrowserSingleLayout.this.mImageAspectRatio);
                                DoubleExposureBrowserSingleLayout.this.transitToPrepairingState();
                            }
                            retValue = 1;
                            break;
                        } else if (131226 == cautionId) {
                            CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                            retValue = 1;
                            break;
                        }
                        break;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        if (131227 == cautionId) {
                            DoubleExposureBrowserSingleLayout.this.mTxtVwOk = DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.getButtonOk();
                            DoubleExposureBrowserSingleLayout.this.mTxtVwCancel = DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.getButtonCancel();
                            if (DoubleExposureBrowserSingleLayout.this.mTxtVwOk == null || !DoubleExposureBrowserSingleLayout.this.mTxtVwOk.isSelected()) {
                                if (DoubleExposureBrowserSingleLayout.this.mTxtVwCancel == null || !DoubleExposureBrowserSingleLayout.this.mTxtVwCancel.isSelected()) {
                                    AppLog.info(DoubleExposureBrowserSingleLayout.this.TAG, "Pressed S1_ON and both are null.");
                                    break;
                                } else {
                                    AppLog.info(DoubleExposureBrowserSingleLayout.this.TAG, "S1_ON with CANCLE");
                                    CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                                    DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.setFirstImageAspectRatio(null);
                                    retValue = 0;
                                    break;
                                }
                            } else {
                                AppLog.info(DoubleExposureBrowserSingleLayout.this.TAG, "S1_ON with OK");
                                CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                                DoubleExposureBrowserSingleLayout.this.mDoubleExposureUtil.setFirstImageAspectRatio(DoubleExposureBrowserSingleLayout.this.mImageAspectRatio);
                                DoubleExposureBrowserSingleLayout.this.transitToPrepairingState();
                                retValue = 1;
                                break;
                            }
                        } else if (131226 == cautionId) {
                            CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                            retValue = 0;
                            break;
                        }
                        break;
                    default:
                        retValue = -1;
                        break;
                }
                AppLog.exit(DoubleExposureBrowserSingleLayout.this.TAG, AppLog.getMethodName());
                return retValue;
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                AppLog.enter(DoubleExposureBrowserSingleLayout.this.TAG, AppLog.getMethodName());
                AppLog.exit(DoubleExposureBrowserSingleLayout.this.TAG, AppLog.getMethodName());
                return -1;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleButtonSelection() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mTxtVwOk = this.mDoubleExposureUtil.getButtonOk();
        this.mTxtVwCancel = this.mDoubleExposureUtil.getButtonCancel();
        if (this.mTxtVwOk != null && this.mTxtVwCancel != null) {
            this.mTxtVwOk.setSelected(!this.mTxtVwOk.isSelected());
            this.mTxtVwCancel.setSelected(this.mTxtVwCancel.isSelected() ? false : true);
        } else if (this.mTxtVwOk != null) {
            this.mTxtVwOk.setSelected(true);
        } else if (this.mTxtVwCancel != null) {
            this.mTxtVwCancel.setSelected(true);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
