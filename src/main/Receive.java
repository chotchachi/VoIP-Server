package main;

import Model.Account;
import Model.DataSocket;
import Model.Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receive extends Thread {
    private Socket socket = null;
    private String currentPhoneNumber;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;
    Boolean is_running = true;

    public Receive(Socket socket) {
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void run() {
        DataSocket respon = null;
        try {
            while (is_running) {
                inputStream = new ObjectInputStream(socket.getInputStream());
                respon = (DataSocket) inputStream.readObject();
                System.out.println("Server: User " + respon.getAction());
                switch (respon.getAction()) {
                    case "login":
                        login(respon.getData()[0], respon.getData()[1]);
                        break;
                    case "androidLoginOk":
                        addClient(respon.getData()[0]);
                        break;
                    case "register":
                        register(respon);
                        break;
                    case "logout":
                        userDisconnect();
                        break;
                    case "loadcontact":
                        loadContact(respon.getData()[0]);
                        break;
                    case "request_call":
                        requestCall(respon);
                        break;
                    case "respon_call":
                        responCall(respon);
                        break;
                    case "endcall":
                        endCall(respon);
                        break;
                    case "request_video":
                        requestVideo(respon);
                        break;
                    case "respon_video":
                        responVideo(respon);
                    default:
                        System.out.println("Unknow Action");
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            userDisconnect();
            System.out.println("User disconnect 1");
            System.out.println(ex);
        } catch (SQLException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
                userDisconnect();
                System.out.println("User disconnect");
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public void responCall(DataSocket data) {
        for (Client client : ChatServer.listClient) {
            if (client.getPhoneNumber().equals(data.getNguoiNhan().getPhoneNumber())) {
                try {
                    client.getDout().writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    public void requestCall(DataSocket data) {
        for (Client client : ChatServer.listClient) {
            if (client.getPhoneNumber().equals(data.getNguoiNhan().getPhoneNumber())) {
                try {
                    client.getDout().writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
    
    public void requestVideo(DataSocket data){
        for (Client client : ChatServer.listClient) {
            if (client.getPhoneNumber().equals(data.getNguoiNhan().getPhoneNumber())) {
                try {
                    client.getDout().writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
    
    public void responVideo(DataSocket data) {
        for (Client client : ChatServer.listClient) {
            if (client.getPhoneNumber().equals(data.getNguoiNhan().getPhoneNumber())) {
                try {
                    client.getDout().writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
    
    private void endCall(DataSocket data) {
        for (Client client : ChatServer.listClient) {
            if (client.getPhoneNumber().equals(data.getNguoiNhan().getPhoneNumber())) {
                try {
                    client.getDout().writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                break;
            }
        }
    }

    public void loadContact(String phone) throws SQLException, IOException {
        List<Account> accountList = new ArrayList<>();
        Statement statement = ChatServer.connect.createStatement();
        String query = "SELECT account.id, account.phone_number, account.name "
                + "FROM account "
                + "LEFT JOIN danhba "
                + "ON account.phone_number = danhba.contact "
                + "WHERE danhba.account_phone = '" + phone + "'";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            accountList.add(new Account(rs.getInt("id"), rs.getString("phone_number"), rs.getString("name")));
        }

        DataSocket dtsk = new DataSocket();
        dtsk.setAction("receiveContact");
        dtsk.setAccountList(accountList);
        outputStream.writeObject(dtsk);
        outputStream.flush();
    }

    public void login(String phone, String pass) {
        System.out.println("New User Login: " + phone);

        DataSocket dtsk = new DataSocket();
        try {
            Statement statement = ChatServer.connect.createStatement();
            String query = "SELECT * FROM account WHERE phone_number = '" + phone + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                String query2 = "SELECT * FROM account WHERE phone_number = '" + phone + "' AND password = '" + pass + "'";
                ResultSet rs2 = statement.executeQuery(query2);
                if (rs2.next()) {
                    Account account = new Account(rs2.getInt("id"), rs2.getString("phone_number"), rs2.getString("name"));
                    dtsk.setAction("loginok");
                    dtsk.setNguoiGui(account);
                    outputStream.writeObject(dtsk);
                    outputStream.flush();
                    addClient(phone);
                } else {
                    dtsk.setAction("loginwrong");
                    outputStream.writeObject(dtsk);
                    outputStream.flush();
                }
            } else {
                dtsk.setAction("register");
                outputStream.writeObject(dtsk);
                outputStream.flush();
            }
            statement.close();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addClient(String phoneNumber) {
        Client client = new Client(socket, phoneNumber);
        client.setDout(outputStream);
        ChatServer.listClient.add(client);

        this.currentPhoneNumber = phoneNumber;
    }

    public void userDisconnect() {
        for (int i = 0; i < ChatServer.listClient.size(); i++) {
            if (ChatServer.listClient.get(i).getPhoneNumber().equals(this.currentPhoneNumber)) {
                //Xóa khỏi server đang online
                ChatServer.listClient.remove(i);
                System.out.println("Remove: "+this.currentPhoneNumber);
            }
        }
        is_running = false;
    }

    private void register(DataSocket msg) {
        System.out.println("New User Register: " + msg.getData()[0]);

        DataSocket dtsk = new DataSocket();
        try {
            Statement statement = ChatServer.connect.createStatement();
            //them vao db
            String sql = "Insert into account(phone_number, password, name) values ('" + msg.getData()[0] + "','" + msg.getData()[1] + "','" + msg.getData()[2] + "') ";
            statement.executeUpdate(sql);

            dtsk.setAction("registerok");

            outputStream.writeObject(dtsk);
            outputStream.flush();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
