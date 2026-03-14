package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class DigitalZoomInfo extends ActiveLayout {
    private static final int DISPLAYING_TIME = 2000;
    private static final String LOG_MSG_DIGITAL_ZOOM_INFO = "DigitalZoomInfo";
    private static final String LOG_MSG_GET_NOTIFICATON_LISTNER = "getNotificationListener";
    private static final String LOG_MSG_ON_ATTACHED_TO_WINDOW = "onAttachedToWindow";
    private static final String LOG_MSG_ON_FINISH_INFLATE = "onFinishInflate";
    private static final String LOG_MSG_UPDATE_DIGITAL_ZOOM_ICON_AND_MAG_TEXT = "updateDigitalZoomIconAndMagText";
    private static final String TAG = "DigitalZoomInfo";
    private static final int ZOOM_ICON_CLEAR_IMAGE = 1;
    private static final int ZOOM_ICON_COUNT = 3;
    private static final int ZOOM_ICON_PRECISION = 2;
    private static final int ZOOM_ICON_SMART = 0;
    int category;
    private Runnable disappear;
    private TypedArray mAttr;
    CameraSetting mCameraSetting;
    DigitalZoomController mDigitalZoomController;
    private ImageView mDigitalZoomIconView;
    private String mFormat;
    protected Handler mHandler;
    private boolean mIsVisibleMag;
    private boolean mIsVisibleOnMovieRecording;
    private NotificationListener mNotificationListener;
    private int[] mZoomIconResIds;
    private TextView mZoomMagnificationText;

    public DigitalZoomInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotificationListener = null;
        this.mIsVisibleOnMovieRecording = true;
        this.mIsVisibleMag = false;
        this.mHandler = new Handler();
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.base.shooting.widget.DigitalZoomInfo.1
            @Override // java.lang.Runnable
            public void run() {
                DigitalZoomInfo.this.mZoomMagnificationText.setVisibility(4);
            }
        };
        this.mAttr = context.obtainStyledAttributes(attrs, R.styleable.DigitalZoomInfo);
        this.mCameraSetting = CameraSetting.getInstance();
        this.mDigitalZoomController = DigitalZoomController.getInstance();
        this.mFormat = getResources().getString(android.R.string.region_picker_section_all);
        this.mDigitalZoomIconView = new ImageView(context, attrs);
        this.mZoomMagnificationText = new TextView(context, attrs);
        this.mZoomIconResIds = new int[3];
        this.mZoomIconResIds[0] = this.mAttr.getResourceId(0, -1);
        this.mZoomIconResIds[1] = this.mAttr.getResourceId(1, -1);
        this.mZoomIconResIds[2] = this.mAttr.getResourceId(2, -1);
        this.category = ScalarProperties.getInt("model.category");
        Log.i("DigitalZoomInfo", "DigitalZoomInfo");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        updateVisibleFactor();
        updateDigitalZoomIconAndMagText();
        this.mIsVisibleMag = false;
        super.onAttachedToWindow();
        Log.i("DigitalZoomInfo", LOG_MSG_ON_ATTACHED_TO_WINDOW);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.removeCallbacks(this.disappear);
    }

    private void updateDigitalZoomIconAndMagText() {
        Log.i("DigitalZoomInfo", LOG_MSG_UPDATE_DIGITAL_ZOOM_ICON_AND_MAG_TEXT);
        if (this.mIsVisibleOnMovieRecording) {
            String digitalZoomMode = this.mDigitalZoomController.getCurrentDigitalZoomMode();
            boolean isOverInitValue = this.mDigitalZoomController.isDigitalZoomMagOverInitValue();
            if (isOverInitValue) {
                if (this.mDigitalZoomController.isDigitalZoomStatus()) {
                    if (DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART.equals(digitalZoomMode)) {
                        this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[0]);
                    } else if (DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION.equals(digitalZoomMode)) {
                        this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[1]);
                    } else if (DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION.equals(digitalZoomMode)) {
                        this.mDigitalZoomIconView.setImageResource(this.mZoomIconResIds[2]);
                    }
                    this.mDigitalZoomIconView.setVisibility(0);
                } else {
                    this.mDigitalZoomIconView.setVisibility(4);
                }
                String magText = String.format(this.mFormat, this.mDigitalZoomController.getCurrentDigitalZoomMagnificationText());
                this.mZoomMagnificationText.setText(magText);
                this.mZoomMagnificationText.setVisibility(0);
                return;
            }
            if (2 == this.category && DigitalZoomController.getInstance().isOpticalZoomMagOverInitValue()) {
                this.mDigitalZoomIconView.setVisibility(4);
                String magText2 = String.format(this.mFormat, this.mDigitalZoomController.getCurrentDigitalZoomMagnificationText());
                this.mZoomMagnificationText.setText(magText2);
                this.mZoomMagnificationText.setVisibility(0);
                return;
            }
            this.mDigitalZoomIconView.setVisibility(4);
            this.mZoomMagnificationText.setVisibility(4);
            return;
        }
        this.mDigitalZoomIconView.setVisibility(4);
        this.mZoomMagnificationText.setVisibility(4);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDigitalZoomIconView = (ImageView) findViewById(R.id.zoom_icon);
        this.mZoomMagnificationText = (TextView) findViewById(R.id.magnification);
        setVisibility(4);
        Log.i("DigitalZoomInfo", LOG_MSG_ON_FINISH_INFLATE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public boolean isVisible() {
        return (2 == this.category && DigitalZoomController.getInstance().getDigitalZoomPosition() == 0) ? this.mIsVisibleMag : super.isVisible();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.DigitalZoomInfo.2
                private final String[] tags = {CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, CameraNotificationManager.MOVIE_REC_STOP};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag) || CameraNotificationManager.MOVIE_REC_START_SUCCEEDED.equals(tag) || CameraNotificationManager.MOVIE_REC_STOP.equals(tag)) {
                        DigitalZoomInfo.this.updateVisibleFactor();
                    }
                    DigitalZoomInfo.this.mIsVisibleMag = true;
                    DigitalZoomInfo.this.setVisibility(DigitalZoomInfo.this.isVisible() ? 0 : 4);
                    DigitalZoomInfo.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.tags;
                }
            };
        }
        Log.i("DigitalZoomInfo", LOG_MSG_GET_NOTIFICATON_LISTNER);
        return this.mNotificationListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        this.mHandler.removeCallbacks(this.disappear);
        updateDigitalZoomIconAndMagText();
        if (2 == this.category && DigitalZoomController.getInstance().getDigitalZoomPosition() == 0) {
            this.mHandler.postDelayed(this.disappear, 2000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVisibleFactor() {
        this.mIsVisibleOnMovieRecording = true;
        if (2 == this.mCameraSetting.getCurrentMode()) {
            int category = ScalarProperties.getInt("model.category");
            if (2 == category) {
                this.mIsVisibleOnMovieRecording = false;
            }
        }
    }
}
