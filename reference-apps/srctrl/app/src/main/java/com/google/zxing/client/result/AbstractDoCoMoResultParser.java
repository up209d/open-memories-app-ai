package com.google.zxing.client.result;

/* loaded from: classes.dex */
abstract class AbstractDoCoMoResultParser extends ResultParser {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] matchDoCoMoPrefixedField(String prefix, String rawText, boolean trim) {
        return matchPrefixedField(prefix, rawText, ';', trim);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String matchSingleDoCoMoPrefixedField(String prefix, String rawText, boolean trim) {
        return matchSinglePrefixedField(prefix, rawText, ';', trim);
    }
}
