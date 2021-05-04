#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

ESP8266WebServer server(80);            // create webserver object to listen for HTTP requests on port 80

const char* ssid     = "AN5506-04-FA_e60b8";          // wifi credentials
const char* password = "72819f47";

String      htmlPage;                   // webpage text to be sent out by server when main page is accessed

void handleManual(){

}

void handleAutomatic(){

}

void handleNotFound() {
  server.send(404, "text/plain", "404: Not found"); 
}

void buildHtmlPage() {
  htmlPage = "<!DOCTYPE html>";
  htmlPage += "<html>";
  htmlPage += "<head>";                                        // header section
  htmlPage += "<title>WiFi Air Purifier</title>";             // title for browser window
  htmlPage += "</head>";
  
  htmlPage += "<BODY bgcolor='#E0E0D0'>";                      // body section, set background color

  htmlPage += "<br>Hello World!";
  // show led status and action buttons
  String ledState = ((digitalRead(4)) ? "on" : "off");
  htmlPage += "<br>LED: " + ledState;
  htmlPage += "<form action=\"/AUTO\" method=\"POST\"><input type=\"submit\" value=\"LED On\"></form>";
  htmlPage += "<form action=\"/MANUAL\" method=\"POST\"><input type=\"submit\" value=\"LED Off\"></form>";
  htmlPage += "<form action=\"/LEDTOGGLE\" method=\"POST\"><input type=\"submit\" value=\"Toggle LED\"></form>";

  htmlPage += "</body>";
  htmlPage += "</html>";
}

// send main web page when ip+"/" is accessed
void handleRoot() {
  buildHtmlPage();
  server.send(200, "text/html", htmlPage);
}

void setup() {
  Serial.begin(9600);

  // Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to WiFi...");
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();                    // disconnect if previously connected
  delay(100);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.println("WiFi connected.");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());

  // functions to call when client requests are received
  server.on("/", handleRoot);     
  /*server.on("/LEDTOGGLE", handleLEDToggle);  
  server.on("/LEDON", handleLEDOn);
  server.on("/LEDOFF", handleLEDOff);
  server.onNotFound(handleNotFound);*/       

  server.begin();                           // start web server
  Serial.println("HTTP server started");
}

void loop(void) {
  server.handleClient();                    // listen for HTTP requests from clients
}



