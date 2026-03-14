package com.sony.imaging.app.base.playback.layout;

import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.BaseProperties;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public class PseudoRecZoomLayout extends ZoomLayoutBase {
    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_zoom_play;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), BaseProperties.isDownKeyAssignedToIndexTransition() ? android.R.string.js_dialog_before_unload_negative_button : android.R.string.bugreport_screenshot_failure_toast, android.R.string.httpErrorOk));
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        setFooterGuideData(null);
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean previousContents() {
        displayCaution();
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean nextContents() {
        displayCaution();
        return false;
    }

    protected void displayCaution() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!mgr.isVirtualEmpty()) {
            CautionUtilityClass.getInstance().requestTrigger(585);
        }
    }
}
