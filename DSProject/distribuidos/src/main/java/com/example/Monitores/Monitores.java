package com.example.Monitores;

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

    public Monitores(Tipos tipo) {
        this.tipo = tipo;
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
    }

    public void recibirMedidas(ZMQ.Socket subscriber) {
        byte[] mensaje = subscriber.recv();
        // Convertir mensaje en datos atomicos
        String[] datos = new String(mensaje, ZMQ.CHARSET).split("#");

        // El dato del valor del parametro médido
        Double valor = Double.parseDouble(datos[1]);

        // El dato de la fecha en que se genero el valor
        String fecha = datos[2];

        if (validarMedidas(valor)) {
            guardarEnBD(valor, fecha);
            System.out.println("[ Hora" + " : " + this.tipo + " ] -> " + fecha + ":" + valor);
        } else {
            generarAlarma(valor, fecha);
        }
    }

    private void generarAlarma(Double valor, String fecha) {
        //TODO para la siguiente entrega
        System.out.println("\nAlarma generada: " + valor + " " + fecha);
    }

    private void guardarEnBD(Double valor, String fecha) {
        System.out.println("Guardando en Base de Datos...");
        MongoClient client = MongoClients.create("mongodb+srv://kevinevelasco:12345@distribuidos.34vgbvl.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = client.getDatabase("Sistema_Calidad_del_Agua");
        MongoCollection<Document> collection = db.getCollection("sensores");
        Document doc = new Document()
                .append("tipo", this.tipo)
                .append("valor", valor)
                .append("fecha", fecha);
        collection.insertOne(doc);
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
            String tipo = Arrays.stream(presets).filter(x -> x.equalsIgnoreCase(args[0])).findFirst().orElse(null);
            Tipos tipoSensor = Tipos.valueOf(tipo.toUpperCase());
            // Se instancia un objeto Monitor
            Monitores monitor = new Monitores(tipoSensor);

            try (ZMQ.Context context = ZMQ.context(1);
                 ZMQ.Socket subscriber = context.socket(SocketType.SUB)) {

                subscriber.connect("tcp://127.0.0.1:5554"); // Se conecta al sensor publicador

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

