package com.example.androidclientudp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client implements Runnable{
    private ImageView im1, im2;
    private String ID;
    private MainActivity main;
    private String port,address;
    private Bitmap screen;
    private DatagramSocket datagramSocket;
    @Override
    public void run() {
        // "0.tcp.ngrok.io"
        //"192.168.1.101"
        //46.98.191.197
        try {
        //    changeText("Клієнт розпочато");
          //  changeText("In client");
           // datagramSocket.setBroadcast(true);
            InetAddress IPAddress = InetAddress.getByName(address);
            byte[] sendData = new byte[202400];
            Bitmap bitmap = ((BitmapDrawable) im1.getDrawable()).getBitmap();
            sendData = bitmapToByteArray(bitmap);
            Integer sizeSend = sendData.length;
            //отправляю сам массив, конвертированный из битмапа - С СЖАТИЕМ
            DatagramPacket sendPacket = new DatagramPacket(sendData, sizeSend, InetAddress.getByName(address), Integer.valueOf(port));
            datagramSocket.send(sendPacket);
        //    changeText(sendPacket.getAddress() + " " + sendPacket.getPort());
            //принимаю массив
        //    changeText("Перед приемом");

        }catch(Exception e){
         //   changeText("" + e.getMessage() + " kek");
        }
    }
    private int toSizeFromByte(byte[] b, int length){
        int ans = 0;
        for(int i = 0; i < length; i++)
            ans += b[0]*(length-i);
        return ans;
    }
    private byte[] toByteFromSize(int size){
        StringBuffer sb = new StringBuffer(size);
        byte[] answer = new byte[102400];
        for(int i = 0; i < sb.length(); i++)
            answer[i] = (Byte.valueOf(sb.charAt(i) + ""));
        return answer;
    }
    private void changeIm(final Bitmap bitmap){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                im2.setImageBitmap(bitmap);
            }
        });
    }
   /* private void changeText(final String s){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(s);
            }
        });
    }*/
    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }
    public byte[] bitmapToByteArray(Bitmap bmp){
        byte[] toNumber = {0, Byte.valueOf(ID+"")};
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.WEBP, 50, stream);
        byte[] mas = stream.toByteArray();
        return concat(toNumber,mas);
    }
    public Client(){}
    public Client(ImageView im1, ImageView im2, String ID, String port, String address, DatagramSocket datagramSocket, MainActivity main){
        this.im1 = im1;
        this.im2 = im2;
        this.port = port;
        this.address = address;
        this.datagramSocket = datagramSocket;
        this.main = main;
        this.ID = ID;
    }
}
