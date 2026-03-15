package com.sony.imaging.app.base.playback.layout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.BaseProperties;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.playback.widget.ZoomGuideView;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.RelativeLayoutGroup;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public abstract class ZoomLayoutBase extends OptimizedImageLayoutBase implements IModableLayout {
    private static final double CHECK_ROTATE = 1.0d;
    private static final boolean DEBUG = true;
    private static final int DEFAULT_BUIDER_SIZE = 16;
    static final int ERROR_THUMBNAIL_COLOR = 858993459;
    private static final long EVENT_INTERVAL = 100;
    private static final float INITIAL_POWER_MINIMUM = 1.1f;
    private static final int MIN_SIZE_X = 1920;
    private static final int MIN_SIZE_Y = 1080;
    private static final int MOVE_STEP = 20;
    private static final String MSG_BOTTOM = " bottom : ";
    private static final String MSG_CAUTION_ALREADY_DISPLAYED = "Caution already displayed";
    private static final String MSG_CLIP = "clipSize... ";
    private static final String MSG_COMMA = ", ";
    private static final String MSG_DEFAULT_PIVOT = "default pivot";
    private static final String MSG_DISAPPEAR_CAUTION_ON_PAUSED = "disappear Caution onPause";
    private static final String MSG_DRAW = "drawSize... ";
    private static final String MSG_DUMP = "****** LayoutInfo ******";
    private static final String MSG_IMAGE = "imageSize... ";
    private static final String MSG_LAYOUT_SCALE = "scale = ";
    private static final String MSG_LAYOUT_TRANS = "LayoutInfo traslate : ";
    private static final String MSG_LEFT = " left : ";
    private static final String MSG_RIGHT = " right : ";
    private static final String MSG_SIZE = " (w,h)=";
    private static final String MSG_STORE_TRANS = "Store traslate : ";
    private static final String MSG_TOP = " top : ";
    private static final String MSG_TRANS_BY = "traslate by : ";
    private static final String MSG_VIEW = "ViewSize... ";
    private static final String MSG_X = " x = ";
    private static final String MSG_Y = " y = ";
    protected static final int OPTIMIZEDIMAGEVIEW_CENTER_X = 320;
    protected static final int OPTIMIZEDIMAGEVIEW_CENTER_Y = 240;
    protected static final int OPTIMIZEDIMAGEVIEW_HEIGHT = 480;
    protected static final int OPTIMIZEDIMAGEVIEW_WIDTH = 640;
    private static final int PIXEL_ALIGN = 10;
    private static final float SCALE_STEP = 1.1f;
    private static final String TAG = "ZoomLayoutBase";
    private static final int THRESH_BUIDER_SIZE = 64;
    private static StringBuilder builder = new StringBuilder();
    private float MAX_SCALE;
    private float SCALE_BASE;
    private ImageView mArrowDown;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;
    private ImageView mArrowUp;
    private PointF mDisplayAspect;
    private ZoomGuideView mGuideView;
    double mImageToGuideScale;
    double mImageToVGAScale;
    private TouchArea mTouchLayer;
    protected ZoomCautionCallback mZoomCautionCallback;
    Rect mGuideRect = new Rect();
    protected boolean mIsDispModeChanged = false;
    private int mScaleLevel = 0;
    private int mDeviceWidth = 0;
    private int mDeviceHeight = 0;
    private final Point DEFAULT_TRANSLATE = new Point(OPTIMIZEDIMAGEVIEW_CENTER_X, OPTIMIZEDIMAGEVIEW_CENTER_Y);
    private Point mTranslate = null;
    private Point mTranslateDenom = null;
    private boolean mIsZoomInLimit = false;
    private boolean mIsZoomOutLimit = false;
    private OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 1);

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsDispModeChanged = false;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        if (THRESH_BUIDER_SIZE < builder.capacity()) {
            builder.setLength(16);
            builder.trimToSize();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.mGuideView = (ZoomGuideView) view.findViewById(R.id.ZoomGuideView);
        if (this.mGuideView != null) {
            this.mGuideView.removeAllViews();
        }
        this.mArrowLeft = (ImageView) view.findViewById(R.id.ArrowIconLeft);
        this.mArrowUp = (ImageView) view.findViewById(R.id.ArrowIconUp);
        this.mArrowRight = (ImageView) view.findViewById(R.id.ArrowIconRight);
        this.mArrowDown = (ImageView) view.findViewById(R.id.ArrowIconDown);
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (this.mTouchLayer != null) {
            this.mTouchLayer.setTouchAreaListener(null);
            this.mTouchLayer = null;
        }
        this.mGuideView = null;
        this.mArrowLeft = null;
        this.mArrowUp = null;
        this.mArrowRight = null;
        this.mArrowDown = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mZoomCautionCallback = null;
        this.mIsZoomInLimit = false;
        this.mIsZoomOutLimit = false;
        this.mScaleLevel = 0;
        this.mTranslateDenom = null;
        this.mTranslate = null;
        this.mDeviceWidth = 0;
        this.mDeviceHeight = 0;
        reflectDispMode();
        updateDisplayAspect();
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        PTag.end("Into playzoom. End");
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mZoomCautionCallback != null) {
            logcat(MSG_DISAPPEAR_CAUTION_ON_PAUSED);
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this.mZoomCautionCallback);
            CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_INVALID_IMAGE_MAGNIFY);
            this.mZoomCautionCallback = null;
        }
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        updateDisplayAspect();
        updateDisplay();
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        this.mIsZoomInLimit = false;
        this.mIsZoomOutLimit = false;
        reflectDispMode();
        updateDisplayAspect();
        float scale = ((float) Math.pow(1.100000023841858d, this.mScaleLevel)) * this.SCALE_BASE;
        this.mOptImgView.setScale(scale, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
        updateGuideThumbnail();
        update();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateDisplay() {
        ContentsManager mgr = ContentsManager.getInstance();
        OptimizedImage image = mgr.getOptimizedImage(mgr.getContentsId(), getImageType());
        updateOptimizedImage(checkDisplayCaution(image, mgr));
        updateGuideThumbnail();
        update();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateOptimizedImage(OptimizedImage image) {
        Point p;
        if (this.mOptImgView != null) {
            super.updateOptimizedImage(image);
            this.mOptImgView.setScale(1.0f, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
            OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
            if (info != null) {
                dumpLayoutInfo(info);
                float scale = 1.0f;
                int imgW = info.viewSize.width();
                info.viewSize.height();
                if (this.mDeviceWidth == 0) {
                    DisplayManager.DeviceInfo deviceInfo = DisplayModeObserver.getInstance().getActiveDeviceInfo();
                    if (deviceInfo != null) {
                        this.mDeviceWidth = deviceInfo.res_w;
                        this.mDeviceHeight = deviceInfo.res_h;
                        scale = imgW / this.mDeviceWidth;
                        logcat(builder.replace(0, builder.length(), MSG_LAYOUT_SCALE).append(scale).toString());
                    }
                    if (1 >= Environment.getVersionPfAPI()) {
                        this.SCALE_BASE = scale;
                    } else {
                        this.SCALE_BASE = ScalarProperties.getInt("ui.playback.zoom.type") == 1 ? 1.1f : scale;
                    }
                    this.MAX_SCALE = 2.0f * scale;
                    this.mOptImgView.setTranslate(this.DEFAULT_TRANSLATE, OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
                }
                EventParcel touchEventAutoReview = null;
                if (this.data != null) {
                    touchEventAutoReview = (EventParcel) this.data.getParcelable(EventParcel.KEY_TOUCH);
                    this.data.remove(EventParcel.KEY_TOUCH);
                }
                if (touchEventAutoReview != null) {
                    MotionEvent event = touchEventAutoReview.mEvent1;
                    Rect yuvRect = touchEventAutoReview.mRect;
                    if (event != null && yuvRect != null) {
                        p = moveTouchPos(event, yuvRect);
                    } else {
                        p = new Point(info.imageSize.width() / 2, info.imageSize.height() / 2);
                    }
                } else {
                    p = new Point(info.imageSize.width() / 2, info.imageSize.height() / 2);
                }
                this.mOptImgView.setDisplayPosition(p, OptimizedImageView.PositionType.POS_TYPE_CENTER_BOUNDED);
                float nextScale = ((float) Math.pow(1.100000023841858d, this.mScaleLevel)) * this.SCALE_BASE;
                OptimizedImageView optimizedImageView = this.mOptImgView;
                if (nextScale > this.MAX_SCALE) {
                    nextScale = this.MAX_SCALE;
                }
                optimizedImageView.setScale(nextScale, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void invalidate() {
        this.mZoomCautionCallback = null;
        this.mIsZoomInLimit = false;
        this.mIsZoomOutLimit = false;
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info != null) {
            this.mTranslateDenom = new Point(info.imageSize.width(), info.imageSize.height());
            this.mTranslate = new Point(info.clipSize.centerX(), info.clipSize.centerY());
            logcat(builder.replace(0, builder.length(), MSG_STORE_TRANS).append(MSG_X).append(this.mTranslate.x).append(MSG_Y).append(this.mTranslate.y).toString());
        }
        ContentsManager mgr = ContentsManager.getInstance();
        OptimizedImage image = mgr.getOptimizedImage(mgr.getContentsId(), getImageType());
        if (checkDisplayCaution(image, mgr) != null) {
            super.updateOptimizedImage(image);
            this.mOptImgView.setScale(1.0f, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
            OptimizedImageView.LayoutInfo info2 = this.mOptImgView.getLayoutInfo();
            if (info2 != null) {
                int viewW = info2.viewSize.width();
                float scale = viewW / this.mDeviceWidth;
                this.MAX_SCALE = 2.0f * scale;
                float nextScale = ((float) Math.pow(1.100000023841858d, this.mScaleLevel)) * this.SCALE_BASE;
                OptimizedImageView optimizedImageView = this.mOptImgView;
                if (this.MAX_SCALE < nextScale) {
                    nextScale = this.MAX_SCALE;
                }
                optimizedImageView.setScale(nextScale, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
                OptimizedImageView.LayoutInfo info3 = this.mOptImgView.getLayoutInfo();
                if (info3 != null) {
                    int imgW = info3.imageSize.width();
                    int imgH = info3.imageSize.height();
                    Point translate = new Point(info3.clipSize.centerX() - ((this.mTranslate.x * imgW) / this.mTranslateDenom.x), info3.clipSize.centerY() - ((this.mTranslate.y * imgH) / this.mTranslateDenom.y));
                    logcat(builder.replace(0, builder.length(), MSG_TRANS_BY).append(MSG_X).append(translate.x).append(MSG_Y).append(translate.y).toString());
                    this.mOptImgView.translate(translate, new Point(imgW, imgH), OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
                    updateGuideThumbnail();
                    update();
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateOptimizedImageLayoutParam(OptimizedImageView img, ContentsManager mgr) {
        super.updateOptimizedImageLayoutParam(img, mgr);
        img.setPivot(new Point(OPTIMIZEDIMAGEVIEW_CENTER_X, OPTIMIZEDIMAGEVIEW_CENTER_Y));
    }

    private void updateGuideThumbnail() {
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info != null) {
            ContentsManager mgr = ContentsManager.getInstance();
            Bitmap guideBm = getGuideBitmap(mgr);
            if (guideBm == null) {
                int width = (int) ((info.imageSize.width() / 10) * this.mDisplayAspect.x);
                int height = (int) ((info.imageSize.height() / 10) * this.mDisplayAspect.y);
                guideBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(guideBm);
                canvas.drawColor(ERROR_THUMBNAIL_COLOR);
            }
            this.mImageToGuideScale = guideBm.getWidth() / info.imageSize.width();
            this.mGuideView.setOptThumnail(guideBm);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected OptimizedImageView.DisplayType getDisplayType() {
        return OptimizedImageView.DisplayType.DISPLAY_TYPE_CENTER_INNER;
    }

    protected OptimizedImage checkDisplayCaution(OptimizedImage image, ContentsManager mgr) {
        if (image == null) {
            openCaution();
            return null;
        }
        ContentInfo content = mgr.getContentInfo(mgr.getContentsId());
        long imgWidth = 0;
        long imgHeight = 0;
        int contentType = 0;
        if (content != null) {
            imgWidth = content.getLong("ImageWidth");
            imgHeight = content.getLong("ImageLength");
            contentType = content.getInt("ContentType");
        }
        if (imgWidth > imgHeight) {
            if (imgWidth < 1920 || imgHeight < 1080) {
                openCaution();
                return null;
            }
        } else if (imgWidth < 1080 || imgHeight < 1920) {
            openCaution();
            return null;
        }
        switch (contentType) {
            case 9:
            case 11:
            case 12:
                openCaution();
                return null;
            case 10:
            default:
                return image;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ZoomCautionCallback implements CautionUtilityClass.triggerCautionCallback {
        ZoomCautionCallback() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.triggerCautionCallback
        public void onCallback() {
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this);
            ZoomLayoutBase.this.mZoomCautionCallback = null;
            ZoomLayoutBase.this.transitionBrowser();
        }
    }

    protected void openCaution() {
        hideArrow();
        if (this.mZoomCautionCallback != null) {
            logcat(MSG_CAUTION_ALREADY_DISPLAYED);
        }
        this.mZoomCautionCallback = new ZoomCautionCallback();
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_IMAGE_MAGNIFY);
        CautionUtilityClass.getInstance().registerCallbackTriggerDisapper(this.mZoomCautionCallback);
    }

    private Point moveTouchPos(MotionEvent touch, Rect view) {
        int touchX = (int) touch.getX();
        int touchY = (int) touch.getY();
        int viewWidth = view.width();
        int viewHeight = view.height();
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info == null) {
            return new Point(0, 0);
        }
        int diffX = touchX - (viewWidth / 2);
        int diffY = touchY - (viewHeight / 2);
        long imgWidth = 0;
        long imgHeight = 0;
        ContentsManager mgr = ContentsManager.getInstance();
        ContentInfo content = mgr.getContentInfo(mgr.getContentsId());
        if (content != null) {
            imgWidth = content.getLong("ImageWidth");
            imgHeight = content.getLong("ImageLength");
        }
        int centerX = 0;
        int centerY = 0;
        if (BackupReader.getVPicDisplay() == BackupReader.VPicDisplay.PORTRAIT && content != null) {
            int angle = content.getInt("Orientation");
            switch (angle) {
                case 6:
                    long temp = imgWidth;
                    imgWidth = imgHeight;
                    imgHeight = temp;
                    break;
                case 8:
                    long temp2 = imgWidth;
                    imgWidth = imgHeight;
                    imgHeight = temp2;
                    break;
            }
        }
        double aspect = imgWidth / imgHeight;
        if (aspect >= CHECK_ROTATE) {
            double transScaleX = imgWidth / viewWidth;
            centerX = (int) ((imgWidth / 2) + (diffX * transScaleX * this.mDisplayAspect.x));
            centerY = (int) ((imgHeight / 2) + (diffY * transScaleX * this.mDisplayAspect.y));
        } else if (aspect < CHECK_ROTATE) {
            double transScaleY = imgHeight / viewHeight;
            centerX = (int) ((imgWidth / 2) + (diffX * transScaleY * this.mDisplayAspect.x));
            centerY = (int) ((imgHeight / 2) + (diffY * transScaleY * this.mDisplayAspect.y));
        }
        return new Point(centerX, centerY);
    }

    protected Bitmap getGuideBitmap(ContentsManager cMgr) {
        ContentsManager.ThumbnailOption op = new ContentsManager.ThumbnailOption();
        op.clipByAspect = true;
        if (BackupReader.getVPicDisplay() == BackupReader.VPicDisplay.PORTRAIT) {
            op.rotateIt = true;
        } else {
            op.rotateIt = false;
        }
        op.postScale = this.mDisplayAspect;
        Bitmap guideBm = cMgr.getThumbnail(cMgr.getContentsId(), op);
        return guideBm;
    }

    private void updateDisplayAspect() {
        DisplayModeObserver displayModeObserver = DisplayModeObserver.getInstance();
        int aspect = displayModeObserver.getActiveDeviceOsdAspect();
        this.mDisplayAspect = 2 == aspect ? ContentsManager.SCALE_WIDE_PANEL_PIXEL : ContentsManager.SCALE_SQUARE_PIXEL;
        if (this.mDisplayAspect == null) {
            this.mDisplayAspect = new PointF(1.0f, 1.0f);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int getImageType() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public int getKeyBeepPattern() {
        return 18;
    }

    protected void reflectDispMode() {
        int dispMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        RelativeLayoutGroup group = (RelativeLayoutGroup) this.mView.findViewById(R.id.pb_zoom_info);
        if (group != null) {
            group.setVisibility((this.mIsDispModeChanged && 3 == dispMode) ? 4 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        this.mOptImgView.redraw();
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info != null) {
            Point p = this.mOptImgView.getTranslate();
            logcat(builder.replace(0, builder.length(), MSG_LAYOUT_TRANS).append(MSG_X).append(p.x).append(MSG_Y).append(p.x).toString());
            Rect clip = info.clipSize;
            Rect image = info.imageSize;
            Rect clip2 = transformPixel(clip, image);
            this.mGuideRect.set((int) ((clip2.left * this.mImageToGuideScale) / this.mDisplayAspect.y), (int) ((clip2.top * this.mImageToGuideScale) / this.mDisplayAspect.x), (int) ((clip2.right * this.mImageToGuideScale) / this.mDisplayAspect.y), (int) ((clip2.bottom * this.mImageToGuideScale) / this.mDisplayAspect.x));
            this.mGuideView.drawGuideRect(this.mGuideRect);
            updateArrow();
        }
    }

    private Rect transformPixel(Rect clip, Rect image) {
        if (Math.abs(clip.left - image.left) <= 10) {
            clip.left = image.left;
        }
        if (Math.abs(clip.top - image.top) <= 10) {
            clip.top = image.top;
        }
        if (Math.abs(clip.right - image.right) <= 10) {
            clip.right = image.right;
        }
        if (Math.abs(clip.bottom - image.bottom) <= 10) {
            clip.bottom = image.bottom;
        }
        return clip;
    }

    private void updateArrow() {
        OptimizedImageView.Translatability transable = this.mOptImgView.getTranslatability(OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
        if (transable == null) {
            hideArrow();
            return;
        }
        if (this.mArrowLeft != null) {
            this.mArrowLeft.setVisibility(transable.right ? 0 : 4);
        }
        if (this.mArrowUp != null) {
            this.mArrowUp.setVisibility(transable.bottom ? 0 : 4);
        }
        if (this.mArrowRight != null) {
            this.mArrowRight.setVisibility(transable.left ? 0 : 4);
        }
        if (this.mArrowDown != null) {
            this.mArrowDown.setVisibility(transable.top ? 0 : 4);
        }
    }

    private void hideArrow() {
        if (this.mArrowLeft != null) {
            this.mArrowLeft.setVisibility(4);
        }
        if (this.mArrowUp != null) {
            this.mArrowUp.setVisibility(4);
        }
        if (this.mArrowRight != null) {
            this.mArrowRight.setVisibility(4);
        }
        if (this.mArrowDown != null) {
            this.mArrowDown.setVisibility(4);
        }
    }

    public void zoomIn() {
        PTag.start("change scale start");
        if (!this.mIsZoomInLimit) {
            this.mScaleLevel++;
            float nextScale = ((float) Math.pow(1.100000023841858d, this.mScaleLevel)) * this.SCALE_BASE;
            this.mIsZoomOutLimit = false;
            if (nextScale > this.MAX_SCALE) {
                this.mIsZoomInLimit = true;
                nextScale = this.MAX_SCALE;
                this.mScaleLevel--;
            }
            OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
            if (info != null) {
                Rect oldRect = info.clipSize;
                this.mOptImgView.setScale(nextScale, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
                OptimizedImageView.LayoutInfo info2 = this.mOptImgView.getLayoutInfo();
                if (info2 != null) {
                    if (oldRect.equals(info2.clipSize)) {
                        zoomIn();
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        update();
        PTag.end("change scale end");
    }

    public void zoomOut() {
        PTag.start("Change scale. Start");
        if (!this.mIsZoomOutLimit) {
            this.mScaleLevel--;
            OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
            if (info != null) {
                Rect rect = info.clipSize;
                float nextScale = ((float) Math.pow(1.100000023841858d, this.mScaleLevel)) * this.SCALE_BASE;
                this.mOptImgView.setScale(nextScale, OptimizedImageView.BoundType.BOUND_TYPE_LONG_EDGE);
                this.mIsZoomInLimit = false;
                if (this.mOptImgView.getLayoutInfo() != null) {
                    OptimizedImageView.Translatability translability = this.mOptImgView.getTranslatability(OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
                    if (translability != null && !translability.top && !translability.bottom && !translability.left && !translability.right) {
                        this.mIsZoomOutLimit = true;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        update();
        PTag.end("Change scale. End");
    }

    public void moveX(boolean right) {
        PTag.start("Change translate. Start");
        Point currentPos = new Point();
        if (right) {
            currentPos.offset(20, 0);
        } else {
            currentPos.offset(-20, 0);
        }
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info != null) {
            Rect drawRect = info.drawSize;
            Point denomPoint = new Point(drawRect.height(), drawRect.height());
            this.mOptImgView.translate(currentPos, denomPoint, OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
            update();
            PTag.end("Change translate. End");
        }
    }

    public void moveY(boolean up) {
        PTag.start("Change translate. Start");
        Point currentPos = new Point();
        if (up) {
            currentPos.offset(0, -20);
        } else {
            currentPos.offset(0, 20);
        }
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info != null) {
            Rect drawRect = info.drawSize;
            Point denomPoint = new Point(drawRect.width(), drawRect.height());
            this.mOptImgView.translate(currentPos, denomPoint, OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
            update();
            PTag.end("Change translate. End");
        }
    }

    public void moveXY(boolean right, boolean up) {
        PTag.start("Change translate. Start");
        Point currentPos = new Point();
        currentPos.offset(right ? 20 : -20, up ? -20 : 20);
        OptimizedImageView.LayoutInfo info = this.mOptImgView.getLayoutInfo();
        if (info != null) {
            Rect drawRect = info.drawSize;
            Point denomPoint = new Point(drawRect.width(), drawRect.height());
            this.mOptImgView.translate(currentPos, denomPoint, OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
            update();
            PTag.end("Change translate. End");
        }
    }

    /* loaded from: classes.dex */
    private class ZoomTouchListener implements TouchArea.OnTouchAreaListener {
        int deltaX;
        int deltaY;
        int oldX;
        int oldY;
        long previousExcTime = 0;

        private ZoomTouchListener() {
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchDown(MotionEvent e) {
            this.oldX = (int) e.getX();
            this.oldY = (int) e.getY();
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
            if (!isReleasedInside) {
                return false;
            }
            Point center = new Point();
            OptimizedImageView.LayoutInfo info = ZoomLayoutBase.this.mOptImgView.getLayoutInfo();
            if (info == null) {
                return false;
            }
            center.x = -(((int) e.getX()) - (info.drawSize.right / 2));
            center.y = -(((int) e.getY()) - (info.drawSize.bottom / 2));
            ZoomLayoutBase.this.mOptImgView.translate(center, OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
            ZoomLayoutBase.this.zoomIn();
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onFlick(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!ZoomLayoutBase.this.mIsZoomOutLimit && ZoomLayoutBase.EVENT_INTERVAL <= System.currentTimeMillis() - this.previousExcTime) {
                this.deltaX = ((int) e2.getX()) - this.oldX;
                this.deltaY = ((int) e2.getY()) - this.oldY;
                Point currentPos = new Point(this.deltaX, this.deltaY);
                ZoomLayoutBase.this.mOptImgView.translate(currentPos, OptimizedImageView.TranslationType.TRANS_TYPE_INNER_CENTER);
                this.previousExcTime = System.currentTimeMillis();
                this.oldX = (int) e2.getX();
                this.oldY = (int) e2.getY();
                ZoomLayoutBase.this.update();
            }
            return true;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_INDEX;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        moveX(true);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        moveY(false);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        moveX(false);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        moveY(true);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftUpKey() {
        moveXY(true, false);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftDownKey() {
        moveXY(true, true);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightUpKey() {
        moveXY(false, false);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightDownKey() {
        moveXY(false, true);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return transitionBrowser() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return transitionBrowser() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return transitionBrowser() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        if (this.mIsZoomOutLimit) {
            thinZoomKey();
            transitionBrowser();
            return 1;
        }
        zoomOut();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        zoomIn();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        if (Environment.getVersionOfHW() == 1 || BaseProperties.isDownKeyAssignedToIndexTransition()) {
            return previousContents() ? 1 : -1;
        }
        zoomOut();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        if (Environment.getVersionOfHW() == 1 || BaseProperties.isDownKeyAssignedToIndexTransition()) {
            return nextContents() ? 1 : -1;
        }
        zoomIn();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        if (Environment.getVersionOfHW() != 1 && !BaseProperties.isDownKeyAssignedToIndexTransition()) {
            return !previousContents() ? -1 : 1;
        }
        zoomOut();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        if (Environment.getVersionOfHW() != 1 && !BaseProperties.isDownKeyAssignedToIndexTransition()) {
            return !nextContents() ? -1 : 1;
        }
        zoomIn();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        int i = 3;
        DisplayModeObserver observer = DisplayModeObserver.getInstance();
        int dispMode = observer.getActiveDispMode(1);
        if (3 == dispMode && this.mIsDispModeChanged) {
            i = 1;
        }
        observer.setDisplayMode(1, i);
        this.mIsDispModeChanged = true;
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 18;
    }

    private void logcat(String str) {
        Log.d(TAG, str);
    }

    private void dumpLayoutInfo(OptimizedImageView.LayoutInfo info) {
        logcat(MSG_DUMP);
        builder.replace(0, builder.length(), MSG_CLIP).append(MSG_LEFT).append(info.clipSize.left).append(MSG_TOP).append(info.clipSize.top).append(MSG_RIGHT).append(info.clipSize.right).append(MSG_BOTTOM).append(info.clipSize.bottom).append(MSG_SIZE).append(info.clipSize.width()).append(", ").append(info.clipSize.height());
        logcat(builder.toString());
        builder.replace(0, builder.length(), MSG_DRAW).append(MSG_LEFT).append(info.drawSize.left).append(MSG_TOP).append(info.drawSize.top).append(MSG_RIGHT).append(info.drawSize.right).append(MSG_BOTTOM).append(info.drawSize.bottom).append(MSG_SIZE).append(info.drawSize.width()).append(", ").append(info.drawSize.height());
        logcat(builder.toString());
        builder.replace(0, builder.length(), MSG_IMAGE).append(MSG_LEFT).append(info.imageSize.left).append(MSG_TOP).append(info.imageSize.top).append(MSG_RIGHT).append(info.imageSize.right).append(MSG_BOTTOM).append(info.imageSize.bottom).append(MSG_SIZE).append(info.imageSize.width()).append(", ").append(info.imageSize.height());
        logcat(builder.toString());
        builder.replace(0, builder.length(), MSG_VIEW).append(MSG_LEFT).append(info.viewSize.left).append(MSG_TOP).append(info.viewSize.top).append(MSG_RIGHT).append(info.viewSize.right).append(MSG_BOTTOM).append(info.viewSize.bottom).append(MSG_SIZE).append(info.viewSize.width()).append(", ").append(info.viewSize.height());
        logcat(builder.toString());
    }
}
