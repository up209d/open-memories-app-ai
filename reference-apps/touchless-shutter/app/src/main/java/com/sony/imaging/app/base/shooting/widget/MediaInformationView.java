package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.Oscillator;

/* loaded from: classes.dex */
public class MediaInformationView extends RelativeLayout implements Oscillator.OnPeriodListener {
    private static final int ACCESSING = 1;
    private static final int NOCARD = 0;
    private static final int REMAINING_MAX = 9999;
    private SparseArray<Drawable> ICON_STATE_RESID_MAP;
    private final String[] TAGS_Still;
    private CameraListener mCameraListener;
    private CameraNotificationManager mCameraNotifier;
    private RelativeLayout.LayoutParams mLayoutParams;
    private MediaMountEventListener mMediaListener;
    protected MediaNotificationManager mMediaNotifier;
    private ImageView mNocardOrAccessingImage;
    protected DigitView mRemainingView;

    public MediaInformationView(Context context) {
        this(context, null);
    }

    public MediaInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS_Still = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.MediaInformationIcon);
        Drawable nocard = attr.getDrawable(0);
        Drawable accessing = attr.getDrawable(1);
        Drawable memorycard = attr.getDrawable(2);
        if (memorycard != null) {
            int iconWidth = attr.getDimensionPixelSize(3, 0);
            int iconHeight = attr.getDimensionPixelSize(4, 0);
            memorycard.setBounds(0, 0, iconWidth, iconHeight);
        }
        attr.recycle();
        this.ICON_STATE_RESID_MAP = new SparseArray<>();
        this.ICON_STATE_RESID_MAP.append(0, nocard);
        this.ICON_STATE_RESID_MAP.append(1, accessing);
        this.ICON_STATE_RESID_MAP.append(3, memorycard);
        this.ICON_STATE_RESID_MAP.append(4, memorycard);
        this.ICON_STATE_RESID_MAP.append(2, memorycard);
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
        this.mCameraNotifier = CameraNotificationManager.getInstance();
        this.mCameraListener = new CameraListener();
        this.mLayoutParams = new RelativeLayout.LayoutParams(-1, -1);
        createRemainingView(context, attrs);
        this.mNocardOrAccessingImage = new ImageView(context);
        setVisibility(4);
    }

    protected void createRemainingView(Context context, AttributeSet attrs) {
        this.mRemainingView = new DigitView(context, attrs);
        this.mRemainingView.setTextAlign(19);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
        this.mCameraNotifier.setNotificationListener(this.mCameraListener);
        changeVisibilty();
        refresh();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        this.mCameraNotifier.removeNotificationListener(this.mCameraListener);
    }

    private void showNocardOrAccessing(int nocardOrAccessing) {
        if (indexOfChild(this.mRemainingView) >= 0) {
            this.mRemainingView.clear();
            removeView(this.mRemainingView);
        }
        if (indexOfChild(this.mNocardOrAccessingImage) < 0) {
            this.mNocardOrAccessingImage.setScaleType(ImageView.ScaleType.FIT_START);
            addView(this.mNocardOrAccessingImage, this.mLayoutParams);
            this.mNocardOrAccessingImage.bringToFront();
        }
        if (nocardOrAccessing == 0) {
            this.mNocardOrAccessingImage.setImageDrawable(this.ICON_STATE_RESID_MAP.get(0));
            Oscillator.attach(8, this);
        } else if (nocardOrAccessing == 1) {
            Oscillator.detach(8, this);
            this.mNocardOrAccessingImage.setVisibility(0);
            this.mNocardOrAccessingImage.setImageDrawable(this.ICON_STATE_RESID_MAP.get(1));
        }
    }

    private void showMediaTypeAndRemaining(int state, int remaining) {
        if (indexOfChild(this.mNocardOrAccessingImage) >= 0) {
            Oscillator.detach(8, this);
            this.mNocardOrAccessingImage.setImageDrawable(null);
            removeView(this.mNocardOrAccessingImage);
        }
        Drawable memorycard = this.ICON_STATE_RESID_MAP.get(state);
        if (memorycard != null) {
            if (indexOfChild(this.mRemainingView) < 0) {
                addView(this.mRemainingView, this.mLayoutParams);
                this.mRemainingView.bringToFront();
            }
            this.mRemainingView.setImage(this.ICON_STATE_RESID_MAP.get(state));
            showRemaining(remaining);
        }
    }

    protected void showRemaining(int remaining) {
        if (getVisibility() != 4) {
            int value = Math.min(remaining, REMAINING_MAX);
            String text = value < 0 ? "" : Integer.toString(value);
            this.mRemainingView.setValue(text);
            if (value == 0) {
                this.mRemainingView.blink(true);
                this.mRemainingView.highlight(true);
            } else {
                this.mRemainingView.blink(false);
                this.mRemainingView.highlight(false);
            }
        }
    }

    /* loaded from: classes.dex */
    class MediaMountEventListener implements NotificationListener {
        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MediaInformationView.this.getMediaMountEvent();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            MediaInformationView.this.displayInfo(tag);
        }
    }

    protected String[] getMediaMountEvent() {
        return this.TAGS_Still;
    }

    protected void displayInfo(String tag) {
        if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
            refresh();
        } else if (tag.equals(MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE) && this.mMediaNotifier.isMounted()) {
            showRemaining(getRemaining());
        }
    }

    /* loaded from: classes.dex */
    class CameraListener implements NotificationListener {
        private final String[] TAGS = {CameraNotificationManager.APSC_MODE_CHANGED, CameraNotificationManager.REC_MODE_CHANGED};

        CameraListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (!MediaInformationView.this.mMediaNotifier.isNoCard()) {
                MediaInformationView.this.showRemaining(MediaInformationView.this.getRemaining());
            }
            if (tag.equals(CameraNotificationManager.REC_MODE_CHANGED)) {
                MediaInformationView.this.changeVisibilty();
            }
        }
    }

    @Override // com.sony.imaging.app.util.Oscillator.OnPeriodListener
    public void onPeriod(int Hz, boolean highlow) {
        if (8 == Hz) {
            int visibility = highlow ? 0 : 4;
            this.mNocardOrAccessingImage.setVisibility(visibility);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refresh() {
        if (this.mMediaNotifier.isNoCard()) {
            showNocardOrAccessing(0);
            return;
        }
        int state = this.mMediaNotifier.getMediaState();
        if (state == 1) {
            showNocardOrAccessing(1);
        } else {
            showMediaTypeAndRemaining(state, getRemaining());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isMovieMode() {
        return Environment.isMovieAPISupported() && 2 == ExecutorCreator.getInstance().getRecordingMode();
    }

    protected int getRemaining() {
        return this.mMediaNotifier.getRemaining();
    }

    protected void changeVisibilty() {
        int visible = 4;
        int current = getVisibility();
        if (!isMovieMode()) {
            visible = 0;
        }
        if (visible != current) {
            if (visible == 4) {
                this.mRemainingView.setValue("");
            }
            setVisibility(visible);
        }
    }
}
