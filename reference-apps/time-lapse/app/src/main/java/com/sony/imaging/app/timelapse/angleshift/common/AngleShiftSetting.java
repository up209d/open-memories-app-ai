package com.sony.imaging.app.timelapse.angleshift.common;

import android.graphics.Rect;
import android.widget.ImageView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFaderController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFrameRateController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftMovieController;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class AngleShiftSetting {
    private static final String TAG = AngleShiftSetting.class.getSimpleName();
    private static int mTotalShots = 0;
    private static int mStartPos = 0;
    private static int mEndPos = 0;
    private static AngleShiftSetting mInstance = null;
    private static int FRAME_LINE_WEIGHT = 4;
    private static String INVALID_RECTANGLE = "-1";
    private int mFrameId = 0;
    private String mAngleShiftSelectedFrame = null;
    private int mFirstImageWidth = 0;
    private int mFirstImageHeight = 0;
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private int mImageOrigWidth = 0;
    private int mImageOrigHeight = 0;
    private Rect mStartRect = null;
    private Rect mEndRect = null;
    private int mMinCropFrameWidth = 1920;
    private int mMinCropFrameHeight = 1080;
    private float mPreRCRatio = 1.0f;
    private Rect mPanStartRect = null;
    private Rect mPanEndRect = null;
    private Rect mTiltStartRect = null;
    private Rect mTiltEndRect = null;
    private Rect mZoomStartRect = null;
    private Rect mZoomEndRect = null;
    private Rect mNoEffectStartRect = null;
    private Rect mNoEffectEndRect = null;
    private Rect mCustomDefaultStartRect = null;
    private Rect mCustomDefaultEndRect = null;
    private Rect mCustomStartRect = null;
    private Rect mCustomEndRect = null;
    private boolean mAngleShiftCustomDefaultRect = false;
    private boolean mAngleShiftCustomAdjustRect = false;
    private RotateImageFilter.ROTATION_DEGREE mRotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
    private int mImageAspect = 3;
    private boolean mIsValidImage = true;
    private RotateImageFilter.ROTATION_DEGREE mOrigDegree = null;
    private int mTempStartPosIndex = 0;
    private int mTempEndPosIndex = 0;
    private int mInputWidth = 0;
    private int mInputHeight = 0;

    public static AngleShiftSetting getInstance() {
        if (mInstance == null) {
            mInstance = new AngleShiftSetting();
        }
        return mInstance;
    }

    public void setDefaultValue() {
        mTotalShots = PlayBackController.getInstance().getShootingnum();
        mStartPos = 0;
        mEndPos = mTotalShots - 1;
    }

    public void setStartPosIndex(int startPos) {
        mStartPos = startPos;
    }

    public void setEndPosIndex(int endPos) {
        mEndPos = endPos;
    }

    public int getTotalShots() {
        return mTotalShots;
    }

    public int getTargetNumber() {
        return (mEndPos - mStartPos) + 1;
    }

    public int getStartPos() {
        return mStartPos + 1;
    }

    public int getStartPosIndex() {
        return mStartPos;
    }

    public String getStartPosString(int pos) {
        String startPos = "" + pos + "/" + getTotalShots();
        return startPos;
    }

    public String getStartPosString() {
        return getStartPosString(getStartPos());
    }

    public int getEndPos() {
        return mEndPos + 1;
    }

    public int getEndPosIndex() {
        return mEndPos;
    }

    public String getEndPosString(int pos) {
        String endPos = "" + pos + "/" + getTotalShots();
        return endPos;
    }

    public String getEndPosString() {
        return getEndPosString(getEndPos());
    }

    public int getPlackBackDuration(int startPos, int endPpos) {
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            int playTime = ((endPpos - startPos) + 1) / 24;
            return playTime;
        }
        if (!"framerate-30p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            return 0;
        }
        int playTime2 = ((endPpos - startPos) + 1) / 30;
        return playTime2;
    }

    public int getPlackBackDuration() {
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            int playTime = ((mEndPos - mStartPos) + 1) / 24;
            return playTime;
        }
        if (!"framerate-30p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            return 0;
        }
        int playTime2 = ((mEndPos - mStartPos) + 1) / 30;
        return playTime2;
    }

    public void setframeId(int frameId) {
        this.mFrameId = frameId;
    }

    public int getframeId() {
        return this.mFrameId;
    }

    public void setSelectedFrame(String startEnd) {
        this.mAngleShiftSelectedFrame = startEnd;
    }

    public String getSelectedFrame() {
        return this.mAngleShiftSelectedFrame;
    }

    public void updateFaderIcon(ImageView fader) {
        if (AngleShiftFaderController.FADE_ON.equalsIgnoreCase(AngleShiftFaderController.getInstance().getValue())) {
            fader.setImageResource(R.drawable.p_16_dd_parts_tm_fade_on_normal);
        } else {
            fader.setImageResource(R.drawable.p_16_dd_parts_tm_fade_off_normal);
        }
    }

    public void updateMovieSizeIcon(ImageView movieSize) {
        if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            movieSize.setImageResource(R.drawable.p_16_dd_parts_tm_1920x1080_normal);
        } else {
            movieSize.setImageResource(R.drawable.p_16_dd_parts_tm_1280x720_normal);
        }
    }

    public void updateRecModeIcon(ImageView recMode) {
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            recMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else {
            recMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
    }

    public void setAngleShiftFirstImageWidth(int width) {
        this.mFirstImageWidth = width;
    }

    public void setAngleShiftFirstImageHeight(int height) {
        this.mFirstImageHeight = height;
    }

    public int getAngleShiftFirstImageWidth() {
        return this.mFirstImageWidth;
    }

    public int getAngleShiftFirstImageHeight() {
        return this.mFirstImageHeight;
    }

    public int getAngleShiftImageWidth() {
        return this.mImageWidth;
    }

    public int getAngleShiftImageHeight() {
        return this.mImageHeight;
    }

    public int getAngleShiftImageOriginalWidth() {
        return this.mImageOrigWidth;
    }

    public int getAngleShiftImageOriginalHeight() {
        return this.mImageOrigHeight;
    }

    public void setAngleShitCropLimitationAndRatio(int width, int height) {
        float preRCRatio_1280_720 = 1.0f;
        float preRCRatio_1920_1080 = 1.0f;
        this.mPreRCRatio = 1.0f;
        this.mMinCropFrameWidth = 1920;
        this.mMinCropFrameHeight = 1080;
        if (3264 < width) {
            preRCRatio_1280_720 = 3264.0f / width;
        }
        if (4912 < width) {
            preRCRatio_1920_1080 = 4912.0f / width;
        }
        if (AngleShiftMovieController.MOVIE_1280x720.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            this.mPreRCRatio = preRCRatio_1280_720;
            if (3264 < width) {
                this.mMinCropFrameWidth = 1280;
                this.mMinCropFrameHeight = 720;
                return;
            }
            return;
        }
        this.mPreRCRatio = preRCRatio_1920_1080;
    }

    public int aligned4pixel(int pixels) {
        int pixel4 = pixels / 4;
        return pixel4 * 4;
    }

    public boolean isPreRC() {
        return this.mPreRCRatio != 1.0f;
    }

    public int getMinCropFrameWidth() {
        return this.mMinCropFrameWidth;
    }

    public int getMinCropFrameHeight() {
        return this.mMinCropFrameHeight;
    }

    public float getPreRCRatio() {
        return this.mPreRCRatio;
    }

    public void setAngleShiftRect(int inWidth, int inHeight) {
        int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        setAngleShitCropLimitationAndRatio(inWidth, inHeight);
        this.mImageOrigWidth = inWidth;
        this.mImageOrigHeight = inHeight;
        int imageWidth = (int) (inWidth * getPreRCRatio());
        int imageHeight = (int) (inHeight * getPreRCRatio());
        int imageWidth2 = aligned4pixel(imageWidth);
        int imageHeight2 = aligned4pixel(imageHeight);
        this.mImageWidth = imageWidth2;
        this.mImageHeight = imageHeight2;
        if (4 == theme) {
            setAngleShiftCustomDefaultRect(imageWidth2, imageHeight2);
            this.mStartRect = getAngleShiftCustomStartRect();
            this.mEndRect = getAngleShiftCustomEndRect();
        } else {
            releaseAngleShiftCustomRect();
            if (theme == 0) {
                setAngleShiftPanRect(imageWidth2, imageHeight2);
                this.mStartRect = getAngleShiftPanStartRect();
                this.mEndRect = getAngleShiftPanEndRect();
            } else if (1 == theme) {
                setAngleShiftTiltRect(imageWidth2, imageHeight2);
                this.mStartRect = getAngleShiftTiltStartRect();
                this.mEndRect = getAngleShiftTiltEndRect();
            } else if (2 == theme) {
                setAngleShiftZoomRect(imageWidth2, imageHeight2);
                this.mStartRect = getAngleShiftZoomStartRect();
                this.mEndRect = getAngleShiftZoomEndRect();
            } else if (3 == theme) {
                setAngleShiftNoEffectRect(imageWidth2, imageHeight2);
                this.mStartRect = getAngleShiftNoEffectStartRect();
                this.mEndRect = getAngleShiftNoEffectEndRect();
            }
        }
        AppLog.info(TAG, "setAngleShiftRect(theme): " + theme);
        AppLog.info(TAG, "setAngleShiftRect(mStartRect): " + this.mStartRect);
        AppLog.info(TAG, "setAngleShiftRect(mEndRect): " + this.mEndRect);
    }

    public Rect getAngleShiftStartRect() {
        AppLog.info(TAG, "getAngleShiftStartRect : " + this.mStartRect);
        return this.mStartRect;
    }

    public Rect getAngleShiftEndRect() {
        if (getTargetNumber() < 2) {
            return this.mStartRect;
        }
        AppLog.info(TAG, "getAngleShiftEndRect : " + this.mEndRect);
        return this.mEndRect;
    }

    public void setAngleShiftPanRect(int imageWidth, int imageHeight) {
        int width = (((imageWidth * 5) / 6) / 4) * 4;
        if (width < this.mMinCropFrameWidth) {
            width = this.mMinCropFrameWidth;
        }
        int height = (((width * 9) / 16) / 2) * 2;
        if (height < this.mMinCropFrameHeight) {
            height = this.mMinCropFrameHeight;
        }
        int top = (((imageHeight - height) / 2) / 2) * 2;
        this.mPanStartRect = new Rect(0, top, width, top + height);
        this.mPanEndRect = new Rect(imageWidth - width, top, imageWidth, top + height);
    }

    public void setAngleShiftTiltRect(int imageWidth, int imageHeight) {
        int width;
        int height;
        if (imageHeight < imageWidth) {
            width = (((imageWidth * 2) / 3) / 4) * 4;
            if (width < this.mMinCropFrameWidth) {
                width = this.mMinCropFrameWidth;
            }
            int height2 = (width * 9) / 16;
            height = (height2 / 2) * 2;
            if (height < this.mMinCropFrameHeight) {
                height = this.mMinCropFrameHeight;
            }
        } else {
            width = imageWidth;
            int height3 = (width * 9) / 16;
            height = (height3 / 2) * 2;
        }
        int left = (((imageWidth - width) / 2) / 4) * 4;
        this.mTiltStartRect = new Rect(left, imageHeight - height, left + width, imageHeight);
        this.mTiltEndRect = new Rect(left, 0, left + width, height);
    }

    public void setAngleShiftZoomRect(int imageWidth, int imageHeight) {
        int height = (((imageWidth * 9) / 16) / 2) * 2;
        int top = (((imageHeight - height) / 2) / 2) * 2;
        this.mZoomEndRect = new Rect(0, top, imageWidth, top + height);
        int width = (((imageWidth * 2) / 3) / 4) * 4;
        if (width < this.mMinCropFrameWidth) {
            width = this.mMinCropFrameWidth;
        }
        int height2 = (((width * 9) / 16) / 2) * 2;
        if (height2 < this.mMinCropFrameHeight) {
            height2 = this.mMinCropFrameHeight;
        }
        int top2 = (((imageHeight - height2) / 2) / 2) * 2;
        int left = (((imageWidth - width) / 2) / 4) * 4;
        this.mZoomStartRect = new Rect(left, top2, left + width, top2 + height2);
    }

    public void setAngleShiftNoEffectRect(int imageWidth, int imageHeight) {
        int width = (imageWidth / 4) * 4;
        int height = (((imageWidth * 9) / 16) / 2) * 2;
        int top = (((imageHeight - height) / 2) / 2) * 2;
        this.mNoEffectStartRect = new Rect(0, top, width, top + height);
        this.mNoEffectEndRect = new Rect(0, top, width, top + height);
    }

    public void setAngleShiftCustomDefaultRect(int imageWidth, int imageHeight) {
        this.mCustomDefaultStartRect = new Rect(0, imageHeight - this.mMinCropFrameHeight, this.mMinCropFrameWidth, imageHeight);
        this.mCustomDefaultEndRect = new Rect(imageWidth - this.mMinCropFrameWidth, 0, imageWidth, this.mMinCropFrameHeight);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(imageWidth): " + imageWidth);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(imageHeight): " + imageHeight);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(mMinCropFrameWidth): " + this.mMinCropFrameWidth);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(mMinCropFrameHeight): " + this.mMinCropFrameHeight);
        if (this.mCustomStartRect == null || this.mCustomEndRect == null) {
            resetAngleShiftCustomRect();
        }
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(mCustomDefaultStartRect): " + this.mCustomDefaultStartRect);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(mCustomDefaultEndRect): " + this.mCustomDefaultEndRect);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(mCustomStartRect): " + this.mCustomStartRect);
        AppLog.info(TAG, "setAngleShiftCustomDefaultRect(mCustomEndRect): " + this.mCustomEndRect);
    }

    public void releaseAngleShiftCustomRect() {
        AppLog.info(TAG, "releaseAngleShiftCustomRect");
        this.mCustomStartRect = null;
        this.mCustomEndRect = null;
        setAngleShiftCustomDefaultRect(true);
    }

    public void resetAngleShiftCustomRect() {
        this.mCustomStartRect = this.mCustomDefaultStartRect;
        this.mCustomEndRect = this.mCustomDefaultEndRect;
        AppLog.info(TAG, "resetAngleShiftCustomRect(mCustomStartRect): " + this.mCustomStartRect);
        AppLog.info(TAG, "resetAngleShiftCustomRect(mCustomEndRect): " + this.mCustomEndRect);
    }

    public void resetAngleShiftCustomRectByMovieSizeChanged() {
        this.mCustomStartRect = null;
        this.mCustomEndRect = null;
        this.mAngleShiftCustomDefaultRect = true;
    }

    public void resizeCustomRect() {
        float scalef;
        int imageWidth;
        int imageHeight;
        if (!isAngleShiftCustomDefaultRect()) {
            int minCropWidth = 1920;
            int minCropHeight = 1080;
            float preRCRatio_1280_720 = 1.0f;
            float preRCRatio_1920_1080 = 1.0f;
            if (3264 < this.mImageOrigWidth) {
                preRCRatio_1280_720 = 3264.0f / this.mImageOrigWidth;
            }
            if (4912 < this.mImageOrigWidth) {
                preRCRatio_1920_1080 = 4912.0f / this.mImageOrigWidth;
            }
            int i = this.mImageOrigWidth;
            int i2 = this.mImageOrigHeight;
            if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
                if (preRCRatio_1280_720 != 1.0f) {
                    scalef = preRCRatio_1920_1080 / preRCRatio_1280_720;
                    int imageWidth2 = (int) (this.mImageOrigWidth * preRCRatio_1920_1080);
                    int imageHeight2 = (int) (this.mImageOrigHeight * preRCRatio_1920_1080);
                    imageWidth = aligned4pixel(imageWidth2);
                    imageHeight = aligned4pixel(imageHeight2);
                } else {
                    return;
                }
            } else if (preRCRatio_1280_720 != 1.0f) {
                scalef = preRCRatio_1280_720 / preRCRatio_1920_1080;
                imageWidth = aligned4pixel((int) (this.mImageOrigWidth * preRCRatio_1280_720));
                imageHeight = aligned4pixel((int) (this.mImageOrigHeight * preRCRatio_1280_720));
                if (3264 < this.mImageOrigWidth) {
                    minCropWidth = 1280;
                    minCropHeight = 720;
                }
            } else {
                return;
            }
            this.mCustomStartRect.left = (int) (r8.left * scalef);
            this.mCustomStartRect.top = (int) (r8.top * scalef);
            this.mCustomStartRect.right = (int) (r8.right * scalef);
            this.mCustomStartRect.bottom = (int) (r8.bottom * scalef);
            this.mCustomEndRect.left = (int) (r8.left * scalef);
            this.mCustomEndRect.top = (int) (r8.top * scalef);
            this.mCustomEndRect.right = (int) (r8.right * scalef);
            this.mCustomEndRect.bottom = (int) (r8.bottom * scalef);
            if (this.mCustomStartRect.width() < minCropWidth || this.mCustomStartRect.height() < minCropHeight) {
                this.mCustomStartRect.right = this.mCustomStartRect.left + minCropWidth;
                this.mCustomStartRect.bottom = this.mCustomStartRect.top + minCropHeight;
            }
            if (this.mCustomEndRect.width() < minCropWidth || this.mCustomEndRect.height() < minCropHeight) {
                this.mCustomEndRect.right = this.mCustomEndRect.left + minCropWidth;
                this.mCustomEndRect.bottom = this.mCustomEndRect.top + minCropHeight;
            }
            if (this.mCustomStartRect.right > imageWidth) {
                this.mCustomStartRect.left -= this.mCustomStartRect.right - imageWidth;
                if (this.mCustomStartRect.left < 0) {
                    this.mCustomStartRect.left = 0;
                }
                this.mCustomStartRect.right = imageWidth;
            }
            if (this.mCustomStartRect.bottom > imageHeight) {
                this.mCustomStartRect.top -= this.mCustomStartRect.bottom - imageHeight;
                if (this.mCustomStartRect.top < 0) {
                    this.mCustomStartRect.top = 0;
                }
                this.mCustomStartRect.bottom = imageHeight;
            }
            if (this.mCustomEndRect.right > imageWidth) {
                this.mCustomEndRect.left -= this.mCustomEndRect.right - imageWidth;
                if (this.mCustomEndRect.left < 0) {
                    this.mCustomEndRect.left = 0;
                }
                this.mCustomEndRect.right = imageWidth;
            }
            if (this.mCustomEndRect.bottom > imageHeight) {
                this.mCustomEndRect.top -= this.mCustomEndRect.bottom - imageHeight;
                if (this.mCustomEndRect.top < 0) {
                    this.mCustomEndRect.top = 0;
                }
                this.mCustomEndRect.bottom = imageHeight;
            }
        }
    }

    public void setAngleShiftCustomStartRect(Rect rect) {
        this.mCustomStartRect = rect;
        AppLog.info(TAG, "setAngleShiftCustomStartRect: " + this.mCustomStartRect);
    }

    public void setAngleShiftCustomEndRect(Rect rect) {
        this.mCustomEndRect = rect;
        AppLog.info(TAG, "setAngleShiftCustomEndRect: " + this.mCustomEndRect);
    }

    public Rect getAngleShiftPanStartRect() {
        return this.mPanStartRect;
    }

    public Rect getAngleShiftPanEndRect() {
        return this.mPanEndRect;
    }

    public Rect getAngleShiftTiltStartRect() {
        return this.mTiltStartRect;
    }

    public Rect getAngleShiftTiltEndRect() {
        return this.mTiltEndRect;
    }

    public Rect getAngleShiftZoomStartRect() {
        return this.mZoomStartRect;
    }

    public Rect getAngleShiftZoomEndRect() {
        return this.mZoomEndRect;
    }

    public Rect getAngleShiftNoEffectStartRect() {
        return this.mNoEffectStartRect;
    }

    public Rect getAngleShiftNoEffectEndRect() {
        return this.mNoEffectEndRect;
    }

    public Rect getAngleShiftCustomStartRect() {
        if (isAngleShiftCustomDefaultRect() && !isAngleShiftCustomAdjustRect()) {
            AppLog.info(TAG, "getAngleShiftCustomStartRect(mCustomDefaultStartRect): " + this.mCustomDefaultStartRect);
            return this.mCustomDefaultStartRect;
        }
        AppLog.info(TAG, "getAngleShiftCustomStartRect(mCustomStartRect): " + this.mCustomStartRect);
        return this.mCustomStartRect;
    }

    public Rect getAngleShiftCustomEndRect() {
        if (getTargetNumber() < 2) {
            if (isAngleShiftCustomDefaultRect() && !isAngleShiftCustomAdjustRect()) {
                return this.mCustomDefaultStartRect;
            }
            return this.mCustomStartRect;
        }
        if (isAngleShiftCustomDefaultRect() && !isAngleShiftCustomAdjustRect()) {
            AppLog.info(TAG, "getAngleShiftCustomEndRect(mCustomDefaultEndRect): " + this.mCustomDefaultEndRect);
            return this.mCustomDefaultEndRect;
        }
        AppLog.info(TAG, "getAngleShiftCustomEndRect(mCustomEndRect): " + this.mCustomEndRect);
        return this.mCustomEndRect;
    }

    public Rect getAngleShiftCustomDefaultStartRect() {
        return this.mCustomDefaultStartRect;
    }

    public Rect getAngleShiftCustomDefaultEndRect() {
        return this.mCustomDefaultEndRect;
    }

    public void setAngleShiftCustomDefaultRect(boolean isDefault) {
        this.mAngleShiftCustomDefaultRect = isDefault;
    }

    public boolean isAngleShiftCustomDefaultRect() {
        if (this.mCustomDefaultStartRect != null && this.mCustomDefaultStartRect.equals(this.mCustomStartRect) && this.mCustomDefaultEndRect != null && this.mCustomDefaultEndRect.equals(this.mCustomEndRect)) {
            this.mAngleShiftCustomDefaultRect = true;
        }
        return this.mAngleShiftCustomDefaultRect;
    }

    public void setAngleShiftCustomAdjustRect(boolean isDefault) {
        this.mAngleShiftCustomAdjustRect = isDefault;
    }

    public boolean isAngleShiftCustomAdjustRect() {
        return this.mAngleShiftCustomAdjustRect;
    }

    public void setRotateDegree(RotateImageFilter.ROTATION_DEGREE angle) {
        this.mRotateDegree = angle;
    }

    public RotateImageFilter.ROTATION_DEGREE getRotateDegree() {
        return this.mRotateDegree;
    }

    public boolean isRotatedImage() {
        return RotateImageFilter.ROTATION_DEGREE.DEGREE_0 != this.mRotateDegree;
    }

    public void setImageAspect(int aspect) {
        this.mImageAspect = aspect;
    }

    public int getImageAspect() {
        return this.mImageAspect;
    }

    public int getSingleYUVDisplayHeight() {
        if (1 != getImageAspect()) {
            return 366;
        }
        if (getRotateDegree() != RotateImageFilter.ROTATION_DEGREE.DEGREE_0 && getRotateDegree() != RotateImageFilter.ROTATION_DEGREE.DEGREE_180) {
            return 366;
        }
        return 360;
    }

    public OptimizedImage getRotatedImage(RotateImageFilter filter, OptimizedImage inImage, boolean isReleased) {
        OptimizedImage rotatedImage = null;
        filter.setRotation(getInstance().getRotateDegree());
        filter.setSource(inImage, isReleased);
        boolean result = filter.execute();
        if (result) {
            rotatedImage = filter.getOutput();
        } else {
            AppLog.info("getRotatedImage", "result: " + result);
        }
        filter.clearSources();
        return rotatedImage;
    }

    public Rect getScaledCroppedRect(int actualWidth, int actualHeigt, int dispWidth, int dispHeight, Rect actualRect) {
        Rect scaledRect = new Rect();
        double scaleValueY = dispHeight / actualHeigt;
        double scaleValueX = scaleValueY;
        if (169 == ScalarProperties.getInt("device.panel.aspect") && DisplayModeObserver.getInstance().getActiveDevice() != 1) {
            scaleValueX = (3.0d * scaleValueX) / 4.0d;
        }
        int marginX = (int) (((dispWidth - (actualWidth * scaleValueX)) / 2.0d) + 0.5d);
        int marginY = (480 - dispHeight) / 2;
        scaledRect.left = (((int) (actualRect.left * scaleValueX)) + marginX) - FRAME_LINE_WEIGHT;
        scaledRect.right = ((int) (actualRect.right * scaleValueX)) + marginX + FRAME_LINE_WEIGHT;
        scaledRect.top = (((int) (actualRect.top * scaleValueY)) + marginY) - FRAME_LINE_WEIGHT;
        scaledRect.bottom = ((int) (actualRect.bottom * scaleValueY)) + marginY + FRAME_LINE_WEIGHT;
        return scaledRect;
    }

    public void setOptimizedImageValidness(boolean isValid) {
        this.mIsValidImage = isValid;
    }

    public boolean isValidOptimizedImage() {
        return this.mIsValidImage;
    }

    public void setCancelRotationDegree(RotateImageFilter.ROTATION_DEGREE origDegree) {
        this.mOrigDegree = origDegree;
    }

    public void cancelRotation() {
        setRotateDegree(this.mOrigDegree);
    }

    public boolean needCancelRotation() {
        return this.mOrigDegree != null;
    }

    public void setTempFramePosition(int start, int end) {
        this.mTempStartPosIndex = start;
        this.mTempEndPosIndex = end;
    }

    public void fixFramePosition() {
        setStartPosIndex(this.mTempStartPosIndex);
        setEndPosIndex(this.mTempEndPosIndex);
    }

    public void backupCustomInputImageSize() {
        String size = "" + this.mInputWidth + "/" + this.mInputHeight;
        BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CUSTOM_INPUT_IMAGE_SZIE_KEY, size);
        AppLog.info("backupCustomInputImageSize", "size: " + size);
    }

    public void setImageSize(int width, int height) {
        this.mInputWidth = width;
        this.mInputHeight = height;
    }

    public boolean isSameInputImageSizeForCustom() {
        String size = "" + this.mInputWidth + "/" + this.mInputHeight;
        AppLog.info("isSameInputImageSizeForCustom", "size: " + size);
        return size.equalsIgnoreCase(BackUpUtil.getInstance().getPreferenceString(AngleShiftConstants.AS_CUSTOM_INPUT_IMAGE_SZIE_KEY, "0/0"));
    }

    public void backupCustomSettings() {
        int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        if (theme != 4) {
            AppLog.info("backupCustomSettings", "Theme is not custom.");
            return;
        }
        if (getAngleShiftCustomStartRect() == null || getAngleShiftCustomEndRect() == null) {
            AppLog.info("backupCustomSettings", "Rect is null");
            return;
        }
        String startRect = "" + getAngleShiftCustomStartRect().left + "/" + getAngleShiftCustomStartRect().top + "/" + getAngleShiftCustomStartRect().right + "/" + getAngleShiftCustomStartRect().bottom;
        String endRect = "" + getAngleShiftCustomEndRect().left + "/" + getAngleShiftCustomEndRect().top + "/" + getAngleShiftCustomEndRect().right + "/" + getAngleShiftCustomEndRect().bottom;
        RotateImageFilter.ROTATION_DEGREE rotationDegree = getRotateDegree();
        String degree = AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_0;
        if (rotationDegree.equals(RotateImageFilter.ROTATION_DEGREE.DEGREE_0)) {
            degree = AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_0;
        } else if (rotationDegree.equals(RotateImageFilter.ROTATION_DEGREE.DEGREE_90)) {
            degree = AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_90;
        } else if (rotationDegree.equals(RotateImageFilter.ROTATION_DEGREE.DEGREE_180)) {
            degree = AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_180;
        } else if (rotationDegree.equals(RotateImageFilter.ROTATION_DEGREE.DEGREE_270)) {
            degree = AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_270;
        }
        AppLog.info("backupCustomSettings", "startRect : " + startRect);
        AppLog.info("backupCustomSettings", "endRect   : " + endRect);
        AppLog.info("backupCustomSettings", "degree : " + degree);
        BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CUSTOM_ROTATE_DEGREE_KEY, degree);
        BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CUSTOM_START_FRAME_INFO_KEY, startRect);
        BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CUSTOM_END_FRAME_INFO_KEY, endRect);
    }

    public void backupCustomSettingsAgain() {
        int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        if (theme == 4) {
            String startRect = "" + getAngleShiftCustomStartRect().left + "/" + getAngleShiftCustomStartRect().top + "/" + getAngleShiftCustomStartRect().right + "/" + getAngleShiftCustomStartRect().bottom;
            String endRect = "" + getAngleShiftCustomEndRect().left + "/" + getAngleShiftCustomEndRect().top + "/" + getAngleShiftCustomEndRect().right + "/" + getAngleShiftCustomEndRect().bottom;
            AppLog.info("backupCustomSettings", "startRect : " + startRect);
            AppLog.info("backupCustomSettings", "endRect   : " + endRect);
            BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CUSTOM_START_FRAME_INFO_KEY, startRect);
            BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CUSTOM_END_FRAME_INFO_KEY, endRect);
        }
    }

    public void applyBackupCustomSettings() {
        AppLog.info("backupCustomSettings", "applyBackupCustomSettings");
        String rotateString = BackUpUtil.getInstance().getPreferenceString(AngleShiftConstants.AS_CUSTOM_ROTATE_DEGREE_KEY, AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_0);
        String startRectString = BackUpUtil.getInstance().getPreferenceString(AngleShiftConstants.AS_CUSTOM_START_FRAME_INFO_KEY, INVALID_RECTANGLE);
        String endRectString = BackUpUtil.getInstance().getPreferenceString(AngleShiftConstants.AS_CUSTOM_END_FRAME_INFO_KEY, INVALID_RECTANGLE);
        if (INVALID_RECTANGLE.equalsIgnoreCase(startRectString) || INVALID_RECTANGLE.equalsIgnoreCase(endRectString)) {
            getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
            getInstance().setAngleShiftCustomDefaultRect(true);
            getInstance().setAngleShiftCustomStartRect(null);
            getInstance().setAngleShiftCustomEndRect(null);
            return;
        }
        RotateImageFilter.ROTATION_DEGREE degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        if (rotateString.equals(AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_0)) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        } else if (rotateString.equals(AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_90)) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_90;
        } else if (rotateString.equals(AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_180)) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_180;
        } else if (rotateString.equals(AngleShiftConstants.AS_CUSTOM_ROTATION_DEGREE_270)) {
            degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
        }
        Rect startRect = unpackRectangle(startRectString);
        Rect endRect = unpackRectangle(endRectString);
        getInstance().setRotateDegree(degree);
        getInstance().setAngleShiftCustomStartRect(startRect);
        getInstance().setAngleShiftCustomEndRect(endRect);
        getInstance().setAngleShiftCustomDefaultRect(false);
    }

    private Rect unpackRectangle(String srect) {
        String[] rectArray = srect.split("/");
        int left = Integer.parseInt(rectArray[0]);
        int top = Integer.parseInt(rectArray[1]);
        int right = Integer.parseInt(rectArray[2]);
        int bottom = Integer.parseInt(rectArray[3]);
        Rect rect = new Rect(left, top, right, bottom);
        return rect;
    }

    public void printRect(String tag, String methodName, Rect src, Rect dest) {
        AppLog.info(tag, "" + methodName + " : left " + src.left + " --> " + dest.left);
        AppLog.info(tag, "" + methodName + " : top  " + src.top + " --> " + dest.top);
        AppLog.info(tag, "" + methodName + " : right " + src.right + " --> " + dest.right);
        AppLog.info(tag, "" + methodName + " : bottom " + src.bottom + " --> " + dest.bottom);
    }
}
