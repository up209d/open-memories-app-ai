package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.LapseController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextIntervalLapse extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextIntervalLapse.class.getSimpleName();

    public RotationalSubLcdTextIntervalLapse(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextIntervalLapse(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextIntervalLapse(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = this.mText;
        LapseController lapseController = LapseController.getInstance();
        String interval = null;
        try {
            interval = lapseController.getValue("Interval");
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        if (LapseController.INTERVAL_1S.equals(interval)) {
            String ret2 = getResources().getString(R.string.mediasize_na_foolscap);
            return ret2;
        }
        if (LapseController.INTERVAL_2S.equals(interval)) {
            String ret3 = getResources().getString(R.string.mediasize_na_gvrnmt_letter);
            return ret3;
        }
        if (LapseController.INTERVAL_5S.equals(interval)) {
            String ret4 = getResources().getString(R.string.mediasize_na_index_3x5);
            return ret4;
        }
        if (LapseController.INTERVAL_10S.equals(interval)) {
            String ret5 = getResources().getString(R.string.mediasize_na_index_4x6);
            return ret5;
        }
        if (LapseController.INTERVAL_30S.equals(interval)) {
            String ret6 = getResources().getString(R.string.mediasize_na_index_5x8);
            return ret6;
        }
        if (LapseController.INTERVAL_60S.equals(interval)) {
            String ret7 = getResources().getString(R.string.mediasize_na_junior_legal);
            return ret7;
        }
        Log.e(TAG, "Incorrect value:" + interval);
        return ret;
    }
}
