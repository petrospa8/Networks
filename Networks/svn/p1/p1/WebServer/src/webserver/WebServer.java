package webserver;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

public class WebServer{ 
	
	Socket clientSocket;
        public static int port;
        public static String index;
        public static String root;
        public static boolean permission;
        private String HTTPversion = "HTTP/1.0";
        private String serverName = "Pedro Pillado's Java HTTP server";
        private String badRequestError = "badRequest.html";
        private String notFoundError = "notFound.html";
        private String requestLogs = "requests.log";
        private String errorLog = "errors.log";
        enum config {
            DEFAULT_PATH("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/"),
            PORT("5000"), 
            DIRECTORY_INDEX("welcome.html"), 
            DIRECTORY("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/"), 
            ALLOW("true");

            private final String value;

            config(String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }
        }

        public WebServer(String[] args){
            if (args.length > 1) {
                System.err.println("Format: <Path (optional)>");
                System.exit(-1);
            }
            else if(args.length == 0){
                configure(config.DEFAULT_PATH.getValue());
            }
            else if(args.length == 1){
                configure(args[0]);
            }
        }
        
	public static void main(String[] args) {
            
            new WebServer(args);

            port = Integer.parseInt(config.PORT.getValue());
            try {
			ServerSocket serverConnect = new ServerSocket(port);
			System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");
			
			// we listen until user halts server execution
			while (true) {
				Socket clientSocket = serverConnect.accept();
			
				// create dedicated thread to manage the client connection
				WebThread thread = new WebThread(clientSocket);
				thread.start();
			}
			
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
            
	}
        
    private void configure(String serverPath) {
        Properties props = new Properties();
        File configFile;
        FileInputStream inputSt = null;
        FileOutputStream outputSt = null;
        String aux;
        
        try {
            if (!serverPath.endsWith("/")){
                serverPath += "/";
            }            
            configFile = new File(serverPath + "server.properties");
            configFile.createNewFile();
            inputSt = new FileInputStream(configFile);
            props.load(inputSt);
            
            while ((aux = props.getProperty(config.PORT.name())) == null) {
                props.setProperty(config.PORT.name(), config.PORT.getValue());
            }
            
            port = Integer.parseInt(aux);
            
            while ((index = props.getProperty(config.DIRECTORY_INDEX.name())) == null)
                props.setProperty(config.DIRECTORY_INDEX.name(), config.DIRECTORY_INDEX.getValue());
            
            if (index.startsWith("/")){
                index = index.substring(1);
            }
            
            while ((root = props.getProperty(config.DIRECTORY.name())) == null){
                props.setProperty(config.DIRECTORY.name(), config.DIRECTORY.getValue());
            }
            
            if (root.endsWith("/")){
                root = root.substring(0, root.length() - 1);
            }
            
            while ((aux = props.getProperty(config.ALLOW.name())) == null){
                props.setProperty(config.ALLOW.name(), config.ALLOW.getValue());
            }
            
            permission = Boolean.parseBoolean(aux);
            
            outputSt = new FileOutputStream(configFile);
            props.store(outputSt, "server properties");
            
            HTTPversion = "HTTP/1.0";
            serverName = "Pedro Pillado's Java HTTP server";
            requestLogs = "requests.log";
            errorLog = "errors.log";
            badRequestError = "badRequest.html";
            notFoundError = "notFound.html";
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        
        } finally {
            try {
                if (inputSt != null)
                    inputSt.close();
                if (outputSt != null)
                    outputSt.close();
            
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
	
}