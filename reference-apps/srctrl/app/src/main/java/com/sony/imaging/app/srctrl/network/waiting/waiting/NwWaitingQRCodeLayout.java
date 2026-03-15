package com.sony.imaging.app.srctrl.network.waiting.waiting;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import java.lang.reflect.Array;
import java.util.HashMap;

/* loaded from: classes.dex */
public class NwWaitingQRCodeLayout extends Layout implements IModableLayout {
    private static final int LEFT_MARGIN_169 = 449;
    private static final int LEFT_MARGIN_43 = 424;
    private static final String QR_FORMAT_VERSION = "W01:";
    private static final String QR_KEY_CAMERANAME = "C:";
    private static final String QR_KEY_EPASS = "P:";
    private static final String QR_KEY_MACADRESS = "M:";
    private static final String QR_KEY_SSID = "S:";
    private static final String QR_SEPARATOR = ";";
    private static final int WIDTH_169 = 147;
    private static final int WIDTH_43 = 196;
    private OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 0);

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.nw_layout_waiting_qr);
        return view;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    private String getQRString() {
        String cameraName;
        String ssid = NetworkRootState.getMySsid();
        int ssidPrefixIndex = ssid.indexOf(SRCtrlConstants.SSID_STRING_DIRECT) + SRCtrlConstants.SSID_STRING_DIRECT.length();
        String ssidPrefix = ssid.substring(ssidPrefixIndex, ssidPrefixIndex + 4);
        String psk = NetworkRootState.getMyPsk();
        String macAddr = SRCtrlEnvironment.getInstance().getMacAddr();
        if (UtilPFWorkaround.isDeviceNameModifyNeeded()) {
            cameraName = ssid.substring(ssidPrefixIndex + 5, ssid.length());
        } else {
            cameraName = NetworkRootState.getMyDeviceName();
        }
        String qrString = QR_FORMAT_VERSION + QR_KEY_SSID + escapeQRText(ssidPrefix) + ";" + QR_KEY_EPASS + escapeQRText(psk) + ";" + QR_KEY_CAMERANAME + escapeQRText(cameraName) + ";" + QR_KEY_MACADRESS + escapeQRText(macAddr) + ";";
        Log.v("QRCode", "String = " + qrString);
        return qrString;
    }

    private Bitmap createQRCordBitmap() {
        long start = System.currentTimeMillis();
        String qrString = getQRString();
        Log.v("QRCode", "get String : " + (System.currentTimeMillis() - start));
        QRCodeWriter qrWriter = new QRCodeWriter();
        try {
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix qrMatrix = qrWriter.encode(qrString, BarcodeFormat.QR_CODE, 57, 57, hints);
            Log.v("QRCode", "exec time : " + (System.currentTimeMillis() - start));
            int on = Color.argb(BatteryIcon.BATTERY_STATUS_CHARGING, 0, 0, 0);
            int off = Color.argb(225, 221, 221, 221);
            int width = qrMatrix.getWidth();
            int height = qrMatrix.getHeight();
            int[][] pixels = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[x][y] = qrMatrix.get(x, y) ? on : off;
                }
            }
            int width2 = width - 8;
            int height2 = height - 8;
            int[] scalePixels = new int[width2 * 4 * height2 * 4];
            int afterWidth = width2 * 4;
            int yindex = 0;
            for (int y2 = 0; y2 < height2; y2++) {
                for (int ycount = 0; ycount < 4; ycount++) {
                    int xindex = 0;
                    for (int x2 = 0; x2 < width2; x2++) {
                        int tmp = pixels[x2 + 4][y2 + 4];
                        for (int xcount = 0; xcount < 4; xcount++) {
                            scalePixels[yindex + xindex] = tmp;
                            xindex++;
                        }
                    }
                    yindex += afterWidth;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width2 * 4, height2 * 4, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(scalePixels, 0, width2 * 4, 0, 0, width2 * 4, height2 * 4);
            Log.v("QRCode", "bitmap create time : " + (System.currentTimeMillis() - start));
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            Log.e("QRCode", "error : " + e.getMessage());
            return null;
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        refresh();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
    }

    private int getAspect() {
        DisplayModeObserver mDisplayObserver = DisplayModeObserver.getInstance();
        int aspect = mDisplayObserver.getActiveDeviceOsdAspect();
        return aspect;
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        refresh();
    }

    private void refresh() {
        ((TextView) getView().findViewById(R.id.nw_parts_cmn_header_0_txt)).setText(SRCtrl.getAppStringId(getActivity()));
        ((TextView) getView().findViewById(R.id.nw_parts_wait_qr_0_txt)).setText(R.string.STRID_MSG_WIFI_CONNECT_QRCODE);
        RelativeLayout r2 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_qr_2);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_SSID));
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setTypeface(Typeface.UNIVERS);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(NetworkRootState.getMySsid());
        RelativeLayout rQr = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_qr_3);
        ImageView qrImage = (ImageView) rQr.findViewById(R.id.nw_parts_wait_qr_3_img);
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) qrImage.getLayoutParams();
        if (2 == getAspect()) {
            param.leftMargin = LEFT_MARGIN_169;
            param.width = WIDTH_169;
            qrImage.setLayoutParams(param);
        } else {
            param.leftMargin = LEFT_MARGIN_43;
            param.width = WIDTH_43;
            qrImage.setLayoutParams(param);
        }
        Log.v("QRCode", Name.PREFIX_START);
        Bitmap qrBitmap = createQRCordBitmap();
        Log.v("QRCode", "end");
        qrImage.setImageBitmap(qrBitmap);
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.nw_parts_cmn_footer_0_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_FOOTER_GUIDE_GOTO_PASS, R.string.STRID_FUNC_FOOTER_GUIDE_GOTO_PASS_NEX));
        }
        setKeyBeepPattern(getResumeKeyBeepPattern());
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected NetworkRootState getRootContainer() {
        return (NetworkRootState) getData(NetworkRootState.PROP_ID_APP_ROOT);
    }

    private static String escapeQRText(String val) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            switch (c) {
                case HandoffOperationInfo.GET_CONTENT_COUNT /* 44 */:
                    buffer.append("\\,");
                    break;
                case HandoffOperationInfo.START_LIVEVIEW /* 58 */:
                    buffer.append("\\:");
                    break;
                case HandoffOperationInfo.STOP_LIVEVIEW /* 59 */:
                    buffer.append("\\;");
                    break;
                case '\\':
                    buffer.append("\\\\");
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }
        return buffer.toString();
    }
}
