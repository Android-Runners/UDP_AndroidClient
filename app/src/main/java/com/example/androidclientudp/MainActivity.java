package com.example.androidclientudp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;
    private ImageView im1, im2;
    private TextView text, textID;
    private EditText address, port, ID;
    private static final int GALLERY_REQUEST = 1;
    private Bitmap bitRes;
    private DatagramSocket datagramSocket;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection; // Токен, позволяющий приложению захватить содержимое экрана, или аудио
    private RecordService recordService;
    private MainActivity mainActivity;
    private VideoView videoView;
    private ServerSocket serverSocket;
    private TextView textIP;
    private TextView textView;
    private Socket socket;
    private Fromfile fromFile;
    private TextView yourId;
    private boolean isInit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        final MainActivity main = this;
        yourId = (TextView)findViewById(R.id.YourId);
        textIP = (TextView)findViewById(R.id.textView2);
        videoView = (VideoView)findViewById(R.id.videoView);
        textID = (TextView)findViewById(R.id.textID);
        address = (EditText)findViewById(R.id.editText);
        port = (EditText)findViewById(R.id.editText2);
        ID = (EditText)findViewById(R.id.editText3);
        mainActivity = this;
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        final FloatingActionButton init = (FloatingActionButton) findViewById(R.id.fab2);
     //   final FloatingActionButton toStore = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try {
                   if(!isInit){
                       Toast.makeText(getApplicationContext(), "Ви ще не ініціалізовані", Toast.LENGTH_SHORT).show();
                   }else {
                       if (recordService.isRunning()) {
                           recordService.stopRecord();
                           textID.setText(R.string.start_record);
                           Fromfile fromFile = new Fromfile(mainActivity, Initialization.getSocket(), Initialization.getInput(), Initialization.getOutput(), recordService.getPathVideo(), textID, videoView, ID.getText() + "");
                         //  textID.setText("91");
                           Thread thread = new Thread(fromFile);
                           thread.start();
                           thread.join();

                       } else {
                           Intent captureIntent = projectionManager.createScreenCaptureIntent();
                           startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                       }
                   }
               }catch (Exception e) {
                    textID.setText(e.getMessage());
               }
            }
        });
    /*    toStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im1.setImageBitmap(getBitStorage());
            }
        });*/
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isInit)
                        Toast.makeText(getApplicationContext(), "Ви вже ініціалізовані", Toast.LENGTH_SHORT).show();
                    else {
                        Thread thread = new Thread(new Initialization(address.getText() + "", port.getText() + "", textID, yourId, mainActivity));
                        thread.start();
                        thread.join();
                        isInit = true;
                    }
                }catch(Exception e) {}
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
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

        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, imageReturnedIntent);
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
            textID.setText(R.string.stop_record);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            textID.setText(recordService.isRunning() ? R.string.stop_record : R.string.start_record);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };
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
