package com.cube.cubetest;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class OpenGLRenderer implements Renderer {

    private final static int POSITION_COUNT = 3;
    private static final int TEXTURE_COUNT = 2;
    private static final int STRIDE = (POSITION_COUNT
            + TEXTURE_COUNT) * 4;

    private Context context;

    private FloatBuffer vertexData;

    private int aPositionLocation;
    private int aTextureLocation;
    private int uTextureUnitLocation;
    private int uMatrixLocation;

    private int programId;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private int texture;
    private int textureBackground;
    private int textureVibe;
    private int textureVibe1;


    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0.2f, 0.2f, 0.4f, 1f);
        glEnable(GL_DEPTH_TEST);

        createAndUseProgram();
        getLocations();
        prepareData();
        bindData();
        createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        bindMatrix();
    }

    private void prepareData() {
        float x = 0.3f;
        float y = 0.3f;
        float z = 0.3f;
        float[] vertices = {

                //front
                -x,  y, z,   0, 0,
                -x, -y, z,   0, 1,
                x,  y, z,   1, 0,
                x, -y, z,   1, 1,
                //back
                -x,  y, -z,   0, 0,
                -x, -y, -z,   0, 1,
                x,  y, -z,   1, 0,
                x, -y, -z,   1, 1,
                //right
                x,  y, -z,   0, 0,
                x, -y, -z,   0, 1,
                x,  y, z,   1, 0,
                x, -y, z,   1, 1,
                //left
                -x,  y, -z,   0, 0,
                -x, -y, -z,   0, 1,
                -x,  y, z,   1, 0,
                -x, -y, z,   1, 1,
                //top
                x,  y, z,   0, 0,
                -x, y, z,   0, 1,
                x,  y, -z,   1, 0,
                -x, y, -z,   1, 1,
                //under
                x,  -y, z,   0, 0,
                -x, -y, z,   0, 1,
                x,  -y, -z,   1, 0,
                -x, -y, -z,   1, 1,
                //ground
                2.3f, -1, -10,   0, 0,
                -2.3f, -1, -10,  0, 1,
                2.3f, -1, 10,    1, 0,
                -2.3f, -1, 10,   1, 1,
                //rightGround
                4f, -4, -10,     0, 1,
                4f, -4, 10,      1, 1,
                4, 4, -10,       0, 0,
                4, 4, 10,        1, 0,
                //leftGround
                -4f, -4, -10,     0, 1,
                -4f, -4, 10,      1, 1,
                -4, 4, -10,       0, 0,
                -4, 4, 10,        1, 0,
                //top
                -4f, 4, 10,      0, 1,
                4f, 4, 10,      1, 1,
                -4f, 4, -10,     0, 0,
                4f, 4, -10,     1, 0,
                //background
                -6, -6, -10,     0, 1,
                6, -6, -10,      1, 1,
                -6, 6, -10,      0, 0,
                6, 6, -10,       1, 0,

        };

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);

        texture = TextureUtils.loadTexture(context, R.drawable.cube);
        textureBackground = TextureUtils.loadTexture(context, R.drawable.road);
        textureVibe = TextureUtils.loadTexture(context, R.drawable.right_left);
        textureVibe1 = TextureUtils.loadTexture(context, R.drawable.neon);
}

    private void createAndUseProgram() {
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
    }

    private void getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        aTextureLocation = glGetAttribLocation(programId, "a_Texture");
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit");
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    private void bindData() {
        // координаты вершин
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // координаты текстур
        vertexData.position(POSITION_COUNT);
        glVertexAttribPointer(aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aTextureLocation);

        // помещаем текстуру в target 2D юнита 0
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);

        //помещаем текстуру_бэкграунда в target 2D юнита 0
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureBackground);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureVibe);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureVibe1);

        // юнит текстуры
        glUniform1i(uTextureUnitLocation, 0);
    }

    private void createProjectionMatrix(int width, int height) {
        float ratio = 1;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2;
        float far = 20;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private void createViewMatrix() {
        // точка положения камеры
        float eyeX = 0;
        float eyeY = 1;
        float eyeZ = 7;

        // точка направления камеры
        float centerX = 0;
        float centerY = 0;
        float centerZ = -4;

        // up-вектор
        float upX = 0;
        float upY = 1;
        float upZ = 0;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawCube();

        glBindTexture(GL_TEXTURE_2D, textureBackground);
        glDrawArrays(GL_TRIANGLE_STRIP,24,4 );
        glBindTexture(GL_TEXTURE_2D, textureVibe);
        glDrawArrays(GL_TRIANGLE_STRIP,28,4 );
        glDrawArrays(GL_TRIANGLE_STRIP,32,4 );
        glDrawArrays(GL_TRIANGLE_STRIP,36,4 );
        glBindTexture(GL_TEXTURE_2D, textureVibe1);
        glDrawArrays(GL_TRIANGLE_STRIP, 40,4);
    }
    private void drawCube() {
        glBindTexture(GL_TEXTURE_2D, texture);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 4, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 8, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 12, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 16, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 20, 4);
    }
}