package com.example.cameraopengl.globjects;

import android.opengl.GLES20;

import com.example.cameraopengl.GLHelpers;
import com.example.cameraopengl.GLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLLine {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 uColor;" +
            "void main() {" +
            "  gl_FragColor = uColor;" +
            "}";

    private int glProgram;
    private int positionLocation;
    private int colorLocation;
    private int mvpMatrixLocation;

    static int COORS_PER_VERTEX = 3;
    static float lineCoords[] ={
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
    };

    private final int vertexStride = 3 * 4; //  number of float per vertex * 4 Bytes (float)
    private final int vertexCount = lineCoords.length / COORS_PER_VERTEX;

    // rgba
    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };

    int buffers[];

    public GLLine() {
        buffers = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

        vertexBuffer = ByteBuffer.allocateDirect(lineCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 6 * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);

        glProgram = GLHelpers.loadShader(vertexShaderCode, fragmentShaderCode);

        // Get location to vertex shader's vPosition member
        positionLocation = GLES20.glGetAttribLocation(glProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionLocation); // TODO: once per frame ? need to be disable ?

        // Get location to fragment shader's uColor member
        colorLocation = GLES20.glGetUniformLocation(glProgram, "uColor");;

        // Get location to shader's transformation matrix
        mvpMatrixLocation = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
    }

    public void setVertices(float v0, float v1, float v2, float v3, float v4, float v5) {
        lineCoords[0] = v0;
        lineCoords[1] = v1;
        lineCoords[2] = v2;
        lineCoords[3] = v3;
        lineCoords[4] = v4;
        lineCoords[5] = v5;

        vertexBuffer.put(lineCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }

    public void setColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
    }

    public void draw(float[] mvpMatrix) {
        // Activate the glProgram in the current OpenGL ES context
        GLES20.glUseProgram(glProgram);

        // Set vertex data
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        //GLES20.glVertexAttribPointer(positionLocation, COORS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionLocation);
        GLES20.glVertexAttribPointer(positionLocation, COORS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glUniform4fv(colorLocation, 1, color, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixLocation, 1, false , mvpMatrix, 0);

        GLES20.glLineWidth(10.0f);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES , 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionLocation);
    }
}
