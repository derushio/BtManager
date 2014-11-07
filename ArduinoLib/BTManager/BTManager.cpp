/*
 * btmanager.cpp
 *
 *  Created on: 2014/09/08
 *      Author: derushio
 */

#include "BTManager.h"

char esc[] = {2, 2, 2};
// エスケープシーケンスを定義

SoftwareSerial btSerial(BT_RX, BT_TX);
// Bluetooth通信用のシリアル

BTManager::BTManager(String btName, String passWord) {
    this->btName = btName;
    this->passWord = passWord;
    btSerial.begin(BT_BAUD);
    // 引数から名前を決定、btSerialを起動
}

void BTManager::init() {

    btSerial.print("starting");

    delay(1500);
    btSerial.print(esc);
    delay(1500);
    // エスケープを送信してATコマンドが通るようにする（ペアリング中等はこれがないと通らない）

    btSerial.print("AT\r");
    delay(1500);

    btSerial.print("ATZ\r");
    delay(1500);
    // AT（ソフトウェア初期化）、ATZ（ハードウェア初期化）

    btSerial.print("AT+BTNAME=" + btName + "\r");
    delay(1500);

    btSerial.print("AT+BTKEY=" + passWord + "\r");
    delay(1500);
    // 名前、パスワードを設定

    btSerial.print("AT+BTSCAN\r");
    delay(1500);
    // Bluetoothスキャンを開始

    btSerial.flush();
}

String BTManager::readMessage() {
    String message = "";
    char c;

    while (btSerial.available() > 0) {
        c = btSerial.read();
        message += String(c);
        // メッセージがある場合はmessageに格納
    }

    return message;
    // Stringとしてメッセージを返す
}

void BTManager::writeMessage(String message) {
    btSerial.print(message);
    // 受け取ったStringをBluetoothシリアルに書き込む
}

