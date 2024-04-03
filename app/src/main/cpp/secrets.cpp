#include "secrets.h"

void getApiKey(char* buffer) {
    for (int i = 0; i < API_KEY_LENGTH; i++) {
        buffer[i] = API_KEY[i];
    }
}
