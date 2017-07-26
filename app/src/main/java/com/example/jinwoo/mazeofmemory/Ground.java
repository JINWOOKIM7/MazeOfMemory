package com.example.jinwoo.mazeofmemory;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Ground {

    private static float mVertices[], mColors[], mNormals[], mUVs[];
    private static short mIndices[];
    private FloatBuffer mVertexBuffer, mColorBuffer, mNormalBuffer, mUVBuffer;
    private ShortBuffer mDrawListBuffer;
    private int mProgram;
    private int mPositionHandle, mColorHandle, mNormalHandle, mUVHandle;
    private int mMtxMVPHandle, mMtxMVHandle;
    private int mBitmapHandle, mSamplerLoc;
    private float mWidth = 0, mHeight = 0, mDepth = 0;

    public Ground(int program) {
        mProgram = program;
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        mUVHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoord");
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
        mWidth = mDepth = 10.0f;
        mHeight = -1.0f;

        mVertices = new float[] {   // in counterclockwise order
                -mWidth*0.5f, mHeight, -mDepth*0.5f,
                -mWidth*0.5f, mHeight,  mDepth*0.5f,
                mWidth*0.5f, mHeight,  mDepth*0.5f,
                mWidth*0.5f, mHeight, -mDepth*0.5f,

                -mWidth*0.5f, mHeight, -mDepth*0.5f,
                -mWidth*0.5f, mHeight,  mDepth*0.5f,
                -mWidth*0.3f, mHeight, -mDepth*0.5f,
                -mWidth*0.3f, mHeight,  mDepth*0.5f,
                -mWidth*0.1f, mHeight, -mDepth*0.5f,
                -mWidth*0.1f, mHeight,  mDepth*0.5f,
                mWidth*0.1f, mHeight, -mDepth*0.5f,
                mWidth*0.1f, mHeight,  mDepth*0.5f,
                mWidth*0.3f, mHeight, -mDepth*0.5f,
                mWidth*0.3f, mHeight,  mDepth*0.5f,
                mWidth*0.5f, mHeight, -mDepth*0.5f,
                mWidth*0.5f, mHeight,  mDepth*0.5f,

                -mWidth*0.5f, mHeight, -mDepth*0.5f,
                mWidth*0.5f, mHeight, -mDepth*0.5f,
                -mWidth*0.5f, mHeight, -mDepth*0.3f,
                mWidth*0.5f, mHeight, -mDepth*0.3f,
                -mWidth*0.5f, mHeight, -mDepth*0.1f,
                mWidth*0.5f, mHeight, -mDepth*0.1f,
                -mWidth*0.5f, mHeight,  mDepth*0.1f,
                mWidth*0.5f, mHeight,  mDepth*0.1f,
                -mWidth*0.5f, mHeight,  mDepth*0.3f,
                mWidth*0.5f, mHeight,  mDepth*0.3f,
                -mWidth*0.5f, mHeight,  mDepth*0.5f,
                mWidth*0.5f, mHeight,  mDepth*0.5f
        };

        ByteBuffer buffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = buffer.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        mIndices = new short[] { 0, 1, 2, 0, 2, 3 };

        ByteBuffer indexbuffer = ByteBuffer.allocateDirect(mIndices.length * 2);
        indexbuffer.order(ByteOrder.nativeOrder());
        mDrawListBuffer = indexbuffer.asShortBuffer();
        mDrawListBuffer.put(mIndices);
        mDrawListBuffer.position(0);

        mColors = new float [] {
                0.8f, 0.8f, 0.8f, 1.0f,
                0.8f, 0.8f, 0.8f, 1.0f,
                0.8f, 0.8f, 0.8f, 1.0f,
                0.8f, 0.8f, 0.8f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        ByteBuffer colorbuffer = ByteBuffer.allocateDirect(mColors.length * 4);
        colorbuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = colorbuffer.asFloatBuffer();
        mColorBuffer.put(mColors);
        mColorBuffer.position(0);

        mNormals = new float[] {
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        };

        ByteBuffer normalbuffer = ByteBuffer.allocateDirect(mNormals.length * 4);
        normalbuffer.order(ByteOrder.nativeOrder());
        mNormalBuffer = normalbuffer.asFloatBuffer();
        mNormalBuffer.put(mNormals);
        mNormalBuffer.position(0);

        mUVs = new float [] {
                0.0f, 0.0f,
                0.0f, mDepth,
                mWidth, mDepth,
                mWidth, 0.0f,

                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,

                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f
        };

        ByteBuffer uvbuffer = ByteBuffer.allocateDirect(mUVs.length * 4);
        uvbuffer.order(ByteOrder.nativeOrder());
        mUVBuffer = uvbuffer.asFloatBuffer();
        mUVBuffer.put(mUVs);
        mUVBuffer.position(0);
    }

    public void draw(float[] mtxMVP, float[] mtxMV) {
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, mNormalBuffer);

        GLES20.glEnableVertexAttribArray(mUVHandle);
        GLES20.glVertexAttribPointer(mUVHandle, 2, GLES20.GL_FLOAT, false, 0, mUVBuffer);

        GLES20.glUniformMatrix4fv(mMtxMVPHandle, 1, false, mtxMVP, 0);
        GLES20.glUniformMatrix4fv(mMtxMVHandle, 1, false, mtxMV, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBitmapHandle);
        GLES20.glUniform1i(mSamplerLoc, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glLineWidth(2.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 4, 24);

        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
        GLES20.glDisableVertexAttribArray(mUVHandle);
    }
}
