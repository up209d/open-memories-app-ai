package com.sony.imaging.app.startrails.playback.layout;

import android.content.ContentResolver;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.BaseProperties;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.database.DBConstants;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.database.StarTrailsBO;
import com.sony.imaging.app.startrails.playback.STPlayRootContainer;
import com.sony.imaging.app.startrails.playback.controller.PlayBackController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PlaybackLayout extends OptimizedImageLayoutBase implements IModableLayout, NotificationListener {
    private static final int PAUSE = 1;
    private static final int PLAY = 0;
    private static final int SHOW_FIRST = 2;
    private static final String TAG = "PLAYBACK";
    private static final Rect YUV_LAYOUT_NORMAL = new Rect(0, 0, 640, 480);
    private static Rect mScratchRect = new Rect();
    private ImageView mAppIcon;
    private ViewGroup mCurrentView;
    private String mDate;
    private TextView mDateView;
    private ImageView mFolderIcon;
    private String mFolderName;
    private TextView mFolderNumberView;
    private Handler mHandler;
    private TextView mNoItemsToDisplayView;
    private OptimizedImage mOptImage;
    private OptimizedImageView mOptImageView;
    private TextView mShootingNumView;
    private TextView mThemeNameView;
    private String mTime;
    private TextView mTimeView;
    ArrayList<StarTrailsBO> stBOList;
    public boolean mTransitionToDeletion = false;
    public boolean mSetOptimizedImage = false;
    public ContentResolver mResolver = null;
    private int mCurrentItemToPlay = -1;
    private int mShootingNum = 0;
    private boolean mIsPlaying = false;
    private int mCurrItemOnDisplay = 0;
    private final int INTEGER_CONSTANT_ZERO = 0;
    private PlayBackController pbCntl = null;
    private StartRunnableTask mStartRunnableTask = null;
    private Runnable mWaitRunnable = null;
    private Rect targetLayout = null;
    private OptimizedImage optImage = null;
    private RelativeLayout mErrorLayout = null;
    private FooterGuide mFooterGuide = null;
    private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, STConstants.UPDATE_DELETED_FRAME, STConstants.REFRESH_PLAYBACK_VIEW, DisplayModeObserver.TAG_DEVICE_CHANGE};
    public OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 1);

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
        } catch (Exception e) {
        }
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(getLayoutResource());
        }
        this.mOptImageView = this.mCurrentView.findViewById(R.id.imageSingle);
        this.mOptImageView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.startrails.playback.layout.PlaybackLayout.1
            public void onDisplay(int errCd) {
                PlaybackLayout.this.mSetOptimizedImage = true;
            }
        });
        this.mDateView = (TextView) this.mCurrentView.findViewById(R.id.date);
        this.mTimeView = (TextView) this.mCurrentView.findViewById(R.id.time);
        this.mShootingNumView = (TextView) this.mCurrentView.findViewById(R.id.shooting_num);
        this.mThemeNameView = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        this.mAppIcon = (ImageView) this.mCurrentView.findViewById(R.id.app_icon);
        updateAppIcon();
        this.mFolderIcon = (ImageView) this.mCurrentView.findViewById(R.id.folder_icon);
        this.mFolderNumberView = (TextView) this.mCurrentView.findViewById(R.id.folder_number);
        this.mNoItemsToDisplayView = (TextView) this.mCurrentView.findViewById(R.id.no_item_view);
        this.mErrorLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.pb_parts_cmn_singlepb_image_err);
        this.mFolderName = "";
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setVisibility(4);
        this.mFolderIcon.setImageResource(17304123);
        this.mResolver = getActivity().getContentResolver();
        this.pbCntl = PlayBackController.getInstance();
        this.pbCntl.setContentResolver(this.mResolver);
        if (this.mHandler == null) {
            this.mHandler = new MyHandler();
        }
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.e("INSIDE", "onPause");
        if (this.mListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(1);
        }
        if (this.pbCntl != null) {
            this.pbCntl.releaseAllAllocatedData();
        }
        deInitialize();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        closeAllChildLayout();
        super.closeLayout();
    }

    private void closeAllChildLayout() {
        AppLog.trace(TAG, "closeAllChildLayout started");
        try {
            DeleteMultipleProcessingLayout layout = (DeleteMultipleProcessingLayout) getLayout(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT);
            layout.closeLayout();
        } catch (Exception e) {
        }
        try {
            DeleteMultipleConfirmLayout layout2 = (DeleteMultipleConfirmLayout) getLayout(STPlayRootContainer.ID_DELETE_MULTIPLE_LAYOUT);
            layout2.closeLayout();
        } catch (Exception e2) {
        }
        AppLog.trace(TAG, "closeAllChildLayout finished");
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mSetOptimizedImage = true;
        CameraNotificationManager.getInstance().requestNotify(STBackUpKey.PB_DISPLAY_MODE_KEY);
        if (validateDBStatus()) {
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 3);
            Log.e("INSIDE", "onResume");
            ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
            CameraNotificationManager.getInstance().setNotificationListener(this);
            if (this.mListener != null) {
                DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
            }
            setKeyBeepPattern(getResumeKeyBeepPattern());
            initialize();
            if (this.pbCntl != null && this.stBOList.size() != 0) {
                AppLog.enter(TAG, "stBOList.size() " + this.stBOList.size());
                this.optImage = this.pbCntl.getPlayBackOptimizedImage();
            }
            if (this.optImage != null) {
                updateOptimizedImageLayoutParam();
            }
            viewVisibility();
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        this.mSetOptimizedImage = true;
        super.onReopened();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.CENTER, 3);
    }

    private boolean validateDBStatus() {
        if (DataBaseAdapter.getInstance().isDBStatusCorrupt()) {
            transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
            return false;
        }
        this.stBOList = DataBaseOperations.getInstance().getStartrailsBOList();
        return true;
    }

    private synchronized void initialize() {
        cancelTask();
        makeViewsUnfocusable();
        this.mStartRunnableTask = new StartRunnableTask();
        DataBaseOperations dBaseOperation = DataBaseOperations.getInstance();
        this.stBOList = dBaseOperation.getStartrailsBOList();
        if (this.stBOList != null && this.stBOList.size() > 0) {
            if (ListViewLayout.listPosition != -1) {
                this.mCurrentItemToPlay = ListViewLayout.listPosition;
            } else {
                this.mCurrentItemToPlay = this.stBOList.size() - 1;
                ListViewLayout.listPosition = this.mCurrentItemToPlay;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
            }
            if (!this.pbCntl.isDataExist()) {
                initializePlaybackCursor();
                if (this.pbCntl.isDataExist()) {
                    if (STPlayRootContainer.isSTListView && this.pbCntl.getCount() > 10) {
                        Log.e("case", "list view");
                        startPlayback();
                    } else {
                        Log.e("case", "key");
                        if (this.mHandler != null) {
                            this.mHandler.sendEmptyMessage(2);
                        }
                    }
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        if (this.mFolderNumberView != null) {
            this.mFolderNumberView.setText("");
            this.mFolderNumberView = null;
        }
        if (this.mHandler != null) {
            if (this.mWaitRunnable != null) {
                this.mHandler.removeCallbacks(this.mWaitRunnable);
            }
            if (this.mHandler.hasMessages(1)) {
                this.mHandler.removeMessages(1);
            }
            if (this.mHandler.hasMessages(0)) {
                this.mHandler.removeMessages(0);
            }
            if (this.mHandler.hasMessages(2)) {
                this.mHandler.removeMessages(2);
            }
        }
        if (this.pbCntl != null) {
            this.pbCntl.releaseAllAllocatedData();
        }
        closeChildLayout();
        super.onDestroy();
    }

    public void startPlayback() {
        if (this.pbCntl.isDataExist() && this.pbCntl.getCount() > 0) {
            this.mHandler.sendEmptyMessage(0);
        }
    }

    public int getCurrentDispMode() {
        return BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.PB_DISPLAY_MODE_KEY, 1);
    }

    public void setCurrentDispMode(int mCurrentDispMode) {
        BackUpUtil.getInstance().setPreference(STBackUpKey.PB_DISPLAY_MODE_KEY, Integer.valueOf(mCurrentDispMode));
        CameraNotificationManager.getInstance().requestNotify(STBackUpKey.PB_DISPLAY_MODE_KEY);
    }

    public void changeDispMode() {
        if (getCurrentDispMode() == 1) {
            setCurrentDispMode(0);
            switchDispModeInfoOff();
        } else {
            setCurrentDispMode(1);
            switchDispModeInfoOn();
        }
        if (this.mIsPlaying) {
            setFooterGuide(0);
        } else {
            setFooterGuide(1);
        }
    }

    public void switchDispModeInfoOn() {
        this.mTimeView.setVisibility(0);
        this.mDateView.setVisibility(0);
        this.mAppIcon.setVisibility(0);
        if (this.optImage != null) {
            this.mFolderIcon.setVisibility(0);
            this.mThemeNameView.setVisibility(0);
        } else {
            this.mFolderIcon.setVisibility(4);
            this.mThemeNameView.setVisibility(4);
        }
        this.mFolderNumberView.setVisibility(0);
        this.mShootingNumView.setVisibility(0);
    }

    public void switchDispModeInfoOff() {
        this.mTimeView.setVisibility(4);
        this.mDateView.setVisibility(4);
        this.mThemeNameView.setVisibility(4);
        this.mAppIcon.setVisibility(4);
        this.mFolderIcon.setVisibility(4);
        this.mFolderNumberView.setVisibility(4);
        this.mShootingNumView.setVisibility(4);
        if (this.mFooterGuide != null) {
            this.mFooterGuide.setVisibility(4);
        }
    }

    public void initializePlaybackCursor() {
        this.pbCntl.intializeCursorData(this.mCurrentItemToPlay);
        this.mThemeNameView.setText(getActivity().getString(STUtility.getInstance().getThemeNameResID(this.pbCntl.getmStarTrailsBO().getThemeName())));
        this.mShootingNum = this.pbCntl.getmStarTrailsBO().getShootingNumber();
        this.mShootingNumView.setText("1/" + this.mShootingNum);
    }

    private void handlePlaybackButton() {
        if (this.mIsPlaying) {
            cancelPlayback();
        } else {
            startPlayback();
        }
    }

    public void cancelPlayback() {
        this.mHandler.sendEmptyMessage(1);
    }

    public void handleNextBtn() {
        if (this.stBOList != null && this.mCurrentItemToPlay < this.stBOList.size() - 1) {
            this.mCurrentItemToPlay++;
            ListViewLayout.listPosition = this.mCurrentItemToPlay;
            this.pbCntl.releaseAllAllocatedData();
            initializePlaybackCursor();
            playOneByOne();
            return;
        }
        if (this.stBOList != null && this.mCurrentItemToPlay == this.stBOList.size() - 1) {
            this.mCurrentItemToPlay = 0;
            ListViewLayout.listPosition = this.mCurrentItemToPlay;
            this.pbCntl.releaseAllAllocatedData();
            initializePlaybackCursor();
            playOneByOne();
        }
    }

    public void handlePrevBtn() {
        if (this.mCurrentItemToPlay > 0) {
            this.mCurrentItemToPlay--;
            ListViewLayout.listPosition = this.mCurrentItemToPlay;
            this.pbCntl.releaseAllAllocatedData();
            initializePlaybackCursor();
            playOneByOne();
            return;
        }
        if (this.mCurrentItemToPlay == 0) {
            this.mCurrentItemToPlay = this.stBOList.size() - 1;
            ListViewLayout.listPosition = this.mCurrentItemToPlay;
            this.pbCntl.releaseAllAllocatedData();
            initializePlaybackCursor();
            playOneByOne();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyHandler extends Handler {
        private MyHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Log.e("INSIDE", "handle Message " + msg.what);
            switch (msg.what) {
                case 0:
                    PlaybackLayout.this.mIsPlaying = true;
                    if (PlaybackLayout.this.mSetOptimizedImage) {
                        PlaybackLayout.this.playOneByOne();
                        if (PlaybackLayout.this.pbCntl.moveToNext()) {
                            PlaybackLayout.this.mHandler.sendEmptyMessage(0);
                            break;
                        } else {
                            PlaybackLayout.this.mHandler.sendEmptyMessage(2);
                            break;
                        }
                    } else {
                        PlaybackLayout.this.mHandler.sendEmptyMessage(0);
                        break;
                    }
                case 1:
                    PlaybackLayout.this.mIsPlaying = false;
                    PlaybackLayout.this.mHandler.removeMessages(0);
                    break;
                case 2:
                    PlaybackLayout.this.pbCntl.moveToFirst();
                    PlaybackLayout.this.playOneByOne();
                    PlaybackLayout.this.mIsPlaying = false;
                    PlaybackLayout.this.mHandler.removeMessages(0);
                    break;
            }
            PlaybackLayout.this.setFooterGuide(msg.what);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFooterGuide(int playbackState) {
        if (this.mFooterGuide != null) {
            if (getCurrentDispMode() == 1) {
                this.mFooterGuide.setVisibility(0);
                if (playbackState == 0) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_PB_FOOTER_GUIDE_PAUSE, R.string.STRID_FUNC_TIMELAPSE_PB_FOOTER_GUIDE_PAUSE_SK));
                    return;
                } else if (this.stBOList != null && this.stBOList.size() > 0) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_PB_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_PB_FOOTER_GUIDE_SK));
                    return;
                } else {
                    this.mFooterGuide.setVisibility(4);
                    return;
                }
            }
            this.mFooterGuide.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.stBOList == null || this.stBOList.size() <= 0) {
            return -1;
        }
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SWITCH_MODE);
        handlePlaybackButton();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d("INSIDE", "right key");
        if (this.stBOList == null || this.stBOList.size() <= 0) {
            return -1;
        }
        if (!this.mIsPlaying) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_WHEEL_UP);
            handleNextBtn();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d("INSIDE", "LEFT KEY");
        if (this.stBOList == null || this.stBOList.size() <= 0) {
            return -1;
        }
        if (!this.mIsPlaying) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_WHEEL_DOWN);
            handlePrevBtn();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onUpKeyPushed(KeyEvent event) {
        handlePushedUpKey();
        return super.onUpKeyPushed(event);
    }

    public int handlePushedUpKey() {
        if (this.stBOList == null || this.stBOList.size() <= 0) {
            return -1;
        }
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_UP);
        changeDispMode();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (Environment.getVersionOfHW() == 1) {
            return pushedPbZoomFuncMinus();
        }
        if (BaseProperties.isDownKeyAssignedToIndexTransition()) {
            return pushedPbZoomFuncMinus();
        }
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        if (!this.mIsPlaying && this.stBOList.size() > 0) {
            transitIndexPB();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return pushedSK2Key();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedDeleteKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        if (STUtility.getInstance().isMediaLocked()) {
            CautionUtilityClass.getInstance().requestTrigger(2257);
            return -1;
        }
        if (this.mIsPlaying || this.stBOList.size() <= 0 || this.mIsPlaying) {
            return -1;
        }
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
        if (this.stBOList.size() == this.mCurrentItemToPlay) {
            this.mCurrentItemToPlay--;
        } else if (this.mCurrentItemToPlay < 0) {
            this.mCurrentItemToPlay = 0;
        }
        StarTrailsBO tlBo = this.stBOList.get(this.mCurrentItemToPlay);
        ListViewLayout.listPosition = this.mCurrentItemToPlay;
        ListViewLayout.mSelectedDeleteImageSize = tlBo.getShootingNumber();
        final Bundle b = new Bundle();
        b.putString(DBConstants.COL_SHOOTING_MODE, tlBo.getShootingMode());
        b.putString(DBConstants.COL_FILE_PATH, tlBo.getFullPathFileName());
        b.putInt("ShootCount", ListViewLayout.mSelectedDeleteImageSize);
        this.mWaitRunnable = new Runnable() { // from class: com.sony.imaging.app.startrails.playback.layout.PlaybackLayout.2
            @Override // java.lang.Runnable
            public void run() {
                PlaybackLayout.this.openLayout(STPlayRootContainer.ID_DELETE_MULTIPLE_LAYOUT, b);
            }
        };
        if (this.mHandler == null) {
            this.mHandler = new MyHandler();
        }
        this.mHandler.postDelayed(this.mWaitRunnable, 500L);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.CENTER, 1);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_BACK_CANCEL);
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
        }
        this.pbCntl.setCurrentPBState((byte) 2);
        this.pbCntl.setPrevPBState((byte) 0);
        transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
        return 1;
    }

    private void transitIndexPB() {
        this.pbCntl.setCurrentPBState((byte) 1);
        this.pbCntl.setPrevPBState((byte) 0);
        transitionIndexPb();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        this.pbCntl.setCurrentPBState((byte) 2);
        this.pbCntl.setPrevPBState((byte) 0);
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToLeft() {
        return turnDialToLeft();
    }

    private int turnDialToLeft() {
        if (this.stBOList == null || this.stBOList.size() <= 0) {
            return -1;
        }
        if (!this.mIsPlaying) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_DOWN);
        }
        if (this.mIsPlaying || this.mCurrItemOnDisplay <= 1) {
            return 1;
        }
        this.pbCntl.moveToPosition(this.mCurrItemOnDisplay - 2);
        playOneByOne();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToRight() {
        return turnDialToRight();
    }

    private int turnDialToRight() {
        if (this.stBOList == null || this.stBOList.size() <= 0) {
            return -1;
        }
        if (!this.mIsPlaying) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_UP);
        }
        if (!this.mIsPlaying && this.mCurrItemOnDisplay < this.pbCntl.getCount()) {
            this.pbCntl.moveToPosition(this.mCurrItemOnDisplay);
            playOneByOne();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToLeft(KeyEvent event) {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToRight(KeyEvent event) {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToLeft(KeyEvent event) {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToRight(KeyEvent event) {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        return turnedShuttleToLeft();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        return turnedShuttleToRight();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playOneByOne() {
        Log.e("INSIDE", "playOneByOne");
        Log.e("cursor position", "" + this.pbCntl.getPosition());
        this.optImage = this.pbCntl.getPlayBackOptimizedImage();
        this.mSetOptimizedImage = false;
        this.mOptImageView.setOptimizedImage(this.optImage);
        this.mDate = this.pbCntl.getDate();
        this.mTime = this.pbCntl.getTime();
        this.mFolderName = this.pbCntl.getFolderNumber();
        this.mCurrItemOnDisplay = this.pbCntl.getPosition() + 1;
        if (this.optImage != null) {
            updateOptimizedImageLayoutParam();
            this.mErrorLayout.setVisibility(8);
            this.mTimeView.setText(STUtility.getInstance().getFormattedTime(this.mTime));
            this.mDateView.setText(STUtility.getInstance().getFormatteddate(this.mDate));
            this.mFolderNumberView.setText(this.mFolderName);
            this.mShootingNumView.setText(this.mCurrItemOnDisplay + "/" + this.mShootingNum);
            if (getCurrentDispMode() == 1) {
                this.mFolderIcon.setVisibility(0);
                this.mThemeNameView.setVisibility(0);
                return;
            } else {
                this.mFolderIcon.setVisibility(4);
                this.mThemeNameView.setVisibility(4);
                return;
            }
        }
        this.mErrorLayout.setVisibility(0);
        this.mTimeView.setText("");
        this.mDateView.setText("");
        this.mFolderNumberView.setText("");
        this.mShootingNumView.setText("");
        this.mFolderIcon.setVisibility(4);
        this.mThemeNameView.setVisibility(4);
    }

    private void releaseOptimizedImage(OptimizedImage optImage) {
        if (optImage != null) {
            optImage.release();
        }
    }

    private void releaseOptimizedImageView(OptimizedImageView optImageView) {
        if (optImageView != null) {
            if (optImageView.getBackground() != null) {
                optImageView.getBackground().setCallback(null);
            }
            optImageView.setOptimizedImage((OptimizedImage) null);
        }
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
            if (!MediaNotificationManager.getInstance().isNoCard()) {
                executeTask();
                return;
            }
            return;
        }
        if (tag.equals(STConstants.UPDATE_DELETED_FRAME)) {
            DataBaseOperations.getInstance().setStartrailsBOList();
            this.stBOList = DataBaseOperations.getInstance().getStartrailsBOList();
            if (this.stBOList.size() == 0) {
                Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
                getHandler().sendMessageAtFrontOfQueue(msg);
                updateView();
                return;
            }
            try {
                if (this.mCurrentItemToPlay == this.stBOList.size()) {
                    handlePrevBtn();
                } else {
                    this.mCurrentItemToPlay--;
                    handleNextBtn();
                }
                ListViewLayout.listPosition = this.mCurrentItemToPlay;
                updateView();
                STPlayRootContainer.isSTListView = false;
            } catch (Exception e) {
                Log.d(TAG, "Cursor is not prepared" + e.getMessage());
            }
        }
    }

    public void viewVisibility() {
        if (this.stBOList != null && this.stBOList.size() > 0) {
            ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
            this.mNoItemsToDisplayView.setVisibility(8);
            this.mFooterGuide.setVisibility(0);
            if (getCurrentDispMode() == 1) {
                switchDispModeInfoOn();
                return;
            } else {
                switchDispModeInfoOff();
                return;
            }
        }
        this.mNoItemsToDisplayView.setVisibility(0);
        this.mFooterGuide.setVisibility(4);
        this.mErrorLayout.setVisibility(8);
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        this.mDateView.setVisibility(4);
        this.mTimeView.setVisibility(4);
        this.mFolderIcon.setVisibility(4);
        this.mFolderNumberView.setVisibility(4);
        this.mShootingNumView.setVisibility(4);
        this.mThemeNameView.setVisibility(4);
        this.pbCntl.releaseAllAllocatedData();
        if (this.mOptImage != null) {
            this.mOptImage.release();
            this.mOptImage = null;
        }
    }

    private void updateAppIcon() {
        if (this.mAppIcon != null) {
            int device = DisplayModeObserver.getInstance().getActiveDevice();
            if (device == 1) {
                this.mAppIcon.setBackgroundResource(R.drawable.p_16_aa_parts_strl_appicon);
            } else {
                this.mAppIcon.setBackgroundResource(R.drawable.p_16_dd_parts_strl_appicon);
            }
        }
    }

    private void deInitialize() {
        cancelTask();
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mCurrentView = null;
        this.mResolver = null;
        this.mDateView = null;
        this.mTimeView = null;
        this.mShootingNumView = null;
        this.mThemeNameView = null;
        releaseOptimizedImage(this.optImage);
        releaseOptimizedImage(this.mOptImage);
        releaseOptimizedImageView(this.mOptImageView);
        releaseImageViewDrawable(this.mAppIcon);
        releaseImageViewDrawable(this.mFolderIcon);
        this.mDate = null;
        this.mTime = null;
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
        }
        this.mHandler = null;
        this.mNoItemsToDisplayView = null;
        this.mCurrentItemToPlay = -1;
        this.mShootingNum = 0;
        this.mIsPlaying = false;
        this.mCurrItemOnDisplay = 0;
        if (this.pbCntl != null) {
            this.pbCntl.releaseAllAllocatedData();
        }
        this.mFooterGuide = null;
        this.pbCntl = null;
    }

    private void executeTask() {
        this.mStartRunnableTask.execute();
    }

    private void cancelTask() {
        if (this.mStartRunnableTask != null) {
            this.mStartRunnableTask.removeCallbacks();
            this.mStartRunnableTask = null;
        }
    }

    /* loaded from: classes.dex */
    public class StartRunnableTask {
        private Handler handler = new Handler();
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.startrails.playback.layout.PlaybackLayout.StartRunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                if (PlaybackLayout.this.pbCntl == null) {
                    PlaybackLayout.this.pbCntl = PlayBackController.getInstance();
                }
                Log.d(PlaybackLayout.TAG, "StartRunnableTask " + PlaybackLayout.this.pbCntl.getCursor());
                if (!PlaybackLayout.this.pbCntl.isDataExist() && PlaybackLayout.this.stBOList != null && PlaybackLayout.this.stBOList.size() > 0) {
                    PlaybackLayout.this.mCurrentItemToPlay = PlaybackLayout.this.stBOList.size() - 1;
                    PlaybackLayout.this.initializePlaybackCursor();
                    if (PlaybackLayout.this.pbCntl.isDataExist()) {
                        if (PlaybackLayout.this.mHandler != null && PlaybackLayout.this.pbCntl.getPrevPBState() == 0) {
                            PlaybackLayout.this.mHandler.sendEmptyMessage(2);
                            return;
                        } else {
                            PlaybackLayout.this.startPlayback();
                            return;
                        }
                    }
                    Log.d(PlaybackLayout.TAG, "***************************StartRunnableTask1 " + PlaybackLayout.this.pbCntl.getCursor());
                    StartRunnableTask.this.execute();
                }
            }
        };

        public StartRunnableTask() {
        }

        public void execute() {
            if (this.handler != null) {
                this.handler.post(this.r);
            }
        }

        public void removeCallbacks() {
            this.handler.removeCallbacks(this.r);
            this.r = null;
        }
    }

    private void makeViewsUnfocusable() {
        if (getView() != null) {
            getView().setFocusable(false);
            getView().setFocusableInTouchMode(false);
        }
    }

    private void closeChildLayout() {
        if (getLayout(STPlayRootContainer.ID_DELETE_MULTIPLE_LAYOUT).getView() != null) {
            getLayout(STPlayRootContainer.ID_DELETE_MULTIPLE_LAYOUT).closeLayout();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getLayoutResource() {
        return R.layout.pb_layout_playback_screen;
    }

    public void updateOptimizedImageLayoutParam() {
        this.mOptImageView.setDisplayType(getDisplayType());
        this.targetLayout = null;
        this.targetLayout = YUV_LAYOUT_NORMAL;
        this.mOptImageView.getHitRect(mScratchRect);
        Log.i(TAG, LogHelper.getScratchBuilder("MSG_OPT_IMG_VIEW_LAYOUT").append(mScratchRect).append(" -> ").append(this.targetLayout).toString());
        if (!this.targetLayout.equals(mScratchRect)) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.mOptImageView.getLayoutParams();
            params.leftMargin = this.targetLayout.left;
            params.topMargin = this.targetLayout.top;
            params.width = this.targetLayout.width();
            params.height = this.targetLayout.height();
            Point p = new Point(params.width >> 1, params.height >> 1);
            Log.i(TAG, LogHelper.getScratchBuilder("MSG_SET_PIVOT").append(p.x).append(", ").append(p.y).toString());
            this.mOptImageView.setPivot(p);
            this.mOptImageView.setLayoutParams(params);
        }
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        if (this.optImage != null) {
            updateOptimizedImageLayoutParam();
        }
        updateAppIcon();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_TRANSPARENT;
    }
}
