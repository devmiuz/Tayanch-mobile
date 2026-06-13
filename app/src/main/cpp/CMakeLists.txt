# Pillar 8/18 — Binary-level secret storage.
# Builds libtayanch_secrets.so, which holds the app's static secrets (the HMAC
# app-signature key and the endpoint XOR key) as obfuscated byte tables in native
# code. Strings here land in the .so, not the DEX, so `strings`/JADX on the APK's
# classes.dex never reveal them.
cmake_minimum_required(VERSION 3.22.1)
project("tayanch_secrets")

add_library(tayanch_secrets SHARED secrets.cpp)

find_library(log-lib log)
target_link_libraries(tayanch_secrets ${log-lib})

# Strip symbols in release so the exported JNI surface is minimal.
target_compile_options(tayanch_secrets PRIVATE -fvisibility=hidden -O2)

# 16 KB page-size support (required by Google Play for Android 15+). Align the
# .so's LOAD segments to 16 KB so it loads on 4 KB *and* 16 KB page devices.
target_link_options(tayanch_secrets PRIVATE "-Wl,-z,max-page-size=16384")
