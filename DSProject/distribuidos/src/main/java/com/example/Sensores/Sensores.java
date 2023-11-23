package com.example.Sensores;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.IntStream;

import com.example.Monitores.Rango;
import com.example.Monitores.Tipos;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import static com.example.Monitores.Tipos.*;

public class Sensores {

    private Tipos tipo;
    private Double tiempo;
    private Double medida;
    private ConfigFile configFile;

    public Sensores() {

    }

    public Sensores(Tipos tipo, Double tiempo) {
        this.tipo = tipo;
        this.tiempo = tiempo;
    }

    // Getters
    public Tipos getTipo() {
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
        configFile = new ConfigFile(direccionArchivo);
        Double[] probabilidades = new Double[3];

        // Leer el archivo
        if (configFile.leerArchivo()) {
            // Asignar las probabilidades
            /*
             * Probabilidad[0] Rango
             * Probabilidad[1] FueraRango
             * Probabilidad[2] Error
             */
            probabilidades[0] = configFile.getProbabilidadRango();
            probabilidades[1] = configFile.getProbabilidadFueraRango();
            probabilidades[2] = configFile.getProbabilidadError();

            // Se escoge un número al azar entre el 0 y 1
            Random r = new Random();
            Double aleatorio = 0 + (1 - 0) * r.nextDouble();
            double limite_inferior = 0, limite_superior = probabilidades[0];
            String nombres[] = new String[]{"RANGO", "FUERA", "ERROR"};
            System.out.println("Valor del aleatorio" + aleatorio);
            for (int posicion : IntStream.rangeClosed(0, probabilidades.length - 1).toArray()) {
                //System.out.println(posicion);
                System.out.println("El limite inferior: " + limite_inferior + " y el limite superior: " + limite_superior);
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
                Rango rango = new Rango();
                switch (this.tipo) {
                    case TEMPERATURA: {
                        rango = new Rango(68.0, 89.0, TEMPERATURA);
                        break;
                    }
                    case PH: {
                        rango = new Rango(6.0, 8.0, PH);
                        break;
                    }
                    case OXIGENO: {
                        rango = new Rango(2.0, 11.0, OXIGENO);
                        break;
                    }
                }
                valor = rango.getMin() + (rango.getMax() - rango.getMin()) * r.nextDouble();
                break;
            }
            // Los valores fuera corresponden a los valores que estan fuera del rango
            case "FUERA": {
                Rango rango = new Rango();
                int aleatorio2 = (int) (Math.random() * (1 - 0)) + 0;
                switch (this.tipo) {
                    // 0 por debajo del intervalo y 1 por encima del intervalo
                    case TEMPERATURA: {
                        if (aleatorio2 == 1) {
                            rango = new Rango(89.1, 115.0, TEMPERATURA);
                        } else {
                            rango = new Rango(0.0, 67.9, TEMPERATURA);
                        }
                        break;
                    }
                    case PH: {
                        if (aleatorio2 == 1) {
                            rango = new Rango(8.1, 14.0, PH);
                        } else {
                            rango = new Rango(0.0, 5.9, PH);
                        }
                        break;
                    }
                    case OXIGENO: {
                        if (aleatorio2 == 1) {
                            rango = new Rango(11.1, 15.0, OXIGENO);
                        } else {
                            rango = new Rango(0.0, 1.9, OXIGENO);
                        }
                        break;
                    }
                }
                valor = rango.getMin() + (rango.getMax() - rango.getMin()) * r.nextDouble();
                break;
            }
            // Los valores de error corresponden a medidas negativas
            case "ERROR": {
                Rango rango = new Rango();
                switch (this.tipo) {
                    case TEMPERATURA: {
                        rango = new Rango(0.0, 115.0, TEMPERATURA);
                        break;
                    }
                    case PH: {
                        rango = new Rango(0.0, 14.0, PH);
                        break;
                    }
                    case OXIGENO: {
                        rango = new Rango(0.0, 15.0, OXIGENO);
                        break;
                    }
                }
                valor = -1 * (0.1 + (rango.getMax() - rango.getMin()) * r.nextDouble());
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
         * El objeto de clase sensores para hacer la generación de los datos aleatorios
         * El objeto encargado de formatear fechas y tiempos
         */
        Sensores sensor;
        String mensaje;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Se verifica que se hayan ingresado los tres argumentos
        if (args.length < 3) {
            System.out.println("Debe ingresar tres argumentos para iniciar el programa\n");
            System.out.println("Para ejecutar el programa debe ingresar los siguientes argumentos: ");
            System.out.println("Arg[0] -> Tipo de sensor");
            System.out.println("Arg[1] -> Tiempo de envio (Segundos)");
            System.out.println("Arg[2] -> Dirección de archivo de configuración en comillas");
            System.exit(1);
        } else if ((args[0].equalsIgnoreCase("TEMPERATURA") || args[0].equalsIgnoreCase("PH") || args[0].equalsIgnoreCase("OXIGENO") && Double.parseDouble(args[1]) > 0 && !args[2].isEmpty())) {
            // Se instancia una clase Sensores
            Tipos tipo = Tipos.valueOf(args[0].toUpperCase());
            sensor = new Sensores(tipo, Double.parseDouble(args[1]));

            try (ZContext context = new ZContext()) {
                // Mientras el proceso se este ejecutando
                while (!Thread.currentThread().isInterrupted()) {

                    System.out.println("El tipo del sensor es "+tipo);

                    // El valor del sensor se envia al sistema publicador subscriptor
                    sensor.calcularMedida(args[2]);
                    // Socket que habla con el sistema
                    ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                    socket.connect("tcp://localhost:5555");

                    // Se guarda la hora de ejecución
                    LocalDateTime now = LocalDateTime.now();

                    // Se forma el mensaje a enviar
                    mensaje = sensor.getTipo().toString().toUpperCase() + "#" + sensor.getMedida() + "#" + dtf.format(now);

                    System.out.println("Mensaje a enviar: " + mensaje);

                    // Se envia la medida al sistema
                    socket.send(mensaje.getBytes(ZMQ.CHARSET));

                    // Se recibe la respuesta del sistema
                    byte[] respuesta = socket.recv(0);

                    // Se recibe la respuesta del sistema
                    System.out.println("Respuesta: " + new String(respuesta, ZMQ.CHARSET));


                    /*
                     * Forma como se captura el mensaje enviado de un proceso
                     *
                     * String[]contenido = mensaje.split("-");
                     *
                     * Arrays.asList(contenido).stream().forEach(System.out::println);
                     */

                    // Se coloca al sensor a dormir según el tiempo declarado en los argumentos
                    Thread.sleep(sensor.getTiempo().intValue() * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Recuerde:");
            System.out.println("Debe ingresar un tipo de sensor valido");
            System.out.println("Los tipos de sensor validos son: TEMPERATURA, PH y OXIGENO");
            System.out.println("El tiempo de envio debe ser mayor a 0");
            System.out.println("La dirección del archivo de configuración no puede estar vacia");
            System.exit(1);
        }

    }
}
