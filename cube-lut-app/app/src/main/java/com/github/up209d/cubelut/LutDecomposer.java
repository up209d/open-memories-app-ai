package com.github.up209d.cubelut;

import android.util.Log;

public class LutDecomposer {
    private static final String TAG = "LutDecomposer";
    private static final int GAMMA_TABLE_SIZE = 1024;

    public static class IspParams {
        public short[] gammaCurve;  // 1024 entries for GammaTable
        public int[] rgbMatrix;     // 9 entries (3x3 flat) for RGBMatrix
    }

    /**
     * Decompose a 3D LUT into an approximate 1D gamma curve + 3x3 RGB matrix
     * for real-time ISP preview. This is an approximation - the capture pipeline
     * uses the full 3D LUT via JNI for accuracy.
     */
    public static IspParams decompose(float[] lutData, int cubeSize) {
        if (lutData == null || cubeSize != 33) {
            return null;
        }

        IspParams params = new IspParams();

        // Step 1: Extract 1D curves by sampling along the neutral axis (R=G=B)
        // and along each individual channel axis at the neutral midpoint
        float[] rCurve = new float[cubeSize];
        float[] gCurve = new float[cubeSize];
        float[] bCurve = new float[cubeSize];

        for (int i = 0; i < cubeSize; i++) {
            // Sample the neutral axis: R=G=B=i
            int idx = (i * cubeSize * cubeSize + i * cubeSize + i) * 3;
            rCurve[i] = lutData[idx];
            gCurve[i] = lutData[idx + 1];
            bCurve[i] = lutData[idx + 2];
        }

        // Step 2: Average the 3 channel curves into a single luminance curve
        // and interpolate to 1024 points for GammaTable
        params.gammaCurve = new short[GAMMA_TABLE_SIZE];
        for (int i = 0; i < GAMMA_TABLE_SIZE; i++) {
            float t = (float) i / (GAMMA_TABLE_SIZE - 1);
            float cubePos = t * (cubeSize - 1);
            int idx0 = (int) cubePos;
            int idx1 = Math.min(idx0 + 1, cubeSize - 1);
            float frac = cubePos - idx0;

            float rVal = lerp(rCurve[idx0], rCurve[idx1], frac);
            float gVal = lerp(gCurve[idx0], gCurve[idx1], frac);
            float bVal = lerp(bCurve[idx0], bCurve[idx1], frac);

            // Average for single-channel gamma table
            float avg = (rVal + gVal + bVal) / 3.0f;
            // Scale to 16-bit range (0-65535) as used by Sony GammaTable
            int val = Math.round(avg * 65535.0f);
            if (val < 0) val = 0;
            if (val > 65535) val = 65535;
            params.gammaCurve[i] = (short) val;
        }

        // Step 3: Fit a 3x3 RGB matrix by sampling the LUT at key points
        // Use least-squares regression on sampled points
        params.rgbMatrix = fitRGBMatrix(lutData, cubeSize);

        FileLogger.log("DECOMPOSE", "ISP params generated: gammaCurve[" + GAMMA_TABLE_SIZE
                + "], rgbMatrix[9]");
        return params;
    }

    private static int[] fitRGBMatrix(float[] lutData, int cubeSize) {
        // Sample ~100 evenly spaced points from the LUT
        // Solve for the best-fit 3x3 matrix: output = matrix * input
        int sampleStep = 4; // sample every 4th point along each axis
        int nSamples = 0;

        // Count samples first
        for (int b = 0; b < cubeSize; b += sampleStep) {
            for (int g = 0; g < cubeSize; g += sampleStep) {
                for (int r = 0; r < cubeSize; r += sampleStep) {
                    nSamples++;
                }
            }
        }

        // Build input/output matrices for least-squares
        // We solve: [outR] = [m00 m01 m02] [inR]
        //           [outG]   [m10 m11 m12] [inG]
        //           [outB]   [m20 m21 m22] [inB]
        double[][] inRGB = new double[nSamples][3];
        double[][] outRGB = new double[nSamples][3];
        int s = 0;

        for (int bi = 0; bi < cubeSize; bi += sampleStep) {
            for (int gi = 0; gi < cubeSize; gi += sampleStep) {
                for (int ri = 0; ri < cubeSize; ri += sampleStep) {
                    float inR = (float) ri / (cubeSize - 1);
                    float inG = (float) gi / (cubeSize - 1);
                    float inB = (float) bi / (cubeSize - 1);

                    int idx = (bi * cubeSize * cubeSize + gi * cubeSize + ri) * 3;
                    inRGB[s][0] = inR;
                    inRGB[s][1] = inG;
                    inRGB[s][2] = inB;
                    outRGB[s][0] = lutData[idx];
                    outRGB[s][1] = lutData[idx + 1];
                    outRGB[s][2] = lutData[idx + 2];
                    s++;
                }
            }
        }

        // Solve 3 independent least-squares problems (one per output channel)
        // Using normal equations: (A^T A) x = A^T b
        double[] matrix = new double[9];
        for (int ch = 0; ch < 3; ch++) {
            // A^T A (3x3)
            double[][] ata = new double[3][3];
            double[] atb = new double[3];

            for (int i = 0; i < nSamples; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        ata[j][k] += inRGB[i][j] * inRGB[i][k];
                    }
                    atb[j] += inRGB[i][j] * outRGB[i][ch];
                }
            }

            // Solve 3x3 system via Cramer's rule
            double[] x = solve3x3(ata, atb);
            matrix[ch * 3] = x[0];
            matrix[ch * 3 + 1] = x[1];
            matrix[ch * 3 + 2] = x[2];
        }

        // Convert to Sony's integer format (multiply by 1024, fixed-point)
        int[] result = new int[9];
        for (int i = 0; i < 9; i++) {
            result[i] = (int) Math.round(matrix[i] * 1024);
        }

        Log.d(TAG, "RGB Matrix: ["
                + result[0] + "," + result[1] + "," + result[2] + " / "
                + result[3] + "," + result[4] + "," + result[5] + " / "
                + result[6] + "," + result[7] + "," + result[8] + "]");
        return result;
    }

    private static double[] solve3x3(double[][] a, double[] b) {
        // Gaussian elimination with partial pivoting
        double[][] m = new double[3][4];
        for (int i = 0; i < 3; i++) {
            m[i][0] = a[i][0];
            m[i][1] = a[i][1];
            m[i][2] = a[i][2];
            m[i][3] = b[i];
        }

        for (int col = 0; col < 3; col++) {
            // Find pivot
            int maxRow = col;
            double maxVal = Math.abs(m[col][col]);
            for (int row = col + 1; row < 3; row++) {
                if (Math.abs(m[row][col]) > maxVal) {
                    maxVal = Math.abs(m[row][col]);
                    maxRow = row;
                }
            }
            // Swap rows
            double[] tmp = m[col];
            m[col] = m[maxRow];
            m[maxRow] = tmp;

            if (Math.abs(m[col][col]) < 1e-10) {
                // Singular matrix, return identity
                return new double[]{col == 0 ? 1 : 0, col == 1 ? 1 : 0, col == 2 ? 1 : 0};
            }

            // Eliminate below
            for (int row = col + 1; row < 3; row++) {
                double factor = m[row][col] / m[col][col];
                for (int j = col; j < 4; j++) {
                    m[row][j] -= factor * m[col][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[3];
        for (int i = 2; i >= 0; i--) {
            x[i] = m[i][3];
            for (int j = i + 1; j < 3; j++) {
                x[i] -= m[i][j] * x[j];
            }
            x[i] /= m[i][i];
        }
        return x;
    }

    private static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
}
