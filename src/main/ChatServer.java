package main;

import Model.Client;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatServer {
    public static ArrayList<Client> listClient = new ArrayList<>();
    public static Connection connect = null; 
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        ConnectSever listen = new ConnectSever();
        listen.start();  
        
        connect = ConnectionUtils.getMySQLConnection();
        System.out.println("Connect DB: " + connect);
    }
}
