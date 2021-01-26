/**
 * @author luis.rodriguez.soutullo
 */
package eco.tcp;

import java.net.*;
import java.io.*;

public class ClienteTCP {
    
    public static void main(String argv[]) {
        if (argv.length != 3) {
            System.err.println(("Formato: ClienteTCP <maquina> <puerto> <mensaje>"));
            System.exit(-1);
        }
        
        Socket socket = null;
        BufferedReader sEntrada = null;
        PrintWriter sSalida = null;
        
        try {
            // Obtenemos la dirección IP del servidor
            InetAddress dirServidor = InetAddress.getByName(argv[0]);
            
            // Obtenemos el puerto del servidor
            int puertoServidor = Integer.parseInt(argv[1]);
            
            // Obtenemos el mensaje
            String mensaje = argv[2];
            
            // Creamos el socket y establecemos la conexión con el servidor
            socket = new Socket(dirServidor, puertoServidor);
            
            // Establecemos un timeout de 30 segs
            socket.setSoTimeout(30000);
            
            System.out.println("CLIENTE: Conexion establecida con "
                    + dirServidor.toString() + " al puerto " + puertoServidor);
            
            // Establecemos el canar de entrada
            sEntrada = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            // Establecemos el canal de salida
            sSalida = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("CLIENTE: Enviando "  + mensaje);

            // Enviamos el mensaje al servidor
            sSalida.println(mensaje);

            // Recibimos la respuesta del servidor
            String recibido = sEntrada.readLine();

            System.out.println("CLIENTE: Recibido " + recibido);
        }
        catch (SocketTimeoutException e) {
            System.err.println("30 segs sin recibir nada");
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        finally {
            try {
                if (sEntrada != null)
                    sEntrada.close();
                
                if (sSalida != null)
                    sSalida.close();
            
                if (socket != null)
                    socket.close();
            }
            catch (IOException e) {
                //e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
