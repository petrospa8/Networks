/**
 * @author luis.rodriguez.soutullo
 */
package eco.tcp;

import java.net.*;
import java.io.*;

public class ServidorTCP {
    
    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Formato: ServidorTCP <puerto>");
            System.exit(-1);
        }
        
        ServerSocket sServidor = null;
        Socket sCliente = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        
        try {
            // Creamos el socket del servidor
            sServidor = new ServerSocket(5000);
            
            // Establecemos un timeout de 30 segs
            sServidor.setSoTimeout(30000);
            
            while (true) {
                // Esperamos posibles conexiones
                sCliente = sServidor.accept();
                
                // Establecemos el canal de entrada
                br = new BufferedReader(new InputStreamReader(sCliente.getInputStream()));
                
                // Establecemos el canal de salida
                pw = new PrintWriter(sCliente.getOutputStream(), true);
                
                // Recibimos el mensaje del cliente
                String mensaje = br.readLine();
                
                // Enviamos el eco al cliente
                pw.println(mensaje);
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
                // Cerramos los flujos
                if (br != null)
                    br.close();
                
                if (pw != null)
                    pw.close();
                
                // Cerramos los sockets
                if (sServidor != null)
                    sServidor.close();
                
                if (sCliente != null)
                    sCliente.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
