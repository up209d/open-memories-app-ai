package com.sony.imaging.app.portraitbeauty.playback.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautySAWrapper;
import com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout.AdjustEffectChangeListener;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautySoftSkinController;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyAdjustEffectState;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class AdjustEffectPreviewLayout extends PseudoRecSingleLayout implements AdjustEffectChangeListener {
    private static int WHITESKINLEVELOFF = 0;
    private Runnable mEffectRunnable;
    private int mFaceNum;
    private Handler mHandler;
    private int mIsoInfo;
    private int mSoftSkinLevel;
    private int mWhiteSkinLevel;
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private OptimizedImageView mOptimizedImageView = null;
    private TextView mTxtVwTitle = null;
    private FooterGuide mFooterGuide = null;
    private OptimizedImage mOriginalOptImage = null;
    private OptimizedImage mEffectedOptImage = null;
    private boolean mIsEffectRun = false;
    private boolean mIsEffectRunQued = false;
    private PortraitBeautySAWrapper mPortraitBeautySAWrapper = null;

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = obtainViewFromPool(R.layout.adjust_effect_preview_layout);
        }
        if (this.mOptimizedImageView == null) {
            this.mOptimizedImageView = this.mCurrentView.findViewById(R.id.opt_img_view);
        }
        if (this.mEffectedOptImage != null) {
            this.mEffectedOptImage.release();
            this.mEffectedOptImage = null;
        }
        if (this.mPortraitBeautySAWrapper == null) {
            this.mPortraitBeautySAWrapper = PortraitBeautySAWrapper.getInstance();
            this.mPortraitBeautySAWrapper.initialize();
            this.mPortraitBeautySAWrapper.setSize(false);
        }
        this.mFooterGuide = (FooterGuide) this.mView.findViewById(R.id.footer_guide);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mView;
    }

    private void setOptimizedImage() {
        setOptImage();
        if (this.mEffectedOptImage != null) {
            func_applyEffect_AdjustEffect();
        } else {
            func_restart_effect_mOriginalOptImage();
            func_applyEffect_AdjustEffect();
        }
    }

    void setOptImage() {
        if (this.mOriginalOptImage == null) {
            ContentsManager mgr = ContentsManager.getInstance();
            ContentInfo contentInfo = mgr.getContentInfo(mgr.getContentsId());
            this.mOriginalOptImage = mgr.getOptimizedImage(mgr.getContentsId(), getImageType());
            if (this.mOriginalOptImage == null || !this.mOriginalOptImage.isValid()) {
                this.mOriginalOptImage = mgr.getOptimizedImageWithoutCache(mgr.getContentsId(), getImageType());
            }
            this.mIsoInfo = contentInfo.getInt("ISOSpeedRatings");
            this.mFaceNum = this.mPortraitBeautySAWrapper.getFaces(this.mOriginalOptImage);
            ScaleImageFilter scaleFilter = new ScaleImageFilter();
            scaleFilter.setSource(this.mOriginalOptImage, false);
            scaleFilter.setDestSize(this.mOriginalOptImage.getWidth(), this.mOriginalOptImage.getHeight());
            boolean isExecuted = scaleFilter.execute();
            if (isExecuted) {
                this.mEffectedOptImage = scaleFilter.getOutput();
            }
            scaleFilter.release();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int getOptimizedImageViewResourceId() {
        return R.id.opt_img_view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.adjust_effect_preview_layout;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mIsEffectRun = false;
        this.mIsEffectRunQued = false;
        PortraitBeautyAdjustEffectState.sAdjustEffectChangeListener = this;
        if (this.mFooterGuide != null) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.years));
        }
        setOptimizedImage();
        updateOptimizedImage(this.mEffectedOptImage);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateDisplay() {
        updateOptimizedImage(this.mEffectedOptImage);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        release();
        super.closeLayout();
    }

    private void release() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mEffectRunnable);
            this.mHandler = null;
        }
        if (this.mPortraitBeautySAWrapper != null) {
            this.mPortraitBeautySAWrapper.terminate();
            this.mPortraitBeautySAWrapper = null;
        }
        if (this.mOriginalOptImage != null) {
            this.mOriginalOptImage.release();
            this.mOriginalOptImage = null;
        }
        if (this.mEffectedOptImage != null) {
            this.mEffectedOptImage.release();
            this.mEffectedOptImage = null;
        }
        if (this.mOptimizedImageView != null) {
            this.mOptimizedImageView.release();
            this.mOptimizedImageView.setOptimizedImage((OptimizedImage) null);
            this.mOptimizedImageView = null;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        release();
        this.mCurrentView = null;
        System.gc();
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mTxtVwTitle != null) {
            this.mTxtVwTitle.setVisibility(0);
        }
        super.onReopened();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout.AdjustEffectChangeListener
    public void onUpdateEffect(int whiteSkinEffect, int softskinEffect) {
        if (!this.mOriginalOptImage.isValid()) {
            Log.d(this.TAG, "hello. i am invalid");
        }
        if (this.mOriginalOptImage != null) {
            this.mSoftSkinLevel = softskinEffect;
            this.mWhiteSkinLevel = whiteSkinEffect;
            this.mEffectRunnable = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.layout.AdjustEffectPreviewLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    if (AdjustEffectPreviewLayout.this.mEffectedOptImage != null) {
                        AdjustEffectPreviewLayout.this.mEffectedOptImage.release();
                        AdjustEffectPreviewLayout.this.mEffectedOptImage = null;
                    }
                    ScaleImageFilter scaleFilter = new ScaleImageFilter();
                    scaleFilter.setSource(AdjustEffectPreviewLayout.this.mOriginalOptImage, false);
                    scaleFilter.setDestSize(AdjustEffectPreviewLayout.this.mOriginalOptImage.getWidth(), AdjustEffectPreviewLayout.this.mOriginalOptImage.getHeight());
                    boolean isExecuted = scaleFilter.execute();
                    if (isExecuted) {
                        AdjustEffectPreviewLayout.this.mEffectedOptImage = scaleFilter.getOutput();
                    }
                    scaleFilter.release();
                    if (AdjustEffectPreviewLayout.WHITESKINLEVELOFF != AdjustEffectPreviewLayout.this.mWhiteSkinLevel) {
                        AdjustEffectPreviewLayout.this.mPortraitBeautySAWrapper.setOptimizeImage(AdjustEffectPreviewLayout.this.mOriginalOptImage, AdjustEffectPreviewLayout.this.mEffectedOptImage);
                        AdjustEffectPreviewLayout.this.mEffectedOptImage = AdjustEffectPreviewLayout.this.mPortraitBeautySAWrapper.executeSA();
                        AppLog.info(AdjustEffectPreviewLayout.this.TAG, "Face Brightening level is " + AdjustEffectPreviewLayout.this.mWhiteSkinLevel);
                    } else {
                        AppLog.info(AdjustEffectPreviewLayout.this.TAG, "Face Brightening level is OFF");
                    }
                    if (AdjustEffectPreviewLayout.this.mFaceNum <= 0 || 1 == AdjustEffectPreviewLayout.this.mSoftSkinLevel) {
                        AppLog.info(AdjustEffectPreviewLayout.this.TAG, "SoftSkin level is OFF");
                    } else {
                        AdjustEffectPreviewLayout.this.mEffectedOptImage = AdjustEffectPreviewLayout.this.mPortraitBeautySAWrapper.setSoftSkinEffect(AdjustEffectPreviewLayout.this.mEffectedOptImage, AdjustEffectPreviewLayout.this.mFaceNum, AdjustEffectPreviewLayout.this.mSoftSkinLevel, AdjustEffectPreviewLayout.this.mIsoInfo, true);
                        AppLog.info(AdjustEffectPreviewLayout.this.TAG, "SoftSkin level is " + AdjustEffectPreviewLayout.this.mSoftSkinLevel);
                    }
                    AdjustEffectPreviewLayout.this.updateDisplay();
                    AdjustEffectPreviewLayout.this.mIsEffectRun = false;
                    if (AdjustEffectPreviewLayout.this.mIsEffectRunQued) {
                        AdjustEffectPreviewLayout.this.mIsEffectRunQued = false;
                        AdjustEffectPreviewLayout.this.mHandler.postDelayed(AdjustEffectPreviewLayout.this.mEffectRunnable, 20L);
                    }
                    AppLog.info(AdjustEffectPreviewLayout.this.TAG, "mEffectRunnable end ");
                }
            };
            if (this.mIsEffectRun) {
                this.mIsEffectRunQued = true;
            } else {
                this.mIsEffectRun = true;
                this.mHandler.postDelayed(this.mEffectRunnable, 20L);
            }
        }
    }

    private void func_applyEffect_AdjustEffect() {
        this.mWhiteSkinLevel = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, 4);
        if (WHITESKINLEVELOFF != this.mWhiteSkinLevel) {
            this.mPortraitBeautySAWrapper.setOptimizeImage(this.mOriginalOptImage, this.mEffectedOptImage);
            this.mEffectedOptImage = this.mPortraitBeautySAWrapper.executeSA();
            AppLog.info(this.TAG, "Face Brightening level is " + this.mWhiteSkinLevel);
        }
        this.mSoftSkinLevel = PortraitBeautySoftSkinController.getInstance().convValue_String2Int(BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, PortraitBeautySoftSkinController.SOFTSKIN_MID));
        if (this.mFaceNum > 0 && 1 != this.mSoftSkinLevel) {
            if (this.mFaceNum > 0) {
                this.mEffectedOptImage = this.mPortraitBeautySAWrapper.setSoftSkinEffect(this.mEffectedOptImage, this.mFaceNum, this.mSoftSkinLevel, this.mIsoInfo, true);
            }
            AppLog.info(this.TAG, "SoftSkin level is " + this.mSoftSkinLevel);
            return;
        }
        AppLog.info(this.TAG, "SoftSkin level is OFF");
    }

    private void func_restart_effect_mOriginalOptImage() {
        if (this.mOriginalOptImage != null) {
            this.mOriginalOptImage.release();
            this.mOriginalOptImage = null;
        }
        this.mPortraitBeautySAWrapper.terminate();
        this.mPortraitBeautySAWrapper = null;
        this.mEffectedOptImage = null;
        if (this.mPortraitBeautySAWrapper == null) {
            this.mPortraitBeautySAWrapper = PortraitBeautySAWrapper.getInstance();
            this.mPortraitBeautySAWrapper.initialize();
            this.mPortraitBeautySAWrapper.setSize(false);
        }
        if (this.mOriginalOptImage == null) {
            ContentsManager mgr = ContentsManager.getInstance();
            ContentInfo contentInfo = mgr.getContentInfo(mgr.getContentsId());
            this.mOriginalOptImage = mgr.getOptimizedImage(mgr.getContentsId(), getImageType());
            if (this.mOriginalOptImage == null || !this.mOriginalOptImage.isValid()) {
                this.mOriginalOptImage = mgr.getOptimizedImageWithoutCache(mgr.getContentsId(), getImageType());
            }
            this.mIsoInfo = contentInfo.getInt("ISOSpeedRatings");
            this.mFaceNum = this.mPortraitBeautySAWrapper.getFaces(this.mOriginalOptImage);
            ScaleImageFilter scaleFilter = new ScaleImageFilter();
            scaleFilter.setSource(this.mOriginalOptImage, false);
            scaleFilter.setDestSize(this.mOriginalOptImage.getWidth(), this.mOriginalOptImage.getHeight());
            boolean isExecuted = scaleFilter.execute();
            if (isExecuted) {
                this.mEffectedOptImage = scaleFilter.getOutput();
            }
            scaleFilter.release();
        }
    }
}
