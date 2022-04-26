package com.example.cameraopengl.globjects;

import android.opengl.GLES20;

import com.example.cameraopengl.GLHelpers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLCylinder {
    private String vertexShaderCode =
            "attribute vec4 position;" +
            "attribute vec4 color;" +
            "uniform mat4 mvp_matrix;" +
            //"varying vec4 vcolor;" +
            "void main() {" +
            //"   vcolor = color;" +
            "   gl_Position = mvp_matrix * position;" +
            "}";

    private String fragmentShaderCode =
            //"varying vec4 vcolor;" +
            "void main() {" +
            //"   gl_FragColor = vcolor;" +
            "   gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);" +
            "}";

    private float colors[] = {
         1.0f,  0.0f,  0.0f,  1.0f,    // red
         0.0f,  1.0f,  0.0f,  1.0f,    // green
         0.0f,  0.0f,  1.0f,  1.0f     // blue
    };

    private float radius = 2;
    private float height = 2;

    private int steps = 15;
    private double angleSteps = 2 * Math.PI / steps;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private final int FLOAT_SIZE = 4;
    private final int VERTEX_NB_COMPONENTS = 3;
    private final int VERTEX_COUNT = ((steps + 1) * 2); // 2 is the number of vertex circle on the cylinder
    //private final int VERTEX_COUNT = ((steps + 1 + 1)); // 2 is the number of vertex circle on the cylinder
    private final int BUFFER_SIZE = VERTEX_COUNT * VERTEX_NB_COMPONENTS;

    private int buffers[];

    private int glProgram;
    private int positionAttributeLocation;
    private int colorAttributeLocation;
    private int mvpMatrixUniformLocation;

    public void createCylinder() {

        vertexBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (int i = 0; i <= steps; i++) {
            float x = (float) (radius * Math.cos(angleSteps * i));
            float y = (float) (-radius * Math.sin(angleSteps * i));

            vertexBuffer.put(new float[] {x, y, 0.0f});
            vertexBuffer.put(new float[] {x, y, height});
        }
        vertexBuffer.position(0);
    }

    public GLCylinder() {
        glProgram = GLHelpers.loadShader(vertexShaderCode, fragmentShaderCode);

        createCylinder();

        buffers = new int[2];
        GLES20.glGenBuffers(2, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BUFFER_SIZE * FLOAT_SIZE, vertexBuffer, GLES20.GL_STATIC_DRAW);

        /*colorBuffer = ByteBuffer.allocateDirect(colors.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, colors.length * FLOAT_SIZE, colorBuffer, GLES20.GL_STATIC_DRAW);*/

        positionAttributeLocation = GLES20.glGetAttribLocation(glProgram, "position");
        colorAttributeLocation = GLES20.glGetAttribLocation(glProgram, "color");
        mvpMatrixUniformLocation = GLES20.glGetUniformLocation(glProgram, "mvp_matrix");
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(glProgram);

        GLES20.glEnableVertexAttribArray(positionAttributeLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glVertexAttribPointer(positionAttributeLocation, VERTEX_NB_COMPONENTS, GLES20.GL_FLOAT, false, VERTEX_NB_COMPONENTS * FLOAT_SIZE, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        /*GLES20.glEnableVertexAttribArray(colorAttributeLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glVertexAttribPointer(colorAttributeLocation, 4, GLES20.GL_FLOAT, false, 4 * FLOAT_SIZE, 0);*/

        GLES20.glUniformMatrix4fv(mvpMatrixUniformLocation, 1, false , mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
    }
}
