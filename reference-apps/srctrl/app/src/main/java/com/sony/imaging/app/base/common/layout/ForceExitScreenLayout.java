package com.sony.imaging.app.base.common.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class ForceExitScreenLayout extends Layout {
    public static final String DIALOG_MESSAGE = "MESSAGE";
    private Callback callback;

    /* loaded from: classes.dex */
    public interface Callback {
        void onClose();

        void onFinish();
    }

    public void setCallback(Callback c) {
        this.callback = c;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = obtainViewFromPool(R.layout.layout_dlt06_1btn_msg_dlg);
        TextView ok = (TextView) v.findViewById(R.id.button_ok);
        if (ok != null) {
            ok.setSelected(true);
        }
        return v;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.cmn_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.years));
        }
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null) {
            int msgId = 0;
            if (this.data != null) {
                msgId = this.data.getInt(DIALOG_MESSAGE);
            }
            if (msgId > 0) {
                label.setText(msgId);
            } else {
                label.setText("");
            }
        }
        setKeyBeepPattern(5);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (232 == event.getScanCode() && CustomizableFunction.Unchanged.equals(func)) {
            this.callback.onFinish();
            return -1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_BLACK;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }
}
