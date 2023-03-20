const int in1 = 8;
const int in2 = 9;
const int in3 = 10;
const int in4 = 11;
const int en1 = 13;
const int en2 = 12;

const int s2 = 7; // color sensore pin s2 to arduino pin7
const int s3 = 6; // color sensor pin s3 to arduino pin 8
const int outpin = 4; // color sensor out to arduino pin 4

int redStr;
int greenStr;
int blueStr;

unsigned int pulseWidth;
int location = 1;
int direc = 1;


void setup() {
  // put your setup code here, to run once:

  Serial.begin(9600); // turn on serial board

  pinMode(s2, OUTPUT);
  pinMode(s3, OUTPUT);
  pinMode(outpin, INPUT);

  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);
  pinMode(en1, OUTPUT);
  pinMode(en2, OUTPUT);

}

void loop() {
  // put your main code here, to run repeatedly:
  
  // Kırmızı okuyarak başlayılım. s2 ve s3 low olmalı.
  digitalWrite(s2, LOW);
  digitalWrite(s3, LOW);

  pulseWidth = pulseIn(outpin, LOW);

  redStr = (pulseWidth/400) -1;
  redStr = 255 - redStr;

  // Yeşil okuyarak başlayılım. s2 ve s3 high olmalı.
  digitalWrite(s2, HIGH);
  digitalWrite(s3, HIGH);

  pulseWidth = pulseIn(outpin, LOW);

  greenStr = (pulseWidth/400) -1;
  greenStr = 255 - greenStr;

  // Mavi okuyarak başlayılım. s2 low ve s3 high olmalı.
  digitalWrite(s2, LOW);
  digitalWrite(s3, HIGH);

  pulseWidth = pulseIn(outpin, LOW);

  blueStr = (pulseWidth/400) -1;
  blueStr = 255 - blueStr;

  Serial.print(redStr);
  Serial.print(" , ");
  Serial.print(greenStr);
  Serial.print(" , ");  
  Serial.println(blueStr);
  delay(40);

 /* if(!(redStr < 253 && greenStr < 253)) // BU kısımdan dolayı hata olabilir. !!!!!!!!
  {
    if(greenStr > redStr)// Yeşil olma durumu
    {
      digitalWrite(in1, HIGH); // sağ pil yönü
      digitalWrite(in2, HIGH);  //  sağ pil ters yönü
      digitalWrite(in3, HIGH);   // Sol pilin ters yönü
      digitalWrite(in4, HIGH);  //pil sol pil yönü      
    }
    else // Kırmızı olma durumu. Eksi puan almamak için harekete devam ediyor.
    {
      if(direc)
      {
        moveright();
        location++;
      }
      else
      {
        moveleft();
        location--;
      }
    }
  }
  else // renksiz olma durumu. Bu durumda yeşili bulma için harekete devam ediyor.
  {
    if(direc)
      {
        moveright();
        location++;
      }
      else
      {
        moveleft();
        location--;
      }
    
  }
  */
  if(greenStr > redStr)// Yeşil olma durumu
    {
      analogWrite(en1, 255);
      analogWrite(en2, 255);
      digitalWrite(in1, HIGH); // sağ pil yönü
      digitalWrite(in2, HIGH);  //  sağ pil ters yönü
      digitalWrite(in3, HIGH);   // Sol pilin ters yönü
      digitalWrite(in4, HIGH);  //pil sol pil yönü      
    }
    else // Kırmızı olma durumu. Eksi puan almamak için harekete devam ediyor.
    {
      if(direc)
      {
        moveright();
        location++;
      }
      else
      {
        moveleft();
        location--;
      }
    }
  if(location == 5)
  {
    direc = 0;
  }
  if(location == 1)
  {
    direc = 1;
  }
  
}

void moveright()
{
  analogWrite(en1, 255);
  analogWrite(en2, 255);
  digitalWrite(in1, LOW); // yukarı sağ
  digitalWrite(in2, HIGH);  //  yukarı sola
  digitalWrite(in3, LOW);   // aşağdaki sağ
  digitalWrite(in4, HIGH);  //aşağıda ki sola
  delay(140);
}
void moveleft()
{
  analogWrite(en1, 255);
  analogWrite(en2, 255);
  digitalWrite(in1, HIGH); // yukarı sağ
  digitalWrite(in2, LOW);  //  yukarı sola
  digitalWrite(in3, HIGH);   // aşağdaki sağ
  digitalWrite(in4, LOW);  //aşağıda ki sola
  delay(140);
}
