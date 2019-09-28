package com.myfabric.myutil;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
  
public class HelloServer {  
  
    public static final int SERVER_PORT = 7051;  
    private ServerSocket serverSocket = null;  
    private ExecutorService executorService = null;  
    private final int POOL_SIZE = 2;  
      
    public HelloServer() throws Exception{  
        int cpuCount = Runtime.getRuntime().availableProcessors();  
        executorService = Executors.newFixedThreadPool(cpuCount * POOL_SIZE);  
        serverSocket = new ServerSocket(SERVER_PORT); 
         
        System.out.println(",...");  
        while(true){  
            try{  
                Socket socket = serverSocket.accept();  
                executorService.execute(new HelloResponser(socket));  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
        }  
    }  
      
    //  
    class HelloResponser implements Runnable{  
        private Socket socket = null;  
          
        public HelloResponser(Socket socket){  
            this.socket = socket;  
        }  
          
        @Override  
        public void run() {  
            try{  
                //  
                String clientIp = socket.getInetAddress().getHostAddress();  
                System.out.println(" " + clientIp + ":" + socket.getPort() + " ");  
                InputStream socketInStream = socket.getInputStream();  
                BufferedReader br = new BufferedReader(new InputStreamReader(socketInStream, "UTF-8"));  
                  
                //  
                OutputStream socketOutStream = socket.getOutputStream();  
                String clientRequestString = null;  
                while((clientRequestString = br.readLine()) != null){  
                    System.out.println(" " + clientIp + " :" + clientRequestString);  
                    String serverReturn = null;  
                    if(clientRequestString.equals("sb")){  
                        serverReturn = "SB.\r\n";  
                        System.out.println("" + clientIp + ":" + serverReturn);  
                        socketOutStream.write(serverReturn.getBytes("UTF-8"));  
                        System.out.println(" " + clientIp + ":" + socket.getPort() + " ");  
                        break;  
                    }else{  
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");  
                        serverReturn = df.format(new Date()) + "\r\n";  
                        System.out.println(" " + clientIp + ":" + serverReturn);  
                        socketOutStream.write(serverReturn.getBytes("UTF-8"));  
                    }  
                }  
            }catch(Exception e){  
                e.printStackTrace();  
            }finally{  
                    try {  
                        if(socket != null){  
                            socket.close();  
                        }  
                    } catch (IOException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                }  
            }  
    }  
      
    //  
    public static void main(String[] args) throws Exception {  
        new HelloServer();  
    }  
}  
