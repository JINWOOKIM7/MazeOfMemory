package com.example.jinwoo.mazeofmemory;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    private MainGLSurfaceView mGLSurfaceView;
    private SoundPool soundPool;
    private int soundID;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        mGLSurfaceView = new MainGLSurfaceView(this,width,height);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setActivity(this);
        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC,0);
        try{
            AssetManager manager = getAssets();
            AssetFileDescriptor fileDescriptor = manager.openFd("col.mp3");
            soundID = soundPool.load(fileDescriptor,1);
        }catch(IOException ex){
            Log.e("MainActivity","error in loading sound " + ex.toString());
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        try{
            AssetManager manager = getAssets();
            AssetFileDescriptor descriptor = manager.openFd("bgm.m4a");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(),descriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        }catch (IOException ex){
            Log.e("error","erro" + ex.toString());
            mediaPlayer=null;
        }
        MainActivity activity = this;
        Context context = activity.getApplicationContext();


    }

    protected void onPause(){
        super.onPause();
        mGLSurfaceView.onPause();

        if(mediaPlayer != null){
            mediaPlayer.pause();
            if(isFinishing()){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

    protected void onResume(){
        super.onResume();
        mGLSurfaceView.onResume();

        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void playSound(){
        try{
            soundPool.play(soundID,1.0f,1.0f,0,0,1.0f);
        }catch (Exception ex){
            Toast toast = Toast.makeText(getApplicationContext(),"Error"+ex.toString(),Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}