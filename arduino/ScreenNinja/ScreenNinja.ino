#include <MeetAndroid.h>

// Bluetooth MAC: 00:06:66:08:E7:60
MeetAndroid meetAndroid;

const int tempSignalPin  = 0;
const int rightSignalPin = 4;  // 0v off; ~10v on
const int leftSignalPin  = 3;  // 0v off; ~10v on
const int highBeamPin    = 2; // 0v off; 12v on
const int neutralPin     = 6; // (NEGATIVE TRIGGER)
const int lowFuelPin     = 10; // ~4.5-5v low fuel; ?v full (NEGATIVE TRIGGER?!?!?!) -- 579
const int tachSignalPin  = 5; // 0v off; ~6.5v running
const int fuelInjPin     = 9; // (NEGATIVE TRIGGER)
const int oilPin         = 7; // (NEGATIVE TRIGGER)
const int waterTempPin   = 8; // (NEGATIVE TRIGGER)

void setup() {
  Serial.begin(9600); // USB
//  Serial.begin(115200); // BLUETOOTH
  pinMode(tempSignalPin,  INPUT);
  pinMode(rightSignalPin, INPUT);
  pinMode(leftSignalPin,  INPUT);
  pinMode(highBeamPin,    INPUT);
  pinMode(neutralPin,     INPUT);
  pinMode(lowFuelPin,     INPUT);
  pinMode(tachSignalPin,  INPUT);
  pinMode(fuelInjPin,     INPUT);
  pinMode(oilPin,         INPUT);
  pinMode(waterTempPin,   INPUT);
}

void loop() {
  int val = analogRead(tempSignalPin);
  float voltage = val * 5.0; // our arduino is 5v, note that this is powered from the 3.3v rail tho
  voltage /= 1024.0;
  float temperatureC = (voltage - 0.5) * 100; // convert from 10mv/degree withh 500mv offset
  float temperatureF = (temperatureC * 9.0 / 5.0) + 32.0; // convert to Farenheight
  Serial.print(temperatureF); Serial.println(" degrees F");  
  
  val = analogRead(highBeamPin);
  if(val > 0) Serial.println("High Beams");
  val = analogRead(leftSignalPin);
  if(val > 0) Serial.println("Left Turn");
  val = analogRead(rightSignalPin);
  if(val > 0) Serial.println("Right Turn");
  val = analogRead(tachSignalPin);
  if(val > 0) Serial.println("Tach");
  val = analogRead(neutralPin);
Serial.print("Neutral "); Serial.println(val);
  val = analogRead(lowFuelPin);
Serial.print("Low Fuel "); Serial.println(val); 
  val = analogRead(fuelInjPin);
Serial.print("Fuel Injectors "); Serial.println(val); 
  val = analogRead(oilPin);
  if(val == 0) Serial.println("Oil");
  val = analogRead(waterTempPin);
  if(val == 0) Serial.println("Water");
  Serial.println("--------");
  delay(1000);

//
//  // Required for continued communication
//  meetAndroid.receive();
//
//  // Special start character
//  String out = "_";
//  
//  // Check for right turn signal
//  int val = analogRead(rightSignalPin);
//  out += (val > 100) ? "1" : "0";
//  out += "|";
//
//  // Check for left turn signal
//  val = analogRead(leftSignalPin);
//  out += (val > 100) ? "1" : "0";
//  out += "|";
//  
//  // Check for high beams on
//  val = analogRead(highBeamPin);
//  out += (val > 100) ? "1" : "0";
//  out += "|";
//  
//  // Check for neutral indicator on
//  val = analogRead(neutralPin);
//  out += (val == 100) ? "1" : "0";
//  out += "|";  
//
//  // Check for low fuel indicator on
//  val = analogRead(lowFuelPin);
//  out += (val == 100) ? "1" : "0";
//  out += "|";  
//  
//  // Get tachometer reading and convert to proper speed (TODO!!!!)
//  val = analogRead(tachSignalPin);
//  out += (val == 100) ? "1" : "0";
//  out += "|";  
//
//  // Check for the fuel injection indicator on
//  val = analogRead(fuelInjPin);
//  out += (val == 100) ? "1" : "0";
//  out += "|";  
//
//  // Check for the oil indicator on
//  val = analogRead(oilPin);
//  out += (val == 100) ? "1" : "0";
//  out += "|";  
//  
//  // Check for the water temperature indicator on
//  val = analogRead(waterTempPin);
//  out += (val == 100) ? "1" : "0";
//  out += "|";  
//  // Send outside temperature
//  out += (temperatureF);
//  out += "|";
//  
//  // Prepare string to send
//  // Add special end character (for error checking on Java side)
//  out += "~";
//  
//  // Now convert the string into a character buffer for transmission over Bluetooth
//  int length = out.length();
//  char charBuff[length+1];
//  out.toCharArray(charBuff, length+1);
//  
//  // Finally send the values to the Android app
//  meetAndroid.send(charBuff);
//
//  // Take a nap, you deserve it!
//  delay(500);
}
