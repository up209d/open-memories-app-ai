package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

/* loaded from: classes.dex */
public final class MonochromeRectangleDetector {
    private static final int MAX_MODULES = 32;
    private final BitMatrix image;

    public MonochromeRectangleDetector(BitMatrix image) {
        this.image = image;
    }

    public ResultPoint[] detect() throws NotFoundException {
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        int halfHeight = height >> 1;
        int halfWidth = width >> 1;
        int deltaY = Math.max(1, height / 256);
        int deltaX = Math.max(1, width / 256);
        ResultPoint pointA = findCornerFromCenter(halfWidth, 0, 0, width, halfHeight, -deltaY, 0, height, halfWidth >> 1);
        int top = ((int) pointA.getY()) - 1;
        ResultPoint pointB = findCornerFromCenter(halfWidth, -deltaX, 0, width, halfHeight, 0, top, height, halfHeight >> 1);
        int left = ((int) pointB.getX()) - 1;
        ResultPoint pointC = findCornerFromCenter(halfWidth, deltaX, left, width, halfHeight, 0, top, height, halfHeight >> 1);
        int right = ((int) pointC.getX()) + 1;
        ResultPoint pointD = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, deltaY, top, height, halfWidth >> 1);
        int bottom = ((int) pointD.getY()) + 1;
        ResultPoint pointA2 = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, bottom, halfWidth >> 2);
        return new ResultPoint[]{pointA2, pointB, pointC, pointD};
    }

    private ResultPoint findCornerFromCenter(int centerX, int deltaX, int left, int right, int centerY, int deltaY, int top, int bottom, int maxWhiteRun) throws NotFoundException {
        int[] range;
        int[] lastRange = null;
        int y = centerY;
        int x = centerX;
        while (y < bottom && y >= top && x < right && x >= left) {
            if (deltaX == 0) {
                range = blackWhiteRange(y, maxWhiteRun, left, right, true);
            } else {
                range = blackWhiteRange(x, maxWhiteRun, top, bottom, false);
            }
            if (range == null) {
                if (lastRange == null) {
                    throw NotFoundException.getNotFoundInstance();
                }
                if (deltaX == 0) {
                    int lastY = y - deltaY;
                    if (lastRange[0] < centerX) {
                        if (lastRange[1] > centerX) {
                            return new ResultPoint(deltaY > 0 ? lastRange[0] : lastRange[1], lastY);
                        }
                        return new ResultPoint(lastRange[0], lastY);
                    }
                    return new ResultPoint(lastRange[1], lastY);
                }
                int lastX = x - deltaX;
                if (lastRange[0] < centerY) {
                    if (lastRange[1] > centerY) {
                        return new ResultPoint(lastX, deltaX < 0 ? lastRange[0] : lastRange[1]);
                    }
                    return new ResultPoint(lastX, lastRange[0]);
                }
                return new ResultPoint(lastX, lastRange[1]);
            }
            lastRange = range;
            y += deltaY;
            x += deltaX;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0021  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x005a  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0064 A[EDGE_INSN: B:55:0x0064->B:34:0x0064 BREAK  A[LOOP:3: B:27:0x0056->B:49:0x0056], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x002b A[EDGE_INSN: B:74:0x002b->B:15:0x002b BREAK  A[LOOP:1: B:8:0x001d->B:68:0x001d], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int[] blackWhiteRange(int r8, int r9, int r10, int r11, boolean r12) {
        /*
            r7 = this;
            int r5 = r10 + r11
            int r0 = r5 >> 1
            r2 = r0
        L5:
            if (r2 < r10) goto L32
            if (r12 == 0) goto L14
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r2, r8)
            if (r5 == 0) goto L1c
        L11:
            int r2 = r2 + (-1)
            goto L5
        L14:
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r8, r2)
            if (r5 != 0) goto L11
        L1c:
            r4 = r2
        L1d:
            int r2 = r2 + (-1)
            if (r2 < r10) goto L2b
            if (r12 == 0) goto L44
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r2, r8)
            if (r5 == 0) goto L1d
        L2b:
            int r3 = r4 - r2
            if (r2 < r10) goto L31
            if (r3 <= r9) goto L5
        L31:
            r2 = r4
        L32:
            int r2 = r2 + 1
            r1 = r0
        L35:
            if (r1 >= r11) goto L6b
            if (r12 == 0) goto L4d
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r1, r8)
            if (r5 == 0) goto L55
        L41:
            int r1 = r1 + 1
            goto L35
        L44:
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r8, r2)
            if (r5 == 0) goto L1d
            goto L2b
        L4d:
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r8, r1)
            if (r5 != 0) goto L41
        L55:
            r4 = r1
        L56:
            int r1 = r1 + 1
            if (r1 >= r11) goto L64
            if (r12 == 0) goto L79
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r1, r8)
            if (r5 == 0) goto L56
        L64:
            int r3 = r1 - r4
            if (r1 >= r11) goto L6a
            if (r3 <= r9) goto L35
        L6a:
            r1 = r4
        L6b:
            int r1 = r1 + (-1)
            if (r1 <= r2) goto L82
            r5 = 2
            int[] r5 = new int[r5]
            r6 = 0
            r5[r6] = r2
            r6 = 1
            r5[r6] = r1
        L78:
            return r5
        L79:
            com.google.zxing.common.BitMatrix r5 = r7.image
            boolean r5 = r5.get(r8, r1)
            if (r5 == 0) goto L56
            goto L64
        L82:
            r5 = 0
            goto L78
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.common.detector.MonochromeRectangleDetector.blackWhiteRange(int, int, int, int, boolean):int[]");
    }
}
