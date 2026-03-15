package com.sony.imaging.app.srctrl.playback.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;

/* loaded from: classes.dex */
public class PlaybackLayout extends Layout {
    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.playback_main_sid);
        View textView = view.findViewById(R.id.caution_text);
        textView.setVisibility(0);
        return obtainViewFromPool(R.layout.playback_main_sid);
    }
}
