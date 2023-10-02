package com.example.Sistema;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class SistemaPS {


  public SistemaPS(){

  }

  public void enviarDatos (ZMQ.Socket socket, byte[] datos){
    socket.send(datos,0);
  }

  public byte[] recibirDatos(ZMQ.Socket socket){
    return socket.recv(0);
  }

  public static void main(String[] args) throws Exception {
    try (ZContext context = new ZContext()) {

      // Se crea un objeto de clase SistemaPS
      SistemaPS sistemaPS = new SistemaPS();

      // Se crea un socket para hablar con los sensores
      ZMQ.Socket socket = context.createSocket(SocketType.REP);
      
      // Se crea un socket para publicar los datos a los subscriptores
      ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
      
      // Se asgina una dirección del publicador
      publisher.bind("tcp://127.0.0.1:5554");

      // La dirección asociada al Sistema para los sensores
      socket.bind("tcp://localhost:5555");

      while (!Thread.currentThread().isInterrupted()) {


        // Le llega un mensaje al Sistema de uno de los sensores
        byte[] request = sistemaPS.recibirDatos(socket);
        
        // El mensaje se envia a todos los subscriptores del sistema
        sistemaPS.enviarDatos(publisher, request);

        // Se almacena el mensaje de los sensores
        String value = new String(request, ZMQ.CHARSET);

        // Imprime por pantalla el mensaje
        System.out.println(
            "Received " + ": [" + value + "]");

        // Devuelve una respuesta al sensor
        String response = "Recibido";
        sistemaPS.enviarDatos(socket, response.getBytes(ZMQ.CHARSET));

      }
    }
  }
}
