#include <Arduino.h>
#include <SoftwareSerial.h>
#include <PMS.h>

// PINOUTS --------------------------------------
#define PMS5003_RX          15  //D8
#define PMS5003_TX          13  //D7

SoftwareSerial softwareSerial(PMS5003_TX, PMS5003_RX);
PMS pms(softwareSerial);
PMS::DATA data;

void setup()
{
  Serial.begin(9600);   
  softwareSerial.begin(9600);  
}

void loop()
{
  if (pms.read(data))
  {
    Serial.print("PM 1.0 (ug/m3): ");
    Serial.println(data.PM_AE_UG_1_0);

    Serial.print("PM 2.5 (ug/m3): ");
    Serial.println(data.PM_AE_UG_2_5);

    Serial.print("PM 10.0 (ug/m3): ");
    Serial.println(data.PM_AE_UG_10_0);

    Serial.println();
  }

  // Do other stuff...
}