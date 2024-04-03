#include "secrets.h"

static int api_key_bytes API_KEY_BYTES_DEFINITION;

void getApiKey(char* buffer) {
    for (int i = 0; i < API_KEY_LENGTH; i++) {
        buffer[i] = api_key_bytes[i] ^ XOR_VALUE;
    }
}
