package com.example.Sistema;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class SistemaPS {
      public static void main(String[] args) throws Exception
  {
    try (ZContext context = new ZContext()) {
      //  Se crea un socket para hablar con los sensores
      ZMQ.Socket socket = context.createSocket(SocketType.REP);
      //  La dirección asociada al Sistema
      socket.bind("tcp://*:5555");

      while (!Thread.currentThread().isInterrupted()) {
        // Le llega un mensaje al Sistema
        byte[] reply = socket.recv(0);
        String value = new String(reply, ZMQ.CHARSET);

        // Imprime por pantalla el mensaje
        System.out.println(
          "Received " + ": [" + value + "]"
        );

        // Devuelve una respuesta
        String response = "world";
        socket.send(response.getBytes(ZMQ.CHARSET), 0);

        
        Thread.sleep(1000); //  Do some 'work'
      }
    }
  }
}
