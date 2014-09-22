/*
 * btmanager.cpp
 *
 *  Created on: 2014/09/08
 *      Author: derushio
 */

#include "BTManager.h"

SoftwareSerial btSerial(BT_RX, BT_TX);
char esc[] = { 2, 2, 2 };

BTManager::BTManager(String BTName) {
	this->BTName = BTName;
	response = "";

	connectionStatus = false;
	pairingStatus = false;

	btSerial.begin(BT_BAUD);
}

void BTManager::begin() {
	Serial.println("Starting Device...");

	delay(1500);
	btSerial.print(esc);
	Serial.println("esc");
	delay(1500);

	btSerial.print("AT\r");
	Serial.println("AT");
	Serial.println(readMessage());

	delay(1500);

	btSerial.print("ATZ\r");
	Serial.println("ATZ");
	Serial.println(readMessage());

	delay(1500);

	btSerial.print("AT+BTNAME=" + BTName + "\r");
	Serial.println("AT+BTNAME=" + BTName + "\r");
	Serial.println(readMessage());

	delay(1500);

	btSerial.print("AT+BTKEY=1234\r");
	Serial.println("AT+BTKEY=1234");
	Serial.println(readMessage());

	delay(1500);

	btSerial.print("AT+BTSCAN\r");
	Serial.println("AT+BTSCAN");
	Serial.println(readMessage());

	delay(1500);

	btSerial.print("AT+BTINFO?0\r");
	Serial.println("AT+BTINFO?0");
	Serial.println(readMessage());

	delay(1500);

	Serial.println("Sterted Device.");
}

String BTManager::readMessage() {
	String str = "";
	while (1) {
		int timeoutCount = 0;

		while (!btSerial.available()) {
			if (timeoutCount > 50) {
				return str;
			}
			timeoutCount++;
			delay(100);
		}
		str += btSerial.read();
	}
}
