package com.example.androidclientudp;

import android.widget.TextView;

public class MyTimer implements Runnable{

    MainActivity main;
    TextView textView;
    boolean bool = false;

    public MyTimer(){}

    public MyTimer(MainActivity main, TextView textView){
        this.main = main;
        this.textView = textView;
    }
    private void changeText(final String s){
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(s);
            }
        });
    }
    public void close(){
        bool = true;
    }
    @Override
    public void run() {
        int seconds = 1;
        while(true){
            synchronized (this) {
                if (bool) return;
            }
            try {
                changeText(seconds+"");
                Thread.sleep(1000);
                seconds++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
