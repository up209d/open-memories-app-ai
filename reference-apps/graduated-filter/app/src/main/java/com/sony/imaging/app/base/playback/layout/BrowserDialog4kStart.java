package com.sony.imaging.app.base.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class BrowserDialog4kStart extends PlayLayoutBase implements I4kPlaybackTrigger {
    private static final String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE};
    protected I4kPlaybackTriggerFunction m4kPbTrigger;
    protected NotificationListener mDisplayModeListener;

    @Override // com.sony.imaging.app.base.playback.layout.I4kPlaybackTrigger
    public void set4kPlaybackFunction(I4kPlaybackTriggerFunction trigger) {
        this.m4kPbTrigger = trigger;
    }

    protected NotificationListener getDisplayObserverListener() {
        return new DisplayModeListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DisplayModeListener implements NotificationListener {
        protected DisplayModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return BrowserDialog4kStart.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag) && !DisplayModeObserver.getInstance().is4kDeviceAvailable() && BrowserDialog4kStart.this.m4kPbTrigger != null) {
                BrowserDialog4kStart.this.m4kPbTrigger.close4kStartingDialog();
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.cmn_layout_dialog_string_4lines_one_button;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView text4Lines = (TextView) view.findViewById(R.id.dialog_string_4lines);
        if (text4Lines != null) {
            text4Lines.setText(android.R.string.kg_reordering_delete_drop_target_text);
        }
        TextView textButton = (TextView) view.findViewById(R.id.button);
        if (textButton != null) {
            textButton.setText(android.R.string.phoneTypeOther);
        }
        setFooterGuideData(new FooterGuideDataResId(getActivity(), android.R.string.years));
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.cmn_footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mDisplayModeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
        }
        this.mDisplayModeListener = getDisplayObserverListener();
        DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayModeListener);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mDisplayModeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
            this.mDisplayModeListener = null;
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (this.m4kPbTrigger != null) {
            this.m4kPbTrigger.close4kStartingDialog();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.m4kPbTrigger != null) {
            this.m4kPbTrigger.start4kPlayback();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        if (result == 0) {
            if (!CustomizableFunction.Unchanged.equals(func)) {
                return -1;
            }
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
                case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
                case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
                case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
                case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                    return result;
                default:
                    return -1;
            }
        }
        return result;
    }
}
