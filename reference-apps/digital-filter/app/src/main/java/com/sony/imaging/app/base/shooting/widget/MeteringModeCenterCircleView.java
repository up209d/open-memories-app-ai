package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MeteringModeCenterCircleView extends ActiveImage {
    private NotificationListener mMeteringModeListener;

    public MeteringModeCenterCircleView(Context context) {
        this(context, null);
    }

    public MeteringModeCenterCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpotMeteringArea);
        setImageResource(typedArray.getResourceId(0, android.R.drawable.ic_qs_dnd));
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mMeteringModeListener == null) {
            this.mMeteringModeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.MeteringModeCenterCircleView.1
                private String[] TAGS = {"MeteringMode"};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String arg0) {
                    MeteringModeCenterCircleView.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mMeteringModeListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String meteringmode = MeteringController.getInstance().getValue(null);
        changeVisibilty(meteringmode);
    }

    private void changeVisibilty(String mode) {
        if (MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT.equals(mode)) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }
}
