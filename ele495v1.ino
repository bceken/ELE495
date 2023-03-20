#include<SoftwareSerial.h>
SoftwareSerial bluetooth(8,9); //rx,tx

void setup() {
        // Seri portumuza baud rate veriyoruz.
        Serial.begin(9600);
        bluetooth.begin(9600);
    }

void loop() {
        // Serial1 erişilebilir durumdaysa true değer alır ve aşağıdaki kodları çalıştırır.
      serialDetector();  
    }

void sendData(int score){
      //score= veri skorumuz toplam 
    }
    
void serialDetector(){
            while(bluetooth.available()) { 
            char recievedData=bluetooth.read();           

            //Gelen değeri serial portundan ekrana yazıyoruz.
            Serial.print(recievedData);
            bluetooth.write(recievedData);

           delay(300);//0,3s

        }
  }
        
