package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MovieFormatIcon extends ActiveImage {
    private static final HashMap<String, Integer> RESID_DICTIONARY = new HashMap<>();
    private static final String TAG = " MovieFormatIcon";
    private DatabaseManager dbManager;
    private NotificationListener mListener;
    private TypedArray mTypedArray;

    static {
        RESID_DICTIONARY.put(MovieFormatController.AVCHD, 1);
        RESID_DICTIONARY.put(MovieFormatController.MP4, 0);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_S, 2);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_S_4K, 3);
    }

    public MovieFormatIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        this.dbManager = DatabaseManager.getInstance();
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MovieFormat);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.MovieFormatIcon.1
                private String[] TAGS = {CameraNotificationManager.MOVIE_FORMAT};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (MovieFormatIcon.this.isVisible()) {
                        MovieFormatIcon.this.setVisibility(0);
                        MovieFormatIcon.this.refresh();
                    } else {
                        MovieFormatIcon.this.setVisibility(4);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        if (Environment.isMovieAPISupported()) {
            String setting = MovieFormatController.getInstance().getValue("movie_format");
            int resid = 0;
            if (!DatabaseManager.getInstance().isReady()) {
                Integer styleable = RESID_DICTIONARY.get(setting);
                if (styleable != null) {
                    resid = this.mTypedArray.getResourceId(styleable.intValue(), 0);
                }
            } else {
                String panel = null;
                if (DisplayModeObserver.getInstance().getActiveDevice() == 0) {
                    panel = DatabaseManager.DISPLAY_PANEL;
                } else if (DisplayModeObserver.getInstance().getActiveDevice() == 1) {
                    panel = DatabaseManager.DISPLAY_EVF;
                } else if (DisplayModeObserver.getInstance().getActiveDevice() == 2) {
                    panel = DatabaseManager.DISPLAY_PANEL;
                }
                resid = this.dbManager.getResourceId(DatabaseManager.SETTING_FORMAT, setting, panel);
            }
            setImageResource(resid);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        return Environment.isMovieAPISupported() && !MovieFormatController.getInstance().isUnavailableSceneFactor("record_setting");
    }
}
