package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectSever extends Thread{
    public ServerSocket serverSocket = null;
    public Socket socket = null;
    public int port = 8888;
    
    @Override
    public void run(){
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server bắt đầu chạy tại cổng " +port+ "\n");
        } catch (IOException ex) {
            Logger.getLogger(ConnectSever.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Không thể khởi động Server \n");
            return;
        }
        try {
            while(true){
                try {
                    Socket connectSocket = serverSocket.accept();
                    System.out.println("Client kết nối: " + connectSocket);
 
                    Receive handler = new Receive(connectSocket);
                    executor.execute(handler);
                } catch (IOException e) {
                    System.err.println("Kết nối lỗi: " + e);
                }
            }
        } catch (SecurityException ex) {
                    Logger.getLogger(ConnectSever.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
}
