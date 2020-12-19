package Model;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket = null;
    private String phoneNumber;
    private ObjectOutputStream dout = null;

    public Client(Socket sk, String phoneNumber) {
        this.socket = sk;
        this.phoneNumber = phoneNumber;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ObjectOutputStream getDout() {
        return dout;
    }

    public void setDout(ObjectOutputStream dout) {
        this.dout = dout;
    }
}
