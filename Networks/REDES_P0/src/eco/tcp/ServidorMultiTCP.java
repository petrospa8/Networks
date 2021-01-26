/**
 * @author luis.rodriguez.soutullo
 */
package eco.tcp;

import java.net.*;
import java.io.*;

public class ServidorMultiTCP {
    
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Formato: ServidorMultiTCP <puerto>");
            System.exit(-1);
        }
        
        ServerSocket sServidor = null;
        
        try {
            // Creamos el socket del servidor
            sServidor = new ServerSocket(5000);
            
            // Establecemos un timeout de 30 segs
            sServidor.setSoTimeout(30000);
            
            while (true) {
                // Esperamos posibles conexiones
                Socket sCliente = sServidor.accept();
                // Creamos un objeto ThreadServidor, pasándole la nueva conexión
                ThreadServidor ts = new ThreadServidor(sCliente);
                // Indicamos su ejecución con el método start();
                ts.start();
            }
        }
        catch (SocketTimeoutException e) {
            System.err.println("30 segs sin recibir nada");
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                // Cerramos el socket del servidor
                if (sServidor != null)
                    sServidor.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
