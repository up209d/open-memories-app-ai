package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.ProgressManager;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class TransferringLayoutSync extends Layout {
    private static final String TAG = TransferringLayoutSync.class.getSimpleName();
    protected View mCurrentView;
    protected AppNameConstantView mScreenTitleView;
    TextView mButtonText = null;
    TextView mTransferNumber = null;
    ProgressBar mProgressBar = null;
    int mNumOfTranserFiles = 0;
    private NotificationListener progressListener = new NotificationListener() { // from class: com.sony.imaging.app.synctosmartphone.layout.TransferringLayoutSync.1
        private final String[] tags = {ConstantsSync.TRANSFER_PROGRESS};

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int progress = NetworkStateUtil.getInstance().getNumberOfNotifySyncImage();
            TransferringLayoutSync.this.mTransferNumber.setText(progress + StringBuilderThreadLocal.SLASH + String.valueOf(TransferringLayoutSync.this.mNumOfTranserFiles));
            TransferringLayoutSync.this.mProgressBar.setProgress(progress);
        }
    };

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.transferring_layout_panel);
        }
        createView();
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mTransferNumber = (TextView) this.mCurrentView.findViewById(R.id.transfer_number_text);
        this.mButtonText = (TextView) this.mCurrentView.findViewById(R.id.button_text);
        Log.d(TAG, "onCreateView = " + (this.mCurrentView != null));
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        SyncBackUpUtil.getInstance().getStartUTC(0L);
        int numOfNotifySyncImage = NetworkStateUtil.getInstance().getNumberOfNotifySyncImage();
        this.mNumOfTranserFiles = NetworkStateUtil.getInstance().getNumOfTotalSyncImage();
        this.mTransferNumber.setText(String.valueOf(numOfNotifySyncImage) + StringBuilderThreadLocal.SLASH + String.valueOf(this.mNumOfTranserFiles));
        this.mProgressBar = (ProgressBar) getView().findViewById(R.id.progress_bar_foreground);
        if (this.mProgressBar != null) {
            this.mProgressBar.setMax(this.mNumOfTranserFiles);
            this.mProgressBar.setProgress(numOfNotifySyncImage);
        }
        this.mButtonText.setSelected(true);
        ProgressManager.getInstance().setNotificationListener(this.progressListener);
        this.mScreenTitleView.setVisibility(0);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mScreenTitleView.setVisibility(4);
        ProgressManager.getInstance().removeNotificationListener(this.progressListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey = 0");
        return 0;
    }

    private void deInitializeView() {
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }
}
