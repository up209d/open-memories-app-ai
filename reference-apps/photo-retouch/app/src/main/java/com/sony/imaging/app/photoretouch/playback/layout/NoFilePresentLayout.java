package com.sony.imaging.app.photoretouch.playback.layout;

import android.util.Log;
import com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;

/* loaded from: classes.dex */
public class NoFilePresentLayout extends BrowserIndexNoFileLayout {
    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        Log.d(this.TAG, "=====onPause...PhotoRetouchIndexLayout");
        closeAlertLayout();
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.no_image_layout;
    }

    private void closeAlertLayout() {
        if (getLayout(Constant.ID_MESSAGEALERT).getView() != null) {
            getLayout(Constant.ID_MESSAGEALERT).closeLayout();
        } else if (getLayout(Constant.ID_MESSAGEUNSUPPORTEDFILE).getView() != null) {
            getLayout(Constant.ID_MESSAGEUNSUPPORTEDFILE).closeLayout();
        } else if (getLayout(Constant.ID_MESSAGEAPPSTART).getView() != null) {
            getLayout(Constant.ID_MESSAGEAPPSTART).closeLayout();
        }
    }
}
