#include <MeetAndroid.h>

// Bluetooth MAC: 00:06:66:08:E7:60
MeetAndroid meetAndroid;

const int rightSignalPin = 8;  // 0v off; ~10v on
const int leftSignalPin  = 9;  // 0v off; ~10v on
const int highBeamPin    = 10; // 0v off; 12v on
const int neutralPin     = 11; // 10v in gear; 0v in neutral (NEGATIVE TRIGGER)
const int lowFuelPin     = 12; // ~4.5-5v low fuel; ?v full (NEGATIVE TRIGGER)
const int tachSignalPin  = 13; // 0v off; ~6.5v running
const int fuelInjPin     = 14; // (NEGATIVE TRIGGER)
const int oilPin         = 15; // (NEGATIVE TRIGGER)
const int waterTempPin   = 16; // (NEGATIVE TRIGGER)

void setup() {
//  Serial.begin(9600); // USB
  Serial.begin(115200); // BLUETOOTH
  pinMode(highBeamPin,    INPUT);
  pinMode(leftSignalPin,  INPUT);
  pinMode(rightSignalPin, INPUT);
  pinMode(neutralPin,     INPUT);
  pinMode(lowFuelPin,     INPUT);
  pinMode(tachSignalPin,  INPUT);
  pinMode(fuelInjPin,     INPUT);
  pinMode(oilPin,         INPUT);
  pinMode(waterTempPin,   INPUT);
}

void loop() {
  // Required for continued communication
  meetAndroid.receive();

  
  // Special start character
  String out = "_";
  
  // Check for right turn signal
  int val = analogRead(rightSignalPin);
  out += (val > 100) ? "1" : "0";
  out += "|";

  // Check for left turn signal
  val = analogRead(leftSignalPin);
  out += (val > 100) ? "1" : "0";
  out += "|";
  
  // Check for high beams on
  val = analogRead(highBeamPin);
  out += (val > 100) ? "1" : "0";
  out += "|";
  
  // Check for neutral indicator on
  val = analogRead(neutralPin);
  out += (val > 100) ? "1" : "0";
  out += "|";  

  // Check for low fuel indicator on
  val = analogRead(lowFuelPin);
  out += (val > 100) ? "1" : "0";
  out += "|";  
  
  // Get tachometer reading and convert to proper speed (TODO!!!!)
  val = analogRead(tachSignalPin);
  out += (val > 100) ? "1" : "0";
  out += "|";  

  // Check for the fuel injection indicator on
  val = analogRead(fuelInjPin);
  out += (val > 100) ? "1" : "0";
  out += "|";  

  // Check for the oil indicator on
  val = analogRead(oilPin);
  out += (val > 100) ? "1" : "0";
  out += "|";  
  
  // Check for the water temperature indicator on
  val = analogRead(waterTempPin);
  out += (val > 100) ? "1" : "0";
  out += "|";  
  
  // Prepare string to send
  // Add special end character (for error checking on Java side)
  out += "~";
  
  // Now convert the string into a character buffer for transmission over Bluetooth
  int length = out.length();
  char charBuff[length+1];
  out.toCharArray(charBuff, length+1);
  
  // Finally send the values to the Android app
  meetAndroid.send(charBuff);
  
  // Take a nap, you deserve it!
  delay(500);
}
