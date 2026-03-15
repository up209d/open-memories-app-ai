package com.google.zxing.qrcode.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class AlignmentPatternFinder {
    private final int height;
    private final BitMatrix image;
    private final float moduleSize;
    private final ResultPointCallback resultPointCallback;
    private final int startX;
    private final int startY;
    private final int width;
    private final List<AlignmentPattern> possibleCenters = new ArrayList(5);
    private final int[] crossCheckStateCount = new int[3];

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlignmentPatternFinder(BitMatrix image, int startX, int startY, int width, int height, float moduleSize, ResultPointCallback resultPointCallback) {
        this.image = image;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.moduleSize = moduleSize;
        this.resultPointCallback = resultPointCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlignmentPattern find() throws NotFoundException {
        AlignmentPattern confirmed;
        AlignmentPattern confirmed2;
        int startX = this.startX;
        int height = this.height;
        int maxJ = startX + this.width;
        int middleI = this.startY + (height >> 1);
        int[] stateCount = new int[3];
        for (int iGen = 0; iGen < height; iGen++) {
            int i = middleI + ((iGen & 1) == 0 ? (iGen + 1) >> 1 : -((iGen + 1) >> 1));
            stateCount[0] = 0;
            stateCount[1] = 0;
            stateCount[2] = 0;
            int j = startX;
            while (j < maxJ && !this.image.get(j, i)) {
                j++;
            }
            int currentState = 0;
            while (j < maxJ) {
                if (this.image.get(j, i)) {
                    if (currentState == 1) {
                        stateCount[currentState] = stateCount[currentState] + 1;
                    } else if (currentState == 2) {
                        if (!foundPatternCross(stateCount) || (confirmed2 = handlePossibleCenter(stateCount, i, j)) == null) {
                            stateCount[0] = stateCount[2];
                            stateCount[1] = 1;
                            stateCount[2] = 0;
                            currentState = 1;
                        } else {
                            return confirmed2;
                        }
                    } else {
                        currentState++;
                        stateCount[currentState] = stateCount[currentState] + 1;
                    }
                } else {
                    if (currentState == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                }
                j++;
            }
            if (foundPatternCross(stateCount) && (confirmed = handlePossibleCenter(stateCount, i, maxJ)) != null) {
                return confirmed;
            }
        }
        if (!this.possibleCenters.isEmpty()) {
            return this.possibleCenters.get(0);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static float centerFromEnd(int[] stateCount, int end) {
        return (end - stateCount[2]) - (stateCount[1] / 2.0f);
    }

    private boolean foundPatternCross(int[] stateCount) {
        float moduleSize = this.moduleSize;
        float maxVariance = moduleSize / 2.0f;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(moduleSize - stateCount[i]) >= maxVariance) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x002d, code lost:            if (r3[1] <= r13) goto L13;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0030, code lost:            if (r0 < 0) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0036, code lost:            if (r1.get(r12, r0) != false) goto L48;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x003a, code lost:            if (r3[0] > r13) goto L49;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x003c, code lost:            r3[0] = r3[0] + 1;        r0 = r0 - 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0047, code lost:            if (r3[0] > r13) goto L57;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0049, code lost:            r0 = r11 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x004b, code lost:            if (r0 >= r2) goto L52;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0051, code lost:            if (r1.get(r12, r0) == false) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0055, code lost:            if (r3[1] > r13) goto L51;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0057, code lost:            r3[1] = r3[1] + 1;        r0 = r0 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0060, code lost:            if (r0 == r2) goto L58;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0064, code lost:            if (r3[1] > r13) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0066, code lost:            if (r0 >= r2) goto L54;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x006c, code lost:            if (r1.get(r12, r0) != false) goto L55;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0070, code lost:            if (r3[2] > r13) goto L53;     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0072, code lost:            r3[2] = r3[2] + 1;        r0 = r0 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x007d, code lost:            if (r3[2] > r13) goto L60;     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x007f, code lost:            r4 = (r3[0] + r3[1]) + r3[2];     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0092, code lost:            if ((java.lang.Math.abs(r4 - r14) * 5) >= (r14 * 2)) goto L61;     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0098, code lost:            if (foundPatternCross(r3) == false) goto L62;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:?, code lost:            return centerFromEnd(r3, r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:48:?, code lost:            return Float.NaN;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:?, code lost:            return Float.NaN;     */
    /* JADX WARN: Code restructure failed: missing block: B:50:?, code lost:            return Float.NaN;     */
    /* JADX WARN: Code restructure failed: missing block: B:53:?, code lost:            return Float.NaN;     */
    /* JADX WARN: Code restructure failed: missing block: B:54:?, code lost:            return Float.NaN;     */
    /* JADX WARN: Code restructure failed: missing block: B:57:?, code lost:            return Float.NaN;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:?, code lost:            return Float.NaN;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private float crossCheckVertical(int r11, int r12, int r13, int r14) {
        /*
            r10 = this;
            r9 = 2
            r5 = 2143289344(0x7fc00000, float:NaN)
            r8 = 1
            r7 = 0
            com.google.zxing.common.BitMatrix r1 = r10.image
            int r2 = r1.getHeight()
            int[] r3 = r10.crossCheckStateCount
            r3[r7] = r7
            r3[r8] = r7
            r3[r9] = r7
            r0 = r11
        L14:
            if (r0 < 0) goto L29
            boolean r6 = r1.get(r12, r0)
            if (r6 == 0) goto L29
            r6 = r3[r8]
            if (r6 > r13) goto L29
            r6 = r3[r8]
            int r6 = r6 + 1
            r3[r8] = r6
            int r0 = r0 + (-1)
            goto L14
        L29:
            if (r0 < 0) goto L2f
            r6 = r3[r8]
            if (r6 <= r13) goto L30
        L2f:
            return r5
        L30:
            if (r0 < 0) goto L45
            boolean r6 = r1.get(r12, r0)
            if (r6 != 0) goto L45
            r6 = r3[r7]
            if (r6 > r13) goto L45
            r6 = r3[r7]
            int r6 = r6 + 1
            r3[r7] = r6
            int r0 = r0 + (-1)
            goto L30
        L45:
            r6 = r3[r7]
            if (r6 > r13) goto L2f
            int r0 = r11 + 1
        L4b:
            if (r0 >= r2) goto L60
            boolean r6 = r1.get(r12, r0)
            if (r6 == 0) goto L60
            r6 = r3[r8]
            if (r6 > r13) goto L60
            r6 = r3[r8]
            int r6 = r6 + 1
            r3[r8] = r6
            int r0 = r0 + 1
            goto L4b
        L60:
            if (r0 == r2) goto L2f
            r6 = r3[r8]
            if (r6 > r13) goto L2f
        L66:
            if (r0 >= r2) goto L7b
            boolean r6 = r1.get(r12, r0)
            if (r6 != 0) goto L7b
            r6 = r3[r9]
            if (r6 > r13) goto L7b
            r6 = r3[r9]
            int r6 = r6 + 1
            r3[r9] = r6
            int r0 = r0 + 1
            goto L66
        L7b:
            r6 = r3[r9]
            if (r6 > r13) goto L2f
            r6 = r3[r7]
            r7 = r3[r8]
            int r6 = r6 + r7
            r7 = r3[r9]
            int r4 = r6 + r7
            int r6 = r4 - r14
            int r6 = java.lang.Math.abs(r6)
            int r6 = r6 * 5
            int r7 = r14 * 2
            if (r6 >= r7) goto L2f
            boolean r6 = r10.foundPatternCross(r3)
            if (r6 == 0) goto L2f
            float r5 = centerFromEnd(r3, r0)
            goto L2f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.AlignmentPatternFinder.crossCheckVertical(int, int, int, int):float");
    }

    private AlignmentPattern handlePossibleCenter(int[] stateCount, int i, int j) {
        int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2];
        float centerJ = centerFromEnd(stateCount, j);
        float centerI = crossCheckVertical(i, (int) centerJ, stateCount[1] * 2, stateCountTotal);
        if (!Float.isNaN(centerI)) {
            float estimatedModuleSize = ((stateCount[0] + stateCount[1]) + stateCount[2]) / 3.0f;
            for (AlignmentPattern center : this.possibleCenters) {
                if (center.aboutEquals(estimatedModuleSize, centerI, centerJ)) {
                    return center.combineEstimate(centerI, centerJ, estimatedModuleSize);
                }
            }
            AlignmentPattern point = new AlignmentPattern(centerJ, centerI, estimatedModuleSize);
            this.possibleCenters.add(point);
            if (this.resultPointCallback != null) {
                this.resultPointCallback.foundPossibleResultPoint(point);
            }
        }
        return null;
    }
}
