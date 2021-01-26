/**
 * @author luis.rodriguez.soutullo
 */
package eco.udp;

import java.net.*;

/** Ejemplo que implementa un servidor de eco usando UDP. */

public class ServidorUDP {
    
    private static DatagramSocket ds;
    
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Formato: ServidorUDP <puerto>");
            System.exit(-1);
        }
        
        try {
            // Creamos el socket del servidor
            ds = new DatagramSocket(5000);
            
            // Establecemos un timeout de 30 segs
            ds.setSoTimeout(30000);
            
            byte[] paquete = new byte[1024];
            
            while (true) {
                // Preparamos un datagrama para recepción
                DatagramPacket rdp = new DatagramPacket(paquete, paquete.length);
                
                // Recibimos el mensaje
                ds.receive(rdp);
                
                // Preparamos el datagrama que vamos a enviar
                DatagramPacket sdp = new DatagramPacket(rdp.getData(), rdp.getLength(), 
                        rdp.getAddress(), rdp.getPort());
                
                // Enviamos el mensaje
                ds.send(sdp);
            }
        }
        catch (SocketTimeoutException e) {
            System.err.println("30 segs sin recibir nada");
        }
        catch (java.io.IOException e) {
            System.err.println("Error: " + e.getMessage());
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally{
            // Cerramos el socket
            if (ds != null)
                ds.close();
        }
    }
}
