package com.cube.cubeonjogl;

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
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static com.cube.cubeonjogl.R.drawable.box0;

public class OpenGLRenderer implements Renderer {
    private final static int POSITION_COUNT = 3;
    private Context context;
    private FloatBuffer vertexData;
    private ByteBuffer indexArray;
    private int aPositionLocation;
    private int uTextureUnitLocation;
    private int uMatrixLocation;
    private int programId;


    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMatrix = new float[16];

    private int texture;
    private float z1;
    private float z2;

    public OpenGLRenderer(Context context) {
        this.context = context;
    }
    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);
        createAndUseProgram();
        getLocations();
        prepareData();
        bindData();
        createViewMatrix();
        Matrix.setIdentityM(mModelMatrix, 0);
    }
    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        bindMatrix();
    }
    private void prepareData() {
        z1 = .5f;
        z2 = -.5f;
        float[] vertices = {
                // вершины куба
                 -.5f,  .5f,  z1,   // верхняя левая ближняя
                 .5f,  .5f,  z1, // верхняя правая ближняя
                 -.5f,  -.5f,  z1,    // нижняя левая ближняя
                 .5f,  -.5f,  z1,  // нижняя правая ближняя
                 -.5f,  .5f,  z2,   // верхняя левая дальняя
                 0.5f,  .5f, z2,   // верхняя правая дальняя
                 -.5f, -.5f, z2,    // нижняя левая дальняя
                 .5f, -.5f, z2 ,   // нижняя правая дальняя
        };
        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);
        indexArray =  ByteBuffer.allocateDirect(36)
                .put(new byte[] {
                // грани куба
                        // ближняя
                        1, 3, 0,
                        0, 3, 2,
                        // дальняя
                        4, 6, 5,
                        5, 6, 7,
                        // левая
                        0, 2, 4,
                        4, 2, 6,
                        // правая
                        5, 7, 1,
                        1, 7, 3,
                        // верхняя
                        5, 1, 4,
                        4, 1, 0,
                        // нижняя
                        6, 2, 7,
                        7, 2, 3
                });
        //Подача текстур
        indexArray.position(0);
        texture = TextureUtils.loadTextureCube(context, new int[]{
                box0, box0,
                box0, box0,
                box0, box0});
    }

    private void createAndUseProgram() {
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
    }

    private void getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit");
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    private void bindData() {
        // координаты вершин
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        // помещаем текстуру в target CUBE_MAP юнита 0
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
        // юнит текстуры
        glUniform1i(uTextureUnitLocation, 0);
    }
//Эта часть отвечает за отображение куба(растяжение, сжатие по осям)
    private void createProjectionMatrix(int width, int height) {
        float ratio = 1;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2;
        float far = 12;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        }
        else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }
//Эта часть отвечает за положение и направление камеры
    private void createViewMatrix() {
        // точка положения камеры
        float eyeX = 0;
        float eyeY = 2;
        float eyeZ = 10;
        // точка направления камеры
        float centerX = 0;
        float centerY = 0;
        float centerZ = -5;
        // up-вектор
        float upX = 0;
        float upY = 1;
        float upZ = 0;
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }
    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }
    long TIME = 10000L;
    long TIMED = 1000L;
    @Override
    public void onDrawFrame(GL10 arg0) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Matrix.setIdentityM(mModelMatrix, 0);
        setModelMatrix();
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
    }
    // вращение вокруг своей оси
    private void setModelMatrix() {
        float angle = (float)(SystemClock.uptimeMillis() % TIME) / TIME * 360;
        float distance = (float)(SystemClock.uptimeMillis() % TIMED) / TIMED * 8;
        Matrix.translateM(mModelMatrix, 0, 0, 0, distance);
        Matrix.rotateM(mModelMatrix, 0, angle, 1,0, 0);
        bindMatrix();
    }
}
