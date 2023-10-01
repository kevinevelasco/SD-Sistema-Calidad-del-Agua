package com.example.distribuidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.IntStream;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Sensores {

    private String tipo;
    private Double tiempo;
    private Double medida;

    public Sensores() {

    }

    public Sensores(String tipo, Double tiempo) {
        this.tipo = tipo;
        this.tiempo = tiempo;
    }

    // Getters
    public String getTipo() {
        return tipo;
    }

    public Double getTiempo() {
        return tiempo;
    }

    public Double getMedida() {
        return medida;
    }

    // Métodos de la clase
    public boolean calcularMedida(String direccionArchivo) {
        // Variables necesarias en el programa
        ConfigFile configFile = new ConfigFile(direccionArchivo);
        Double[] probabilidades = new Double[3];

        // Leer el archivo
        if (configFile.leerArchivo()) {
            // Asignar las probabilidades
            /*
             * Probabilidad[0] Rango
             * Probabilidad[1] FueraRango
             * Probabilidad[2] Error
             */
            probabilidades[0] = configFile.probabilidadRango;
            probabilidades[1] = configFile.probabilidadFueraRango;
            probabilidades[2] = configFile.probabilidadError;

            // Se escoge un número al azar entre el 0 y 1
            Random r = new Random();
            Double aleatorio = 0 + (1 - 0) * r.nextDouble();
            double limite_inferior = 0, limite_superior = probabilidades[0];
            String nombres[] = new String[] { "RANGO", "FUERA", "ERROR" };
            System.out.println("Valor del aleatorio" + aleatorio);
            for (int posicion : IntStream.rangeClosed(0, probabilidades.length - 1).toArray()) {
                System.out.println(posicion);
                System.out
                        .println("El limite inferior: " + limite_inferior + "y el limite superior: " + limite_superior);
                if (limite_inferior <= aleatorio && aleatorio < limite_superior && limite_superior > 0) {
                    this.medida = generarMedida(nombres[posicion]);
                }
                if (posicion + 1 < probabilidades.length) {
                    limite_inferior = limite_superior;
                    limite_superior += probabilidades[posicion + 1];
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private double generarMedida(String probabilidad) {
        Double valor = 0.0;
        Random r = new Random();
        System.out.println("Probabilidad: " + probabilidad);
        switch (probabilidad) {
            case "RANGO": {
                switch (this.tipo.toUpperCase()) {
                    case "TEMPERATURA": {
                        valor = 68.0 + (89.0 - 68.0) * r.nextDouble();
                        break;
                    }
                    case "PH": {
                        valor = 6.0 + (8.0 - 6.0) * r.nextDouble();
                        break;
                    }
                    case "OXIGENO": {
                        valor = 2.0 + (11.0 - 2.0) * r.nextDouble();
                        break;
                    }
                }
                break;
            }
            // Los valores fuera corresponden a los valores que estan fuera del rango
            case "FUERA": {
                int aleatorio2 = (int) (Math.random() * (1 - 0)) + 0;
                switch (this.tipo.toUpperCase()) {
                    // 0 por debajo del intervalo y 1 por encima del intervalo
                    case "TEMPERATURA": {
                        if (aleatorio2 == 1) {
                            valor = 89.1 + (115.0 - 89.1) * r.nextDouble();
                        } else {
                            valor = 0.0 + (67.9 - 0.0) * r.nextDouble();
                        }
                        break;
                    }
                    case "PH": {
                        if (aleatorio2 == 1) {
                            valor = 8.1 + (14.0 - 8.1) * r.nextDouble();
                        } else {
                            valor = 0.0 + (5.9 - 0.0) * r.nextDouble();
                        }
                        break;
                    }
                    case "OXIGENO": {
                        if (aleatorio2 == 1) {
                            valor = 11.1 + (15.0 - 11.1) * r.nextDouble();
                        } else {
                            valor = 0.0 + (1.9 - 0.0) * r.nextDouble();
                        }
                        break;
                    }
                }
                break;
            }
            // Los valores de error corresponden a medidas negativas
            case "ERROR": {
                switch (this.tipo.toUpperCase()) {
                    case "TEMPERATURA": {
                        valor = -1 * (0.1 + (115.0 - 0.0) * r.nextDouble());
                        break;
                    }
                    case "PH": {
                        valor = -1 * (0.1 + (14.0 - 0.0) * r.nextDouble());
                        break;
                    }
                    case "OXIGENO": {
                        valor = -1 * (0.1 + (115.0 - 0.0) * r.nextDouble());
                        break;
                    }
                }
                break;
            }
        }
        System.out.println("Valor " + valor);
        return valor;
    }

    public static void main(String[] args) {
        // Se leen los siguientes argumentos
        /*
         * Arg[0] -> Tipo de sensor
         * Arg[1] -> Tiempo de envio (Segundos)
         * Arg[2] -> Dirección de archivo de configuración
         */

        /*
         * Se definen las variables requeridas
         */

        Sensores sensor;
        String mensaje;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Se verifica que se hayan ingresado los tres argumentos
        if (args.length < 3) {
            System.out.println("Debe ingresar tres argumentos para iniciar el programa");
            System.exit(1);
        } else {
            // Se instancia una clase Sensores
            sensor = new Sensores(args[0], Double.parseDouble(args[1]));

            sensor.calcularMedida(args[2]);

            try (ZContext context = new ZContext()) {
                // Mientras el proceso se este ejecutando
                while (!Thread.currentThread().isInterrupted()) {
                    sensor.calcularMedida(args[2]);
                    // El valor del sensor se envia al sistema publicador subscriptor
                    sensor.calcularMedida(args[2]);
                    // Socket que habla con el sistema
                    ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                    socket.connect("tcp://localhost:5555");

                    // Se guarda la hora de ejecución
                    LocalDateTime now = LocalDateTime.now();

                    // Se forma el mensaje a enviar
                    mensaje = sensor.getTipo() + "-" + sensor.getMedida() + "-" + dtf.format(now);

                    // Se envia la medida al sistema
                    socket.send(mensaje.getBytes(ZMQ.CHARSET));

                    // Se recibe la respuesta del sistema
                    socket.recv(0);

                    /*
                     * Forma como se captura el mensaje enviado de un proceso
                     * 
                     * String[]contenido = mensaje.split("-");
                     * 
                     * Arrays.asList(contenido).stream().forEach(System.out::println);
                     */

                    // Se coloca al sensor a dormir
                    Thread.sleep(sensor.getTiempo().intValue() * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
