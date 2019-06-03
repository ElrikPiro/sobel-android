#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_gmail_david_baselga_soberandroidapp_MainActivity_sobelFilter(
        JNIEnv* env,
        jobject here,
        jstring src) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
