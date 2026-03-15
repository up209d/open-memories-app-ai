package com.google.zxing.oned.rss;

import com.google.zxing.NotFoundException;
import com.google.zxing.oned.OneDReader;
import com.sony.imaging.app.base.common.AudioVolumeController;

/* loaded from: classes.dex */
public abstract class AbstractRSSReader extends OneDReader {
    private static final int MAX_AVG_VARIANCE = 51;
    private static final float MAX_FINDER_PATTERN_RATIO = 0.89285713f;
    private static final int MAX_INDIVIDUAL_VARIANCE = 115;
    private static final float MIN_FINDER_PATTERN_RATIO = 0.7916667f;
    private final int[] decodeFinderCounters = new int[4];
    private final int[] dataCharacterCounters = new int[8];
    private final float[] oddRoundingErrors = new float[4];
    private final float[] evenRoundingErrors = new float[4];
    private final int[] oddCounts = new int[this.dataCharacterCounters.length / 2];
    private final int[] evenCounts = new int[this.dataCharacterCounters.length / 2];

    /* JADX INFO: Access modifiers changed from: protected */
    public final int[] getDecodeFinderCounters() {
        return this.decodeFinderCounters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int[] getDataCharacterCounters() {
        return this.dataCharacterCounters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final float[] getOddRoundingErrors() {
        return this.oddRoundingErrors;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final float[] getEvenRoundingErrors() {
        return this.evenRoundingErrors;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int[] getOddCounts() {
        return this.oddCounts;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int[] getEvenCounts() {
        return this.evenCounts;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int parseFinderValue(int[] counters, int[][] finderPatterns) throws NotFoundException {
        for (int value = 0; value < finderPatterns.length; value++) {
            if (patternMatchVariance(counters, finderPatterns[value], MAX_INDIVIDUAL_VARIANCE) < 51) {
                return value;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int count(int[] array) {
        int count = 0;
        for (int a : array) {
            count += a;
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void increment(int[] array, float[] errors) {
        int index = 0;
        float biggestError = errors[0];
        for (int i = 1; i < array.length; i++) {
            if (errors[i] > biggestError) {
                biggestError = errors[i];
                index = i;
            }
        }
        array[index] = array[index] + 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void decrement(int[] array, float[] errors) {
        int index = 0;
        float biggestError = errors[0];
        for (int i = 1; i < array.length; i++) {
            if (errors[i] < biggestError) {
                biggestError = errors[i];
                index = i;
            }
        }
        array[index] = array[index] - 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isFinderPattern(int[] counters) {
        int firstTwoSum = counters[0] + counters[1];
        int sum = counters[2] + firstTwoSum + counters[3];
        float ratio = firstTwoSum / sum;
        if (ratio < MIN_FINDER_PATTERN_RATIO || ratio > MAX_FINDER_PATTERN_RATIO) {
            return false;
        }
        int minCounter = Integer.MAX_VALUE;
        int maxCounter = AudioVolumeController.INVALID_VALUE;
        for (int counter : counters) {
            if (counter > maxCounter) {
                maxCounter = counter;
            }
            if (counter < minCounter) {
                minCounter = counter;
            }
        }
        return maxCounter < minCounter * 10;
    }
}
