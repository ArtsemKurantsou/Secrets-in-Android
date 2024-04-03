#include "jni.h"
#include <string>

void getApiKey(char* buffer);

extern "C" JNIEXPORT jstring JNICALL
Java_com_kurantsov_integritycheck_NativeSecrets_getApiKeyFromNative(
        JNIEnv *env,
        jobject thiz
) {
    char key_buffer[API_KEY_LENGTH + 1] ;
    key_buffer[API_KEY_LENGTH] = '\0';
    getApiKey(key_buffer);
    return env->NewStringUTF(key_buffer);
}