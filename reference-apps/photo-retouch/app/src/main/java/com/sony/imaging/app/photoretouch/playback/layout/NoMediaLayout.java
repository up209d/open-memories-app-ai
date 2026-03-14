package com.sony.imaging.app.photoretouch.playback.layout;

import android.view.KeyEvent;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;

/* loaded from: classes.dex */
public class NoMediaLayout extends PseudoRecNoFileLayout {
    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.no_media;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 14;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                return -1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout
    protected void displayCaution() {
        TextView textView = (TextView) getView().findViewById(R.id.message_text);
        String message = getActivity().getResources().getString(R.string.STRID_AMC_STR_01911);
        textView.setText(message);
        if (getLayout(Constant.ID_MESSAGEALERT).getView() != null) {
            getLayout(Constant.ID_MESSAGEALERT).closeLayout();
        }
        setKeyBeepPattern(0);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (getLayout(Constant.ID_MESSAGEALERT).getView() != null) {
            getLayout(Constant.ID_MESSAGEALERT).closeLayout();
        }
        super.onDestroyView();
    }
}
