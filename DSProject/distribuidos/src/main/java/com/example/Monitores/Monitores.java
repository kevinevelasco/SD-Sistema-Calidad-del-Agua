package com.example.Monitores;

import java.io.IOException;
import java.util.Arrays;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class Monitores {

    private Tipos tipo;
    private String puerto;


    public Monitores(Tipos tipo) {
        this.tipo = tipo;
        switch (tipo) {
            case TEMPERATURA: {
                this.puerto = "6010";
                break;
            }
            case PH: {
                this.puerto = "6020";
                break;
            }
            case OXIGENO: {
                this.puerto = "6030";
                break;
            }
        }
    }

    private static void runPowerShellCommand(String tipo) {
        try {

            // c:; cd 'c:\Users\estudiante\Desktop\DSProject\distribuidos'; & 'C:\Program
            // Files\Java\jdk1.8.0_202\bin\java.exe' '-cp'
            // 'C:\Users\ESTUDI~1\AppData\Local\Temp\2\cp_cfsa52b0dkw9v1rvaqdfzbfc2.jar'
            // 'com.example.Monitores.Monitores'
            String comando = "c:; cd 'c:\\Users\\estudiante\\Desktop\\DSProject\\distribuidos'; java '-cp'  'C:\\Users\\ESTUDI~1\\AppData\\Local\\Temp\\2\\cp_cfsa52b0dkw9v1rvaqdfzbfc2.jar' 'com.example.Replica.Replica' "
                    + tipo;

            // Command to run PowerShell command with pause at the end
            String[] commandToRun = { "cmd.exe", "/c", "start", "cmd.exe", "/k", "powershell.exe", "-Command",
                    comando };

            // Use ProcessBuilder to start the command
            ProcessBuilder processBuilder = new ProcessBuilder(commandToRun);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Print the exit code
            System.out.println("PowerShell command executed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void suscribirmeSensor(ZMQ.Socket subscriber) {
        // Se hace que el suscriptor se subscriba al tema específico
        /*
         * Ejemplo:
         * Si existe un sensor de temperatura
         * temperatura#valor-fecha
         *
         * El monitor que se haya subscrito al contenido de temperatura le llegara
         * Solo los mensajes que tenga el formato descrito llegaran a este subscritor.
         */
        subscriber.subscribe((this.tipo + "#").getBytes());
        System.out.println("Me suscribi al sensor");
    }

    public void recibirMedidas(ZMQ.Socket subscriber) {

        // Crear código para replica
        

        System.out.println("Recibo medidas");

        byte[] mensaje = subscriber.recv();
        // Convertir mensaje en datos atomicos
        String[] datos = new String(mensaje, ZMQ.CHARSET).split("#");

        // El dato del valor del parametro médido
        Double valor = Double.parseDouble(datos[1]);

        // El dato de la fecha en que se genero el valor
        String fecha = datos[2];


        if (validarMedidas(valor)) {
            //guardarEnBD(valor, fecha);
            System.out.println("[ Hora" + " : " + this.tipo + " ] -> " + fecha + ":" + valor);
        } else {
            generarAlarma(valor, fecha);
        }

    }

    private void generarAlarma(Double valor, String fecha) {
        //TODO para la siguiente entrega
        System.out.println("\nAlarma generada: " + valor + " " + fecha);
    }


    public boolean validarMedidas(Double valor) {
        Validador validador = new Validador();
        return (validador.validar(valor, this.tipo));
    }

    public static void main(String[] args) {
        String[] presets = {"temperatura", "ph", "oxigeno"};
        if (args.length != 1) {
            System.out.println("Forma de uso: Ingrese tipo de sensor{temperatura, ph, oxigeno}");
            System.exit(1);
        } else if (args[0].equalsIgnoreCase("temperatura") || args[0].equalsIgnoreCase("ph") || args[0].equalsIgnoreCase("oxigeno")) {
            String[] split = args[0].split("#");
            String tipo = Arrays.stream(presets).filter(x -> x.equalsIgnoreCase(split[0])).findFirst().orElse(null);
            Tipos tipoSensor = Tipos.valueOf(tipo.toUpperCase());
            // Se instancia un objeto Monitor
            Monitores monitor = new Monitores(tipoSensor);

            try (ZMQ.Context context = ZMQ.context(1);
                 ZMQ.Socket subscriber = context.socket(SocketType.SUB)) {

                subscriber.connect("tcp://127.0.0.1:5554"); // Se conecta al sensor publicador

                if(!args[0].contains("REPLICA")){
                    runPowerShellCommand(monitor.tipo.toString());
                }

                // Se suscribe al sensor al publicador
                monitor.suscribirmeSensor(subscriber);
                System.out.println("Monitor " + tipo + " conectado al sistema");
                while (true) {
                    System.out.println("Esperando medidas...");
                    // Se recibe la medida enviada
                    monitor.recibirMedidas(subscriber);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El tipo de sensor ingresado no es reconocido");
            System.exit(1);
        }
    }
}

