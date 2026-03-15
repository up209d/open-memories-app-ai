package com.sony.imaging.app.srctrl.network.dd;

import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;

/* loaded from: classes.dex */
public class SecurityUtils {
    public static String escapeXMLText(String val) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            switch (c) {
                case '\"':
                    buffer.append("&quot;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '\'':
                    buffer.append("&apos;");
                    break;
                case HandoffOperationInfo.STOP_BULB_SHOOTING /* 60 */:
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }
        return buffer.toString();
    }
}
