package com.sony.imaging.app.fw;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class KeyConverter implements IKeyHandler {
    private static final String MSG_APPLY = "apply mode=";
    private static final String MSG_CODE = " code=";
    private static final String MSG_FAIL_TO_CONV = "apply converted to non customizable function : ";
    private static final String MSG_TO = " -> ";
    private static final String TAG = "KeyConverter";
    private static final StringBuilder sStringBuilder = new StringBuilder();
    protected String mCategory;
    protected WeakReference<IConvertibleKeyHandler> mConvertibleKeyHandler;
    protected ICustomKeyMgr mCustomKeyMgr;

    public KeyConverter(IConvertibleKeyHandler handler) {
        this(CustomKeyMgr.getInstance(), handler, ICustomKey.CATEGORY_NONE);
    }

    public KeyConverter(IConvertibleKeyHandler handler, String category) {
        this(CustomKeyMgr.getInstance(), handler, category);
    }

    public KeyConverter(ICustomKeyMgr mgr, IConvertibleKeyHandler handler) {
        this(mgr, handler, ICustomKey.CATEGORY_NONE);
    }

    public KeyConverter(ICustomKeyMgr mgr, IConvertibleKeyHandler handler, String category) {
        this.mCustomKeyMgr = mgr;
        this.mConvertibleKeyHandler = new WeakReference<>(handler);
        this.mCategory = category;
    }

    public void setHandler(IConvertibleKeyHandler handler) {
        this.mConvertibleKeyHandler = new WeakReference<>(handler);
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public final int apply(KeyEvent event, String category) {
        int code = event.getScanCode();
        if (638 == code) {
            KeyStatus keystatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED);
            if (keystatus != null && 1 == keystatus.valid) {
                code = keystatus.status;
            } else {
                Log.e(TAG, "getKeyStatus returns valid against ISV_KEY_SLI_AF_MF_AEL");
            }
        }
        ICustomKey key = this.mCustomKeyMgr.get(code);
        IKeyFunction func = CustomizableFunction.Unchanged;
        int action = event.getAction();
        if (key != null && category != null && !ICustomKey.CATEGORY_NONE.equals(category)) {
            func = key.getAssigned(category);
            CustomizableFunction customizableFunc = null;
            if (func != null) {
                try {
                    customizableFunc = (CustomizableFunction) func;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (customizableFunc != null) {
                sStringBuilder.replace(0, sStringBuilder.length(), MSG_APPLY).append(category).append(MSG_CODE).append(code).append(" -> ").append(customizableFunc.name());
                Log.i(TAG, sStringBuilder.toString());
            } else {
                sStringBuilder.replace(0, sStringBuilder.length(), MSG_FAIL_TO_CONV).append(code);
                Log.i(TAG, sStringBuilder.toString());
            }
        }
        IConvertibleKeyHandler handler = this.mConvertibleKeyHandler.get();
        if (action == 0 || 2 == action) {
            int result = handler.onConvertedKeyDown(event, func);
            return result;
        }
        int result2 = handler.onConvertedKeyUp(event, func);
        return result2;
    }

    @Override // com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        return apply(event, this.mCategory);
    }

    @Override // com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return apply(event, this.mCategory);
    }
}
