package com.example.androidclientudp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Resender implements Runnable {
    private DatagramSocket datagramSocket;
    private ImageView im2;
    private int count = 0;
    private MainActivity main;
    private byte[] receiveData = new byte[202400];
    private TextView textID;
    public Resender(DatagramSocket datagramSocket, ImageView im2, TextView textID, MainActivity main) {
        this.datagramSocket = datagramSocket;
        this.im2 = im2;
        this.main = main;
        this.textID = textID;
    }
    private void changeText(final String s){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textID.setText(s);
            }
        });
    }
    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }
    @Override
    public void run() {
        try {
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                datagramSocket.receive(receivePacket);
                //Конвертирую в битмап и вывожу картинку
                Bitmap receiveBitmap = BitmapFactory.decodeByteArray(receiveData, 0, receivePacket.getLength());
                //changeText((receiveBitmap==null) + " " + receiveData.length + " " + receivePacket.getLength() + " \n" + (datagramSocket==null));
                changeIm(receiveBitmap);
            }
        }catch(IOException e){}
    }
    private void changeIm(final Bitmap bitmap){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                im2.setImageBitmap(bitmap);
            }
        });
    }
}
