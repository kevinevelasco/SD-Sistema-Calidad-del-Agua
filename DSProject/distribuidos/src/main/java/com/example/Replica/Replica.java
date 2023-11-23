package com.example.Replica;

import java.io.IOException;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Replica {
    private String tipo;
    private String puerto;
    private ZContext context;
    private ZMQ.Socket requester;

    public Replica(String tipo) {
        this.tipo = tipo;
        switch (tipo) {
            case "TEMPERATURA":
                this.puerto = "6010";
                break;
            case "PH":
                this.puerto = "6020";
                break;
            case "OXIGENO":
                this.puerto = "6030";
                break;
        }
        this.context = new ZContext();
        this.requester = context.createSocket(SocketType.REQ);
        String direccion = "tcp://localhost:" + this.puerto;
        this.requester.connect(direccion);
    }

    public void suscribirmeSensor(ZMQ.Socket subscriber) {
        // Se suscribe al sensor que se suscribe el monitor
    }

    public void close() {
        requester.close();
        context.close();
    }

    public static void main(String[] args) {
        String tipo = args[0];

        Replica replica = new Replica(tipo);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // La replica le manda un mensaje a su monitor
                String request = "Heartbeat";
                replica.requester.send(request.getBytes(), 0);

                // Esperamos un segundo por la respuesta del monitor
                ZMQ.Poller poller = replica.context.createPoller(1);
                poller.register(replica.requester, ZMQ.Poller.POLLIN);

                // Se espera por una respuesta o TIMEOUT
                if (poller.poll(7000) == 0) {
                    System.out.println("No se recibió una respuesta");

                    // Close the existing socket and context
                    replica.close();

                    // Create a new instance of the replica
                    replica = new Replica(tipo);

                    // Additional logic if needed before sending the next request
                    runPowerShellCommand(tipo);
                } else {
                    byte[] reply = replica.requester.recv(0);
                    System.out.println("Recibió una respuesta" + new String(reply, ZMQ.CHARSET));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the socket and context when done
            replica.close();
        }
    }

    private static void runPowerShellCommand(String tipo) {
        try {

            // c:; cd 'c:\Users\estudiante\Desktop\DSProject\distribuidos'; & 'C:\Program
            // Files\Java\jdk1.8.0_202\bin\java.exe' '-cp'
            // 'C:\Users\ESTUDI~1\AppData\Local\Temp\2\cp_cfsa52b0dkw9v1rvaqdfzbfc2.jar'
            // 'com.example.Monitores.Monitores'
            String argumento = tipo + "#REPLICA";

            String comando = "c:; cd 'c:\\Users\\estudiante\\Desktop\\DSProject\\distribuidos'; java '-cp'  'C:\\Users\\ESTUDI~1\\AppData\\Local\\Temp\\2\\cp_cfsa52b0dkw9v1rvaqdfzbfc2.jar' 'com.example.Monitores.Monitores' "
                    + argumento;

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
}
