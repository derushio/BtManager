#include <SoftwareSerial.h>

#include <BTManager.h>

BTManager *btManager;

void setup() {
    Serial.begin(9600);
    Serial.println("Device Starting...");

    btManager = new BTManager("derushio", "1234");
    btManager->init();

    Mouse.begin();
}

void loop() {
    String message = btManager->readMessage();
    if (message != "") {
        Serial.println(message);
        int codeLength = 16;
        char code[codeLength];

        int xIndex = message.indexOf("x");
        int yIndex = message.indexOf("y");
        int sIndex = message.indexOf("s");
        int endIndex = message.indexOf("e");

        long xMove = 0;
        long yMove = 0;
        long scroll = 0;

        if (xIndex != -1 || yIndex != -1 || sIndex != -1 || endIndex != -1) {
            message.substring(xIndex + 1, yIndex).toCharArray(code, codeLength);
            xMove = atoi(code);
            Serial.println(xMove);

            message.substring(yIndex + 1, sIndex).toCharArray(code, codeLength);
            yMove = atoi(code);
            Serial.println(yMove);

            message.substring(sIndex + 1, endIndex).toCharArray(code, codeLength);
            scroll = atoi(code);
            Serial.println(scroll);

            while (xMove != 0 || yMove != 0 || scroll != 0) {
                int x = 0;
                int y = 0;
                int s = 0;

                if (xMove > 0) {
                    x = 1;
                    xMove--;
                } else if (xMove < 0) {
                    x = -1;
                    xMove++;
                }

                if (yMove > 0) {
                    y = 1;
                    yMove--;
                } else if (yMove < 0) {
                    y = -1;
                    yMove++;
                }

                if (scroll > 0) {
                    s = 1;
                    scroll--;
                } else if (scroll < 0) {
                    s = -1;
                    scroll++;
                }

                Mouse.move(x, y, s);

            }
        }
    }
}
