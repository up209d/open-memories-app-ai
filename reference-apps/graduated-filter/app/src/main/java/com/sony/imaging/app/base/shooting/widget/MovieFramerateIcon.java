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
public class MovieFramerateIcon extends ActiveImage {
    private static final HashMap<String, Integer> RESID_DICTIONARY = new HashMap<>();
    private static final String TAG = "MovieFramerateIcon";
    private DatabaseManager dbManager;
    private NotificationListener mListener;
    private TypedArray mTypedArray;

    static {
        RESID_DICTIONARY.put(MovieFormatController.FX_60I, 1);
        RESID_DICTIONARY.put(MovieFormatController.FH_60I, 1);
        RESID_DICTIONARY.put(MovieFormatController.HQ_60I, 1);
        RESID_DICTIONARY.put(MovieFormatController.PS_60P, 0);
        RESID_DICTIONARY.put(MovieFormatController.FX_24P, 4);
        RESID_DICTIONARY.put(MovieFormatController.FH_24P, 4);
        RESID_DICTIONARY.put(MovieFormatController.FX_50I, 3);
        RESID_DICTIONARY.put(MovieFormatController.FH_50I, 3);
        RESID_DICTIONARY.put(MovieFormatController.HQ_50I, 3);
        RESID_DICTIONARY.put(MovieFormatController.PS_50P, 2);
        RESID_DICTIONARY.put(MovieFormatController.FX_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.FH_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.MP4_1080_PS_60P, 0);
        RESID_DICTIONARY.put(MovieFormatController.MP4_1080_PS_50P, 2);
        RESID_DICTIONARY.put(MovieFormatController.MP4_1080_HQ_30P, 6);
        RESID_DICTIONARY.put(MovieFormatController.MP4_1080_HQ_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.MP4_720_30P, 6);
        RESID_DICTIONARY.put(MovieFormatController.MP4_720_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_100P, 8);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_120P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_24P, 4);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_30P, 6);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_50P, 2);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_60P, 0);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_100M_100P, 8);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_100M_120P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_60M_100P, 8);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_60M_120P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_4K_100M_24P, 4);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_4K_100M_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_4K_100M_30P, 6);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_4K_60M_24P, 4);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_4K_60M_25P, 5);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_4K_60M_30P, 6);
    }

    public MovieFramerateIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        this.dbManager = DatabaseManager.getInstance();
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MovieFrameRate);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.MovieFramerateIcon.1
                private String[] TAGS = {CameraNotificationManager.MOVIE_FORMAT};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (MovieFramerateIcon.this.isVisible()) {
                        MovieFramerateIcon.this.setVisibility(0);
                        MovieFramerateIcon.this.refresh();
                    } else {
                        MovieFramerateIcon.this.setVisibility(4);
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
            String setting = MovieFormatController.getInstance().getValue("record_setting");
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
                resid = this.dbManager.getResourceId(DatabaseManager.SETTING_FRAMERATE, setting, panel);
            }
            setImageResource(resid);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return Environment.isMovieAPISupported() && !MovieFormatController.getInstance().isUnavailableSceneFactor("record_setting");
    }
}
