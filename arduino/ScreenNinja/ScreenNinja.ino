#include <MeetAndroid.h>

// Bluetooth MAC: 00:06:66:08:E7:60
MeetAndroid meetAndroid;

const int rightSignalPin = 8;  // 0v off; ~10v on
const int leftSignalPin  = 9;  // 0v off; ~10v on
const int highBeamPin    = 10; // 0v off; 12v on
const int neutralPin     = 11; // 10v in gear; 0v in neutral (NEGATIVE TRIGGER)
const int lowFuelPin     = 12; // ~4.5-5v low fuel; ?v full (NEGATIVE TRIGGER)
const int tachSignalPin  = 13; // 0v off; ~6.5v running

void setup() {
//  Serial.begin(9600); // USB
  Serial.begin(115200); // BLUETOOTH
  pinMode(highBeamPin,    INPUT);
  pinMode(leftSignalPin,  INPUT);
  pinMode(rightSignalPin, INPUT);
  pinMode(neutralPin,     INPUT);
  pinMode(lowFuelPin,     INPUT);
  pinMode(tachSignalPin,  INPUT);
}

void loop() {
  meetAndroid.receive();

  String out = "_";
  int val = analogRead(rightSignalPin);
  out += (val > 100) ? "1" : "0";
  out += "|";

  val = analogRead(leftSignalPin);
  out += (val > 100) ? "1" : "0";
  out += "|";
  
  val = analogRead(highBeamPin);
  out += (val > 100) ? "1" : "0";
  
  // Prepare string to send
  out += "~";
  int length = out.length();
  char charBuff[length+1];
  out.toCharArray(charBuff, length+1);
  
  meetAndroid.send(charBuff);
  
  delay(500);
}
