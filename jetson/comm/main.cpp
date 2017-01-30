#include <iostream>
#include <chrono>
#include <cstdio>
#include <thread>
#include "ntcore.h"

#include "networktables/NetworkTable.h"

using namespace std;

int main() {
  NetworkTable::SetClientMode();
  NetworkTable::SetIPAddress("10.58.06.100\n");
  
  NetworkTable::Initialize();

  std::this_thread::sleep_for(std::chrono::seconds(5));
  auto nt = NetworkTable::GetTable("test");

  while (true) { 
    int i = rand() % 1000 + 1;
    std::cout << nt->GetNumber("randomvalue") << "\n";
    std::this_thread::sleep_for(std::chrono::milliseconds(200));
  }

}
