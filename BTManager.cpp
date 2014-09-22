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

	delay(1500);
	btSerial.print(esc);
	delay(1500);

	btSerial.print("AT\r");
	readMessage();
	delay(1500);

	btSerial.print("ATZ\r");
	readMessage();
	delay(1500);

	btSerial.print("AT+BTNAME=" + BTName + "\r");
	readMessage();
	delay(1500);

	btSerial.print("AT+BTKEY=1234\r");
	readMessage();
	delay(1500);

	btSerial.print("AT+BTSCAN\r");
	readMessage();
	delay(1500);

	btSerial.print("AT+BTINFO?0\r");
	readMessage();
	delay(1500);
}

String BTManager::readMessage() {
	String message = "";
	char c;

	while (btSerial.available() == 0) {
	}

	while (btSerial.available() > 0) {
		c = btSerial.read();
		message += String(c);
	}

	return message;
}

