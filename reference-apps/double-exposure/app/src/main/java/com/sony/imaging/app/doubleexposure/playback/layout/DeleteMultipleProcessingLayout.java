package com.sony.imaging.app.doubleexposure.playback.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DeleteMultipleProcessingLayout extends EditorProcessingLayoutBase {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView messageText = (TextView) view.findViewById(R.id.dialog_string_4lines);
        if (messageText != null) {
            messageText.setText(android.R.string.dial_number_using);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return R.layout.cmn_layout_dialog_string_4lines_progress_bar;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase
    protected int getProgressBarResource() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return R.id.executing_progress;
    }
}
