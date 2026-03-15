package com.google.zxing.client.result;

/* loaded from: classes.dex */
public final class WifiParsedResult extends ParsedResult {
    private final boolean hidden;
    private final String networkEncryption;
    private final String password;
    private final String ssid;

    public WifiParsedResult(String networkEncryption, String ssid, String password) {
        this(networkEncryption, ssid, password, false);
    }

    public WifiParsedResult(String networkEncryption, String ssid, String password, boolean hidden) {
        super(ParsedResultType.WIFI);
        this.ssid = ssid;
        this.networkEncryption = networkEncryption;
        this.password = password;
        this.hidden = hidden;
    }

    public String getSsid() {
        return this.ssid;
    }

    public String getNetworkEncryption() {
        return this.networkEncryption;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    @Override // com.google.zxing.client.result.ParsedResult
    public String getDisplayResult() {
        StringBuilder result = new StringBuilder(80);
        maybeAppend(this.ssid, result);
        maybeAppend(this.networkEncryption, result);
        maybeAppend(this.password, result);
        maybeAppend(Boolean.toString(this.hidden), result);
        return result.toString();
    }
}
