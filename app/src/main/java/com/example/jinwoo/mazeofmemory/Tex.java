package com.example.jinwoo.mazeofmemory;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Tex {

    private static float mVertices[],mUVs[];
    private static short mIndices[];
    private FloatBuffer mVertexBuffer, mUVBuffer;
    private ShortBuffer mDrawListBuffer;
    private static int mProgram;
    private int mPositionHandle, mTexCoordHandle;
    private int mMtxHandle, mSamplerLoc;
    private int mBitmapHandle, mBitmapCount = 0;
    private Bitmap mBitmap[];
    private float mWidth = 0 , mHeight = 0;

    public Tex(int program){
        mProgram = program;
        mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram,"vTexCoord");
        mMtxHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgram,"sTexture");
    }

    public void setBitmap(int handle, float width, float height){
        mBitmapCount = 1;
        mWidth = width;
        mHeight = height;
        mBitmapHandle = handle;

        setupBuffer();
    }

    public void setupBuffer(){
        mVertices = new float[]{
                -mWidth/4,mHeight/4,0.0f,
                -mWidth/4,-mHeight/4,0.0f,
                mWidth/4,-mHeight/4,0.0f,
                mWidth/4,mHeight/4,0.0f,
        };

        ByteBuffer buffer = ByteBuffer.allocateDirect(mVertices.length*4);
        buffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = buffer.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        mIndices = new short[] {0, 1, 2, 0, 2, 3};    //반시계방향

        ByteBuffer indexbuffer = ByteBuffer.allocateDirect(mIndices.length*2);
        indexbuffer.order(ByteOrder.nativeOrder());
        mDrawListBuffer = indexbuffer.asShortBuffer();
        mDrawListBuffer.put(mIndices);
        mDrawListBuffer.position(0);

        mUVs = new float[]{
                0.0f,0.0f,
                0.0f,1.0f,
                1.0f,1.0f,
                1.0f,0.0f,
        };

        ByteBuffer uvbuffer = ByteBuffer.allocateDirect(mUVs.length*4);
        uvbuffer.order(ByteOrder.nativeOrder());
        mUVBuffer = uvbuffer.asFloatBuffer();
        mUVBuffer.put(mUVs);
        mUVBuffer.position(0);
    }

    public void draw(float[] mtx){
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mUVBuffer);

        GLES20.glUniformMatrix4fv(mMtxHandle, 1, false, mtx, 0);

        GLES20.glEnable(GLES20.GL_BLEND); // ->밥 주변이 검은색으로된다.
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBitmapHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
    }

    private int getImageHandle(Bitmap bitmap){
        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1,textureIDs,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_MIRRORED_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return textureIDs[0];
    }
}
