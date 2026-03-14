package com.sony.imaging.app.portraitbeauty.playback.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.ImageEditor;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyPlayDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class PortraitBeautyBrowserSingleLayout extends BrowserSingleLayout {
    IEachPlaybackTriggerFunction mEachPbTrigger;
    private int mFaceNum;
    private FooterGuide mFooterGuide = null;
    private View mCurrentView = null;
    private int mcautionID = 0;
    private ImageAnalyzer.AnalyzedFace[] mFaces = null;

    public void setEachPbTrigger(IEachPlaybackTriggerFunction trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        PortraitBeautyPlayDisplayModeObserver.getInstance().toggleDisplayMode(1);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mCurrentView != null) {
            this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(getFooterGuideResource());
            if (this.mFooterGuide != null) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_PLAY_FGUIDE_PJONE));
            }
        }
        BackUpUtil.getInstance().setPreference("FACE_SELECTION", 0);
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, -1);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mFooterGuide = null;
        this.mCurrentView = null;
        if (this.mcautionID != 0) {
            CautionUtilityClass.getInstance().disapperTrigger(this.mcautionID);
            this.mcautionID = 0;
        }
        this.mFaces = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (true == (DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus())) {
            this.mcautionID = PortraitBeautyInfo.CAUTION_ID_PROTECT_MS_SLOT1;
            CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
            return 1;
        }
        if (!(DatabaseUtil.MediaStatus.READ_ONLY == DatabaseUtil.checkMediaStatus())) {
            if (!MediaNotificationManager.getInstance().isMounted()) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_CARD_ON_PLAYBACK;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                return 1;
            }
            if (!PortraitBeautyUtil.getInstance().isSpaceAvailableInMemoryCard()) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                return 1;
            }
            if (isUnsupportedCaution()) {
                return 1;
            }
            this.mcautionID = 0;
            getHandler().sendEmptyMessage(100);
            return 1;
        }
        return -1;
    }

    private boolean isUnsupportedCaution() {
        boolean result = false;
        ContentsManager contentManager = ContentsManager.getInstance();
        ContentsIdentifier contentsIdentifier = contentManager.getContentsId();
        ImageEditor.releaseOptImage(CatchLightPlayBackLayout.sSelectedOptimizedImage);
        ContentInfo info = (ContentInfo) ContentsManager.getInstance().getValue(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE);
        if (info == null || info.getLong("MkNoteImgQual") == 0) {
            this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
            CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
            return true;
        }
        if (info == null || !PortraitBeautyUtil.getInstance().getAvailableValue_AspectRatios().contains(PortraitBeautyUtil.getInstance().aspectRatioComparer(info.getInt("AspectRatio")))) {
            this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
            CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
            return true;
        }
        try {
            OptimizedImage optImage = contentManager.getOptimizedImageWithoutCache(contentsIdentifier, 1);
            if (optImage == null || (optImage != null && !optImage.isValid())) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                return true;
            }
            String imageAspectRatio = PortraitBeautyUtil.getInstance().getAspectRatio(optImage.getWidth(), optImage.getHeight());
            ImageAnalyzer imgAnalyzer1 = new ImageAnalyzer();
            Log.d(this.TAG, "fineFaces in");
            this.mFaces = new ImageAnalyzer.AnalyzedFace[8];
            this.mFaceNum = imgAnalyzer1.findFaces(optImage, this.mFaces);
            imgAnalyzer1.release();
            if (optImage != null) {
                optImage.release();
            }
            if (this.mFaces != null) {
                int len = this.mFaces.length;
                for (int i = 0; i < len; i++) {
                    this.mFaces[i] = null;
                }
            }
            if (imageAspectRatio == null) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_UNSUPPORTED_IMAGE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                result = true;
            } else if (this.mFaceNum == 0) {
                this.mcautionID = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_FACE;
                CautionUtilityClass.getInstance().requestTrigger(this.mcautionID);
                result = true;
            }
            return result;
        } catch (Exception e) {
            return true;
        }
    }
}
