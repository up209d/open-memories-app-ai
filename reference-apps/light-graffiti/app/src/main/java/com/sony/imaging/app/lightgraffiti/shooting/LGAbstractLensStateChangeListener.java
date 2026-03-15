package com.sony.imaging.app.lightgraffiti.shooting;

import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public abstract class LGAbstractLensStateChangeListener implements NotificationListener {
    private final int LENSSTATE_CHANGE_DELAY_INTERVAL = 100;

    protected abstract void onLensStateChanged();

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{LGConstants.LG_MESSAGE_LENS_STATE_CHANGED};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        Log.d("LGAbstractLensStateChangeListener", "onNotify");
        if (tag.equals(LGConstants.LG_MESSAGE_LENS_STATE_CHANGED)) {
            Runnable attachLensHandle = new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.d("LGAbstractLensStateChangeListener", "Runnable run");
                    if (!LGUtility.getInstance().isLensAttachEventReady) {
                        Log.d("LGAbstractLensStateChangeListener", "call onLensStateChanged");
                        LGAbstractLensStateChangeListener.this.onLensStateChanged();
                    }
                }
            };
            Log.d("LGAbstractLensStateChangeListener", "postDelayed");
            new Handler().postDelayed(attachLensHandle, 100L);
        }
    }
}
