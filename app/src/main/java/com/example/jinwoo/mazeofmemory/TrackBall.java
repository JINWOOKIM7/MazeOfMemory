package com.example.jinwoo.mazeofmemory;
import android.opengl.Matrix;

public class TrackBall {

    private int mWidth, mHeight;
    private double[] mLastPos;

    // a Quaternion
    private double mScalar;
    private double [] mVector;

    public float [] rotationMatrix;

    public TrackBall() {
        initialize();
        return;
    }

    void initialize() {
        mScalar = 1.0f;
        mVector = new double[] { 0.0, 0.0, 0.0 };
        rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix, 0);

        mLastPos = new double[3];
        return;
    }

    void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
        return;
    }

    void project(int xi, int yi, double [] vec) {
        // project x, y onto a hemisphere centered within width, height
        vec[0] = ( 2.0*xi - mWidth ) / (double)mWidth;
        vec[1] = ( mHeight - 2.0*yi ) / (double)mHeight;
        double d = Math.sqrt( vec[0]*vec[0] + vec[1]*vec[1] );
        vec[2] = Math.cos( Math.PI * 0.5 * ( (d<1.0)? d : 1.0 ) );

        // normalize
        normalize(vec);
        return;
    }

    void start(int xi, int yi) {
        project(xi, yi, mLastPos);
        return;
    }

    void end(int xi, int yi) {
        double [] currPos = new double[3];
        double [] diff = new double[3];

        project(xi, yi, currPos);

        diff[0] = currPos[0] - mLastPos[0];
        diff[1] = currPos[1] - mLastPos[1];
        diff[2] = currPos[2] - mLastPos[2];

        if (diff[0] != 0 || diff[1] != 0 || diff[2] != 0) {
            double angle = Math.PI * 0.5 * Math.sqrt(diff[0]*diff[0] + diff[1]*diff[1] + diff[2]*diff[2]);
            double [] axis = new double[3];
            crossProduct(currPos, mLastPos, axis);
            normalize(axis);

            // create a quaternion
            double s2 = Math.sin(angle * 0.5);
            double [] v2 = new double[] { s2*axis[0], s2*axis[1], s2*axis[2] };
            s2 = Math.cos(angle * 0.5);

            // quaternions update -- multiplication of quaternions
            double s1 = mScalar;
            double [] v1 = new double[]{ mVector[0], mVector[1], mVector[2] };
            double [] v3 = new double[3];
            crossProduct(v1, v2, v3);
            mScalar = ( s1 * s2 ) - dotProduct( v1, v2 );
            mVector[0] = s1*v2[0] + s2*v1[0] + v3[0];
            mVector[1] = s1*v2[1] + s2*v1[1] + v3[1];
            mVector[2] = s1*v2[2] + s2*v1[2] + v3[2];

            // normalize the quaternion
            double det = 1.0 / Math.sqrt(mScalar*mScalar + mVector[0]*mVector[0] + mVector[1]*mVector[1] + mVector[2]*mVector[2]);
            mScalar *= det;
            mVector[0] *= det;
            mVector[1] *= det;
            mVector[2] *= det;

            // rotation with quaternions
            // P' = quat * P * quat^-1
            // M = { { 1-2b^2-2c^2, 2ab-2sc,     2ac+2sb },
            //		 { 2ab+2sc,     1-2a^2-2c^2, 2bc-2sa },
            //		 { 2ac-2sb,     2bc+2sa,     1-2a^2-2b^2 } };
            rotationMatrix[0] = 1.0f - 2.0f * (float)(mVector[1]*mVector[1] + mVector[2]*mVector[2]);
            rotationMatrix[1] =        2.0f * (float)(mVector[0]*mVector[1] - mScalar*mVector[2]);
            rotationMatrix[2] =        2.0f * (float)(mVector[2]*mVector[0] + mScalar*mVector[1]);
            //rotationMatrix[3] = 0.0f;

            rotationMatrix[4] =        2.0f * (float)(mVector[0]*mVector[1] + mScalar*mVector[2]);
            rotationMatrix[5] = 1.0f - 2.0f * (float)(mVector[2]*mVector[2] + mVector[0]*mVector[0]);
            rotationMatrix[6] =        2.0f * (float)(mVector[1]*mVector[2] - mScalar*mVector[0]);
            //rotationMatrix[7] = 0.0f;

            rotationMatrix[8] =         2.0f * (float)(mVector[2]*mVector[0] - mScalar*mVector[1]);
            rotationMatrix[9] =         2.0f * (float)(mVector[1]*mVector[2] + mScalar*mVector[0]);
            rotationMatrix[10] = 1.0f - 2.0f * (float)(mVector[0]*mVector[0] + mVector[1]*mVector[1]);
            //rotationMatrix[11] = 0.0f;

            //rotationMatrix[12] = rotationMatrix[13] = rotationMatrix[14] = 0.0f;
            //rotationMatrix[15] = 1.0f;

            mLastPos[0] = currPos[0];
            mLastPos[1] = currPos[1];
            mLastPos[2] = currPos[2];
        }
        return;
    }

    void normalize(double [] vec) {
        double det = 1.0 / Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
        vec[0] *= det;
        vec[1] *= det;
        vec[2] *= det;
        return;
    }

    double dotProduct(double [] av, double [] bv) {
        return (av[0]*bv[0] + av[1]*bv[1] + av[2]*bv[2]);
    }

    void crossProduct(double [] av, double [] bv, double [] cv) {
        cv[0] = av[1]*bv[2] - av[2]*bv[1];
        cv[1] = av[2]*bv[0] - av[0]*bv[2];
        cv[2] = av[0]*bv[1] - av[1]*bv[0];
        return;
    }
}
