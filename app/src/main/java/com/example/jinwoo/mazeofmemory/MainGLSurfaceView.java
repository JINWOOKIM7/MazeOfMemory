package com.example.jinwoo.mazeofmemory;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MainGLSurfaceView extends GLSurfaceView {

    private final MainGLRenderer mRenderer;

    public MainGLSurfaceView(Context context,int width,int height){
        super(context);

        setEGLContextClientVersion(2);
        mRenderer = new MainGLRenderer(context,width,height);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    public void setActivity(MainActivity activity){
        mRenderer.setActivity(activity);
    }

    public void onPause(){
        super.onPause();
        mRenderer.onPause();
    }

    public void onResume(){
        super.onResume();
        mRenderer.onResume();
    }

    public boolean onTouchEvent(MotionEvent event){
        mRenderer.onTouchEvent(event);
        return true;
    }
}