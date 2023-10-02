package com.example.Monitores;

import java.util.Arrays;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class Monitores {

    private String tipo;

    public Monitores(String tipo) {
        this.tipo = tipo;
    }

    public void suscribirmeSensor(ZMQ.Socket subscriber) {
        // Se hace que el subscridor se subscriba al tema específico
        /*
         * Ejemplo:
         * Si existe un sensor de temperatura
         * temperatura#valor-fecha
         * 
         * El monitor que se haya subscrito al contenido de temperatura le llegara
         * Solo los mensajes que tenga el formato descrito llegaran a este esubscritor.
         */
        subscriber.subscribe((tipo).getBytes());
    }

    public void recibirMedidas(ZMQ.Socket subscriber) {
        byte[] mensaje = subscriber.recv();
        // Convertir mensaje en datos atomicos
        String[] datos = new String(mensaje, ZMQ.CHARSET).split("#");

        // El dato del valor del parametro médido
        Double valor = Double.parseDouble(datos[1]);

        // El dato de la fecha en que se genero el valor
        String fecha = datos[2];

        System.out.println("[ Hora" + " : " + tipo + " ] -> " + fecha + ":" + valor);
        

    }

    public void validarMedidas() {
        
    }

    public static void main(String[] args) {
        String[] presets = { "temperatura", "ph", "oxigeno" };
        if (args.length != 1) {
            System.out.println("Forma de uso: Ingrese tipo de sensor{temperatura, ph, oxigeno}");
            System.exit(1);
        } else {
            String tipo = Arrays.stream(presets).filter(x -> x.equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if (tipo != null) {
                // Se instancia un objeto Monitor
                Monitores monitor = new Monitores(tipo);

                try (ZMQ.Context context = ZMQ.context(1);
                        ZMQ.Socket subscriber = context.socket(SocketType.SUB)) {

                    subscriber.connect("tcp://127.0.0.1:5554"); // Se conecta al sensor publicador

                    // Se suscribe al sensor al publicador
                    monitor.suscribirmeSensor(subscriber);

                    while (true) {
                        // Se recibe la medida enviada
                        monitor.recibirMedidas(subscriber);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else {
                System.out.println("El tipo de sensor ingresado no es reconocido");
                System.exit(1);
            }
        }

    }
}
