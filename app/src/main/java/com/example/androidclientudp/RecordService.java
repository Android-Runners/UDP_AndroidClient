package com.example.androidclientudp;

import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.view.Surface;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private Surface surface;
    private boolean running;
    private int width = 720;
    private int height = 1080;
    private int dpi;
    private String pathVideo, nameVideo;

    public String getPathVideo() { return pathVideo; }
    public Surface getSurface() { return surface; }

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        running = false;
        mediaRecorder = new MediaRecorder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    public boolean isRunning() {
        return running;
    }

    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }

        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        running = true;
        return true;
    }

    public boolean stopRecord() {
            if (!running) {
                return false;
            }
        running = false;
            while(true) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    virtualDisplay.release();
                    mediaProjection.stop();
                    break;
                } catch (Throwable th) {
                }
            }
            return true;
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {

     //   mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Устанавливает источник звука, используемый для записи
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE); // источник видео, используется для записи
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // формат получаевомого файла записи
        nameVideo = System.currentTimeMillis() + ".mp4";
        pathVideo = getsaveDirectory() + nameVideo;
        mediaRecorder.setOutputFile(pathVideo); // Целевое местоположение и имя файла записи
        mediaRecorder.setVideoSize(width, height); // размер видео
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // Кодировщик видео
   //     mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // кодировщик аудио
        mediaRecorder.setVideoEncodingBitRate(1024 * 5); // устанавливает "битрэйт" файла записи. Прописано - 5 мегабит
        // 409600 бит - кодирование H264
        mediaRecorder.setVideoFrameRate(35); // частотак кадров в секунду
        try {
            mediaRecorder.prepare(); // подготавливает для записи и кодирования данных
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/"; // Путь сохранения

            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }

            Toast.makeText(getApplicationContext(), rootDir, Toast.LENGTH_SHORT).show();

            return rootDir;
        } else {
            return null;
        }
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }
}
