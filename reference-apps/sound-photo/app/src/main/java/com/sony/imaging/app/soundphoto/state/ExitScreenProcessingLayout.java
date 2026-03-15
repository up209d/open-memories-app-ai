package com.sony.imaging.app.soundphoto.state;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtil;
import com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtilListener;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class ExitScreenProcessingLayout extends Layout implements SoundPhotoDataBaseUtilListener {
    public static final String TAG = "ExitScreenUpdate";
    private ExitScreenLayout.Callback callback;
    private final int INITIAL_START = 0;
    private final int FIRST_STEP = 4;
    private final long WAIT_TIME = 700;
    private int FINAL_STEP = 100;
    private ProgressBar mProgressBar = null;

    public void setCallback(ExitScreenLayout.Callback c) {
        SoundPhotoDataBaseUtil.getInstance().setListener(this);
        this.callback = c;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.cmn_layout_dialog_string_4lines_progress_bar);
        TextView messageText = (TextView) view.findViewById(R.id.dialog_string_4lines);
        if (messageText != null) {
            messageText.setText(R.string.STRID_AMC_STR_02090);
        }
        return view;
    }

    protected int getLayoutResource() {
        return R.layout.cmn_layout_dialog_string_4lines_progress_bar;
    }

    protected int getProgressBarResource() {
        return R.id.executing_progress;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (SPUtil.getInstance().getMediaCardStatus()) {
            SoundPhotoDataBaseUtil.getInstance().startAutoSynchBackground(true);
        }
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.cmn_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null) {
            label.setText(R.string.STRID_AMC_STR_02090);
        }
        setKeyBeepPattern(5);
        this.mProgressBar = (ProgressBar) getView().findViewById(getProgressBarResource());
        this.mProgressBar.setProgress(0);
        AppLog.trace(TAG, "onResume");
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        return -1;
    }

    public void onProgress(int current, int max) {
        if (this.mProgressBar != null) {
            this.mProgressBar.setProgress((this.mProgressBar.getMax() * current) / max);
        }
    }

    public void onNotify() {
        if (this.mProgressBar != null) {
            AppLog.trace(TAG, " publishProgress not null of mProgressBar Final proceed ");
            this.mProgressBar.setProgress(this.FINAL_STEP);
        }
        closeLayout();
    }

    @Override // com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtilListener
    public void finishedUpdateStatus() {
        AppLog.info(TAG, "TESTTAG************finishedUpdateStatus************");
        onNotify();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, "TESTTAG*********closeLayout");
        if (this.callback != null) {
            SPUtil.getInstance().setRecoveryStatus(true);
            this.callback.onFinish();
            this.callback = null;
        } else {
            AppLog.exit(TAG, "TESTTAG*********closeLayout before finish");
            super.closeLayout();
            if (((BaseApp) getActivity()) != null) {
                ((BaseApp) getActivity()).finish(false);
            }
            AppLog.exit(TAG, "TESTTAG*********closeLayout After finish");
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mProgressBar = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtilListener
    public void progress(int progress, int maxProgress) {
        AppLog.info(TAG, "TESTTAG  progress level " + progress);
        AppLog.info(TAG, "TESTTAG  progress max size " + maxProgress);
        onProgress(progress, maxProgress);
    }
}
