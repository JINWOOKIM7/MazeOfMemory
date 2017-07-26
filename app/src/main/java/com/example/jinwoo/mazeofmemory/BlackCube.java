package com.example.jinwoo.mazeofmemory;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class BlackCube {

    private static float mVertices[], mColors[], mNormals[];
    private static short mIndices[];
    private FloatBuffer mVertexBuffer, mColorBuffer, mNormalBuffer;
    private ShortBuffer mDrawListBuffer;
    private int mProgram;
    private int mPositionHandle, mColorHandle, mNormalHandle;
    private int mMtxMVPHandle, mMtxMVHandle;
    private float mWidth = 0, mHeight = 0, mDepth = 0;

    public BlackCube(int program) {
        mProgram = program;
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        mMtxMVPHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mMtxMVHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");

        setupBuffer();
    }

    public void setupBuffer() {
        mWidth = mHeight = mDepth = 1.0f;

        mVertices = new float[] {   // in counterclockwise order
                -mWidth / 2, mHeight / 4, -mDepth / 2,
                -mWidth / 2, -mHeight / 4, -mDepth / 2,
                mWidth / 2, -mHeight / 4, -mDepth / 2,
                mWidth / 2, mHeight / 4, -mDepth / 2,
                mWidth / 2, mHeight / 4, mDepth / 2,
                mWidth / 2, -mHeight / 4, mDepth / 2,
                -mWidth / 2, -mHeight / 4, mDepth / 2,
                -mWidth / 2, mHeight / 4, mDepth / 2,
        };

        ByteBuffer buffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = buffer.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        mIndices = new short[] { 0, 3, 2, 0, 2, 1,
                2, 3, 4, 2, 4, 5,
                1, 6, 7, 1, 7, 0,
                3, 0, 7, 3, 7, 4,
                5, 6, 1, 5, 1, 2,
                4, 7, 6, 4, 6, 5 };

        ByteBuffer indexbuffer = ByteBuffer.allocateDirect(mIndices.length * 2);
        indexbuffer.order(ByteOrder.nativeOrder());
        mDrawListBuffer = indexbuffer.asShortBuffer();
        mDrawListBuffer.put(mIndices);
        mDrawListBuffer.position(0);

        mColors = new float [] {
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

        };

        ByteBuffer colorbuffer = ByteBuffer.allocateDirect(mColors.length * 4);
        colorbuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = colorbuffer.asFloatBuffer();
        mColorBuffer.put(mColors);
        mColorBuffer.position(0);

        mNormals = new float[] {
                -0.57735f,  0.57735f, -0.57735f,    // sqrt(3) / 3
                -0.57735f, -0.57735f, -0.57735f,
                0.57735f, -0.57735f, -0.57735f,
                0.57735f,  0.57735f, -0.57735f,
                0.57735f,  0.57735f,  0.57735f,
                0.57735f, -0.57735f,  0.57735f,
                -0.57735f, -0.57735f,  0.57735f,
                -0.57735f,  0.57735f,  0.57735f
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

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, mNormalBuffer);

        GLES20.glUniformMatrix4fv(mMtxMVPHandle, 1, false, mtxMVP, 0);
        GLES20.glUniformMatrix4fv(mMtxMVHandle, 1, false, mtxMV, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
    }
}
