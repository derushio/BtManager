/*
 * btmanager.h
 *
 *  Created on: 2014/09/08
 *      Author: derushio
 */

#ifndef BT_MANAGER_H_
#define BT_MANAGER_H_

#include "Arduino.h"
#include <SoftwareSerial.h>

#define BT_BAUD 9600
#define BT_RX 10
#define BT_TX 7

class BTManager {
private:
	String BTName;
	String response;

	bool connectionStatus;
	bool pairingStatus;

public:
	BTManager(String BTManager);
	void begin();
	bool isConnected();
	bool isPaired();

	void readMessage();
	bool writeMessage(String message);
};

#endif /* BT_MANAGER_H_ */
