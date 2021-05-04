#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WebSocketsServer.h>
#include <SoftwareSerial.h>
#include <PMS.h>
#include "MQ135.h"

// WIFI DETAILS ----------------------------------------

const char* ssid     = "AN5506-04-FA_e60b8";          
const char* password = "72819f47";

// PINOUTS ---------------------------------------------
#define PMS5003_RX          15  // D8
#define PMS5003_TX          13  // D7
#define MQ135_PIN           A0
#define BUILTIN_LED         2   // D4
#define RELAY_PIN           4   // D2
#define SWITCH_PIN          12   // D6

SoftwareSerial softwareSerial(PMS5003_TX, PMS5003_RX);
PMS pms(softwareSerial);
MQ135 mq135(MQ135_PIN);

ESP8266WebServer server;
WebSocketsServer webSocket = WebSocketsServer(81);

PMS::DATA data;

char webpage[] PROGMEM = R"=====(
<html>
<head>
  <title>WiFi Air Purifier</title>
  <script>
    var Socket;
    function init(){
      Socket = new WebSocket('ws://' + window.location.hostname + ':81/');
      Socket.onmessage = function(event){
        var data = JSON.parse(event.data);
        console.log(data);
      }
    }
  </script>
</head>
<body onload="javascript:init()">
  <h4>IoT Air Purifier Server</h4>
</body>
</html>
)=====";

void webSocketEvent(uint8_t num, WStype_t type, uint8_t * payload, size_t length){
  String payload_str = String((char*) payload);

  if(type == WStype_TEXT){
    // if(payload[0] == '#'){
    //   uint16_t brightness = (uint16_t) strtol((const char *) &payload[1], NULL, 10);
    //   brightness = 1024 - brightness;
    //   Serial.print("command= ");
    //   Serial.println(brightness);
    // }

    if(payload_str == "LEDON")digitalWrite(LED_BUILTIN, LOW);
    else if(payload_str == "LEDOFF")digitalWrite(LED_BUILTIN, HIGH);
    else if(payload_str == "AP_ON")digitalWrite(RELAY_PIN, HIGH);
    else if(payload_str == "AP_OFF")digitalWrite(RELAY_PIN, LOW);
  }
}


void setup() {
  Serial.begin(9600);
  softwareSerial.begin(9600);

  pinMode(BUILTIN_LED, OUTPUT);
  pinMode(RELAY_PIN, OUTPUT);
  pinMode(SWITCH_PIN, INPUT);

  digitalWrite(RELAY_PIN, LOW);
  digitalWrite(BUILTIN_LED, LOW);

  Serial.print("Connecting to WiFi!.");
  WiFi.disconnect();
  WiFi.mode(WIFI_STA);
  delay(500);
  WiFi.begin(ssid, password);
  
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());

  server.on("/",[](){
    server.send_P(200, "text/html", webpage);  
  });     

  server.begin();
  webSocket.begin();
  webSocket.onEvent(webSocketEvent);

  
  digitalWrite(BUILTIN_LED, HIGH);
}

void loop() {
  server.handleClient();
  webSocket.loop();
  Serial.println(WiFi.localIP());
  
  if(pms.read(data)){
    String json = "{\"airquality\":";
    json += data.PM_AE_UG_2_5;
    json += ",\"co2\":";
    json += mq135.getPPM();
    json += "}";
    //Serial.println(json);
    webSocket.broadcastTXT(json.c_str(), json.length());
  }

  else{
    String json = "{\"airquality\":";
    json += 99;
    json += ",\"co2\":";
    json += mq135.getPPM();
    json += "}";
    //Serial.println(json);
    webSocket.broadcastTXT(json.c_str(), json.length());
  }
  
}