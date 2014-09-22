/*
 * btmanager.h
 *
 *  Created on: 2014/09/08
 *      Author: derushio
 */

#ifndef BT_MANAGER_H_
#define BT_MANAGER_H_

#include <Arduino.h>
#include <SoftwareSerial.h>

#define BT_BAUD 9600
#define BT_RX 10
#define BT_TX 7

class BTManager {
private:
	static const char esc[] = { 2, 2, 2 };
	// エスケープシーケンスを定義
	String BTName;
public:
	BTManager(String BTManager);

	void begin();
	String readMessage();
	void writeMessage(String message);
};

#endif /* BT_MANAGER_H_ */
