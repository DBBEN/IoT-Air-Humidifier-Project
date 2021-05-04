#include <Arduino.h>
#include "MQ135.h"
 
#define MQ135_PIN              A0
#define READING_SAMPLES        10
#define READING_INTERVAL       200      // Milliseconds
#define READING_SETTLE_TIME    10       // Seconds
#define READING_CALIB_VAL      230

MQ135 mq135(MQ135_PIN);

unsigned long previousTime = 0;
int CO2ppm;

boolean isTime(unsigned long interval){
   unsigned long now = millis();
   if(now - previousTime > interval){
      previousTime = now;
      return 1; 
   }

   else{
      return 0;
   }
}

int CO2_getPPM(uint8_t n, int interval, uint8_t pin){
    int ADCnow[10], CO2comp; 
    int ADCsum = 0;

    for(uint8_t x = 0; x < n; x++){
         ADCnow[x] = analogRead(pin);
         delay(interval);
     }

     for(uint8_t x = 0; x < n; x++){
         ADCsum += ADCnow[x];
     }

    CO2comp = (ADCsum / n) - READING_CALIB_VAL;
    return map(CO2comp, 0, 1024, 400, 5000);
}

 void setup(){
     Serial.begin(9600);
     pinMode(MQ135_PIN, INPUT);

     //while(!isTime(READING_SETTLE_TIME * 1000));
     Serial.println("--- Sensor Ready ---");
 }

 void loop(){
     CO2ppm = mq135.getPPM();//CO2_getPPM(READING_SAMPLES, READING_INTERVAL, MQ135_PIN);
     Serial.println(CO2ppm);
     delay(1000);
 }