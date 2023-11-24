# Sistema-de-Calidad-del-Agua

## Instrucciones de ejecución 
Se debe ejecutar en el editor de código Visual Studio Code con la versión de Java 1.8 

1. Ejecutar sensor. Argumentos: (tipo, intervalo en segundos, dirección del archivo)
```bash
  & 'C:\Program Files\Java\jdk1.8.0_202\bin\java.exe' '-cp' 'C:\Users\ESTUDI~1\AppData\Local\Temp\2\cp_6rtutx1g6ashbljwm83jlirzz.jar' 'com.example.Sensores.Sensores' temperatura 2 'C:\Users\estudiante\Desktop\DSProject\distribuidos\src\main\resources\config.txt'
```
2. Ejecutar sistemaPS, no necesita argumentos
```bash
& 'C:\Program Files\Java\jdk1.8.0_202\bin\java.exe' '-cp' 'C:\Users\ESTUDI~1\AppData\Local\Temp\2\cp_6rtutx1g6ashbljwm83jlirzz1.jar' 'com.example.Sistema.SistemaPS'
```
3. Ejecutar monitores. Argumentos: (tipo de sensor)
```bash
& 'C:\Program Files\Java\jdk1.8.0_202\bin\java.exe' '-cp' 'C:\Users\ESTUDI~1\AppData\Local\Temp\2\cp_6rtutx1g6ashbljwm83jlirzz2.jar' 'com.example.Monitores.Monitores' oxigeno
```
**Se debe cambiar la dirección de donde está ubicado el proyecto y el .jar de ZeroMQ en el archivo monitor [línea 53]**
```bash
Cambiar esto
c:; cd 'C:\\Users\\estudiante\\Desktop\\DSProject\\distribuidos'; java '-cp'  'C:\\Users\\ESTUDI~1\\AppData\\Local\\Temp\\2\\cp_6rtutx1g6ashbljwm83jlirzz.jar' 'com.example.Replica.Replica'
```
4. Ejecutar sistema de calidad
```bash
& 'C:\Program Files\Java\jdk1.8.0_202\bin\java.exe' '-cp' 'C:\Users\ESTUDI~1\AppData\Local\Temp\2\cp_6rtutx1g6ashbljwm83jlirzz3.jar' 'com.example.SistemaCalidad.SistemaCalidad' 
```
5. Cambiar la dirección de donde está ubicado el proyecto y el .jar de ZeroMQ en el archivo réplica [línea 94]
