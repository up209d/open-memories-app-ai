cmake_minimum_required(VERSION 3.10.2)

project("lut-processor")

add_library(lut-processor SHARED lut-processor.cpp)

find_library(LOG_LIBRARY log)
target_link_libraries(lut-processor ${LOG_LIBRARY})
