package com.sony.imaging.app.soundphoto.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.layout.BrowserDialog4kStart;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.soundphoto.R;

/* loaded from: classes.dex */
public class SPBrowserDialog4kStart extends BrowserDialog4kStart {
    @Override // com.sony.imaging.app.base.playback.layout.BrowserDialog4kStart, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView text4Lines = (TextView) view.findViewById(R.id.dialog_string_4lines);
        if (text4Lines != null) {
            text4Lines.setText(R.string.STRID_FUNC_4K_OUTPUT_MSG_START_CONFIRMATION_NONE);
        }
        TextView textButton = (TextView) view.findViewById(R.id.button);
        if (textButton != null) {
            textButton.setText(android.R.string.phoneTypeOther);
        }
        setFooterGuideData(new FooterGuideDataResId(getActivity(), android.R.string.years));
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserDialog4kStart, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        if (result == 0) {
            if (!CustomizableFunction.Unchanged.equals(func)) {
                return -1;
            }
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
                case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
                case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
                case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
                case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                    return result;
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                    return -1;
                default:
                    return -1;
            }
        }
        return result;
    }
}
