#include <SoftwareSerial.h>
#include <BTManager.h>

BTManager *btManager;

void setup() {
  // put your setup code here, to run once:
  
  btManager = new BTManager("test", "1234");
  btManager->init();
  // Bluetoothモジュールを初期化します

}

void loop() {
  // put your main code here, to run repeatedly:
  
  btManager->writeMessage("test");
  // Bluetoothモジュールに送信できます
  
  String message = btManager->readMessage();
  // Bluetoothモジュールから受信できます

}
