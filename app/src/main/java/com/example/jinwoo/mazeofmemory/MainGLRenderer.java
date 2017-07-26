package com.example.jinwoo.mazeofmemory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGLRenderer implements GLSurfaceView.Renderer {

    private MainActivity mainActivity;

    private final float[] mMtxProjection = new float[16];
    private final float[] mMtxView = new float[16];
    private final float[] mMtxModelView = new float[16];
    private final float[] mMtxProjectionAndView = new float[16];

    private final float [] mLightPos = new float[] { 0.0f, 1.0f, 0.0f, 1.0f };
    private final float [] mLightAmbient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
    private final float [] mLightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private final float [] mLightSpecular = new float [] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float mLightShininess = 10.0f;

    private static int mProgram = 0, mTexProgram = 0, mRenderProgram, mTexRenderProgram = 0;

    public static int mDeviceWidth = 0;
    public static int mDeviceHeight = 0;

    long mLastTime;

    private ColorCube [] mCube = new ColorCube[2];
    private TexCube [] mTexCube = new TexCube[2];
    private Ground [] mGround = new Ground[2];
    private Context mContext;

    private float mAngle = 0.0f;

    private LeftCube mLeftCube;
    private RightCube mRightCube;
    private GrayCube mGrayCube;
    private BlackCube mBlackCube;
    private Tex[] mleftTex =new Tex[4];
    private Tex[] mrightTex =new Tex[4];
    private Tex[] mCntTex =new Tex[6];
    private Tex mWinTex;
    private Tex chanceTex;

    private boolean turn = true;
    private boolean leftChance = true;
    private boolean rightChance = true;
    private boolean right=false;
    private boolean left=false;
    private boolean up=false;
    private boolean down=false;
    private boolean flag=false;
    private boolean win=false;
    private float []mLeftPos= new float[] { -3.0f, 0.25f, 3.0f };
    private float []mRightPos= new float[] { 3.0f, 0.25f, 3.0f };

    public MainGLRenderer(Context context, int width, int height) {
        mContext = context;
        mDeviceWidth = width;
        mDeviceHeight = height;
        mLastTime = System.currentTimeMillis() + 100;
    }

    public void onPause() {

    }

    public void onResume() {
        mLastTime = System.currentTimeMillis();
    }

    // Program #1
    public static final String mVertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 fColor;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   fColor = vColor;" +
                    "}";

    public static final String mFragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    "   gl_FragColor = fColor;" +
                    "}";

    // Program #2
    public static final String mTexVertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 vTexCoord;" +
                    "varying vec2 fTexCoord;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   fTexCoord = vTexCoord;" +
                    "}";

    public static final String mTexFragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 fTexCoord;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "   gl_FragColor = texture2D(sTexture, fTexCoord);" +
                    "}";

    // Program #3
    public static final String mRenderVertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "attribute vec3 vNormal;" +
                    "varying vec4 fColor;" +
                    "varying vec3 fPosition, fNormal;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   fPosition = vPosition.xyz;" +
                    "   fNormal = vNormal;" +
                    "   fColor = vColor;" +
                    "}";

    public static final String mRenderFragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 fColor;" +
                    "varying vec3 fPosition, fNormal;" +
                    "uniform mat4 uMVMatrix;" +
                    "uniform vec4 uLightPos, uLightAmbient, uLightDiffuse, uLightSpecular;" +
                    "uniform vec3 uAttenuation, uSpotDirection;" +
                    "uniform float uLightShininess, uSpotExponent;" +
                    "void main() {" +
                    "   vec3 L = normalize(uLightPos.xyz);" +
                    "   vec3 N = normalize(uMVMatrix * vec4(fNormal, 0.0)).xyz;" +
                    "   vec3 E = normalize(-(uMVMatrix * vec4(fPosition, 1.0)).xyz);" +
                    "   vec3 H = normalize(L + E);" +
                    "   vec4 ambient = uLightAmbient * fColor;" +
                    "   float kd = max( dot(L, N), 0.0 );" +
                    "   vec4 diffuse = kd * uLightDiffuse * fColor;" +
                    "   float ks = pow( max( dot(N, H), 0.0 ), uLightShininess );" +
                    "   vec4 specular = ks * uLightSpecular;" +
                    "   vec4 color = ambient + diffuse + specular;" +
                    "   color.a = 1.0;" +
                    "   gl_FragColor = color;" +
                    "}";

    // Program #4
    public static final String mTexRenderVertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec3 vNormal;" +
                    "attribute vec2 vTexCoord;" +
                    "varying vec2 fTexCoord;" +
                    "varying vec3 fPosition, fNormal;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "   fPosition = vPosition.xyz;" +
                    "   fNormal = vNormal;" +
                    "   fTexCoord = vTexCoord;" +
                    "}";

    public static final String mTexRenderFragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 fTexCoord;" +
                    "varying vec3 fPosition, fNormal;" +
                    "uniform mat4 uMVMatrix;" +
                    "uniform vec4 uLightPos, uLightAmbient, uLightDiffuse, uLightSpecular;" +
                    "uniform float uLightShininess;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "   vec3 L = normalize(uLightPos.xyz);" +
                    "   vec3 N = normalize(uMVMatrix * vec4(fNormal, 0.0)).xyz;" +
                    "   vec3 E = normalize(-(uMVMatrix * vec4(fPosition, 1.0)).xyz);" +
                    "   vec3 H = normalize(L + E);" +
                    "   vec4 ambient = uLightAmbient * texture2D(sTexture, fTexCoord);" +
                    "   float kd = max( dot(L, N), 0.0 );" +
                    "   vec4 diffuse = kd * uLightDiffuse * texture2D(sTexture, fTexCoord);" +
                    "   float ks = pow( max( dot(N, H), 0.0 ), uLightShininess );" +
                    "   vec4 specular = ks * uLightSpecular;" +
                    "   vec4 color = ambient + diffuse + specular;" +
                    "   color.a = texture2D(sTexture, fTexCoord).a;" +
                    "   gl_FragColor = color;" +
                    "}";

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Program #1 //색상만
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        // Program #2 //텍스쳐맵핑
        int texVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mTexVertexShaderCode);
        int texFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mTexFragmentShaderCode);
        mTexProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mTexProgram, texVertexShader);
        GLES20.glAttachShader(mTexProgram, texFragmentShader);
        GLES20.glLinkProgram(mTexProgram);
        GLES20.glUseProgram(mTexProgram);

        // Program #3 //렌더링
        int renderVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mRenderVertexShaderCode);
        int renderFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mRenderFragmentShaderCode);
        mRenderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mRenderProgram, renderVertexShader);
        GLES20.glAttachShader(mRenderProgram, renderFragmentShader);
        GLES20.glLinkProgram(mRenderProgram);
        GLES20.glUseProgram(mRenderProgram);

        int lightPosHandle = GLES20.glGetUniformLocation(mRenderProgram, "uLightPos");
        int lightAmbHandle = GLES20.glGetUniformLocation(mRenderProgram, "uLightAmbient");
        int lightDifHandle = GLES20.glGetUniformLocation(mRenderProgram, "uLightDiffuse");
        int lightSepHandle = GLES20.glGetUniformLocation(mRenderProgram, "uLightSpecular");
        int lightShiHandle = GLES20.glGetUniformLocation(mRenderProgram, "uLightShininess");
        GLES20.glUniform4fv(lightPosHandle, 1, mLightPos, 0);
        GLES20.glUniform4fv(lightAmbHandle, 1, mLightAmbient, 0);
        GLES20.glUniform4fv(lightDifHandle, 1, mLightDiffuse, 0);
        GLES20.glUniform4fv(lightSepHandle, 1, mLightSpecular, 0);
        GLES20.glUniform1f(lightShiHandle, mLightShininess);

        // Program #4 //방향성 + 텍스쳐
        int texRenderVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mTexRenderVertexShaderCode);
        int texRenderFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mTexRenderFragmentShaderCode);
        mTexRenderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mTexRenderProgram, texRenderVertexShader);
        GLES20.glAttachShader(mTexRenderProgram, texRenderFragmentShader);
        GLES20.glLinkProgram(mTexRenderProgram);
        GLES20.glUseProgram(mTexRenderProgram);

        int lightPosHandle2 = GLES20.glGetUniformLocation(mTexRenderProgram, "uLightPos");
        int lightAmbHandle2 = GLES20.glGetUniformLocation(mTexRenderProgram, "uLightAmbient");
        int lightDifHandle2 = GLES20.glGetUniformLocation(mTexRenderProgram, "uLightDiffuse");
        int lightSepHandle2 = GLES20.glGetUniformLocation(mTexRenderProgram, "uLightSpecular");
        int lightShiHandle2 = GLES20.glGetUniformLocation(mTexRenderProgram, "uLightShininess");
        GLES20.glUniform4fv(lightPosHandle2, 1, mLightPos, 0);
        GLES20.glUniform4fv(lightAmbHandle2, 1, mLightAmbient, 0);
        GLES20.glUniform4fv(lightDifHandle2, 1, mLightDiffuse, 0);
        GLES20.glUniform4fv(lightSepHandle2, 1, mLightSpecular, 0);
        GLES20.glUniform1f(lightShiHandle2, mLightShininess);

        // Create objects
        mLeftCube = new LeftCube(mRenderProgram);
        mRightCube = new RightCube(mRenderProgram);

        mGrayCube = new GrayCube(mProgram);
        mBlackCube = new BlackCube(mProgram);


        String [] mLFilenames = {"leftup","leftdown","leftleft","leftright" };
        String [] mRFilenames = {"rightup","rightdown","rightleft","rightright" };
        String [] mCNTFilenames = {"one","two","three","four","five","six" };

        Bitmap[] leftimage = new Bitmap[4];
        Bitmap[] rightimage = new Bitmap[4];
        Bitmap[] cntimage = new Bitmap[6];

        int[] leftimageHandle =new int[4];
        int[] rightimageHandle =new int[4];
        int[] cntimageHandle =new int[6];

        for (int i = 0; i < 4; i++) {
            leftimage[i] = BitmapFactory.decodeResource(mContext.getResources(),
                    mContext.getResources().getIdentifier("drawable/" + mLFilenames[i], null, mContext.getPackageName()));
            leftimageHandle[i] = getImageHandle(leftimage[i]);
        }
        for (int i = 0; i < 4; i++) {
            rightimage[i] = BitmapFactory.decodeResource(mContext.getResources(),
                    mContext.getResources().getIdentifier("drawable/" + mRFilenames[i], null, mContext.getPackageName()));
            rightimageHandle[i] = getImageHandle(rightimage[i]);
        }
        for(int i=0; i<4 ; i++) {
            mleftTex[i] = new Tex(mTexProgram);
            mrightTex[i] = new Tex(mTexProgram);
            //mTex[i].setBitmap(imageHandle, 1, 1);
            mleftTex[i].setBitmap(leftimageHandle[i],0.5f,0.5f);
            mrightTex[i].setBitmap(rightimageHandle[i],0.5f,0.5f);
        }
        for (int i = 0; i < 6; i++) {
            cntimage[i] = BitmapFactory.decodeResource(mContext.getResources(),
                    mContext.getResources().getIdentifier("drawable/" + mCNTFilenames[i], null, mContext.getPackageName()));
            cntimageHandle[i] = getImageHandle(cntimage[i]);
        }
        for (int i = 0; i < 6; i++) {
            mCntTex[i] = new Tex(mTexProgram);
            mCntTex[i].setBitmap(cntimageHandle[i],1.0f,1.0f);
        }

        Bitmap chance =  BitmapFactory.decodeResource(mContext.getResources(),
                mContext.getResources().getIdentifier("drawable/bobargb8888", null, mContext.getPackageName()));
        int chanceimageHandle = getImageHandle(chance);

        chanceTex = new Tex(mTexProgram);
        chanceTex.setBitmap(chanceimageHandle, 1.0f, 1.0f);

        Bitmap win =  BitmapFactory.decodeResource(mContext.getResources(),
                mContext.getResources().getIdentifier("drawable/win", null, mContext.getPackageName()));
        int winimageHandle = getImageHandle(win);

        mWinTex = new Tex(mTexProgram);
        mWinTex.setBitmap(winimageHandle, 1.0f, 1.0f);

        // Initialize OpenGL ES parameters
        GLES20.glPolygonOffset(1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, mDeviceWidth, mDeviceHeight);

        Matrix.setIdentityM(mMtxProjection, 0);
        Matrix.setIdentityM(mMtxView, 0);
        Matrix.setIdentityM(mMtxProjectionAndView, 0);

        //float aspect = mDeviceHeight / (float)mDeviceWidth;
        //Matrix.orthoM(mMtxProjection, 0, -1.0f, 1.0f, -aspect, aspect, -1000.0f, 1000.0f);
        Matrix.perspectiveM(mMtxProjection, 0, 90.0f, mDeviceWidth / (float) mDeviceHeight, 0.5f, 15.0f);
        Matrix.setLookAtM(mMtxView, 0, 0.0f, 4.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMtxProjectionAndView, 0, mMtxProjection, 0, mMtxView, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        long currentTime = System.currentTimeMillis();
        if (mLastTime > currentTime)
            return;
        long elapsedTime = currentTime - mLastTime;

        mAngle += elapsedTime * 0.1f;
        if (mAngle > 360.0f)
            mAngle -= 360.0f;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] translationMtx = new float[16];
        float[] MVPMtrix = new float[16];
        float[] modelMatrix = new float[16];
        float[] MVMatrix = new float[16];
        float posX = -3.0f, posZ = -3.0f;
        for(int col = 0; col<4; col++ ){
            for(int row=0; row<4; row++){
                Matrix.setIdentityM(translationMtx, 0);
                Matrix.translateM(translationMtx, 0, posX, -0.25f, posZ);
                Matrix.multiplyMM(MVPMtrix, 0, mMtxProjectionAndView, 0, translationMtx, 0);
                Matrix.multiplyMM(MVMatrix, 0, mMtxView, 0, translationMtx, 0);
                mGrayCube.draw(MVPMtrix,MVMatrix);
                posZ += 2.0f;
            }
            posX += 2.0f;
            posZ = -3.0f;
        }
        posX = -2.0f;
        posZ = -2.0f;
        for(int col = 0; col<3; col++ ){
            for(int row=0; row<3; row++){
                Matrix.setIdentityM(translationMtx, 0);
                Matrix.translateM(translationMtx, 0, posX, -0.25f, posZ);
                Matrix.multiplyMM(MVPMtrix, 0, mMtxProjectionAndView, 0, translationMtx, 0);
                Matrix.multiplyMM(MVMatrix, 0, mMtxView, 0, translationMtx, 0);
                mGrayCube.draw(MVPMtrix,MVMatrix);
                posZ += 2.0f;
            }
            posX += 2.0f;
            posZ = -2.0f;
        }

        posX = -2.0f;
        posZ = -3.0f;
        for(int col = 0; col<3; col++ ){
            for(int row=0; row<4; row++){
                Matrix.setIdentityM(translationMtx, 0);
                Matrix.translateM(translationMtx, 0, posX, -0.25f, posZ);
                Matrix.multiplyMM(MVPMtrix, 0, mMtxProjectionAndView, 0, translationMtx, 0);
                Matrix.multiplyMM(MVMatrix, 0, mMtxView, 0, translationMtx, 0);
                mBlackCube.draw(MVPMtrix,mMtxView);
                posZ += 2.0f;
            }
            posX += 2.0f;
            posZ = -3.0f;
        }
        posX = -3.0f;
        posZ = -2.0f;
        for(int col = 0; col<4; col++ ){
            for(int row=0; row<3; row++){
                Matrix.setIdentityM(translationMtx, 0);
                Matrix.translateM(translationMtx, 0, posX, -0.25f, posZ);
                Matrix.multiplyMM(MVPMtrix, 0, mMtxProjectionAndView, 0, translationMtx, 0);
                Matrix.multiplyMM(MVMatrix, 0, mMtxView, 0, translationMtx, 0);
                mBlackCube.draw(MVPMtrix,mMtxView);
                posZ += 2.0f;
            }
            posX += 2.0f;
            posZ = -2.0f;
        }


        Matrix.setIdentityM(translationMtx, 0);
        Matrix.translateM(translationMtx, 0, mLeftPos[0], mLeftPos[1], mLeftPos[2]);
        Matrix.multiplyMM(MVPMtrix, 0, mMtxProjectionAndView, 0, translationMtx, 0);
        Matrix.multiplyMM(MVMatrix, 0, mMtxView, 0, translationMtx, 0);
        mLeftCube.draw(MVPMtrix, mMtxView);

        Matrix.setIdentityM(translationMtx, 0);
        Matrix.translateM(translationMtx, 0, mRightPos[0], mRightPos[1], mRightPos[2]);
        Matrix.multiplyMM(MVPMtrix, 0, mMtxProjectionAndView, 0, translationMtx, 0);
        Matrix.multiplyMM(MVMatrix, 0, mMtxView, 0, translationMtx, 0);
        mRightCube.draw(MVPMtrix, mMtxView);

        float[] scaleMtx = new float[16];
        Matrix.setIdentityM(scaleMtx, 0);

        if(turn) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.0f, -0.5f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mleftTex[0].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.0f, -0.875f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mleftTex[1].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, -0.75f, -0.875f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mleftTex[2].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.75f, -0.875f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mleftTex[3].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mLastTime = currentTime;
        }
        else{
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.0f, -0.5f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mrightTex[0].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.0f, -0.875f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mrightTex[1].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, -0.75f, -0.875f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mrightTex[2].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.75f, -0.875f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 0.5f, 0.25f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mrightTex[3].draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mLastTime = currentTime;
        }
        if(leftChance) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, -0.75f, 0.5f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 1.0f, 1.0f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            chanceTex.draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
        if(rightChance) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.75f, 0.5f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 1.0f, 1.0f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            chanceTex.draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Matrix.setIdentityM(translationMtx, 0);
        Matrix.translateM(translationMtx, 0, 0.0f, 0.75f, 0.0f);
        Matrix.setIdentityM(scaleMtx, 0);
        Matrix.scaleM(scaleMtx, 0, 0.5f, 0.5f, 0.5f);
        Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
        switch (cnt) {
            case 6:
                mCntTex[5].draw(modelMatrix);
                break;
            case 5:
                mCntTex[4].draw(modelMatrix);
                break;
            case 4:
                mCntTex[3].draw(modelMatrix);
                break;
            case 3:
                mCntTex[2].draw(modelMatrix);
                break;
            case 2:
                mCntTex[1].draw(modelMatrix);
                break;
            case 1:
                mCntTex[0].draw(modelMatrix);
                break;

        }
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        if(win) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            Matrix.setIdentityM(translationMtx, 0);
            Matrix.translateM(translationMtx, 0, 0.0f, 0.0f, 0.0f);
            Matrix.setIdentityM(scaleMtx, 0);
            Matrix.scaleM(scaleMtx, 0, 1.0f, 1.0f, 1.0f);
            Matrix.multiplyMM(modelMatrix, 0, translationMtx, 0, scaleMtx, 0);
            mWinTex.draw(modelMatrix);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

    }

    public static int loadShader(int type, String shderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    public boolean onTouchEvent(MotionEvent event){

        final int x = (int)event.getX();
        final int y = (int)event.getY();
        final int action = event.getAction();
        switch(action&MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_UP:
                if(turn) {
                    selectTouchLeft(x, y);


                }
                else if(!turn) {
                    selectTouchRight(x, y);

                }
                break;
        }
        return true;
    }

    private int getImageHandle(Bitmap bitmap){
        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1, textureIDs, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return textureIDs[0];
    }

    int cnt = 3;

    private void selectTouchLeft(int x, int y){

        float changedX= 2.0f* x/(float)mDeviceWidth-1.0f;
        float changedY= 1.0f -2.0f *y/(float)mDeviceHeight;
        if(changedX <-0.5 && changedY <-0.75) {
            if(mLeftPos[0] == -3.0f)
                mLeftPos[0] = -3.0f;
            else {
                mLeftPos[0] -= 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = false;
                    cnt = 3;
                }
            }
            left = true;
        }
        else if(changedX>0.5f && changedY < -0.75) {
            if(mLeftPos[0] == 3.0f)
                mLeftPos[0] = 3.0f;
            else{
                mLeftPos[0] += 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = false;
                    cnt = 3;
                }
            }
            right =true;
        }
        else if(changedX > -0.25 && changedX < 0.25 && (changedY < -0.25 && changedY >= -0.75)) {
            if(mLeftPos[2] == -3.0f)
                mLeftPos[2] = -3.0f;
            else{
                mLeftPos[2] -= 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = false;
                    cnt = 3;
                }
            }
            up = true;
        }
        else if(changedX > -0.25 && changedX < 0.25 && (changedY < -0.75)) {
            if(mLeftPos[2] == 3.0f)
                mLeftPos[2] = 3.0f;
            else{
                mLeftPos[2] += 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = false;
                    cnt = 3;
                }
            }
            down = true;
        }
        else if(changedX > -0.9 && changedX < -0.5 && (changedY < 0.75 && changedY >= 0.25) && leftChance) {
            cnt += 3;
            leftChance = false;
        }

        if(win){
            if( (changedX > -0.5 && changedX < 0.5 ) && (changedY < 0.5 && changedY >= -0.5))
                win = false;
        }

        collsionLeft(mLeftPos[0], mLeftPos[2]);
        right = false;
        left = false;
        up = false;
        down = false;
    }
    private void selectTouchRight(int x, int y){
        float changedX= 2.0f* x/(float)mDeviceWidth-1.0f;
        float changedY= 1.0f -2.0f *y/(float)mDeviceHeight;
        if(changedX <-0.5 && changedY <-0.75) {
            if(mRightPos[0] == -3.0f)
                mRightPos[0] = -3.0f;
            else {
                mRightPos[0] -= 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = true;
                    cnt = 3;
                }
            }
            left = true;
        }
        else if(changedX>0.5f && changedY < -0.75) {
            if(mRightPos[0] == 3.0f) {
                mRightPos[0] = 3.0f;
                flag = false;
            }
            else{
                mRightPos[0] += 1.0f;
                flag = true;
                cnt--;
                if (cnt == 0) {
                    turn = true;
                    cnt = 3;
                }
            }
            right =true;
        }
        else if(changedX > -0.25 && changedX < 0.25 && (changedY < -0.25 && changedY >= -0.75)) {
            if(mRightPos[2] == -3.0f)
                mRightPos[2] = -3.0f;
            else{
                mRightPos[2] -= 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = true;
                    cnt = 3;
                }
            }
            up = true;
        }
        else if(changedX > -0.25 && changedX < 0.25 && (changedY < -0.75)) {
            if(mRightPos[2] == 3.0f)
                mRightPos[2] = 3.0f;
            else{
                mRightPos[2] += 1.0f;
                cnt--;
                if (cnt == 0) {
                    turn = true;
                    cnt = 3;
                }
            }
            down = true;
        }
        else if(changedX > 0.5 && changedX < 0.9 && (changedY < 0.75 && changedY >= 0.25) && rightChance) {
            cnt += 3;
            rightChance = false;
        }

        collsionRight(mRightPos[0], mRightPos[2]);
        right = false;
        left = false;
        up = false;
        down = false;
    }

    private void collsionLeft(float x, float z){

        if( x == -3.0f && z == -3.0f){
            win = true;
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            leftChance = true;
            rightChance = true;
            turn = true;
            cnt = 3;
        }

        if( (x == -2.0f && z == 1.0f && right) || (x == -3.0f && z == 1.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 0.0f && z == 3.0f && right) || (x == -1.0f && z == 3.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 0.0f && z == 2.0f && right) || (x == -1.0f && z == 2.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == 3.0f && right) || (x == 2.0f && z == 3.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 2.0f && z == 2.0f && right) || (x == 1.0f && z == 2.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
        }
        if( (x == 3.0f && z == 1.0f && right) || (x == 2.0f && z == 1.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == 0.0f && right) || (x == 2.0f && z == 0.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -1.0f && z == 1.0f && right) || (x == -2.0f && z == 1.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -1.0f && z == 0.0f && right) || (x == -2.0f && z == 0.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 0.0f && z == -2.0f && right) || (x == -1.0f && z == -2.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 1.0f && z == -1.0f && right) || (x == 0.0f && z == -1.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 1.0f && z == -2.0f && right) || (x == 0.0f && z == -2.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 1.0f && z == -3.0f && right) || (x == 0.0f && z == -3.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 2.0f && z == 0.0f && right) || (x == 1.0f && z == 0.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 2.0f && z == -1.0f && right) || (x == 1.0f && z == -1.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 2.0f && z == -2.0f && right) || (x == 1.0f && z == -2.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == -3.0f && right) || (x == 2.0f && z == -3.0f && left) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 1.0f && z == 3.0f && down) || (x == 1.0f && z == 2.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }

        if( (x == -1.0f && z == 2.0f && down) || (x == -1.0f && z == 1.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }
        if( (x == 0.0f && z == 2.0f && down) || (x == 0.0f && z == 1.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }

        if( (x == 2.0f && z == 2.0f && down) || (x == 2.0f && z == 1.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }
        if( (x == 3.0f && z == 2.0f && down) || (x == 3.0f && z == 1.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }

        if( (x == -3.0f && z == 0.0f && down) || (x == -3.0f && z == -1.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }
        if( (x == -3.0f && z == -1.0f && down) || (x == -3.0f && z == -2.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }
        if( (x == -2.0f && z == -1.0f && down) || (x == -2.0f && z == -2.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }
        if( (x == -2.0f && z == -2.0f && down) || (x == -2.0f && z == -3.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }

        if( (x == 2.0f && z == -1.0f && down) || (x == 2.0f && z == -2.0f && up) ) {
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            turn = false;
            cnt = 3;
            mainActivity.playSound();

        }
    }
    private void collsionRight(float x, float z){
        if( x == 3.0f && z == -3.0f){
            win = true;
            mLeftPos[0] = -3.0f;
            mLeftPos[2] = 3.0f;
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            leftChance = true;
            rightChance = true;
            turn = true;
            cnt = 3;
        }
        if( (x == -2.0f && z == 1.0f && right) || (x == -3.0f && z == 1.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();

        }

        if( (x == 0.0f && z == 3.0f && right) || (x == -1.0f && z == 3.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();

        }

        if( (x == 0.0f && z == 2.0f && right) || (x == -1.0f && z == 2.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();

        }


        if( (x == 3.0f && z == 3.0f && right && flag) || (x == 2.0f && z == 3.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 2.0f && z == 2.0f && right) || (x == 1.0f && z == 2.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == 1.0f && right) || (x == 2.0f && z == 1.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == 0.0f && right) || (x == 2.0f && z == 0.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -1.0f && z == 1.0f && right) || (x == -2.0f && z == 1.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -1.0f && z == 0.0f && right) || (x == -2.0f && z == 0.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 0.0f && z == -2.0f && right) || (x == -1.0f && z == -2.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 1.0f && z == -1.0f && right) || (x == 0.0f && z == -1.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 1.0f && z == -2.0f && right) || (x == 0.0f && z == -2.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 1.0f && z == -3.0f && right) || (x == 0.0f && z == -3.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 2.0f && z == 0.0f && right) || (x == 1.0f && z == 0.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 2.0f && z == -1.0f && right) || (x == 1.0f && z == -1.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
        }
        if( (x == 2.0f && z == -2.0f && right) || (x == 1.0f && z == -2.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == -3.0f && right) || (x == 2.0f && z == -3.0f && left) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 1.0f && z == 3.0f && down) || (x == 1.0f && z == 2.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == -1.0f && z == 2.0f && down) || (x == -1.0f && z == 1.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 0.0f && z == 2.0f && down) || (x == 0.0f && z == 1.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 2.0f && z == 2.0f && down) || (x == 2.0f && z == 1.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == 3.0f && z == 2.0f && down) || (x == 3.0f && z == 1.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == -3.0f && z == 0.0f && down) || (x == -3.0f && z == -1.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -3.0f && z == -1.0f && down) || (x == -3.0f && z == -2.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -2.0f && z == -1.0f && down) || (x == -2.0f && z == -2.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }
        if( (x == -2.0f && z == -2.0f && down) || (x == -2.0f && z == -3.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

        if( (x == 2.0f && z == -1.0f && down) || (x == 2.0f && z == -2.0f && up) ) {
            mRightPos[0] = 3.0f;
            mRightPos[2] = 3.0f;
            turn = true;
            cnt = 3;
            mainActivity.playSound();
        }

    }

    public void setActivity(MainActivity activity){
        mainActivity = activity;
    }

}