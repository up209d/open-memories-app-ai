package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;

/* loaded from: classes.dex */
public class LGConfirmReviewLayout extends Layout {
    private static final String TAG = LGConfirmReviewLayout.class.getSimpleName();
    TextView mCancel;
    TextView mOk;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = obtainViewFromPool(R.layout.lightgraffiti_layout_confirm_review);
        this.mOk = (TextView) v.findViewById(R.id.button_upper);
        if (this.mOk != null) {
            this.mOk.setText(R.string.STRID_AMC_STR_01197);
            this.mOk.setSelected(true);
        }
        this.mCancel = (TextView) v.findViewById(R.id.button_lower);
        if (this.mCancel != null) {
            this.mCancel.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_CONFIRM_RETRY);
            this.mCancel.setSelected(1 == 0);
        }
        LGStateHolder.getInstance().setShootingEnable(false);
        return v;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setKeyBeepPattern(5);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mOk = null;
        this.mCancel = null;
    }

    private void toggleSelection() {
        if (this.mOk != null && this.mCancel != null) {
            this.mOk.setSelected(!this.mOk.isSelected());
            this.mCancel.setSelected(this.mCancel.isSelected() ? false : true);
        } else if (this.mOk != null) {
            this.mOk.setSelected(true);
        } else if (this.mCancel != null) {
            this.mCancel.setSelected(true);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mOk != null && this.mOk.isSelected()) {
            Log.d(TAG, "Notify FINISH");
            CameraNotificationManager.getInstance().requestNotify(LGConstants.CONFIRM_REVIEW_FINISH);
            return 0;
        }
        if (this.mCancel != null && this.mCancel.isSelected()) {
            Log.d(TAG, "Notify CLOSE");
            CameraNotificationManager.getInstance().requestNotify(LGConstants.CONFIRM_REVIEW_CLOSE);
            return 0;
        }
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "Notify FINISH by pushed S1 key.");
        CameraNotificationManager.getInstance().requestNotify(LGConstants.CONFIRM_REVIEW_FINISH);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                CameraNotificationManager.getInstance().requestNotify(LGConstants.CONFIRM_REVIEW_FINISH);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (scanCode == 516 || scanCode == 552 || scanCode == 553) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.CONFIRM_REVIEW_FINISH);
            return 1;
        }
        if (scanCode == 772 || scanCode == 518 || scanCode == 774) {
            return 0;
        }
        if (scanCode == 530 || scanCode == 786) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.CONFIRM_REVIEW_FINISH);
            return 0;
        }
        return super.onKeyDown(keyCode, event);
    }
}
