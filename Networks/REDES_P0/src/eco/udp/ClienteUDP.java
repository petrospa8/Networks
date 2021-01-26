/**
 * @author luis.rodriguez.soutullo
 */
package eco.udp;

import java.net.*;

/** Ejemplo que implementa un cliente de eco usando UDP. */

public class ClienteUDP {
    
    private static DatagramSocket sDatagram;
    
    public static void main(String argv[]) {
        if (argv.length != 3) {
            System.err.println("Formato: ClienteUDP <maquina> <puerto> <mensaje>");
            System.exit(-1);
        }
        
        sDatagram = null;
        
        try {
            // Creamos el socket no orientado a conexión
            // (en cualquier puerto libre)
            sDatagram = new DatagramSocket();
            
            // Establecemos un timeout de 30 segs
            sDatagram.setSoTimeout(30000);
            
            // Obtenemos la direción IP del servidor
            // (recibida en el primer argumento por linea de comandos)
            InetAddress dirServidor = InetAddress.getByName(argv[0]);
            
            // Obtenemos el puerto del servidor
            // (recibido en el segundo argumento por linea de comandos)
            int puertoServidor = Integer.parseInt(argv[1]);
            
            // Obtenemos el mensaje
            // (tercer argumento de la linea de comandos)
            String mensaje = argv[2];
            
            // Preparamos el datagrama que vamos a enviar y lo enviamos
            DatagramPacket dgramEnv = new DatagramPacket(mensaje.getBytes(),
                    mensaje.getBytes().length, dirServidor, puertoServidor);
            
            // Enviamos el datagrama
            sDatagram.send(dgramEnv);
            System.out.println("CLIENTE: Enviando "
                    + new String(dgramEnv.getData()) + " a "
                    + dgramEnv.getAddress().toString() + ":"
                    + dgramEnv.getPort());
            
            // Preparamos el datagrama de recepción
            byte array[] = new byte[1024];
            DatagramPacket dgramRec = new DatagramPacket(array, array.length);
            
            // Recibimos el mensaje
            sDatagram.receive(dgramRec);
            System.out.println("CLIENTE: Recibido "
                    + new String(dgramRec.getData(), 0, dgramRec.getLength())
                    + " de " + dgramRec.getAddress().toString() + ":"
                    + dgramRec.getPort());
        }
        catch (SocketTimeoutException e) {
            System.err.println("30 segs sin recibir nada");
        }
        catch (java.io.IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        finally {
            // Cerramos el socket para liberar la conexión
            if (sDatagram != null)
                sDatagram.close();
        }
    }
}
