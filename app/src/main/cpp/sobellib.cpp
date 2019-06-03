#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_gmail_david_baselga_soberandroidapp_MainActivity_sobelFilter(
        JNIEnv* env,
        jobject here,
        jstring src) {

    std::string sourceFile(env->GetStringUTFChars(src, NULL));



    return env->NewStringUTF(sourceFile.c_str());
}
