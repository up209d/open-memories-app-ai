package com.sony.imaging.app.srctrl.network.wpspin.input;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;

/* loaded from: classes.dex */
public class NwWpsPinInputLayout extends Layout {
    private static final String TAG = NwWpsPinInputLayout.class.getSimpleName();
    private static InputMethodManager imManager;
    private static Button okButton;
    private static EditText pinEdit;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.nw_layout_pin);
        return view;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        imManager = (InputMethodManager) getActivity().getSystemService("input_method");
        ((TextView) getView().findViewById(R.id.nw_parts_cmn_header_0_txt)).setText(SRCtrl.getAppStringId(getActivity()));
        ((ImageView) getView().findViewById(R.id.nw_parts_pin_2_img)).setVisibility(8);
        ((TextView) getView().findViewById(R.id.nw_parts_pin_1_txt_m)).setVisibility(8);
        ((TextView) getView().findViewById(R.id.nw_parts_pin_1_txt_l)).setVisibility(8);
        pinEdit = (EditText) getView().findViewById(R.id.nw_parts_pin_2_edittxt);
        pinEdit.setVisibility(0);
        pinEdit.setEnabled(true);
        pinEdit.setFocusable(true);
        pinEdit.requestFocus();
        ((TextView) getView().findViewById(R.id.nw_parts_pin_3_txt)).setText(R.string.STRID_INFO_PIN_CODE);
        ((TextView) getView().findViewById(R.id.nw_parts_pin_4_txt)).setText(NetworkRootState.getDirectTargetDeviceName());
        ((TextView) getView().findViewById(R.id.nw_parts_pin_5_txt)).setText(R.string.STRID_FUNC_SENDTOSMARTPHONE_MSG_DIRECT_INPUT_DSLR);
        okButton = (Button) getView().findViewById(R.id.nw_parts_cmn_button_0_button);
        okButton.setText(android.R.string.phoneTypeOther);
        okButton.setVisibility(0);
        okButton.setEnabled(false);
        okButton.setFocusable(false);
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.nw_parts_cmn_footer_0_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.work_profile_deleted_details, android.R.string.httpErrorRedirectLoop));
        }
        pinEdit.setLongClickable(false);
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() { // from class: com.sony.imaging.app.srctrl.network.wpspin.input.NwWpsPinInputLayout.1
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (6 == actionId) {
                    NwWpsPinInputLayout.hideKeypad(v);
                    return false;
                }
                return false;
            }
        };
        pinEdit.setOnEditorActionListener(onEditorActionListener);
        setKeyBeepPattern(getResumeKeyBeepPattern());
        pinEdit.requestFocus();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        pinEdit.setText("");
        hideKeypad(getView());
        pinEdit = null;
        okButton.setEnabled(true);
        okButton = null;
        imManager = null;
        super.onPause();
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected NetworkRootState getRootContainer() {
        return (NetworkRootState) getData(NetworkRootState.PROP_ID_APP_ROOT);
    }

    public static String getPin() {
        return pinEdit.getText().toString();
    }

    public static void openKeypad() {
        if (!pinEdit.requestFocus()) {
            Log.e(TAG, "Failure: requestFocus");
        }
        if (!imManager.showSoftInput(pinEdit, 2)) {
            Log.e(TAG, "Failure: showSoftInput");
        }
    }

    public static void hideKeypad(View v) {
        if (!imManager.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
            Log.e(TAG, "Failure: hideSoftInput");
        }
    }

    public static void clearPin() {
        pinEdit.setText("");
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (pinEdit != null) {
            pinEdit.setEnabled(true);
            pinEdit.setFocusable(true);
            pinEdit.requestFocus();
        }
        if (okButton != null) {
            okButton.setEnabled(false);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (pinEdit != null) {
            pinEdit.clearFocus();
            pinEdit.setFocusable(false);
            pinEdit.setEnabled(false);
        }
        if (okButton != null) {
            okButton.setEnabled(true);
        }
        return 1;
    }

    public static boolean isEditTextFocused() {
        if (pinEdit == null || !pinEdit.isEnabled()) {
            return false;
        }
        return true;
    }
}
