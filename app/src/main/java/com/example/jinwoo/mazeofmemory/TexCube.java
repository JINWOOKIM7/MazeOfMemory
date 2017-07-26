package com.example.jinwoo.mazeofmemory;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TexCube {

    private static float mVertices[], mUVs[], mNormals[];
    private FloatBuffer mVertexBuffer, mUVBuffer, mNormalBuffer;
    private int mProgram;
    private int mPositionHandle, mUVHandle, mNormalHandle;
    private int mMtxMVPHandle, mMtxMVHandle;
    private int mBitmapHandle, mSamplerLoc;
    private float mWidth = 0, mHeight = 0, mDepth = 0;

    public TexCube(int program) {
        mProgram = program;
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mUVHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoord");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        mMtxMVPHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mMtxMVHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
        mSamplerLoc = GLES20.glGetUniformLocation(mProgram, "sTexture");

        setupBuffer();
    }

    public void setBitmap(int handle, int width, int height) {
        mWidth = width;
        mHeight = height;
        mBitmapHandle = handle;
    }

    public void setupBuffer() {
        mWidth = mHeight = mDepth = 1.0f;

        mVertices = new float[] {   // in counterclockwise order
                mWidth/2,  mHeight/2, mDepth/2,
                -mWidth/2,  mHeight/2, mDepth/2,
                -mWidth/2, -mHeight/2, mDepth/2,
                mWidth/2,  mHeight/2, mDepth/2,
                -mWidth/2, -mHeight/2, mDepth/2,
                mWidth/2, -mHeight/2, mDepth/2,

                -mWidth/2, -mHeight/2, -mDepth/2,
                -mWidth/2,  mHeight/2, -mDepth/2,
                mWidth/2,  mHeight/2, -mDepth/2,
                -mWidth/2, -mHeight/2, -mDepth/2,
                mWidth/2,  mHeight/2, -mDepth/2,
                mWidth/2, -mHeight/2, -mDepth/2,

                mWidth/2, mHeight/2,  mDepth/2,
                mWidth/2, mHeight/2, -mDepth/2,
                -mWidth/2, mHeight/2, -mDepth/2,
                mWidth/2, mHeight/2,  mDepth/2,
                -mWidth/2, mHeight/2, -mDepth/2,
                -mWidth/2, mHeight/2,  mDepth/2,

                -mWidth/2, -mHeight/2, -mDepth/2,
                mWidth/2, -mHeight/2, -mDepth/2,
                mWidth/2, -mHeight/2,  mDepth/2,
                -mWidth/2, -mHeight/2, -mDepth/2,
                mWidth/2, -mHeight/2,  mDepth/2,
                -mWidth/2, -mHeight/2,  mDepth/2,

                mWidth/2,  mHeight/2,  mDepth/2,
                mWidth/2, -mHeight/2,  mDepth/2,
                mWidth/2, -mHeight/2, -mDepth/2,
                mWidth/2,  mHeight/2,  mDepth/2,
                mWidth/2, -mHeight/2, -mDepth/2,
                mWidth/2,  mHeight/2, -mDepth/2,

                -mWidth/2, -mHeight/2, -mDepth/2,
                -mWidth/2, -mHeight/2,  mDepth/2,
                -mWidth/2,  mHeight/2,  mDepth/2,
                -mWidth/2, -mHeight/2, -mDepth/2,
                -mWidth/2,  mHeight/2,  mDepth/2,
                -mWidth/2,  mHeight/2, -mDepth/2
        };

        ByteBuffer buffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = buffer.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        mUVs = new float [] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        ByteBuffer uvbuffer = ByteBuffer.allocateDirect(mUVs.length * 4);
        uvbuffer.order(ByteOrder.nativeOrder());
        mUVBuffer = uvbuffer.asFloatBuffer();
        mUVBuffer.put(mUVs);
        mUVBuffer.position(0);

        mNormals = new float[] {
                0.57735f,  0.57735f, 0.57735f,     // sqrt(3) / 3
                -0.57735f,  0.57735f, 0.57735f,
                -0.57735f, -0.57735f, 0.57735f,
                0.57735f,  0.57735f, 0.57735f,
                -0.57735f, -0.57735f, 0.57735f,
                0.57735f, -0.57735f, 0.57735f,

                -0.57735f, -0.57735f, -0.57735f,
                -0.57735f,  0.57735f, -0.57735f,
                0.57735f,  0.57735f, -0.57735f,
                -0.57735f, -0.57735f, -0.57735f,
                0.57735f,  0.57735f, -0.57735f,
                0.57735f, -0.57735f, -0.57735f,

                0.57735f, 0.57735f,  0.57735f,
                0.57735f, 0.57735f, -0.57735f,
                -0.57735f, 0.57735f, -0.57735f,
                0.57735f, 0.57735f,  0.57735f,
                -0.57735f, 0.57735f, -0.57735f,
                -0.57735f, 0.57735f,  0.57735f,

                -0.57735f, -0.57735f, -0.57735f,
                0.57735f, -0.57735f, -0.57735f,
                0.57735f, -0.57735f,  0.57735f,
                -0.57735f, -0.57735f, -0.57735f,
                0.57735f, -0.57735f,  0.57735f,
                -0.57735f, -0.57735f,  0.57735f,

                0.57735f,  0.57735f,  0.57735f,
                0.57735f, -0.57735f,  0.57735f,
                0.57735f, -0.57735f, -0.57735f,
                0.57735f,  0.57735f,  0.57735f,
                0.57735f, -0.57735f, -0.57735f,
                0.57735f,  0.57735f, -0.57735f,

                -0.57735f, -0.57735f, -0.57735f,
                -0.57735f, -0.57735f,  0.57735f,
                -0.57735f,  0.57735f,  0.57735f,
                -0.57735f, -0.57735f, -0.57735f,
                -0.57735f,  0.57735f,  0.57735f,
                -0.57735f,  0.57735f, -0.57735f
        };

        ByteBuffer normalbuffer = ByteBuffer.allocateDirect(mNormals.length * 4);
        normalbuffer.order(ByteOrder.nativeOrder());
        mNormalBuffer = normalbuffer.asFloatBuffer();
        mNormalBuffer.put(mNormals);
        mNormalBuffer.position(0);
    }

    public void draw(float[] mtxMVP, float[] mtxMV) {
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mUVHandle);
        GLES20.glVertexAttribPointer(mUVHandle, 2, GLES20.GL_FLOAT, false, 0, mUVBuffer);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, mNormalBuffer);

        GLES20.glUniformMatrix4fv(mMtxMVPHandle, 1, false, mtxMVP, 0);
        GLES20.glUniformMatrix4fv(mMtxMVHandle, 1, false, mtxMV, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBitmapHandle);
        GLES20.glUniform1i(mSamplerLoc, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mUVHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
    }
}
