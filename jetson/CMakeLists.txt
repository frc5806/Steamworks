cmake_minimum_required(VERSION 2.8)
set (CMAKE_CXX_STANDARD 11)
cmake_policy(SET CMP0015 NEW)
project( MyProject )
find_package( OpenCV REQUIRED )
include_directories("ntcore/wpiutil/include" "ntcore/include")
link_directories("ntcore/lib")
list(APPEND CMAKE_CXX_FLAGS "-std=c++0x")
#set(CMAKE_SHARED_LINKER_FLAGS "-lntcore" "-lwpiutil")
add_executable( MyProject BoilerPipeline.cpp GearPipeline.cpp Socket.cpp main.cpp)
#target_link_libraries( MyProject pthread ntcore wpiutil ${OpenCV_LIBS})
target_link_libraries( MyProject pthread ${OpenCV_LIBS})
