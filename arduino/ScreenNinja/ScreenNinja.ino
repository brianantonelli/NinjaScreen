#include <MeetAndroid.h>

const int rightSignalPin = 8;  // 0v off; ~10v on
const int leftSignalPin  = 9;  // 0v off; ~10v on
const int highBeamPin    = 10; // 0v off; 12v on
const int neutralPin     = 11; // 10v in gear; 0v in neutral
const int lowFuelPin     = 12; // ~4.5-5v low fuel; ?v full
const int tachSignalPin  = 13; // 0v off; ~6.5v running

void setup() {
  Serial.begin(9600); // USB
//  Serial.begin(57600); // BLUETOOTH
  pinMode(highBeamPin,    INPUT);
  pinMode(leftSignalPin,  INPUT);
  pinMode(rightSignalPin, INPUT);
  pinMode(neutralPin,     INPUT);
  pinMode(lowFuelPin,     INPUT);
  pinMode(tachSignalPin,  INPUT);
}

void loop() {
//  meetAndroid.receive();
//  meetAndroid.send(1);
  int val = analogRead(rightSignalPin);
  if(val > 100){
    Serial.println("RIGHT BLINKER IS ON!");
  }

  val = analogRead(leftSignalPin);
  if(val > 100){
    Serial.println("LEFT BLINKER IS ON!");
  }
  
  val = analogRead(highBeamPin);
  if(val > 100){
    Serial.println("HIGH BEAMS ARE ON!");
  }  

  delay(500);
}
