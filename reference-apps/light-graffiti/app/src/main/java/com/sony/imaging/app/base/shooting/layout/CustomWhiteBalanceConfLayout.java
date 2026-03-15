package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.NotificationListener;
import java.util.List;

/* loaded from: classes.dex */
public class CustomWhiteBalanceConfLayout extends Layout implements NotificationListener {
    private static final String COMPENSATION_PREFIX_G = "G";
    private static final String COMPENSATION_PREFIX_M = "M";
    private static final String LIGHT_BALANCE_PREFIX_A = "A";
    private static final String LIGHT_BALANCE_PREFIX_B = "B";
    private static final String TAG = "CustomWhiteBalanceConfLayout";
    private static String mAbformat;
    private static String mGmformat;
    private static String mTempformat;
    private WhiteBalanceController mController;
    private WhiteBalanceController.WhiteBalanceParam mWBparam;
    private static String TEMPERATURE = "colorTemperature";
    private static String COMPENSATION = "colorCompensation";
    private static String LIGHT_BALANCE = "lightBalance";
    private static final String[] LISTENING_TAGS = {CameraNotificationManager.WB_MODE_CHANGE};
    protected View mCurrentView = null;
    private ImageView mResistBGView = null;
    private TextView mResistTextView = null;
    private ImageView mRightArrowView = null;
    private ImageView mLeftArrowView = null;
    private ImageView mRegistIconView = null;
    private TextView mRegistTempTextView = null;
    private TextView mRegistLightTextView = null;
    private TextView mRegistCompTextView = null;
    private int mCustomNum = 1;
    private NotificationListener mDeviceChangeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout.1
        private String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE};

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag)) {
                CustomWhiteBalanceConfLayout.this.updateView();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    };

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mController = WhiteBalanceController.getInstance();
        List<String> supportCWBlist = this.mController.getSupportedCustomWhiteBalance();
        this.mCustomNum = supportCWBlist.size();
        if (this.mCustomNum == 1) {
            this.mController.setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.CUSTOM);
        } else {
            this.mController.setValue(WhiteBalanceController.WHITEBALANCE, "custom1");
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        this.mController = null;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mDeviceChangeListener);
        if (1 < this.mCustomNum && this.mCustomNum <= 3) {
            CameraNotificationManager.getInstance().setNotificationListener(this);
            update();
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDeviceChangeListener);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mWBparam = null;
        this.mCurrentView = null;
        this.mResistBGView = null;
        this.mResistTextView = null;
        this.mRightArrowView = null;
        this.mLeftArrowView = null;
        this.mRegistIconView = null;
        this.mRegistTempTextView = null;
        this.mRegistLightTextView = null;
        this.mRegistCompTextView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int res;
        super.onCreateView(inflater, container, savedInstanceState);
        this.mWBparam = (WhiteBalanceController.WhiteBalanceParam) this.mController.getDetailValue();
        createView();
        int colorTemp = this.data.getInt(TEMPERATURE);
        int colorComp = this.data.getInt(COMPENSATION);
        int lightBalance = this.data.getInt(LIGHT_BALANCE);
        TextView tempTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_conf_temperture_value);
        if (mTempformat == null) {
            mTempformat = getString(android.R.string.prohibit_manual_network_selection_in_gobal_mode);
        }
        tempTextView.setText(String.format(mTempformat, Integer.valueOf(colorTemp)));
        TextView abTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_conf_a_b_value);
        if (mAbformat == null) {
            mAbformat = getString(android.R.string.quick_contacts_not_available);
        }
        if (lightBalance > 0) {
            abTextView.setText(String.format(mAbformat, LIGHT_BALANCE_PREFIX_A + lightBalance));
        } else if (lightBalance < 0) {
            abTextView.setText(String.format(mAbformat, LIGHT_BALANCE_PREFIX_B + (lightBalance * (-1))));
        } else {
            abTextView.setText(String.format(mAbformat, Integer.valueOf(lightBalance)));
        }
        TextView gmTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_conf_g_m_value);
        if (mGmformat == null) {
            mGmformat = getString(android.R.string.radial_numbers_typeface);
        }
        if (colorComp > 0) {
            gmTextView.setText(String.format(mGmformat, COMPENSATION_PREFIX_M + colorComp));
        } else if (colorComp < 0) {
            gmTextView.setText(String.format(mGmformat, COMPENSATION_PREFIX_G + (colorComp * (-1))));
        } else {
            gmTextView.setText(String.format(mGmformat, Integer.valueOf(colorComp)));
        }
        View errorView = this.mCurrentView.findViewById(R.id.cwb_conf_linearLayout_on_error_message);
        if (this.data.getBoolean("inRange")) {
            errorView.setVisibility(4);
            res = getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL);
        } else {
            errorView.setVisibility(0);
            res = getResources().getColor(R.color.RESID_FONTSTYLE_STD_WARNING);
        }
        tempTextView.setTextColor(res);
        abTextView.setTextColor(res);
        gmTextView.setTextColor(res);
        if (1 == this.mCustomNum) {
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.years, android.R.string.years));
        } else {
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), 17041896, android.R.string.years));
        }
        return this.mCurrentView;
    }

    private void createView() {
        this.mCurrentView = obtainViewFromPool(R.layout.cwb_conf);
        this.mResistBGView = (ImageView) this.mCurrentView.findViewById(R.id.cwb_regist_base);
        this.mResistTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_regist_text);
        this.mLeftArrowView = (ImageView) this.mCurrentView.findViewById(R.id.cwb_regist_arrow_left);
        this.mRightArrowView = (ImageView) this.mCurrentView.findViewById(R.id.cwb_regist_arrow_right);
        this.mRegistIconView = (ImageView) this.mCurrentView.findViewById(R.id.cwb_regist_icon_mode);
        if (this.mCustomNum == 1) {
            this.mResistBGView.setVisibility(4);
            this.mResistTextView.setVisibility(4);
            this.mLeftArrowView.setVisibility(4);
            this.mRightArrowView.setVisibility(4);
            this.mRegistIconView.setVisibility(4);
        }
    }

    protected String makeWBOptionText(String option) {
        String ret = "";
        if (this.mWBparam != null) {
            if (WhiteBalanceController.TEMP.equals(option)) {
                ret = this.mWBparam.getColorTemp() + WhiteBalanceController.DISP_TEXT_K;
            } else if (WhiteBalanceController.LIGHT.equals(option)) {
                if (this.mWBparam.getLightBalance() > 0) {
                    ret = WhiteBalanceController.DISP_TEXT_AB_PLUS + Math.abs(this.mWBparam.getLightBalance());
                } else if (this.mWBparam.getLightBalance() == 0) {
                    ret = WhiteBalanceController.DISP_TEXT_AB_ZERO + Math.abs(this.mWBparam.getLightBalance());
                } else {
                    ret = WhiteBalanceController.DISP_TEXT_AB_MINUS + Math.abs(this.mWBparam.getLightBalance());
                }
            } else if (WhiteBalanceController.COMP.equals(option)) {
                if (this.mWBparam.getColorComp() > 0) {
                    ret = WhiteBalanceController.DISP_TEXT_GM_PLUS + Math.abs(this.mWBparam.getColorComp());
                } else if (this.mWBparam.getColorComp() == 0) {
                    ret = WhiteBalanceController.DISP_TEXT_GM_ZERO + Math.abs(this.mWBparam.getColorComp());
                } else {
                    ret = WhiteBalanceController.DISP_TEXT_GM_MINUS + Math.abs(this.mWBparam.getColorComp());
                }
            }
            return ret;
        }
        return "No Parameters";
    }

    private void update() {
        this.mWBparam = (WhiteBalanceController.WhiteBalanceParam) this.mController.getDetailValue();
        this.mRegistIconView.setImageResource(getOptionIcon(this.mController.getValue()));
        this.mRegistTempTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_regist_text_temp);
        this.mRegistTempTextView.setText(makeWBOptionText(WhiteBalanceController.TEMP));
        this.mRegistLightTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_regist_text_ab);
        this.mRegistLightTextView.setText(makeWBOptionText(WhiteBalanceController.LIGHT));
        this.mRegistCompTextView = (TextView) this.mCurrentView.findViewById(R.id.cwb_regist_text_gm);
        this.mRegistCompTextView.setText(makeWBOptionText(WhiteBalanceController.COMP));
        this.mRegistIconView.setVisibility(0);
        this.mResistBGView.setVisibility(0);
        this.mResistTextView.setVisibility(0);
        this.mRegistTempTextView.setVisibility(0);
        this.mRegistLightTextView.setVisibility(0);
        this.mRegistCompTextView.setVisibility(0);
        if (this.mController.getValue() != "custom1") {
            this.mLeftArrowView.setVisibility(0);
        } else {
            this.mLeftArrowView.setVisibility(4);
        }
        if (this.mController.getValue() != "custom3") {
            this.mRightArrowView.setVisibility(0);
        } else {
            this.mRightArrowView.setVisibility(4);
        }
    }

    private int getOptionIcon(String value) {
        if (value.equals("custom1")) {
            return 17307461;
        }
        if (value.equals("custom2")) {
            return 17307498;
        }
        if (value.equals("custom3")) {
            return 17306378;
        }
        return 17307461;
    }

    public void setFooterGuideData(IFooterGuideData data) {
        FooterGuide guide;
        if (this.mCurrentView != null && (guide = (FooterGuide) this.mCurrentView.findViewById(getFooterGuideResource())) != null) {
            guide.setData(data);
        }
    }

    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return LISTENING_TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        update();
    }
}
