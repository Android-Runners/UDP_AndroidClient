package com.example.androidclientudp;

import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Initialization implements Runnable{
    private String port, address;
    private DatagramSocket clientSocket;
    private Boolean isAll = false;
    private byte number;
    private TextView textID;
    private MainActivity main;

    public byte getNumber() {
        return number;
    }
    public Boolean getisAll(){ return isAll; }
    public DatagramSocket getClientSocket() { return clientSocket; }

    public Initialization(String address, String port, DatagramSocket datagramSocket, TextView textID, MainActivity main) {
        this.address = address;
        this.port = port;
        this.clientSocket = datagramSocket;
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
            isAll = true;
            byte[] mas = new byte[1];
            DatagramPacket packet = new DatagramPacket(mas, 1, IPAddress, Integer.valueOf(port));
            clientSocket.send(packet);
            clientSocket.receive(packet);
            number = packet.getData()[0];

        }catch(Exception e){
            changeText(e.getMessage());
        }
    }
}
