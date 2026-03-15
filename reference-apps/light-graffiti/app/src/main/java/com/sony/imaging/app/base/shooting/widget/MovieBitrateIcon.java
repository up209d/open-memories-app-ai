package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MovieBitrateIcon extends ActiveImage {
    private static final String TAG = "MovieBitrateIcon";
    private NotificationListener mListener;
    private TypedArray mTypedArray;
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final HashMap<String, Integer> RESID_DICTIONARY = new HashMap<>();

    static {
        RESID_DICTIONARY.put(MovieFormatController.FX_60I, 0);
        RESID_DICTIONARY.put(MovieFormatController.FH_60I, 1);
        RESID_DICTIONARY.put(MovieFormatController.HQ_60I, 2);
        RESID_DICTIONARY.put(MovieFormatController.PS_60P, 3);
        RESID_DICTIONARY.put(MovieFormatController.FX_24P, 0);
        RESID_DICTIONARY.put(MovieFormatController.FH_24P, 1);
        RESID_DICTIONARY.put(MovieFormatController.FX_50I, 0);
        RESID_DICTIONARY.put(MovieFormatController.FH_50I, 1);
        RESID_DICTIONARY.put(MovieFormatController.HQ_50I, 2);
        RESID_DICTIONARY.put(MovieFormatController.PS_50P, 3);
        RESID_DICTIONARY.put(MovieFormatController.FX_25P, 0);
        RESID_DICTIONARY.put(MovieFormatController.FH_25P, 1);
        RESID_DICTIONARY.put(MovieFormatController.MP4_1080, 4);
        RESID_DICTIONARY.put(MovieFormatController.MP4_720, 5);
        RESID_DICTIONARY.put(MovieFormatController.MP4_VGA, 6);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_100P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_120P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_24P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_25P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_30P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_50P, 7);
        RESID_DICTIONARY.put(MovieFormatController.XAVC_50M_60P, 7);
    }

    public MovieBitrateIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MovieBitRate);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.MovieBitrateIcon.1
                private String[] TAGS = {CameraNotificationManager.MOVIE_FORMAT};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (MovieBitrateIcon.this.isVisible()) {
                        MovieBitrateIcon.this.setVisibility(0);
                        MovieBitrateIcon.this.refresh();
                    } else {
                        MovieBitrateIcon.this.setVisibility(4);
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
            String setting = MovieFormatController.getInstance().getValue(MovieFormatController.MOVIE_RECORD_SETTING);
            Integer styleable = RESID_DICTIONARY.get(setting);
            int resid = 0;
            if (styleable != null) {
                resid = this.mTypedArray.getResourceId(styleable.intValue(), 0);
            }
            setImageResource(resid);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return Environment.isMovieAPISupported() && !MovieFormatController.getInstance().isUnavailableSceneFactor(MovieFormatController.MOVIE_RECORD_SETTING);
    }
}
