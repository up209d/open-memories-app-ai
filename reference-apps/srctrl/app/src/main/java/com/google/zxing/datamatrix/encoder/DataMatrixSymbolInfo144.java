package com.google.zxing.datamatrix.encoder;

import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
final class DataMatrixSymbolInfo144 extends SymbolInfo {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DataMatrixSymbolInfo144() {
        super(false, 1558, AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 22, 22, 36, -1, 62);
    }

    @Override // com.google.zxing.datamatrix.encoder.SymbolInfo
    public int getInterleavedBlockCount() {
        return 10;
    }

    @Override // com.google.zxing.datamatrix.encoder.SymbolInfo
    public int getDataLengthForInterleavedBlock(int index) {
        return index <= 8 ? 156 : 155;
    }
}
