#include <MeetAndroid.h>

const int referenceVolts = 5;
const int R1 = 3300;
const int R2 = 2200;
const int resistorFactor = 255 / (R2/(R1 + R2)); // 254.6
const int highBeamPin = 10;
const int leftSignalPin = 9;
const int rightSignalPin = 8;

void setup() {
  Serial.begin(9600); // USB
//  Serial.begin(57600); // BLUETOOTH
  pinMode(highBeamPin, INPUT);
  pinMode(leftSignalPin, INPUT);
  pinMode(rightSignalPin, INPUT);
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
  Serial.println(val);
  if(val > 100){
    Serial.println("HIGH BEAMS ARE ON!");
  }  
  

//  Serial.println(val);
  //float volts = (val / resistorFactor) * referenceVolts;
  delay(500);
  //Serial.println(volts);
}
