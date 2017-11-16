package com.example.androidclientudp;

import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Initialization implements Runnable{
    private String port, address;
    private DatagramSocket clientSocket;
    private Boolean isAll = false;
    private byte number;
    private TextView textID;
    private MainActivity main;

    public static ObjectInputStream getInput() {
        return input;
    }

    static private ObjectInputStream input;

    public static ObjectOutputStream getOutput() {
        return output;
    }

    static private ObjectOutputStream output;



    public static Socket getSocket() {
        return socket;
    }

    static private Socket socket;


    public byte getNumber() {
        return number;
    }
    public Boolean getisAll(){ return isAll; }
    public DatagramSocket getClientSocket() { return clientSocket; }

    public Initialization(String address, String port, TextView textID, MainActivity main) {
        this.address = address;
        this.port = port;
        this.textID = textID;
        this.main = main;
    }
    private void changeText(final String s){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textID.setText(s);
            }
        });
    }
    @Override
    public void run() {
        try {
            InetAddress IPAddress = InetAddress.getByName(address);
          //  changeText("Before init");
            socket = new Socket(IPAddress, Integer.valueOf(port));
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
           // changeText("After init. " + IPAddress + " " + port);
        }catch(Exception e){
            changeText(e.getMessage());
        }
    }
}
