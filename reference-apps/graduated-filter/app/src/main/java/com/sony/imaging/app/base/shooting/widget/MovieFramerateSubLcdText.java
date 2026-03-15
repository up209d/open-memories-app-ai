package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MovieFramerateSubLcdText extends ShootingSubLcdActiveText {
    private static final String[] ADD_NOTIFIER_TAGS = {CameraNotificationManager.MOVIE_FORMAT};
    private static final HashMap<String, Integer> RESID_DICTIONARY_NTSC = new HashMap<>();
    private static final HashMap<String, Integer> RESID_DICTIONARY_PAL = new HashMap<>();

    static {
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_1080_PS, Integer.valueOf(R.string.resolver_no_work_apps_available_resolve));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_1080_HQ, Integer.valueOf(R.string.report));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_720, Integer.valueOf(R.string.report));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_HS120, Integer.valueOf(R.string.resolver_no_work_apps_available_share));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_HS240, Integer.valueOf(R.string.resolver_personal_tab));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_SSLOW, Integer.valueOf(R.string.resolver_no_work_apps_available_share));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.MP4_VGA, Integer.valueOf(R.string.report));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_50M_60P, Integer.valueOf(R.string.resolver_no_work_apps_available_resolve));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_50M_30P, Integer.valueOf(R.string.report));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_50M_24P, Integer.valueOf(R.string.screen_compat_mode_show));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_100M_240P, Integer.valueOf(R.string.resolver_personal_tab));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_60M_240P, Integer.valueOf(R.string.resolver_personal_tab));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_100M_120P, Integer.valueOf(R.string.resolver_no_work_apps_available_share));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_60M_120P, Integer.valueOf(R.string.resolver_no_work_apps_available_share));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_4K_100M_30P, Integer.valueOf(R.string.report));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_4K_60M_30P, Integer.valueOf(R.string.report));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_4K_100M_24P, Integer.valueOf(R.string.screen_compat_mode_show));
        RESID_DICTIONARY_NTSC.put(MovieFormatController.XAVC_4K_60M_24P, Integer.valueOf(R.string.screen_compat_mode_show));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_1080_PS, Integer.valueOf(R.string.screen_lock));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_1080_HQ, Integer.valueOf(R.string.screen_compat_mode_scale));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_720, Integer.valueOf(R.string.screen_compat_mode_scale));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_HS120, Integer.valueOf(R.string.resolver_personal_tab_accessibility));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_HS240, Integer.valueOf(R.string.resolver_turn_on_work_apps));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_SSLOW, Integer.valueOf(R.string.resolver_personal_tab_accessibility));
        RESID_DICTIONARY_PAL.put(MovieFormatController.MP4_VGA, Integer.valueOf(R.string.screen_compat_mode_scale));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_50M_50P, Integer.valueOf(R.string.screen_lock));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_50M_25P, Integer.valueOf(R.string.screen_compat_mode_scale));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_100M_200P, Integer.valueOf(R.string.resolver_turn_on_work_apps));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_60M_200P, Integer.valueOf(R.string.resolver_turn_on_work_apps));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_100M_100P, Integer.valueOf(R.string.resolver_personal_tab_accessibility));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_60M_100P, Integer.valueOf(R.string.resolver_personal_tab_accessibility));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_4K_100M_25P, Integer.valueOf(R.string.screen_compat_mode_scale));
        RESID_DICTIONARY_PAL.put(MovieFormatController.XAVC_4K_60M_25P, Integer.valueOf(R.string.screen_compat_mode_scale));
    }

    public MovieFramerateSubLcdText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        if (Environment.isMovieAPISupported()) {
            setText(getResourceId());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        return Environment.isMovieAPISupported() && !MovieFormatController.getInstance().isUnavailableSceneFactor("record_setting");
    }

    protected int getResourceId() {
        String vsys = MovieFormatController.getBroadcastSystem();
        HashMap<String, Integer> resourceMap = MovieFormatController.NTSC.equals(vsys) ? RESID_DICTIONARY_NTSC : RESID_DICTIONARY_PAL;
        String setting = MovieFormatController.getInstance().getValue("record_setting");
        if (resourceMap.containsKey(setting)) {
            return resourceMap.get(setting).intValue();
        }
        return 0;
    }

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void setVisibility(int visibility) {
        setText("    ");
        super.setVisibility(visibility);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new CustomActiveTextListener();
        }
        return this.mListener;
    }

    /* loaded from: classes.dex */
    public class CustomActiveTextListener extends ShootingSubLcdActiveText.ActiveTextListener {
        public CustomActiveTextListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText.ActiveTextListener, com.sony.imaging.app.util.NotificationListener
        public /* bridge */ /* synthetic */ void onNotify(String x0) {
            super.onNotify(x0);
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText.ActiveTextListener
        public String[] addTags() {
            return MovieFramerateSubLcdText.ADD_NOTIFIER_TAGS;
        }
    }
}
