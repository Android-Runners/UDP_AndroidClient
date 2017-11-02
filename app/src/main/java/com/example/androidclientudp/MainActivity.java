package com.example.androidclientudp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private ImageView im1, im2;
    private TextView text, textID;
    private EditText address, port, ID;
    private static final int GALLERY_REQUEST = 1;
    private Bitmap bitRes;
    private DatagramSocket datagramSocket;
    private Boolean isInit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        final MainActivity main = this;
        im1 = (ImageView)findViewById(R.id.imageView1);
        im2 = (ImageView)findViewById(R.id.imageView2);
        textID = (TextView)findViewById(R.id.textID);
        address = (EditText)findViewById(R.id.editText);
        port = (EditText)findViewById(R.id.editText2);
        ID = (EditText)findViewById(R.id.editText3);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        final FloatingActionButton init = (FloatingActionButton) findViewById(R.id.fab2);
        final FloatingActionButton toStore = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInit)
                    new Thread(new Client(im1, im2, ID.getText()+"", port.getText()+"", address.getText()+"", datagramSocket, main)).start();
                else
                    Snackbar.make(view, "Ви ще не ініціалізовані", Snackbar.LENGTH_LONG)
                            .setAction("Попередження", null).show();
            }
        });
        toStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im1.setImageBitmap(getBitStorage());
            }
        });
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!isInit) {
                        Initialization initialization = new Initialization(address.getText() + "", port.getText() + "", datagramSocket, textID, main);
                        Thread th = new Thread(initialization);
                        th.start();
                        th.join();
                        textID.setText("Ваш ID: " + initialization.getNumber());
                        new Thread(new Resender(datagramSocket, im2, textID, main)).start();
                        isInit = true;
                    }else
                        Snackbar.make(v, "Ви вже ініціалізовані", Snackbar.LENGTH_LONG)
                                .setAction("Попередження", null).show();
                }catch(Exception io){
                    textID.setText(io.getMessage());
                }
            }
        });
    }
    public Bitmap getBitStorage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        im1.setImageBitmap(bitRes);
        return bitRes;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        bitRes = bitmap;
                        im1.setImageBitmap(bitmap);
                    } catch (IOException e) {
                    }
                }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
