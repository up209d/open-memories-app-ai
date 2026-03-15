package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class URIResultParser extends ResultParser {
    private static final Pattern URL_WITH_PROTOCOL_PATTERN = Pattern.compile("[a-zA-Z0-9]{2,}:");
    private static final Pattern URL_WITHOUT_PROTOCOL_PATTERN = Pattern.compile("([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,}(:\\d{1,5})?(/|\\?|$)");

    @Override // com.google.zxing.client.result.ResultParser
    public URIParsedResult parse(Result result) {
        String rawText = getMassagedText(result);
        if (rawText.startsWith("URL:") || rawText.startsWith("URI:")) {
            return new URIParsedResult(rawText.substring(4).trim(), null);
        }
        String rawText2 = rawText.trim();
        if (isBasicallyValidURI(rawText2)) {
            return new URIParsedResult(rawText2, null);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isBasicallyValidURI(String uri) {
        if (uri.contains(ExposureModeController.SOFT_SNAP)) {
            return false;
        }
        Matcher m = URL_WITH_PROTOCOL_PATTERN.matcher(uri);
        if (m.find() && m.start() == 0) {
            return true;
        }
        Matcher m2 = URL_WITHOUT_PROTOCOL_PATTERN.matcher(uri);
        return m2.find() && m2.start() == 0;
    }
}
