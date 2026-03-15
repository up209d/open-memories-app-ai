package com.google.zxing.oned;

import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.sony.imaging.app.fw.AppRoot;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class EANManufacturerOrgSupport {
    private final List<int[]> ranges = new ArrayList();
    private final List<String> countryIdentifiers = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public String lookupCountryIdentifier(String productCode) {
        initIfNeeded();
        int prefix = Integer.parseInt(productCode.substring(0, 3));
        int max = this.ranges.size();
        for (int i = 0; i < max; i++) {
            int[] range = this.ranges.get(i);
            int start = range[0];
            if (prefix < start) {
                return null;
            }
            int end = range.length == 1 ? start : range[1];
            if (prefix <= end) {
                return this.countryIdentifiers.get(i);
            }
        }
        return null;
    }

    private void add(int[] range, String id) {
        this.ranges.add(range);
        this.countryIdentifiers.add(id);
    }

    private synchronized void initIfNeeded() {
        if (this.ranges.isEmpty()) {
            add(new int[]{0, 19}, "US/CA");
            add(new int[]{30, 39}, "US");
            add(new int[]{60, 139}, "US/CA");
            add(new int[]{300, 379}, "FR");
            add(new int[]{380}, "BG");
            add(new int[]{383}, "SI");
            add(new int[]{385}, "HR");
            add(new int[]{387}, "BA");
            add(new int[]{400, 440}, "DE");
            add(new int[]{450, 459}, "JP");
            add(new int[]{460, 469}, "RU");
            add(new int[]{471}, "TW");
            add(new int[]{474}, "EE");
            add(new int[]{475}, "LV");
            add(new int[]{476}, "AZ");
            add(new int[]{477}, "LT");
            add(new int[]{478}, "UZ");
            add(new int[]{479}, "LK");
            add(new int[]{480}, "PH");
            add(new int[]{481}, "BY");
            add(new int[]{482}, "UA");
            add(new int[]{484}, "MD");
            add(new int[]{485}, "AM");
            add(new int[]{486}, "GE");
            add(new int[]{487}, "KZ");
            add(new int[]{489}, "HK");
            add(new int[]{490, 499}, "JP");
            add(new int[]{500, 509}, "GB");
            add(new int[]{AppRoot.USER_KEYCODE.FN}, "GR");
            add(new int[]{AppRoot.USER_KEYCODE.DIAL2_RIGHT}, ExpandedProductParsedResult.POUND);
            add(new int[]{AppRoot.USER_KEYCODE.DIAL2_LEFT}, "CY");
            add(new int[]{531}, "MK");
            add(new int[]{AppRoot.USER_KEYCODE.MODE_DIAL_AUTO}, "MT");
            add(new int[]{AppRoot.USER_KEYCODE.MODE_DIAL_AEA}, "IE");
            add(new int[]{AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, AppRoot.USER_KEYCODE.MODE_DIAL_PE}, "BE/LU");
            add(new int[]{560}, "PT");
            add(new int[]{569}, "IS");
            add(new int[]{570, AppRoot.USER_KEYCODE.IR_ZOOM_TELE}, "DK");
            add(new int[]{590}, "PL");
            add(new int[]{AppRoot.USER_KEYCODE.LEFT_DOWN}, "RO");
            add(new int[]{AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_C}, "HU");
            add(new int[]{AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S, AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_A}, "ZA");
            add(new int[]{AppRoot.USER_KEYCODE.FOCUS_HOLD}, "GH");
            add(new int[]{AppRoot.USER_KEYCODE.DISP}, "BH");
            add(new int[]{AppRoot.USER_KEYCODE.DIGITAL_ZOOM}, "MU");
            add(new int[]{AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE}, "MA");
            add(new int[]{AppRoot.USER_KEYCODE.UM_S1}, "DZ");
            add(new int[]{AppRoot.USER_KEYCODE.UM_MOVIE_REC}, "KE");
            add(new int[]{AppRoot.USER_KEYCODE.UM_ZOOM_TELE}, "CI");
            add(new int[]{AppRoot.USER_KEYCODE.UM_ZOOM_WIDE}, "TN");
            add(new int[]{AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED}, "SY");
            add(new int[]{AppRoot.USER_KEYCODE.CUSTOM1}, "EG");
            add(new int[]{624}, "LY");
            add(new int[]{AppRoot.USER_KEYCODE.PEAKING}, "JO");
            add(new int[]{AppRoot.USER_KEYCODE.PROJECTOR}, "IR");
            add(new int[]{AppRoot.USER_KEYCODE.ZEBRA}, "KW");
            add(new int[]{AppRoot.USER_KEYCODE.MODE_P}, "SA");
            add(new int[]{AppRoot.USER_KEYCODE.MODE_A}, "AE");
            add(new int[]{AppRoot.USER_KEYCODE.WATER_HOUSING, AppRoot.USER_KEYCODE.RING_COUNTERCW}, "FI");
            add(new int[]{690, 695}, "CN");
            add(new int[]{700, 709}, "NO");
            add(new int[]{729}, "IL");
            add(new int[]{730, 739}, "SE");
            add(new int[]{740}, "GT");
            add(new int[]{741}, "SV");
            add(new int[]{742}, "HN");
            add(new int[]{743}, "NI");
            add(new int[]{744}, "CR");
            add(new int[]{745}, "PA");
            add(new int[]{746}, "DO");
            add(new int[]{750}, "MX");
            add(new int[]{754, 755}, "CA");
            add(new int[]{759}, "VE");
            add(new int[]{760, 769}, "CH");
            add(new int[]{770}, "CO");
            add(new int[]{773}, "UY");
            add(new int[]{775}, "PE");
            add(new int[]{777}, "BO");
            add(new int[]{779}, "AR");
            add(new int[]{780}, "CL");
            add(new int[]{784}, "PY");
            add(new int[]{785}, "PE");
            add(new int[]{AppRoot.USER_KEYCODE.LENS_DETACH}, "EC");
            add(new int[]{789, 790}, "BR");
            add(new int[]{800, 839}, "IT");
            add(new int[]{840, 849}, "ES");
            add(new int[]{850}, "CU");
            add(new int[]{858}, "SK");
            add(new int[]{859}, "CZ");
            add(new int[]{860}, "YU");
            add(new int[]{865}, "MN");
            add(new int[]{867}, "KP");
            add(new int[]{868, 869}, "TR");
            add(new int[]{870, 879}, "NL");
            add(new int[]{880}, "KR");
            add(new int[]{885}, "TH");
            add(new int[]{888}, "SG");
            add(new int[]{890}, "IN");
            add(new int[]{893}, "VN");
            add(new int[]{896}, "PK");
            add(new int[]{899}, "ID");
            add(new int[]{900, 919}, "AT");
            add(new int[]{930, 939}, "AU");
            add(new int[]{940, 949}, "AZ");
            add(new int[]{955}, "MY");
            add(new int[]{958}, "MO");
        }
    }
}
