cmake_minimum_required(VERSION 2.8)
project( MyProject )
find_package( OpenCV REQUIRED )
add_executable( MyProject main.cpp Pipeline.cpp)
target_link_libraries( MyProject ${OpenCV_LIBS} )

