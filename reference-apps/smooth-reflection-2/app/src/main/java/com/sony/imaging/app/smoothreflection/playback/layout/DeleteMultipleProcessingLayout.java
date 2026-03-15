package com.sony.imaging.app.smoothreflection.playback.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase;
import com.sony.imaging.app.smoothreflection.R;

/* loaded from: classes.dex */
public class DeleteMultipleProcessingLayout extends EditorProcessingLayoutBase {
    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView messageText = (TextView) view.findViewById(R.id.dialog_string_4lines);
        if (messageText != null) {
            messageText.setText(android.R.string.dial_number_using);
        }
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.cmn_layout_dialog_string_4lines_progress_bar;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase
    protected int getProgressBarResource() {
        return R.id.executing_progress;
    }
}
