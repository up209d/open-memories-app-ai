package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class FourkSubLcdIcon extends ShootingSubLcdActiveIcon {
    private static final String[] ADD_NOTIFIER_TAGS = {CameraNotificationManager.MOVIE_FORMAT};

    public FourkSubLcdIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
    }

    protected boolean is4k() {
        String setting = MovieFormatController.getInstance().getValue("movie_format");
        return MovieFormatController.XAVC_S_4K.equals(setting);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        if (Environment.isMovieAPISupported() && !MovieFormatController.getInstance().isUnavailableSceneFactor("record_setting")) {
            return is4k();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.SubLcdIconView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon, com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new CustomActiveIconListener();
        }
        return this.mListener;
    }

    /* loaded from: classes.dex */
    public class CustomActiveIconListener extends ShootingSubLcdActiveIcon.ActiveIconListener {
        public CustomActiveIconListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon.ActiveIconListener, com.sony.imaging.app.util.NotificationListener
        public /* bridge */ /* synthetic */ void onNotify(String x0) {
            super.onNotify(x0);
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon.ActiveIconListener
        public String[] addTags() {
            return FourkSubLcdIcon.ADD_NOTIFIER_TAGS;
        }
    }
}
