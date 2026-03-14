cmake_minimum_required(VERSION 3.10.2)

project("native-lib")

add_library(native-lib SHARED native-lib.cpp)

find_library(ANDROID_LIBRARY android)
find_library(LOG_LIBRARY log)
target_link_libraries(native-lib ${ANDROID_LIBRARY} ${LOG_LIBRARY})
