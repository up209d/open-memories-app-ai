package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.MainCursolPosUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MainLayoutSync extends Layout {
    private static final int AUTO_TRANSFER = 2131296269;
    private static final int CHANGE_REGISTRATION = 2131296315;
    private static final int REGISTRATION = 2131296273;
    private static final int SMART_PHONE = 2131296278;
    private static final int TRANSFER_NOW = 2131296331;
    protected View mCurrentView;
    protected AppNameConstantView mScreenTitleView;
    private boolean mbMounted;
    private static final String TAG = MainLayoutSync.class.getSimpleName();
    private static MainLayoutSync mInstance = null;
    TextView mAutoTransfer = null;
    TextView mRegistration = null;
    TextView mTransferNow = null;
    TextView mSmartphoneState = null;
    TextView mStillImageNumber = null;
    ImageView mLinkLine = null;
    ImageView mSmartPhoneImageState = null;
    TextView mToggleButton = null;
    private boolean m_iRight = false;
    private boolean m_iUp = false;
    private int stillImageNumber = 0;
    private int autoTransferStartNum = 1;
    private boolean mbResisteredDevice = false;
    private MediaNotificationManager mMediaNotifier = null;
    private NotificationListener mMediaListener = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.autoTransferStartNum = SyncBackUpUtil.getInstance().getImageStartNum(this.autoTransferStartNum);
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            if (this.autoTransferStartNum == 1) {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.main_layout_panel_default);
            } else {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.main_layout_panel);
            }
        }
        createView();
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mAutoTransfer = (TextView) this.mCurrentView.findViewById(R.id.button_auto_transfer_text);
        if (this.mAutoTransfer != null) {
            this.mAutoTransfer.setText(R.string.STRID_FUNC_AUTOSYNC_AUTO_TRANSFER);
        }
        this.mRegistration = (TextView) this.mCurrentView.findViewById(R.id.button_register_text);
        if (this.mRegistration != null) {
            this.mRegistration.setText(R.string.STRID_FUNC_AUTOSYNC_REGISTER);
        }
        if (this.autoTransferStartNum == 1) {
            this.mTransferNow = null;
        } else {
            this.mTransferNow = (TextView) this.mCurrentView.findViewById(R.id.button_transfer_now_text);
            if (this.mTransferNow != null) {
                this.mTransferNow.setText(R.string.STRID_FUNC_AUTOSYNC_IMMEDIATE_TRANSFER);
            }
        }
        this.mSmartphoneState = (TextView) this.mCurrentView.findViewById(R.id.smartphone_state_text);
        this.mStillImageNumber = (TextView) this.mCurrentView.findViewById(R.id.stillimage_number_text);
        this.mLinkLine = (ImageView) this.mCurrentView.findViewById(R.id.link_line);
        this.mSmartPhoneImageState = (ImageView) this.mCurrentView.findViewById(R.id.smartphone_state_image);
        this.mToggleButton = (TextView) this.mCurrentView.findViewById(R.id.toggle_button_auto_transfer);
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mbResisteredDevice = SyncBackUpUtil.getInstance().getRegister(this.mbResisteredDevice);
        String smartPhone = this.mbResisteredDevice ? SyncBackUpUtil.getInstance().getSmartphone(null) : null;
        long lastStartTime = SyncBackUpUtil.getInstance().getStartUTC(0L);
        if (this.mSmartphoneState != null) {
            if (this.mbResisteredDevice) {
                this.mSmartphoneState.setText(smartPhone);
            } else {
                this.mSmartphoneState.setText(R.string.STRID_FUNC_AUTOSYNC_SMART_PHONE);
            }
        }
        if (this.mRegistration != null) {
            if (this.mbResisteredDevice) {
                this.mRegistration.setText(R.string.STRID_FUNC_AUTOSYNC_CHANGE_REGISTER);
            } else {
                this.mRegistration.setText(R.string.STRID_FUNC_AUTOSYNC_REGISTER);
            }
        }
        if (this.mLinkLine != null) {
            if (0 < lastStartTime) {
                this.mLinkLine.setImageResource(R.drawable.p_16_dd_parts_sync_connect_on);
            } else {
                this.mLinkLine.setImageResource(R.drawable.p_16_dd_parts_sync_connect_off);
            }
        }
        if (this.mSmartPhoneImageState != null) {
            if (this.mbResisteredDevice) {
                this.mSmartPhoneImageState.setImageResource(R.drawable.p_16_dd_parts_sync_smartphone_on);
            } else {
                this.mSmartPhoneImageState.setImageResource(R.drawable.p_16_dd_parts_sync_smartphone_off);
            }
        }
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        MainCursolPosUtil.getInstance().setMediaMount(this.mbMounted);
        if (this.mMediaListener == null) {
            this.mMediaListener = getMediaNotificationListener();
        }
        if (this.mMediaListener != null) {
            this.mMediaNotifier.setNotificationListener(this.mMediaListener);
        }
        displayMainLayout();
        if (this.mToggleButton != null) {
            this.mToggleButton.setSelected(0 != lastStartTime);
        }
        this.mStillImageNumber.setText(String.valueOf(this.stillImageNumber));
        this.mScreenTitleView.setVisibility(0);
    }

    public void displayMainLayout() {
        long lastStartTime = SyncBackUpUtil.getInstance().getStartUTC(0L);
        this.autoTransferStartNum = SyncBackUpUtil.getInstance().getImageStartNum(this.autoTransferStartNum);
        AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
        boolean bret = dbUtil.isNeedUpdateDatabase();
        if (bret) {
            dbUtil.updateDatabase(false);
        } else {
            dbUtil.importDatabase();
        }
        this.stillImageNumber = dbUtil.getNumberOfTransferReservationFiles(lastStartTime);
        this.mStillImageNumber.setText(String.valueOf(this.stillImageNumber));
        if (!this.mbResisteredDevice) {
            this.m_iRight = true;
            this.m_iUp = true;
            if (this.mAutoTransfer != null) {
                this.mAutoTransfer.setSelected(false);
                this.mAutoTransfer.setEnabled(false);
                this.mAutoTransfer.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_DISABLE));
            }
            if (this.mRegistration != null) {
                this.mRegistration.setSelected(true);
            }
            if (1 != this.autoTransferStartNum && this.mTransferNow != null) {
                this.mTransferNow.setVisibility(0);
                this.mTransferNow.setSelected(false);
                this.mTransferNow.setEnabled(false);
                this.mTransferNow.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_DISABLE));
            }
            MainCursolPosUtil.getInstance().setTransferNowButtonEnable(false);
        } else {
            this.m_iRight = false;
            if (this.stillImageNumber == 0 || !this.mbMounted) {
                this.m_iUp = true;
                if (this.mAutoTransfer != null) {
                    this.mAutoTransfer.setSelected(true);
                    this.mAutoTransfer.setEnabled(true);
                    this.mAutoTransfer.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
                }
                if (this.mRegistration != null) {
                    this.mRegistration.setSelected(false);
                }
                if (1 != this.autoTransferStartNum && this.mTransferNow != null) {
                    this.mTransferNow.setVisibility(0);
                    this.mTransferNow.setSelected(false);
                    this.mTransferNow.setEnabled(false);
                    this.mTransferNow.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_DISABLE));
                }
                MainCursolPosUtil.getInstance().setTransferNowButtonEnable(false);
            } else if (1 == this.autoTransferStartNum) {
                this.m_iUp = true;
                if (this.mAutoTransfer != null) {
                    this.mAutoTransfer.setSelected(true);
                    this.mAutoTransfer.setEnabled(true);
                    this.mAutoTransfer.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
                }
                if (this.mRegistration != null) {
                    this.mRegistration.setSelected(false);
                }
                MainCursolPosUtil.getInstance().setTransferNowButtonEnable(false);
            } else {
                this.m_iUp = false;
                if (this.mAutoTransfer != null) {
                    this.mAutoTransfer.setSelected(false);
                    this.mAutoTransfer.setEnabled(true);
                    this.mAutoTransfer.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
                }
                if (this.mRegistration != null) {
                    this.mRegistration.setSelected(false);
                }
                if (this.mTransferNow != null) {
                    this.mTransferNow.setVisibility(0);
                    this.mTransferNow.setSelected(true);
                    this.mTransferNow.setEnabled(true);
                    this.mTransferNow.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
                }
                MainCursolPosUtil.getInstance().setTransferNowButtonEnable(true);
            }
        }
        MainCursolPosUtil.getInstance().setCursolRight(this.m_iRight);
        MainCursolPosUtil.getInstance().setCursolUp(this.m_iUp);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        Log.d(TAG, "onReopened <<<");
        long lastStartTime = SyncBackUpUtil.getInstance().getStartUTC(0L);
        if (this.mToggleButton != null) {
            this.mToggleButton.setSelected(0 != lastStartTime);
        }
        if (this.mLinkLine != null) {
            if (0 < lastStartTime) {
                this.mLinkLine.setImageResource(R.drawable.p_16_dd_parts_sync_connect_on);
            } else {
                this.mLinkLine.setImageResource(R.drawable.p_16_dd_parts_sync_connect_off);
            }
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mScreenTitleView.setVisibility(4);
        Log.d(TAG, "onPause = onPause");
        if (this.mMediaNotifier != null && this.mMediaListener != null) {
            this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        }
        this.mMediaListener = null;
        this.mMediaNotifier = null;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deInitializeView();
        this.mCurrentView = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey");
        setSlectedUpDownButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        setSlectedUpDownButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d(TAG, "pushedRightKey");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d(TAG, "pushedLeftKey");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        Log.d(TAG, "turnedDial1ToRight");
        boolean button_enable = MainCursolPosUtil.getInstance().getTransferNowButtonEnable();
        if (button_enable) {
            setSlectedUpDownButton();
            return 0;
        }
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        boolean button_enable = MainCursolPosUtil.getInstance().getTransferNowButtonEnable();
        if (button_enable) {
            setSlectedUpDownButton();
            return 0;
        }
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        boolean button_enable = MainCursolPosUtil.getInstance().getTransferNowButtonEnable();
        if (button_enable) {
            setSlectedUpDownButton();
            return 0;
        }
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        boolean button_enable = MainCursolPosUtil.getInstance().getTransferNowButtonEnable();
        if (button_enable) {
            setSlectedUpDownButton();
            return 0;
        }
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "pushedDeleteKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.d(TAG, "pushedPlayBackKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Log.d(TAG, "pushedFnKey");
        return 0;
    }

    private void setSlectedButton() {
        if (this.mbResisteredDevice && this.m_iUp) {
            this.m_iRight = !this.m_iRight;
            MainCursolPosUtil.getInstance().setCursolRight(this.m_iRight);
            this.mAutoTransfer.setSelected(this.m_iRight ? false : true);
            this.mRegistration.setSelected(this.m_iRight);
            Log.d(TAG, "setSelctedButton miRight = " + this.m_iRight);
        }
    }

    private void setSlectedUpDownButton() {
        boolean button_enable = MainCursolPosUtil.getInstance().getTransferNowButtonEnable();
        if (button_enable) {
            this.m_iUp = !this.m_iUp;
            MainCursolPosUtil.getInstance().setCursolUp(this.m_iUp);
            if (this.m_iUp) {
                if (this.m_iRight) {
                    this.mRegistration.setSelected(this.m_iUp);
                } else {
                    this.mAutoTransfer.setSelected(this.m_iUp);
                }
                this.mTransferNow.setSelected(false);
            } else {
                this.mRegistration.setSelected(false);
                this.mAutoTransfer.setSelected(false);
                this.mTransferNow.setSelected(true);
            }
            Log.d(TAG, "setSelectedUpDownButton m_iUp = " + this.m_iUp + " m_iRight = " + this.m_iRight);
        }
    }

    private static void setFocusForcibly(View v) {
        boolean focusableInTouchMode = v.isFocusableInTouchMode();
        if (!focusableInTouchMode) {
            v.setFocusableInTouchMode(true);
        }
        v.requestFocus();
        if (!focusableInTouchMode) {
            v.setFocusableInTouchMode(false);
        }
    }

    private void deInitializeView() {
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }

    protected NotificationListener getMediaNotificationListener() {
        return new MediaMountEventListener();
    }

    /* loaded from: classes.dex */
    public class MediaMountEventListener implements NotificationListener {
        private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};

        public MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(MainLayoutSync.TAG, tag);
            if (MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE.equals(tag) && MainLayoutSync.this.mMediaNotifier != null && MainLayoutSync.this.mMediaNotifier.isMounted() != MainLayoutSync.this.mbMounted) {
                MainLayoutSync.this.mbMounted = MainLayoutSync.this.mMediaNotifier.isMounted();
                MainCursolPosUtil.getInstance().setMediaMount(MainLayoutSync.this.mbMounted);
                MainLayoutSync.this.displayMainLayout();
            }
        }
    }
}
