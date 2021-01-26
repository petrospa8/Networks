package webserver;
import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.StringTokenizer;
import java.util.Date;
import static java.lang.Math.toIntExact;
import java.text.ParseException;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebThread extends Thread {
       
    Socket socket;
    StringTokenizer request;
    String msg;
    Date lastRequest;
    String host;
    
    public WebThread(Socket s) {
        this.socket = s;
    }
    
    public void get(boolean ifModifiedSince) throws IOException{
       String fileName;
       String header;
       int headerLgth;
       Path path;
       byte[] bufferOut;
       fileName = request.nextToken();
       String pathStr = WebServer.root + fileName;
       File resource = new File(pathStr);
       long size = resource.length();
       int state;
       int code;
       OutputStream output; 
       path = resource.toPath();
       Date lastModified = new Date(resource.lastModified());
       Date date = new Date();
       if(!ifModifiedSince || (ifModifiedSince && lastModified.compareTo(lastRequest) > 0)){
        header = ( "HTTP/1.0 200 OK\n" + "Date: " + date + "\nServer: Pedro Pillado's Java HTTP server\nLast-Modified: " + lastModified + "\nContent-Length: " + size + "\nContent-Type: " + Files.probeContentType(path) + "\n\n" );
        headerLgth = header.getBytes().length;
        state = 1;
        code = 200;
        
        bufferOut = new byte[headerLgth + toIntExact(resource.length()) + 100];

        System.arraycopy(header.getBytes(), 0, bufferOut, 0, headerLgth);
        try{
            output = this.socket.getOutputStream();
        } catch(IOException e){

            throw new RuntimeException(e);           
        }

        if (resource.exists()){
            if(resource.isDirectory()){
                if(!pathStr.endsWith("/")){
                    pathStr += "/";
                }
                
                pathStr = WebServer.root + WebServer.index;
                File resource2 = new File(pathStr);
                
               
                if (resource2.exists()){
                   lastModified = new Date(resource2.lastModified());
                   size = resource2.length();
                   header = ( "HTTP/1.0 200 OK\n" + "Date: " + date + "\nServer: Pedro Pillado's Java HTTP server\nLast-Modified: " + lastModified + "\nContent-Length: " + size + "\nContent-Type: " + Files.probeContentType(path) + "\n\n" );
                   headerLgth = header.getBytes().length;
                   writeLog(state, code, date, size, "");
                   System.arraycopy(Files.readAllBytes(resource2.toPath()), 0, bufferOut,headerLgth, toIntExact(resource.length()));
                   output.write(bufferOut, 0, bufferOut.length);
                }
                    
                else{
                    if (WebServer.permission){
                        String url = "http://" + host + fileName;
                        buildIndex(resource.list(), url);
                    }
                    else{
                        System.out.println("Not allowed.");
                    }
                }
            }
        else{
            try{
                System.arraycopy(Files.readAllBytes(path), 0, bufferOut,headerLgth, toIntExact(resource.length()));
                writeLog(state, code, date, size, "");
                output.write(bufferOut, 0, bufferOut.length);
            } catch(FileNotFoundException e){

                System.err.println(e.getMessage());
            } catch(IOException e){

                throw new RuntimeException(e);
            }   
        }
        }
        else{
            notFound();
        }
       }
       else{
        notModified(date);
        }
       
    }
    
    public void head(boolean ifModifiedSince) throws IOException{
       String fileName;
       String header;
       String type;
       int headerLgth;
       Path path;
       int state;
       int code;
       fileName = request.nextToken();
       File resource = new File("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/" + fileName);
       long size = resource.length();
      
       OutputStream output; 
       path = resource.toPath();
       type = Files.probeContentType(path);
       Date lastModified = new Date(resource.lastModified());
       byte[] bufferOut;
       Date date = new Date();
       
       if(!ifModifiedSince || (ifModifiedSince && lastModified.compareTo(lastRequest) > 0)){
        header = ( "HTTP/1.0 200 OK\n" + "Date: " + date + "\nServer: Pedro Pillado's Java HTTP server \nLast-Modified: " + lastModified + "\nContent-Length: " + size + "\nContent-Type: " + type + "\n\n" );
        headerLgth = header.getBytes().length;
        state = 0;
        code = 200;
        writeLog(state, code, date, size, "");
        bufferOut = new byte[headerLgth];

        System.arraycopy(header.getBytes(), 0, bufferOut, 0, headerLgth);
        try{
            output = this.socket.getOutputStream();
        } catch(IOException e){

            throw new RuntimeException(e);           
        }

        if (resource.exists()){
            try{
                output.write(bufferOut, 0, bufferOut.length);
            } catch(FileNotFoundException e){

                System.err.println(e.getMessage());
            } catch(IOException e){

                throw new RuntimeException(e);
            }

        }
        else{
            notFound();
        }
       }
       else{
        notModified(date);
        }
    }
       
    
    public void badrequest() throws IOException{
       
       String header, error;
       int headerLgth;
       Path path;       
       int code, state;
       File resource = new File("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/badRequest.html");
       long size = resource.length();
       Date date = new Date();
       OutputStream output; 
       path = resource.toPath();
       header = ( "HTTP/1.0 400 Bad Request\n" + "Date: " + date + "\nServer: Pedro Pillado's Java HTTP server \nContent-Length: " + size + "\nContent-Type: " + Files.probeContentType(path) + "\n\n" );
       headerLgth = header.getBytes().length;
       state = 1;
       code = 400;
       error = "400 Bad Request";
       writeLog(state, code, date, size, error);
       byte[] bufferOut = new byte[headerLgth + toIntExact(resource.length()) + 1000];
       
       
       System.arraycopy(header.getBytes(), 0, bufferOut, 0, headerLgth);
       try{
           output = this.socket.getOutputStream();
       } catch(IOException e){
           
           throw new RuntimeException(e);           
       }
       
       if (resource.exists()){
           try{
               System.arraycopy(Files.readAllBytes(path), 0, bufferOut,headerLgth, toIntExact(resource.length()));
               
               output.write(bufferOut, 0, bufferOut.length);
           } catch(FileNotFoundException e){
               
               System.err.println(e.getMessage());
           } catch(IOException e){
               
               throw new RuntimeException(e);
           }
               
       }
    }
    public void notFound() throws IOException{
       String header, error;
       int headerLgth;
       Path path;       
       int state, code;
       File resource = new File("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/NotFound.html");
       long size = resource.length();
       Date date = new Date();
       OutputStream output; 
       path = resource.toPath();
       header = ( "HTTP/1.0 404 Not Found\n" + "Date: " + date + "\nServer: Pedro Pillado's Java HTTP server \nContent-Length: " + size + "\nContent-Type: " + Files.probeContentType(path) + "\n\n" );
       headerLgth = header.getBytes().length;
       error = "404 Not Found";
       state = 0;
       code = 404;
       writeLog(state, code, date, size, error);
       byte[] bufferOut = new byte[headerLgth + toIntExact(resource.length()) + 1000];
       
       
       System.arraycopy(header.getBytes(), 0, bufferOut, 0, headerLgth);
       try{
           output = this.socket.getOutputStream();
       } catch(IOException e){
           
           throw new RuntimeException(e);           
       }
       
       if (resource.exists()){
           try{
               System.arraycopy(Files.readAllBytes(path), 0, bufferOut,headerLgth, toIntExact(resource.length()));
               
               output.write(bufferOut, 0, bufferOut.length);
           } catch(FileNotFoundException e){
               
               System.err.println(e.getMessage());
           } catch(IOException e){
               
               throw new RuntimeException(e);      
            }
       }
    }
    
    void notModified(Date date) throws IOException{
        String header = ( "HTTP/1.0 304 Not Modified\n" + "Date: " + date + "\nServer: Pedro Pillado's Java HTTP server\n\n");
        int headerLgth = header.getBytes().length;
        int state = 0;
        int size = 0;
        int code = 304;
        OutputStream output;
        byte[] bufferOut = new byte[headerLgth + 100];
        writeLog(state, code, date, size, "");
        try{
            output = this.socket.getOutputStream();
        } catch(IOException e){

            throw new RuntimeException(e);           
        }
        System.arraycopy(header.getBytes(), 0, bufferOut, 0, headerLgth);
        output.write(bufferOut, 0, bufferOut.length);
    }
    void writeLog(int state, int code, Date date, long size, String error){
        File log;
        PrintWriter outputter;
        
        try{
            if(state == 0){
                log = new File("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/requests.log");
                outputter = new PrintWriter(new FileOutputStream(log, true), true);
                
                outputter.println("Request: " + msg);
                outputter.println("IP: " + socket.getInetAddress().getHostName());
                outputter.println("Date: " + date);
                outputter.println("Status code: " + code);
                if(size != 0){
                    outputter.println("Size: " + size);
                }
            }
            else if(state == 1){
                 log = new File("/home/pedro/Documentos/Uni/Redes/WebServer/p1-files/errors.log");
                 outputter = new PrintWriter(new FileOutputStream(log, true), true);
                 outputter.println("Request: " + msg);
                 outputter.println("IP: " + socket.getInetAddress().getHostName());
                 outputter.println("Date: " + date);
                 outputter.println("Error message: " + error);
            }            
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
        
    }
    private void buildIndex (String[] fileList, String url) {
        String links = "";
        OutputStream output;
        links += "<html>" + "<head></head>" + "<body>" + "<ul>";
        
        for (String file : fileList) {
            links += "<li>"
                    + "<a href=\"" + url + file + "\">" + file + "</a>"
                    + "</li>";
        }
        
        links += "</ul>"
                + "</body>"
                + "</html>";
        
        try {
            int headerLgth;
            String header = ( "HTTP/1.0 200 OK\n" + "Date: " + new Date() + "\nServer: Pedro Pillado's Java HTTP server\n\n" );
            headerLgth = header.getBytes().length;
            byte[] bufferOut = new byte[headerLgth + links.getBytes().length];
            try{
                output = this.socket.getOutputStream();
            } catch(IOException e){
                throw new RuntimeException(e);           
        }
            System.arraycopy(header.getBytes(), 0, bufferOut, 0, headerLgth);
            System.arraycopy(links.getBytes(),0, bufferOut, headerLgth, links.getBytes().length);
            output.write(bufferOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    
    public void run() {
        BufferedReader readBuf = null;
        String header = null;
        DataOutputStream writeBuf = null;
        String method;
        int mode = 0;
        String optionalRequest;
        StringTokenizer request2;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        boolean ifModified = false;
        try {
            socket.setSoTimeout(60000);
            while(true){                              

                
                try {
                    readBuf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    msg = readBuf.readLine();
                    writeBuf = new DataOutputStream(socket.getOutputStream());
                    
                    if(msg != null){
                        request = new StringTokenizer(msg);
                        
                        
                        if (request.countTokens() > 0){
                            method = request.nextToken();
                            
                            if (method.equals("GET")){
                                mode = 1; //get will be executed
                            }
                            else if(method.equals("HEAD")){
                                mode = 2; //head will be executed
                            }
                            else{
                                badrequest();
                            }
                        }
                        
                        
                        boolean condition = true;
                        while(condition){
                            optionalRequest = readBuf.readLine();
                            System.out.println(optionalRequest);
                            if(optionalRequest.equals("")){
                                condition = false;
                                break;
                            }
                            else if (optionalRequest.contains("If-Modified-Since:")){
                               
                                try {
                                    lastRequest = dateFormatter.parse(optionalRequest.split(": ")[1]);
                                    System.out.println(lastRequest);
                                } catch (ParseException ex) {
                                    System.err.println(ex.getMessage());
                                }
                                ifModified = true;
                            }
                            else if (optionalRequest.contains("Host:")){
                                host = optionalRequest.split(": ")[1];
                            }
                        }                 
                        if (mode == 1){
                            get(ifModified);
                        }
                        else{
                            head(ifModified);
                        }          
                    }
                    
                }
                catch (SocketTimeoutException e) {
                    System.err.println("No connection requests in 300 secs");
                }
                catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
        catch (SocketException ex) {
            System.err.println(ex.getMessage());
        }finally{
                try {
                    if (readBuf != null){
                        readBuf.close();
                    }
                    if (writeBuf != null){
                        writeBuf.close();
                    }
                    socket.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
                       
         
    }
}