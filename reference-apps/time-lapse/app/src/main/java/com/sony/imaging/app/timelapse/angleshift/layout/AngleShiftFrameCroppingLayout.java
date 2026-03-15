package com.sony.imaging.app.timelapse.angleshift.layout;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;

/* loaded from: classes.dex */
public class AngleShiftFrameCroppingLayout extends DisplayMenuItemsMenuLayout implements IModableLayout {
    private static final int TOGGLE_CENTERING = 2;
    private static final int TOGGLE_OFF = 0;
    private static final int TOGGLE_ORIGIN = 1;
    private static final int TOGGLE_OTHERS = 3;
    private OnLayoutModeChangeListener mLayoutChangeListener;
    private static int PIXEL8 = 8;
    private static int PIXEL2 = 2;
    private View mCurrentView = null;
    private TextView mTitleName = null;
    private TextView mToggleText = null;
    private FooterGuide mFooterGuide = null;
    private boolean mConfirmImageflag = false;
    private ImageView mCroppedStartFrame = null;
    private ImageView mCroppedEndFrame = null;
    Rect mStartRect = null;
    Rect mEndRect = null;
    Rect mOrigStartRect = null;
    Rect mOrigEndRect = null;
    private boolean isKeyDown = false;
    private Rect mOriginalRect = null;
    private int mToggleStatus = 0;
    Rect mToggleRect = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.as_frame_crop);
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        this.mConfirmImageflag = false;
        if (isStartFrame()) {
            this.mCroppedStartFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_upper);
            this.mCroppedStartFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_start_crop_frame_selected);
            if (AngleShiftSetting.getInstance().getTargetNumber() < 2) {
                this.mCroppedEndFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_lower);
                this.mCroppedEndFrame.setBackgroundResource(0);
            } else {
                this.mCroppedEndFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_lower);
                this.mCroppedEndFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_end_crop_frame_disable);
            }
        } else if (AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            this.mCroppedStartFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_lower);
            this.mCroppedStartFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_end_crop_frame_selected);
            this.mCroppedEndFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_upper);
            this.mCroppedEndFrame.setBackgroundResource(0);
        } else {
            this.mCroppedStartFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_lower);
            this.mCroppedStartFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_start_crop_frame_disable);
            this.mCroppedEndFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_upper);
            this.mCroppedEndFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_end_crop_frame_selected);
        }
        this.mCroppedStartFrame.setVisibility(0);
        this.mCroppedEndFrame.setVisibility(0);
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(false);
        this.mStartRect = AngleShiftSetting.getInstance().getAngleShiftStartRect();
        this.mEndRect = AngleShiftSetting.getInstance().getAngleShiftEndRect();
        updateCroppedFrame(this.mStartRect, this.mEndRect);
        this.mToggleText = (TextView) this.mCurrentView.findViewById(R.id.crop_toggle);
        this.mToggleText.setVisibility(4);
        this.isKeyDown = false;
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mTitleName = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        this.mTitleName.setText(getTitleNameId());
        this.mFooterGuide.setData(getCropFooterGudeId());
        this.mLayoutChangeListener = new OnLayoutModeChangeListener(this, 1);
        DisplayModeObserver.getInstance().setNotificationListener(this.mLayoutChangeListener);
        this.mToggleStatus = 0;
        this.mToggleText.setVisibility(4);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mCroppedStartFrame.setBackgroundResource(0);
        this.mCroppedEndFrame.setBackgroundResource(0);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mLayoutChangeListener);
        this.mLayoutChangeListener = null;
        int runStatus = RunStatus.getStatus();
        if (2 == runStatus) {
            AngleShiftSetting.getInstance().setAngleShiftCustomDefaultRect(false);
            AngleShiftSetting.getInstance().backupCustomSettingsAgain();
        }
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(false);
        this.mToggleText.setVisibility(4);
        this.mTitleName = null;
        this.mToggleText = null;
        this.mFooterGuide = null;
        this.mCroppedStartFrame = null;
        this.mCroppedEndFrame = null;
        this.mStartRect = null;
        this.mEndRect = null;
        this.mOrigStartRect = null;
        this.mOrigEndRect = null;
        this.mOriginalRect = null;
        this.mToggleRect = null;
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mCurrentView = null;
        System.gc();
        super.onPause();
    }

    private int getTitleNameId() {
        return !this.mConfirmImageflag ? R.string.STRID_FUNC_TIMELAPSE_EFFECT_CROP_TITLE : R.string.STRID_FUNC_TIMELAPSE_EFFECT_PREVIE;
    }

    private IFooterGuideData getCropFooterGudeId() {
        return new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_CROP_ADJUST_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_CROP_ADJUST_FOOTER_GUIDE_SK);
    }

    private IFooterGuideData getCropConfirmFooterGudeId() {
        return new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_ENTER_RETURN, R.string.STRID_FOOTERGUIDE_ENTER_RETURN_SK);
    }

    private boolean isStartFrame() {
        return AngleShiftConstants.START_FRAMESETTING.equalsIgnoreCase(AngleShiftSetting.getInstance().getSelectedFrame());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        return "ID_ANGLESHIFTFRAMECROPPINGLAYOUT";
    }

    private void handleMenuKey() {
        if (this.mConfirmImageflag) {
            this.mConfirmImageflag = false;
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SHOW_DIRECT);
            this.mCroppedStartFrame.setVisibility(0);
            this.mCroppedEndFrame.setVisibility(0);
            updateTitleText();
            updateFooterGuide(this.mConfirmImageflag);
            return;
        }
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_FRAMESETTING_LAYOUT);
        AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(this.mStartRect);
        AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(this.mEndRect);
        openPreviousMenu();
    }

    private void handleCenterKey() {
        if (this.mConfirmImageflag) {
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT);
            closeLayout();
            AngleShiftSetting.getInstance().setframeId(AngleShiftSetting.getInstance().getStartPosIndex());
        } else {
            this.mConfirmImageflag = true;
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_SCALE_CROPPED_IMAGE);
            this.mCroppedStartFrame.setVisibility(4);
            this.mCroppedEndFrame.setVisibility(4);
            if (AngleShiftSetting.getInstance().isAngleShiftCustomAdjustRect() || this.mToggleStatus != 0) {
                AngleShiftSetting.getInstance().setAngleShiftCustomDefaultRect(false);
                this.mStartRect = AngleShiftSetting.getInstance().getAngleShiftCustomStartRect();
                this.mEndRect = AngleShiftSetting.getInstance().getAngleShiftCustomEndRect();
                AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(false);
                this.mToggleText.setVisibility(4);
            }
            updateTitleText();
            updateFooterGuide(this.mConfirmImageflag);
        }
        AngleShiftSetting.getInstance().setCancelRotationDegree(null);
        AngleShiftSetting.getInstance().setTempFramePosition(AngleShiftSetting.getInstance().getStartPosIndex(), AngleShiftSetting.getInstance().getEndPosIndex());
        this.mToggleStatus = 0;
        AngleShiftSetting.getInstance().backupCustomSettings();
    }

    private void updateTitleText() {
        this.mTitleName.setText(getTitleNameId());
    }

    private void updateFooterGuide(boolean confirmImage) {
        if (confirmImage) {
            this.mFooterGuide.setData(getCropConfirmFooterGudeId());
        } else {
            this.mFooterGuide.setData(getCropFooterGudeId());
        }
    }

    private void updateCroppedFrame(Rect startRect, Rect endRect) {
        RelativeLayout.LayoutParams paramsStart = new RelativeLayout.LayoutParams(-2, -2);
        RelativeLayout.LayoutParams paramsEnd = new RelativeLayout.LayoutParams(-2, -2);
        int width = AngleShiftSetting.getInstance().getAngleShiftImageWidth();
        int height = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        int dispHeight = AngleShiftSetting.getInstance().getSingleYUVDisplayHeight();
        Rect scaledRect = AngleShiftSetting.getInstance().getScaledCroppedRect(width, height, 640, dispHeight, startRect);
        paramsStart.height = scaledRect.height();
        paramsStart.width = scaledRect.width();
        paramsStart.leftMargin = scaledRect.left;
        paramsStart.topMargin = scaledRect.top;
        this.mCroppedStartFrame.setLayoutParams(paramsStart);
        Rect scaledRect2 = AngleShiftSetting.getInstance().getScaledCroppedRect(width, height, 640, dispHeight, endRect);
        paramsEnd.height = scaledRect2.height();
        paramsEnd.width = scaledRect2.width();
        paramsEnd.leftMargin = scaledRect2.left;
        paramsEnd.topMargin = scaledRect2.top;
        this.mCroppedEndFrame.setLayoutParams(paramsEnd);
    }

    private int getPixelsToMove(boolean isMuch) {
        int height = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        int pixels = PIXEL2;
        if (isMuch) {
            pixels = PIXEL8;
        }
        double dispHeight = AngleShiftSetting.getInstance().getSingleYUVDisplayHeight();
        double scaledPixels = ((pixels * height) / dispHeight) + 0.5d;
        int actualPixels = ((int) (scaledPixels / 4.0d)) * 4;
        AppLog.info("getPixelsToMove", "isMuch:" + isMuch);
        AppLog.info("getPixelsToMove", "height:" + height);
        AppLog.info("getPixelsToMove", "dispHeight:" + dispHeight);
        AppLog.info("getPixelsToMove", "scaledPixels:" + scaledPixels);
        AppLog.info("getPixelsToMove", "actualPixels:" + actualPixels);
        return actualPixels;
    }

    private Rect moveToLeftActualRect(Rect targetRect, int pixel) {
        Rect rect = new Rect(targetRect);
        AppLog.info("moveToLeftActualRect", "targetRect:" + targetRect + ", pixel: " + pixel);
        if (targetRect.left >= pixel) {
            rect.left = targetRect.left - pixel;
        } else {
            rect.left = 0;
        }
        rect.right = rect.left + targetRect.width();
        AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), targetRect, rect);
        return rect;
    }

    private Rect moveToRightActualRect(Rect targetRect, int pixel, int imageWidth) {
        Rect rect = new Rect(targetRect);
        AppLog.info("moveToRightActualRect", "targetRect:" + targetRect + ", pixel: " + pixel);
        if (targetRect.right + pixel <= imageWidth) {
            rect.right = targetRect.right + pixel;
        } else {
            rect.right = imageWidth;
        }
        rect.left = rect.right - targetRect.width();
        AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), targetRect, rect);
        return rect;
    }

    private Rect moveToTopActualRect(Rect targetRect, int pixel) {
        AppLog.info("moveToTopActualRect", "targetRect:" + targetRect + ", pixel: " + pixel);
        Rect rect = new Rect(targetRect);
        if (targetRect.top >= pixel) {
            rect.top = targetRect.top - pixel;
        } else {
            rect.top = 0;
        }
        rect.bottom = rect.top + targetRect.height();
        AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), targetRect, rect);
        return rect;
    }

    private Rect moveToBottomActualRect(Rect targetRect, int pixel, int imageHeight) {
        Rect rect = new Rect(targetRect);
        AppLog.info("moveToBottomActualRect", "targetRect:" + targetRect + ", pixel: " + pixel);
        if (targetRect.bottom + pixel <= imageHeight) {
            rect.bottom = targetRect.bottom + pixel;
        } else {
            rect.bottom = imageHeight;
        }
        rect.top = rect.bottom - targetRect.height();
        AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), targetRect, rect);
        return rect;
    }

    private Rect increaseActualRect(Rect targetRect, int pixels, int imageWidth, int imageHeight) {
        Rect rect = new Rect(targetRect);
        int adjustPixel = pixels;
        if (targetRect.width() + (pixels * 2) > imageWidth) {
            int adjustPixel2 = ((imageWidth - targetRect.width()) / 2) / 4;
            adjustPixel = adjustPixel2 * 4;
        }
        if (adjustPixel > 0) {
            rect.left -= adjustPixel;
            rect.right += adjustPixel;
            if (rect.left < 0) {
                rect.right -= rect.left;
                rect.left = 0;
            }
            if (rect.right > imageWidth) {
                rect.left -= rect.right - imageWidth;
                if (rect.left < 0) {
                    rect.left = 0;
                }
                rect.right = imageWidth;
            }
            int height = (rect.width() * 9) / 16;
            int adjustPixel3 = (height - targetRect.height()) / 2;
            rect.top -= adjustPixel3;
            rect.bottom += adjustPixel3;
            if (rect.top < 0) {
                rect.bottom -= rect.top;
                rect.top = 0;
            }
            if (rect.bottom > imageHeight) {
                rect.top -= rect.bottom - imageHeight;
                if (rect.top < 0) {
                    rect.top = 0;
                }
                rect.bottom = imageHeight;
            }
        }
        AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), targetRect, rect);
        return rect;
    }

    private Rect decreaseActualRect(Rect targetRect, int pixels) {
        int imageWidth = AngleShiftSetting.getInstance().getMinCropFrameWidth();
        Rect rect = new Rect(targetRect);
        int adjustPixel = pixels;
        if (targetRect.width() - (pixels * 2) < imageWidth) {
            adjustPixel = (targetRect.width() - imageWidth) / 2;
        }
        if (adjustPixel > 0) {
            rect.left += adjustPixel;
            rect.right -= adjustPixel;
            int height = (rect.width() * 9) / 16;
            int adjustPixel2 = (targetRect.height() - height) / 2;
            rect.top += adjustPixel2;
            rect.bottom -= adjustPixel2;
        }
        AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), targetRect, rect);
        return rect;
    }

    private void moveToLeft(boolean isMuch) {
        int actualPixels = getPixelsToMove(isMuch);
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(true);
        if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            Rect rect = moveToLeftActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), actualPixels);
            updateCroppedFrame(rect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
            AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(rect);
        } else {
            Rect rect2 = moveToLeftActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomEndRect(), actualPixels);
            updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), rect2);
            AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(rect2);
        }
    }

    private void moveToRight(boolean isMuch) {
        int width = AngleShiftSetting.getInstance().getAngleShiftImageWidth();
        int actualPixels = getPixelsToMove(isMuch);
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(true);
        if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            Rect rect = moveToRightActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), actualPixels, width);
            updateCroppedFrame(rect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
            AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(rect);
        } else {
            Rect rect2 = moveToRightActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomEndRect(), actualPixels, width);
            updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), rect2);
            AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(rect2);
        }
    }

    private void moveToTop(boolean isMuch) {
        int actualPixels = getPixelsToMove(isMuch);
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(true);
        if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            Rect rect = moveToTopActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), actualPixels);
            updateCroppedFrame(rect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
            AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(rect);
        } else {
            Rect rect2 = moveToTopActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomEndRect(), actualPixels);
            updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), rect2);
            AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(rect2);
        }
    }

    private void moveToBottom(boolean isMuch) {
        int height = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        int actualPixels = getPixelsToMove(isMuch);
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(true);
        if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            Rect rect = moveToBottomActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), actualPixels, height);
            updateCroppedFrame(rect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
            AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(rect);
        } else {
            Rect rect2 = moveToBottomActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomEndRect(), actualPixels, height);
            updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), rect2);
            AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(rect2);
        }
    }

    private void increaseRect(boolean isMuch) {
        int actualPixels = getPixelsToMove(isMuch);
        int width = AngleShiftSetting.getInstance().getAngleShiftImageWidth();
        int height = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(true);
        if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            Rect rect = increaseActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), actualPixels, width, height);
            updateCroppedFrame(rect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
            AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(rect);
        } else {
            Rect rect2 = increaseActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomEndRect(), actualPixels, width, height);
            updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), rect2);
            AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(rect2);
        }
    }

    private void decreaseRect(boolean isMuch) {
        int actualPixels = getPixelsToMove(isMuch);
        AngleShiftSetting.getInstance().setAngleShiftCustomAdjustRect(true);
        if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
            Rect rect = decreaseActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), actualPixels);
            updateCroppedFrame(rect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
            AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(rect);
        } else {
            Rect rect2 = decreaseActualRect(AngleShiftSetting.getInstance().getAngleShiftCustomEndRect(), actualPixels);
            updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), rect2);
            AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(rect2);
        }
    }

    private void toggleCroppedFrame() {
        if (!this.mConfirmImageflag) {
            switch (this.mToggleStatus) {
                case 0:
                    this.mToggleStatus = 2;
                    if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
                        this.mOriginalRect = AngleShiftSetting.getInstance().getAngleShiftCustomStartRect();
                    } else {
                        this.mOriginalRect = AngleShiftSetting.getInstance().getAngleShiftCustomEndRect();
                    }
                    this.mToggleRect = getCenteringRect(this.mOriginalRect);
                    break;
                case 1:
                    this.mToggleStatus = 2;
                    this.mToggleRect = getCenteringRect(this.mOriginalRect);
                    break;
                case 2:
                    if (AngleShiftSetting.getInstance().getTargetNumber() < 2) {
                        this.mToggleStatus = 1;
                        this.mToggleRect = this.mOriginalRect;
                        break;
                    } else if (isStartFrame()) {
                        this.mToggleStatus = 3;
                        this.mToggleRect = AngleShiftSetting.getInstance().getAngleShiftCustomEndRect();
                        break;
                    } else {
                        this.mToggleStatus = 3;
                        this.mToggleRect = AngleShiftSetting.getInstance().getAngleShiftCustomStartRect();
                        break;
                    }
                case 3:
                    this.mToggleStatus = 1;
                    this.mToggleRect = this.mOriginalRect;
                    break;
            }
            if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
                updateCroppedFrame(this.mToggleRect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
                AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(this.mToggleRect);
            } else {
                updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), this.mToggleRect);
                AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(this.mToggleRect);
            }
            this.mToggleText.setText(getToggleTextId());
            this.mToggleText.setVisibility(0);
            AngleShiftSetting.getInstance().printRect(AppLog.getClassName(), AppLog.getMethodName(), this.mOriginalRect, this.mToggleRect);
        }
    }

    private int getToggleTextId() {
        switch (this.mToggleStatus) {
            case 1:
                return R.string.STRID_AMC_STR_01036;
            case 2:
                return R.string.STRID_FUNC_TIMELAPSE_EFFECT_SWITCH_CENTERING_FRAME;
            case 3:
                if (isStartFrame()) {
                    return R.string.STRID_FUNC_TIMELAPSE_EFFECT_END_FRAME;
                }
                return R.string.STRID_FUNC_TIMELAPSE_EFFECT_START_FRAME;
            default:
                return R.string.STRID_AMC_STR_01036;
        }
    }

    private Rect getCenteringRect(Rect rect) {
        Rect centeringRect = new Rect(rect);
        int width = AngleShiftSetting.getInstance().getAngleShiftImageWidth();
        int height = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        int right = ((width - rect.width()) / 2) / 4;
        centeringRect.left = right * 4;
        centeringRect.right = centeringRect.left + rect.width();
        int top = ((height - rect.height()) / 2) / 2;
        centeringRect.top = top * 2;
        centeringRect.bottom = centeringRect.top + rect.height();
        return centeringRect;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        handleCenterKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mToggleStatus = 0;
        handleMenuKey();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        AngleShiftSetting.getInstance().setAngleShiftCustomDefaultRect(false);
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int result;
        int keyCode2 = event.getScanCode();
        if (!this.mConfirmImageflag) {
            this.mToggleText.setVisibility(4);
            switch (keyCode2) {
                case 103:
                    this.mToggleStatus = 0;
                    moveToTop(this.isKeyDown);
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    this.mToggleStatus = 0;
                    moveToLeft(this.isKeyDown);
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    this.mToggleStatus = 0;
                    moveToRight(this.isKeyDown);
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    this.mToggleStatus = 0;
                    moveToBottom(this.isKeyDown);
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    toggleCroppedFrame();
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    this.mToggleStatus = 0;
                    increaseRect(this.isKeyDown);
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    this.mToggleStatus = 0;
                    decreaseRect(this.isKeyDown);
                    result = 1;
                    break;
                default:
                    result = super.onKeyDown(keyCode2, event);
                    break;
            }
        } else {
            result = super.onKeyDown(keyCode2, event);
        }
        this.isKeyDown = true;
        return result;
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        if (this.mToggleStatus != 0) {
            if (isStartFrame() || AngleShiftSetting.getInstance().getTargetNumber() < 2) {
                updateCroppedFrame(this.mToggleRect, AngleShiftSetting.getInstance().getAngleShiftEndRect());
                AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(this.mToggleRect);
                return;
            } else {
                updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftStartRect(), this.mToggleRect);
                AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(this.mToggleRect);
                return;
            }
        }
        updateCroppedFrame(AngleShiftSetting.getInstance().getAngleShiftCustomStartRect(), AngleShiftSetting.getInstance().getAngleShiftCustomEndRect());
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
        int result;
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                result = -1;
                break;
            default:
                result = 0;
                break;
        }
        this.isKeyDown = false;
        return result;
    }
}
