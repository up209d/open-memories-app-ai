package com.sony.imaging.app.pictureeffectplus.playback.layout;

import android.widget.TextView;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase;
import com.sony.imaging.app.pictureeffectplus.R;

/* loaded from: classes.dex */
public class DeleteMultipleConfirmLayout extends EditorConfirmLayoutBase {
    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_deletor_multiple_confirmation;
    }

    protected EditService getEditService() {
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        TextView label = (TextView) getView().findViewById(R.id.confirm_label);
        if (label != null) {
            String text = getResources().getString(android.R.string.description_target_unlock_tablet, Integer.valueOf(getEditService().countSelected()));
            label.setText(text);
        }
    }
}
