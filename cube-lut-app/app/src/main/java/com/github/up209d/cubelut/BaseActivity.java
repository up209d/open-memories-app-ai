package com.github.up209d.cubelut;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.github.ma1co.openmemories.framework.DeviceInfo;
import com.github.ma1co.openmemories.framework.DisplayManager;
import com.sony.scalar.sysutil.ScalarInput;

public abstract class BaseActivity extends Activity implements DisplayManager.Listener {
    private static final String TAG = "BaseActivity";
    private DisplayManager displayManager;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            displayManager = DisplayManager.create(this);
            displayManager.addListener(this);
            setColorDepth(true);
        } catch (Exception e) {
            Log.w(TAG, "DisplayManager init failed (non-camera device)", e);
            displayManager = null;
        }
        notifyAppInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (displayManager != null) {
            try {
                setColorDepth(false);
                displayManager.release();
            } catch (Exception e) {
                Log.w(TAG, "DisplayManager release failed", e);
            }
            displayManager = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        try {
            switch (scanCode) {
                case ScalarInput.ISV_KEY_UP:
                    return onUpKeyDown();
                case ScalarInput.ISV_KEY_DOWN:
                    return onDownKeyDown();
                case ScalarInput.ISV_KEY_LEFT:
                    return onLeftKeyDown();
                case ScalarInput.ISV_KEY_RIGHT:
                    return onRightKeyDown();
                case ScalarInput.ISV_KEY_ENTER:
                    return onEnterKeyDown();
                case ScalarInput.ISV_KEY_FN:
                    return onFnKeyDown();
                case ScalarInput.ISV_KEY_AEL:
                    return onAelKeyDown();
                case ScalarInput.ISV_KEY_MENU:
                case ScalarInput.ISV_KEY_SK1:
                    return onMenuKeyDown();
                case ScalarInput.ISV_KEY_S1_1:
                    return onFocusKeyDown();
                case ScalarInput.ISV_KEY_S1_2:
                    return true;
                case ScalarInput.ISV_KEY_S2:
                    return onShutterKeyDown();
                case ScalarInput.ISV_KEY_PLAY:
                    return onPlayKeyDown();
                case ScalarInput.ISV_KEY_STASTOP:
                    return onMovieKeyDown();
                case ScalarInput.ISV_KEY_CUSTOM1:
                    return onC1KeyDown();
                case ScalarInput.ISV_KEY_DELETE:
                case ScalarInput.ISV_KEY_SK2:
                    return onDeleteKeyDown();
                case ScalarInput.ISV_KEY_LENS_ATTACH:
                    return onLensAttached();
                case ScalarInput.ISV_DIAL_1_CLOCKWISE:
                case ScalarInput.ISV_DIAL_1_COUNTERCW:
                    return onUpperDialChanged(getDialStatus(ScalarInput.ISV_DIAL_1_STATUS) / 22);
                case ScalarInput.ISV_DIAL_2_CLOCKWISE:
                case ScalarInput.ISV_DIAL_2_COUNTERCW:
                    return onLowerDialChanged(getDialStatus(ScalarInput.ISV_DIAL_2_STATUS) / 22);
                case ScalarInput.ISV_KEY_MODE_DIAL:
                    return onModeDialChanged(getDialStatus(ScalarInput.ISV_KEY_MODE_DIAL));
                default:
                    return super.onKeyDown(keyCode, event);
            }
        } catch (Exception e) {
            // ScalarInput constants may not resolve on non-Sony devices
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        try {
            switch (scanCode) {
                case ScalarInput.ISV_KEY_S1_1:
                    return onFocusKeyUp();
                case ScalarInput.ISV_KEY_S2:
                    return onShutterKeyUp();
                case ScalarInput.ISV_KEY_DELETE:
                case ScalarInput.ISV_KEY_SK2:
                    return onDeleteKeyUp();
                case ScalarInput.ISV_DIAL_1_CLOCKWISE:
                case ScalarInput.ISV_DIAL_1_COUNTERCW:
                case ScalarInput.ISV_DIAL_2_CLOCKWISE:
                case ScalarInput.ISV_DIAL_2_COUNTERCW:
                case ScalarInput.ISV_KEY_MODE_DIAL:
                    return true;
                default:
                    return super.onKeyUp(keyCode, event);
            }
        } catch (Exception e) {
            return super.onKeyUp(keyCode, event);
        }
    }

    protected int getDialStatus(int key) {
        try {
            return ScalarInput.getKeyStatus(key).status;
        } catch (Exception e) {
            return 0;
        }
    }

    // Override these in subclasses
    protected boolean onUpKeyDown() { return false; }
    protected boolean onDownKeyDown() { return false; }
    protected boolean onLeftKeyDown() { return false; }
    protected boolean onRightKeyDown() { return false; }
    protected boolean onEnterKeyDown() { return false; }
    protected boolean onFnKeyDown() { return false; }
    protected boolean onAelKeyDown() { return false; }
    protected boolean onMenuKeyDown() { return false; }
    protected boolean onFocusKeyDown() { return false; }
    protected boolean onFocusKeyUp() { return false; }
    protected boolean onShutterKeyDown() { return false; }
    protected boolean onShutterKeyUp() { return false; }
    protected boolean onPlayKeyDown() { return false; }
    protected boolean onMovieKeyDown() { return false; }
    protected boolean onC1KeyDown() { return false; }
    protected boolean onLensAttached() { return false; }
    protected boolean onUpperDialChanged(int value) { return false; }
    protected boolean onLowerDialChanged(int value) { return false; }
    protected boolean onModeDialChanged(int value) { return false; }

    protected boolean onDeleteKeyDown() { return true; }
    protected boolean onDeleteKeyUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void displayChanged(DisplayManager.Display display) {
        // Handle display changes (EVF/LCD switch)
    }

    protected boolean isCamera() {
        try {
            return DeviceInfo.getInstance().isCamera();
        } catch (Exception e) {
            return false;
        }
    }

    protected void setAutoPowerOffMode(boolean enable) {
        String mode = enable ? "APO/NORMAL" : "APO/NO";
        Intent intent = new Intent();
        intent.setAction("com.android.server.DAConnectionManagerService.apo");
        intent.putExtra("apo_info", mode);
        sendBroadcast(intent);
    }

    protected void setColorDepth(boolean highQuality) {
        if (displayManager != null) {
            try {
                displayManager.setColorDepth(highQuality
                        ? DisplayManager.ColorDepth.HIGH : DisplayManager.ColorDepth.LOW);
            } catch (Exception e) {
                Log.w(TAG, "setColorDepth failed", e);
            }
        }
    }

    protected void notifyAppInfo() {
        Intent intent = new Intent("com.android.server.DAConnectionManagerService.AppInfoReceive");
        intent.putExtra("package_name", getComponentName().getPackageName());
        intent.putExtra("class_name", getComponentName().getClassName());
        sendBroadcast(intent);
    }
}
