package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import java.util.Map;

/* loaded from: classes.dex */
public final class ByQuadrantReader implements Reader {
    private final Reader delegate;

    public ByQuadrantReader(Reader delegate) {
        this.delegate = delegate;
    }

    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return decode(image, null);
    }

    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        int width = image.getWidth();
        int height = image.getHeight();
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        BinaryBitmap topLeft = image.crop(0, 0, halfWidth, halfHeight);
        try {
            return this.delegate.decode(topLeft, hints);
        } catch (NotFoundException e) {
            BinaryBitmap topRight = image.crop(halfWidth, 0, halfWidth, halfHeight);
            try {
                return this.delegate.decode(topRight, hints);
            } catch (NotFoundException e2) {
                BinaryBitmap bottomLeft = image.crop(0, halfHeight, halfWidth, halfHeight);
                try {
                    return this.delegate.decode(bottomLeft, hints);
                } catch (NotFoundException e3) {
                    BinaryBitmap bottomRight = image.crop(halfWidth, halfHeight, halfWidth, halfHeight);
                    try {
                        return this.delegate.decode(bottomRight, hints);
                    } catch (NotFoundException e4) {
                        int quarterWidth = halfWidth / 2;
                        int quarterHeight = halfHeight / 2;
                        BinaryBitmap center = image.crop(quarterWidth, quarterHeight, halfWidth, halfHeight);
                        return this.delegate.decode(center, hints);
                    }
                }
            }
        }
    }

    @Override // com.google.zxing.Reader
    public void reset() {
        this.delegate.reset();
    }
}
