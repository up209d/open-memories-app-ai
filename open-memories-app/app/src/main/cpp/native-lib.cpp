#include <jni.h>
#include <cmath>
#include <string>
#include <cstdint>
#include <android/log.h>

#define LOG_TAG "NativeLibrary"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_github_up209d_openmemories_app_MainActivity_getGreetingFromJNI(JNIEnv *env,
                                                                        jobject /* this */) {
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Returning greeting message...");
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
{
JNIEXPORT jintArray JNICALL
Java_com_github_up209d_openmemories_app_MainActivity_processData(
        JNIEnv *env,
        jobject,
        jintArray inputData,
        jintArray lutData) {
    int channels = 4;
    int cl = 8;
    int cs = cl * cl;
    int cs1 = cs - 1;
    jsize inputLen = env->GetArrayLength(inputData);
    jint *input = env->GetIntArrayElements(inputData, 0);
    jint *lut = env->GetIntArrayElements(lutData, 0);
    for (int i = 0; i < inputLen; i += channels) {
        float r = input[i] / 255.0 * cs1;
        float g = input[i + 1] / 255.0 * cs1;
        float b = input[i + 2] / 255.0 * cs1;
        int ci = static_cast<int>((std::floor(r) + std::floor(g) * cs + std::floor(b) * cs * cs) *
                                  channels);
        input[i] = lut[ci];
        input[i + 1] = lut[ci + 1];
        input[i + 2] = lut[ci + 2];
        input[i + 3] = 255;
    }
    env->ReleaseIntArrayElements(inputData, input, 0);
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Processing Image with LUT...");
    return inputData;
}
}
