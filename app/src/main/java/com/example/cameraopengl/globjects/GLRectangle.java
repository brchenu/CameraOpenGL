package com.example.cameraopengl.globjects;

import android.opengl.GLES20;

import com.example.cameraopengl.GLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRectangle {
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "   gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "uniform vec4 uColor;" +
            "void main() {" +
            "  gl_FragColor = uColor;" +
            "}";

    private int glProgram;
    private int positionLocation;
    private int colorLocation;
    private int mvpMatrixLocation;

    static int COORS_PER_VERTEX = 3;
    static float coords[] = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    private final int vertexStride = 3 * 4; //  number of float per vertex * 4 Bytes (float)
    private final int vertexCount = coords.length / COORS_PER_VERTEX;

    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };

    private FloatBuffer vertexBuffer;

    public GLRectangle() {
        // initialize vertex byte buffer for shape coordinates
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        glProgram = GLRenderer.loadShader(vertexShaderCode, fragmentShaderCode);

        // Get location to vertex shader's vPosition member
        positionLocation = GLES20.glGetAttribLocation(glProgram, "vPosition");

        // Get location to fragment shader's uColor member
        colorLocation = GLES20.glGetUniformLocation(glProgram, "uColor");

        // Get location to shader's transformation matrix
        mvpMatrixLocation = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
    }

    public void setVertices(float coord[]) {
        vertexBuffer.put(coord);
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
        GLES20.glVertexAttribPointer(positionLocation, COORS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionLocation); // TODO: once per frame ? need to be disable ?
        // Set color
        GLES20.glUniform4fv(colorLocation, 1, color, 0);
        // Set project matrix
        GLES20.glUniformMatrix4fv(mvpMatrixLocation, 1, false , mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP , 0, vertexCount);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES , 0, vertexCount);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES , 1, vertexCount);
    }
}
