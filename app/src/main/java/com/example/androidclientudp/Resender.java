package com.example.androidclientudp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Date;

public class Resender implements Runnable {
    private DatagramSocket datagramSocket;
    private ImageView im2;
    private int count = 0;
    private MainActivity main;
    private byte[] receiveData = new byte[1048576];
    private TextView textID;
    private VideoView videoView;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    public Resender(DatagramSocket datagramSocket, TextView textID, VideoView videoView, MainActivity main) {
        this.datagramSocket = datagramSocket;
        this.main = main;
        this.textID = textID;
        this.videoView = videoView;
    }
    public Resender(ObjectInputStream input, ObjectOutputStream output, TextView textID, VideoView videoView, Socket socket, MainActivity main) {
        this.main = main;
        this.input = input;
        this.socket = socket;
        this.textID = textID;
        this.videoView = videoView;
        this.output = output;
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
            Date date = new Date();
            File file = new File("/storage/emulated/0/ScreenRecord/" + date.getTime() + ".mp4");
            if(file.exists()) {
                file.delete();
                file = new File("/storage/emulated/0/ScreenRecord/" + date.getTime() + ".mp4");
            }
            file.createNewFile();
            Object sizeOfFile = input.readObject();
            String sizeString = new String((byte[])sizeOfFile);
            sizeOfFile = (Integer.valueOf(sizeString));
            FileOutputStream fos = new FileOutputStream(file, false);
            Integer sum = 0;
            byte[] answerArray = null;
            while(true){
                Object toGet = input.readObject();
                byte[] receiveByte = (byte[])toGet;
                sum += receiveByte.length;
                if(sum == receiveByte.length)
                    answerArray = receiveByte;
                else
                    answerArray = concat(answerArray, receiveByte);
                if(sum >= (Integer)sizeOfFile)
                    break;
            }
           // changeText(answerArray.length + " Res");
            fos.write(answerArray);
            fos.flush();
            fos.close();
            //changeText(file.length() + " name(Resender): " + file.getName());
            playVideo(file.getAbsolutePath());
        }catch(Exception e){}
    }

    private byte[] remove(byte[] bytes, int index)
    {
        if (index >= 0 && index < bytes.length)
        {
            byte[] copy = new byte[bytes.length-1];
            System.arraycopy(bytes, 0, copy, 0, index);
            System.arraycopy(bytes, index+1, copy, index, bytes.length-index-1);
            return copy;
        }
        return bytes;
    }

    private void playVideo(final String path){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoView.setVideoURI(Uri.parse(path));
                videoView.requestFocus();
                videoView.start();
            }
        });
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
