package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.AbstractRelativeLayoutGroup;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.EventReducer;
import com.sony.imaging.app.util.IVisibilityChanged;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class DatabaseIcon extends ImageView implements AbstractRelativeLayoutGroup.IVisibilityChange {
    private static final int DB_ERROR = -1;
    private static final int DB_FULL = 1;
    private static final String INH_1 = "INH_FACTOR_CONTENT_FULL_FOR_STILL";
    private static final String INH_2 = "INH_FACTOR_NEED_REPAIR_AVINDEX";
    private static final String INH_AVCHD_ERROR = "INH_FACTOR_NEED_REPAIR_AVCHD";
    private static final String INH_AVCHD_FULL = "INH_FACTOR_CONTENT_FULL_FOR_AVCHD_REC";
    private static final String INH_MP4_FULL = "INH_FACTOR_CONTENT_FULL_FOR_MP4";
    private static final String INH_XAVC_FULL = "INH_FACTOR_CONTENT_FULL_FOR_XAVC";
    private static final String TAG = DatabaseIcon.class.getSimpleName();
    private boolean errorAVCHD;
    private boolean errorAvindex;
    private boolean fullForAVCHD;
    private boolean fullForMP4;
    private boolean fullForStill;
    private boolean fullForXAVC;
    private MediaMountEventListener mMediaListener;
    private MediaNotificationManager mMediaNotifier;
    protected IVisibilityChanged mNotifyTarget;
    private TypedArray mTypedArray;
    private EventReducer reducer;
    private Runnable update;

    public DatabaseIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.fullForStill = false;
        this.errorAvindex = false;
        this.fullForMP4 = false;
        this.fullForAVCHD = false;
        this.fullForXAVC = false;
        this.errorAVCHD = false;
        this.update = new Runnable() { // from class: com.sony.imaging.app.base.common.widget.DatabaseIcon.1
            @Override // java.lang.Runnable
            public void run() {
                AvailableInfo.update();
                String[] media = AvindexStore.getExternalMediaIds();
                DatabaseIcon.this.fullForStill = AvailableInfo.isFactor(DatabaseIcon.INH_1, media[0]);
                DatabaseIcon.this.errorAvindex = AvailableInfo.isFactor(DatabaseIcon.INH_2, media[0]);
                DatabaseIcon.this.fullForMP4 = AvailableInfo.isFactor(DatabaseIcon.INH_MP4_FULL, media[0]);
                DatabaseIcon.this.fullForAVCHD = AvailableInfo.isFactor(DatabaseIcon.INH_AVCHD_FULL, media[0]);
                DatabaseIcon.this.fullForXAVC = AvailableInfo.isFactor(DatabaseIcon.INH_XAVC_FULL, media[0]);
                DatabaseIcon.this.errorAVCHD = AvailableInfo.isFactor(DatabaseIcon.INH_AVCHD_ERROR, media[0]);
                DatabaseIcon.this.refreshIcon();
            }
        };
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DatabaseIcon);
        setVisibility(4);
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
        this.reducer = new EventReducer(this.update);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshIcon() {
        Drawable d;
        int dbState = 0;
        if (ExecutorCreator.getInstance().getRecordingMode() == 2) {
            String movieFormat = MovieFormatController.getInstance().getValue(MovieFormatController.MOVIE_FORMAT);
            if (MovieFormatController.MP4.equals(movieFormat)) {
                if (this.errorAvindex) {
                    dbState = -1;
                } else if (this.fullForMP4 || this.fullForStill) {
                    dbState = 1;
                } else {
                    dbState = 0;
                }
            } else if (MovieFormatController.AVCHD.equals(movieFormat)) {
                if (this.errorAvindex || this.errorAVCHD) {
                    dbState = -1;
                } else if (this.fullForAVCHD) {
                    dbState = 1;
                } else {
                    dbState = 0;
                }
            } else if (MovieFormatController.XAVC_S.equals(movieFormat)) {
                if (this.errorAvindex) {
                    dbState = -1;
                } else if (this.fullForXAVC || this.fullForStill) {
                    dbState = 1;
                } else {
                    dbState = 0;
                }
            }
        } else if (this.errorAvindex) {
            dbState = -1;
        } else if (this.fullForStill) {
            dbState = 1;
        } else {
            dbState = 0;
        }
        switch (dbState) {
            case -1:
                d = this.mTypedArray.getDrawable(0);
                setVisibility(0);
                break;
            case 0:
            default:
                d = null;
                setVisibility(4);
                break;
            case 1:
                d = this.mTypedArray.getDrawable(1);
                setVisibility(0);
                break;
        }
        if (d != null) {
            setImageDrawable(d);
        }
    }

    void chechDatabaseFull() {
        this.reducer.push();
    }

    void checkDatabaseError() {
        this.reducer.push();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
        this.reducer.push();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        this.reducer.clear();
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup.IVisibilityChange
    public void setCallback(IVisibilityChanged target) {
        this.mNotifyTarget = target;
    }

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        int current = getVisibility();
        super.setVisibility(visibility);
        if (this.mNotifyTarget != null && visibility != current) {
            this.mNotifyTarget.onVisibilityChanged();
        }
    }

    /* loaded from: classes.dex */
    private class MediaMountEventListener implements NotificationListener {
        private static final String LOG_RECEIVED = " is received.";
        private final String[] tags;

        private MediaMountEventListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(DatabaseIcon.TAG, tag + LOG_RECEIVED);
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE) || tag.equals(MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE)) {
                DatabaseIcon.this.reducer.push();
            }
        }
    }
}
