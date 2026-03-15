package com.sony.imaging.app.timelapse.angleshift.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class AngleShiftFrameSettingLayout extends DisplayMenuItemsMenuLayout {
    private static final int POINTER_HEIGHT = 12;
    private static final int POINTER_LEFT = 42;
    private static final int POINTER_TOP = 352;
    private static final int POINTER_WIDTH = 549;
    private RelativeLayout.LayoutParams mEndFlag;
    private FrameLayout mEndFlagLayout;
    private FrameLayout mFrameLayout;
    private RelativeLayout.LayoutParams mLayoutParams;
    private RelativeLayout.LayoutParams mStartFlag;
    private FrameLayout mStartFlagLayout;
    private View mCurrentView = null;
    private TextView mTitleName = null;
    private ImageView mFlag = null;
    private TextView mPosition = null;
    private TextView mNumberOfImages = null;
    private TextView mPlaybackTime = null;
    private FooterGuide mFooterGuide = null;
    private int mStartPosIndex = 0;
    private int mEndPosIndex = 0;
    private int mTotalShots = 0;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.as_frame_setting);
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mTitleName = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        this.mFlag = (ImageView) this.mCurrentView.findViewById(R.id.start_flag_icon);
        this.mPosition = (TextView) this.mCurrentView.findViewById(R.id.frame_position);
        this.mNumberOfImages = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_value);
        this.mPlaybackTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        this.mTitleName.setText(getTitleNameId());
        this.mFooterGuide.setData(getFooterGudeId());
        if (isStartFrame()) {
            this.mPosition.setText(AngleShiftSetting.getInstance().getStartPosString());
            this.mFlag.setImageResource(R.drawable.p_16_dd_parts_tm_start_square_button);
        } else {
            this.mPosition.setText(AngleShiftSetting.getInstance().getEndPosString());
            this.mFlag.setImageResource(R.drawable.p_16_dd_parts_tm_end_square_button);
        }
        this.mNumberOfImages.setText("" + AngleShiftSetting.getInstance().getTargetNumber());
        this.mPlaybackTime.setText(TLCommonUtil.getInstance().getTimeString(AngleShiftSetting.getInstance().getPlackBackDuration(AngleShiftSetting.getInstance().getStartPos(), AngleShiftSetting.getInstance().getEndPos()), this));
        this.mStartPosIndex = AngleShiftSetting.getInstance().getStartPosIndex();
        this.mEndPosIndex = AngleShiftSetting.getInstance().getEndPosIndex();
        if (isStartFrame()) {
            AngleShiftSetting.getInstance().setframeId(this.mStartPosIndex);
        } else {
            AngleShiftSetting.getInstance().setframeId(this.mEndPosIndex);
        }
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
        this.mTotalShots = AngleShiftSetting.getInstance().getTotalShots();
        this.mFrameLayout = (FrameLayout) this.mCurrentView.findViewById(R.id.frame_layout);
        this.mStartFlagLayout = (FrameLayout) this.mCurrentView.findViewById(R.id.start_flag);
        this.mEndFlagLayout = (FrameLayout) this.mCurrentView.findViewById(R.id.end_flag);
        this.mLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
        this.mStartFlag = new RelativeLayout.LayoutParams(-2, -2);
        this.mEndFlag = new RelativeLayout.LayoutParams(-2, -2);
        updateSeekPointers(this.mStartPosIndex, this.mEndPosIndex, this.mTotalShots);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        releaseImageViewDrawable(this.mFlag);
        this.mFrameLayout.setBackgroundResource(0);
        this.mFrameLayout = null;
        this.mStartFlagLayout.setBackgroundResource(0);
        this.mStartFlagLayout = null;
        this.mEndFlagLayout.setBackgroundResource(0);
        this.mEndFlagLayout = null;
        this.mTitleName = null;
        this.mFlag = null;
        this.mPosition = null;
        this.mNumberOfImages = null;
        this.mPlaybackTime = null;
        this.mFooterGuide = null;
        this.mLayoutParams = null;
        this.mStartFlag = null;
        this.mEndFlag = null;
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mCurrentView = null;
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
        System.gc();
        super.onPause();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void updateFrameNum() {
        if (isStartFrame()) {
            this.mPosition.setText(AngleShiftSetting.getInstance().getStartPosString(this.mStartPosIndex + 1));
        } else {
            this.mPosition.setText(AngleShiftSetting.getInstance().getEndPosString(this.mEndPosIndex + 1));
        }
        this.mNumberOfImages.setText("" + ((this.mEndPosIndex - this.mStartPosIndex) + 1));
        this.mPlaybackTime.setText(TLCommonUtil.getInstance().getTimeString(AngleShiftSetting.getInstance().getPlackBackDuration(this.mStartPosIndex, this.mEndPosIndex), this));
    }

    private void updateSeekPointers(int start, int end, int total) {
        int width = (int) (((((end - start) + 1) / total) * 549.0d) + 0.5d);
        int left = (int) (((start / total) * 549.0d) + 0.5d);
        this.mLayoutParams.leftMargin = left + 42;
        this.mLayoutParams.topMargin = POINTER_TOP;
        this.mLayoutParams.width = width;
        this.mLayoutParams.height = 12;
        this.mFrameLayout.setLayoutParams(this.mLayoutParams);
        this.mFrameLayout.setBackgroundResource(R.drawable.p_16_dd_parts_as_seek_center);
        this.mStartFlag.leftMargin = left + 42;
        this.mStartFlag.topMargin = 333;
        this.mStartFlag.width = 14;
        this.mStartFlag.height = 31;
        this.mStartFlagLayout.setLayoutParams(this.mStartFlag);
        this.mStartFlagLayout.setBackgroundResource(R.drawable.p_16_dd_parts_as_seek_start_position);
        this.mEndFlag.leftMargin = left + 42 + width;
        this.mEndFlag.topMargin = 333;
        this.mEndFlag.width = 14;
        this.mEndFlag.height = 31;
        this.mEndFlagLayout.setLayoutParams(this.mEndFlag);
        this.mEndFlagLayout.setBackgroundResource(R.drawable.p_16_dd_parts_as_seek_end_position);
    }

    private void updateSeekPointer(int start, int end, int total) {
        int width = (int) (((((end - start) + 1) / total) * 549.0d) + 0.5d);
        int left = (int) (((start / total) * 549.0d) + 0.5d);
        if (isStartFrame()) {
            this.mStartFlag.leftMargin = left + 42;
            if (this.mStartFlag.leftMargin > this.mEndFlag.leftMargin) {
                this.mStartFlag.leftMargin = this.mEndFlag.leftMargin;
            }
            this.mStartFlag.topMargin = 333;
            this.mStartFlag.width = 14;
            this.mStartFlag.height = 31;
            this.mStartFlagLayout.setLayoutParams(this.mStartFlag);
            this.mStartFlagLayout.setBackgroundResource(R.drawable.p_16_dd_parts_as_seek_start_position);
        } else {
            this.mEndFlag.leftMargin = left + 42 + width;
            this.mEndFlag.topMargin = 333;
            this.mEndFlag.width = 14;
            this.mEndFlag.height = 31;
            this.mEndFlagLayout.setLayoutParams(this.mEndFlag);
            this.mEndFlagLayout.setBackgroundResource(R.drawable.p_16_dd_parts_as_seek_end_position);
        }
        this.mLayoutParams.leftMargin = this.mStartFlag.leftMargin;
        this.mLayoutParams.topMargin = POINTER_TOP;
        if (this.mEndFlag.leftMargin - this.mStartFlag.leftMargin > 4) {
            this.mLayoutParams.width = (this.mEndFlag.leftMargin - this.mStartFlag.leftMargin) + 6;
        } else {
            this.mLayoutParams.width = 4;
        }
        this.mLayoutParams.height = 12;
        this.mFrameLayout.setLayoutParams(this.mLayoutParams);
        this.mFrameLayout.setBackgroundResource(R.drawable.p_16_dd_parts_as_seek_center);
    }

    private int getTitleNameId() {
        return isStartFrame() ? R.string.STRID_FUNC_TIMELAPSE_EFFECT_START_FRAME : R.string.STRID_FUNC_TIMELAPSE_EFFECT_END_FRAME;
    }

    private IFooterGuideData getFooterGudeId() {
        int themeId = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        return themeId != 4 ? new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_END_FRAME_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_END_FRAME_FOOTER_GUIDE_SK) : new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_END_FRAME2_CUSTOM_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_END_FRAME2_CUSTOM_FOOTER_GUIDE_SK);
    }

    private boolean isStartFrame() {
        return AngleShiftConstants.START_FRAMESETTING.equalsIgnoreCase(AngleShiftSetting.getInstance().getSelectedFrame());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        return "ID_ANGLESHIFTFRAMESETTINGLAYOUT";
    }

    private void moveStartFrameToPrev(int skipNum) {
        if (this.mStartPosIndex - skipNum >= 0) {
            this.mStartPosIndex -= skipNum;
        } else {
            this.mStartPosIndex = 0;
        }
        AngleShiftSetting.getInstance().setframeId(this.mStartPosIndex);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
        updateSeekPointer(this.mStartPosIndex, this.mEndPosIndex, this.mTotalShots);
        updateFrameNum();
    }

    private void moveEndFrameToPrev(int skipNum) {
        if (this.mEndPosIndex - skipNum > this.mStartPosIndex + 1) {
            this.mEndPosIndex -= skipNum;
        } else {
            this.mEndPosIndex = this.mStartPosIndex + 1;
        }
        AngleShiftSetting.getInstance().setframeId(this.mEndPosIndex);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
        updateSeekPointer(this.mStartPosIndex, this.mEndPosIndex, this.mTotalShots);
        updateFrameNum();
    }

    private void moveStartFrameToNext(int skipNum) {
        if (this.mStartPosIndex + skipNum < this.mEndPosIndex - 1) {
            this.mStartPosIndex += skipNum;
        } else {
            this.mStartPosIndex = this.mEndPosIndex - 1;
        }
        AngleShiftSetting.getInstance().setframeId(this.mStartPosIndex);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
        updateSeekPointer(this.mStartPosIndex, this.mEndPosIndex, this.mTotalShots);
        updateFrameNum();
    }

    private void moveEndFrameToNext(int skipNum) {
        if (this.mEndPosIndex + skipNum < this.mTotalShots) {
            this.mEndPosIndex += skipNum;
        } else {
            this.mEndPosIndex = this.mTotalShots - 1;
        }
        AngleShiftSetting.getInstance().setframeId(this.mEndPosIndex);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
        updateSeekPointer(this.mStartPosIndex, this.mEndPosIndex, this.mTotalShots);
        updateFrameNum();
    }

    private void moveFrameToPrev(int skipNum) {
        if (this.mTotalShots > 2) {
            if (isStartFrame()) {
                moveStartFrameToPrev(skipNum);
            } else {
                moveEndFrameToPrev(skipNum);
            }
        }
    }

    private void moveFrameToNext(int skipNum) {
        if (this.mTotalShots > 2) {
            if (isStartFrame()) {
                moveStartFrameToNext(skipNum);
            } else {
                moveEndFrameToNext(skipNum);
            }
        }
    }

    private void moveFrameToPrev() {
        moveFrameToPrev(1);
    }

    private void moveFrameToNext() {
        moveFrameToNext(1);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        moveFrameToNext(10);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        moveFrameToPrev(10);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (AngleShiftSetting.getInstance().needCancelRotation()) {
            AngleShiftSetting.getInstance().cancelRotation();
        }
        AngleShiftSetting.getInstance().fixFramePosition();
        AngleShiftSetting.getInstance().setframeId(AngleShiftSetting.getInstance().getStartPosIndex());
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AngleShiftSetting.getInstance().setStartPosIndex(this.mStartPosIndex);
        AngleShiftSetting.getInstance().setEndPosIndex(this.mEndPosIndex);
        AngleShiftSetting.getInstance().setTempFramePosition(this.mStartPosIndex, this.mEndPosIndex);
        AngleShiftSetting.getInstance().setCancelRotationDegree(null);
        AngleShiftSetting.getInstance().setframeId(this.mStartPosIndex);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT);
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            int result = super.onConvertedKeyDown(event, func);
            return result;
        }
        switch (event.getScanCode()) {
            case 103:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                int result2 = super.onConvertedKeyDown(event, func);
                return result2;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
            if (4 == theme && AngleShiftSetting.getInstance().isValidOptimizedImage()) {
                openFrameCropping();
                return 1;
            }
            return -1;
        }
        if (event.getScanCode() == 103) {
            return -1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    private void openFrameCropping() {
        AngleShiftSetting.getInstance().setStartPosIndex(this.mStartPosIndex);
        AngleShiftSetting.getInstance().setEndPosIndex(this.mEndPosIndex);
        if (isStartFrame()) {
            AngleShiftSetting.getInstance().setframeId(this.mStartPosIndex);
        } else {
            AngleShiftSetting.getInstance().setframeId(this.mEndPosIndex);
        }
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CROP_LAYOUT);
        doOnKeyProcessing("AngleShiftFrameCropping");
    }

    protected void doOnKeyProcessing(String itemid) {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            this.mService.execCurrentMenuItem(itemid);
            if (nextFragmentID != null) {
                PTag.start("Menu open next MenuLayout");
                openNextMenu(itemid, nextFragmentID);
            } else {
                AppLog.error("Error", "Can't open the next MenuLayout");
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        moveFrameToPrev();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        moveFrameToNext();
        return 1;
    }
}
