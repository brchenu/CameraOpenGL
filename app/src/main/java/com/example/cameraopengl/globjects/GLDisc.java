package com.example.cameraopengl.globjects;

import android.graphics.Point;
import android.opengl.GLES20;
import android.util.Log;

import com.example.cameraopengl.GLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GLDisc {
    private String vertexShaderCode =
            "uniform mat4 mvp_matrix;" +
            "attribute vec4 position;" +
            "void main() {" +
            "   gl_Position = mvp_matrix * position;" +
            "}";

    private final String fragmentShaderCode =
            "uniform vec4 color;" +
            "void main() {" +
            "  gl_FragColor = color;" +
            "}";

    private int glProgram;

    private FloatBuffer vertexBuffer;

    private int positionAttributeLocation;
    private int colorUniformLocation;
    private int mvpMatrixUniformLocation;

    private int steps = 50;
    private float stepAngle = (float) (2 * Math.PI) / steps;
    private float radius = 0.5f;

    private FloatBuffer vertices;

    private float[] color = {1.0f, 0.0f, 0.0f, 1.0f};

    private final int VERTEX_NB_COMPONENTS = 2;
    private final int VERTEX_STRIDE = VERTEX_NB_COMPONENTS * 4;
    private final int VERTEX_COUNT = ((steps + 2) * 2) / 2;
    private final int BUFFER_SIZE = VERTEX_COUNT * VERTEX_NB_COMPONENTS;

    public GLDisc() {
        vertices = FloatBuffer.allocate(BUFFER_SIZE);
        initVertex();

        Log.d("x666", "float_buffer: " + Arrays.toString(vertices.array()));

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices.array());
        vertexBuffer.position(0);

        glProgram = GLRenderer.loadShader(vertexShaderCode, fragmentShaderCode);

        positionAttributeLocation = GLES20.glGetAttribLocation(glProgram, "position");

        colorUniformLocation = GLES20.glGetUniformLocation(glProgram, "color");
        mvpMatrixUniformLocation = GLES20.glGetUniformLocation(glProgram, "mvp_matrix");
    }

    public void initVertex() {
        // origin
        vertices.put(0.0f);
        vertices.put(0.0f);
        for (int i = 0; i <= steps; i++) {
            float vertex_x = (float) (radius * Math.sin(stepAngle * i));
            float vertex_y = (float) (-radius * Math.cos(stepAngle * i));

            vertices.put(vertex_x);
            vertices.put(vertex_y);
        }
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(glProgram);

        GLES20.glEnableVertexAttribArray(positionAttributeLocation);
        GLES20.glVertexAttribPointer(positionAttributeLocation, VERTEX_NB_COMPONENTS, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        GLES20.glUniform4fv(colorUniformLocation, 1, color, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixUniformLocation, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);
    }
}