/**
 * @author luis.rodriguez.soutullo
 */
package eco.tcp;

import java.net.*;
import java.io.*;

public class ThreadServidor extends Thread {
    
    Socket socket;
    
    public ThreadServidor(Socket s) {
        // Almacenamos el socket de la conexión
        this.socket = s;
    }
    
    @Override
    public void run() {
        BufferedReader br = null;
        PrintWriter pw = null;
        
        try {
            // Establecemos el canal de entrada
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Establecemos el canal de salida
            pw = new PrintWriter(socket.getOutputStream(), true);

            // Recibimos el mensaje del cliente
            String mensaje = br.readLine();

            // Enviamos el eco al cliente
            pw.println(mensaje);
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
                
                // Cerramos el socket
                socket.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
