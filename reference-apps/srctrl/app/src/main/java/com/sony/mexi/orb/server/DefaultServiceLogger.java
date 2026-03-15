package com.sony.mexi.orb.server;

/* loaded from: classes.dex */
public class DefaultServiceLogger implements DebugLogInterface {
    private void print(String tag, String level, String msg) {
        System.out.printf("[MEXI][%s] %s: %s%n", level, tag, msg);
    }

    @Override // com.sony.mexi.orb.server.DebugLogInterface
    public void error(String tag, String msg) {
        print(tag, "E", msg);
    }

    @Override // com.sony.mexi.orb.server.DebugLogInterface
    public void debug(String tag, String msg) {
        print(tag, "D", msg);
    }

    @Override // com.sony.mexi.orb.server.DebugLogInterface
    public void info(String tag, String msg) {
        print(tag, "V", msg);
    }
}
