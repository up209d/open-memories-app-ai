#include <jni.h>
#include <cmath>
#include <cstdlib>
#include <cstring>
#include <android/log.h>

#define LOG_TAG "LutProcessor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static float* g_lut = NULL;
static int g_lut_size = 0;
static int g_loaded = 0;

// BT.601 YUV<->RGB conversion coefficients
static const float KR = 0.299f;
static const float KG = 0.587f;
static const float KB = 0.114f;

static inline float clampf(float v, float lo, float hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static inline int clampi(int v, int lo, int hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static inline float lerp(float a, float b, float t) {
    return a + t * (b - a);
}

// Trilinear interpolation lookup in 3D LUT
// r, g, b are in [0.0, 1.0]
static void trilinearLookup(float r, float g, float b, float* outR, float* outG, float* outB) {
    int size = g_lut_size;
    int maxIdx = size - 1;

    // Scale to LUT coordinates
    float fr = clampf(r, 0.0f, 1.0f) * maxIdx;
    float fg = clampf(g, 0.0f, 1.0f) * maxIdx;
    float fb = clampf(b, 0.0f, 1.0f) * maxIdx;

    int r0 = (int)fr;
    int g0 = (int)fg;
    int b0 = (int)fb;

    int r1 = (r0 < maxIdx) ? r0 + 1 : r0;
    int g1 = (g0 < maxIdx) ? g0 + 1 : g0;
    int b1 = (b0 < maxIdx) ? b0 + 1 : b0;

    float dr = fr - r0;
    float dg = fg - g0;
    float db = fb - b0;

    // 8 corner indices in flat array (R varies fastest, then G, then B)
    // Index = (b * size * size + g * size + r) * 3
    int stride_r = 3;
    int stride_g = size * 3;
    int stride_b = size * size * 3;

    int base000 = b0 * stride_b + g0 * stride_g + r0 * stride_r;
    int base100 = b0 * stride_b + g0 * stride_g + r1 * stride_r;
    int base010 = b0 * stride_b + g1 * stride_g + r0 * stride_r;
    int base110 = b0 * stride_b + g1 * stride_g + r1 * stride_r;
    int base001 = b1 * stride_b + g0 * stride_g + r0 * stride_r;
    int base101 = b1 * stride_b + g0 * stride_g + r1 * stride_r;
    int base011 = b1 * stride_b + g1 * stride_g + r0 * stride_r;
    int base111 = b1 * stride_b + g1 * stride_g + r1 * stride_r;

    // Trilinear interpolation for each channel
    for (int c = 0; c < 3; c++) {
        float c000 = g_lut[base000 + c];
        float c100 = g_lut[base100 + c];
        float c010 = g_lut[base010 + c];
        float c110 = g_lut[base110 + c];
        float c001 = g_lut[base001 + c];
        float c101 = g_lut[base101 + c];
        float c011 = g_lut[base011 + c];
        float c111 = g_lut[base111 + c];

        // Interpolate along R
        float c00 = lerp(c000, c100, dr);
        float c10 = lerp(c010, c110, dr);
        float c01 = lerp(c001, c101, dr);
        float c11 = lerp(c011, c111, dr);

        // Interpolate along G
        float c0 = lerp(c00, c10, dg);
        float c1 = lerp(c01, c11, dg);

        // Interpolate along B
        float result = lerp(c0, c1, db);

        if (c == 0) *outR = result;
        else if (c == 1) *outG = result;
        else *outB = result;
    }
}

extern "C" {

JNIEXPORT void JNICALL
Java_com_github_up209d_cubelut_LutProcessor_loadLut(
        JNIEnv *env, jobject, jfloatArray data, jint size) {
    // Free previous LUT if loaded
    if (g_lut != NULL) {
        free(g_lut);
        g_lut = NULL;
        g_loaded = 0;
    }

    jsize len = env->GetArrayLength(data);
    int expected = size * size * size * 3;
    if (len != expected) {
        LOGE("LUT data length mismatch: got %d, expected %d", len, expected);
        return;
    }

    g_lut = (float*)malloc(len * sizeof(float));
    if (g_lut == NULL) {
        LOGE("Failed to allocate %d bytes for LUT", (int)(len * sizeof(float)));
        return;
    }

    env->GetFloatArrayRegion(data, 0, len, g_lut);
    g_lut_size = size;
    g_loaded = 1;
    LOGI("LUT loaded: size=%d, entries=%d, memory=%dKB", size, size*size*size,
         (int)(len * sizeof(float) / 1024));
}

JNIEXPORT void JNICALL
Java_com_github_up209d_cubelut_LutProcessor_applyLut(
        JNIEnv *env, jobject, jbyteArray imageData, jint width, jint height) {
    if (!g_loaded || g_lut == NULL) {
        LOGE("No LUT loaded, skipping apply");
        return;
    }

    jsize len = env->GetArrayLength(imageData);
    jbyte* pixels = env->GetByteArrayElements(imageData, NULL);
    if (pixels == NULL) {
        LOGE("Failed to get image array elements");
        return;
    }

    // Process NV21 YUV format (standard Android camera preview/capture format)
    // Y plane: width * height bytes
    // UV plane: width * height / 2 bytes (interleaved V, U)
    int ySize = width * height;
    unsigned char* yPlane = (unsigned char*)pixels;
    unsigned char* uvPlane = (unsigned char*)pixels + ySize;

    for (int j = 0; j < height; j++) {
        for (int i = 0; i < width; i++) {
            int yIdx = j * width + i;
            int uvIdx = (j / 2) * width + (i & ~1); // NV21: V at even, U at odd

            int Y = yPlane[yIdx] & 0xFF;
            int V = uvPlane[uvIdx] & 0xFF;
            int U = uvPlane[uvIdx + 1] & 0xFF;

            // YUV to RGB (BT.601)
            float yf = (Y - 16) / 219.0f;
            float uf = (U - 128) / 224.0f;
            float vf = (V - 128) / 224.0f;

            float rf = clampf(yf + 1.402f * vf, 0.0f, 1.0f);
            float gf = clampf(yf - 0.344136f * uf - 0.714136f * vf, 0.0f, 1.0f);
            float bf = clampf(yf + 1.772f * uf, 0.0f, 1.0f);

            // Apply 3D LUT
            float outR, outG, outB;
            trilinearLookup(rf, gf, bf, &outR, &outG, &outB);

            // RGB to YUV (BT.601)
            outR = clampf(outR, 0.0f, 1.0f);
            outG = clampf(outG, 0.0f, 1.0f);
            outB = clampf(outB, 0.0f, 1.0f);

            int newY = (int)(16 + 219 * (KR * outR + KG * outG + KB * outB));
            yPlane[yIdx] = (unsigned char)clampi(newY, 16, 235);

            // Only update chroma for top-left pixel of each 2x2 block
            if ((i & 1) == 0 && (j & 1) == 0) {
                int newU = (int)(128 + 224 * 0.5f * (outB - (KR * outR + KG * outG + KB * outB)) / (1.0f - KB));
                int newV = (int)(128 + 224 * 0.5f * (outR - (KR * outR + KG * outG + KB * outB)) / (1.0f - KR));
                uvPlane[uvIdx] = (unsigned char)clampi(newV, 16, 240);
                uvPlane[uvIdx + 1] = (unsigned char)clampi(newU, 16, 240);
            }
        }
    }

    env->ReleaseByteArrayElements(imageData, pixels, 0);
    LOGI("LUT applied to %dx%d image", width, height);
}

JNIEXPORT void JNICALL
Java_com_github_up209d_cubelut_LutProcessor_freeLut(JNIEnv *env, jobject) {
    if (g_lut != NULL) {
        free(g_lut);
        g_lut = NULL;
        g_lut_size = 0;
        g_loaded = 0;
        LOGI("LUT freed");
    }
}

JNIEXPORT jboolean JNICALL
Java_com_github_up209d_cubelut_LutProcessor_isLoaded(JNIEnv *env, jobject) {
    return (jboolean)(g_loaded != 0);
}

} // extern "C"
