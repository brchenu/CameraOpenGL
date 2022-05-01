package com.example.cameraopengl.globjects;

import android.opengl.GLES20;

import com.example.cameraopengl.GLHelpers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRectangleTexture {

    private final String vertexShaderCode =
            "uniform mat4 mvp_matrix;" +
            "attribute vec4 position;" +
            "attribute vec2 uv;" +
            "varying vec2 vuv;" +
            "void main() {" +
            "   vuv = uv;" +
            "   gl_Position = mvp_matrix * position;" +
            "}";

    private final String fragmentShaderCode =
            "uniform sampler2D texture;" +
            "varying vec2 vuv;" +
            "void main() {" +
            "  gl_FragColor = texture2D(texture, vuv);" +
            "}";

    private int glProgram;

    private int positionAttributeLocation;
    private int uvAttributeLocation;

    private int mvpMatrixAttributeLocation;

    private int textureUniformLocation;

    private int VERTEX_NB_COMPONENTS = 3;
    private int UV_NB_COMPONENTS = 2;

    float vertices[] = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    private float[] textureUV = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    private final int FLOAT_SIZE = 4;
    private final int vertexStride = VERTEX_NB_COMPONENTS * FLOAT_SIZE;
    private final int vertexCount = vertices.length / VERTEX_NB_COMPONENTS;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;

    private int buffers[];

    public GLRectangleTexture(int program) {
        //glProgram = GLHelpers.loadShader(vertexShaderCode, fragmentShaderCode);
        glProgram = program;

        buffers = new int[2];
        GLES20.glGenBuffers(2, buffers, 0);

        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.length * FLOAT_SIZE, vertexBuffer, GLES20.GL_STATIC_DRAW);

        uvBuffer = ByteBuffer.allocateDirect(textureUV.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        uvBuffer.put(textureUV);
        uvBuffer.position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureUV.length * FLOAT_SIZE, uvBuffer, GLES20.GL_STATIC_DRAW);

        // Get location to vertex shader's member
        positionAttributeLocation = GLES20.glGetAttribLocation(glProgram, "position");
        uvAttributeLocation = GLES20.glGetAttribLocation(glProgram, "uv");

        // Get location to shader's transformation matrix
        mvpMatrixAttributeLocation = GLES20.glGetUniformLocation(glProgram, "mvp_matrix");
        textureUniformLocation = GLES20.glGetUniformLocation(glProgram, "texture");
    }

    public void setVertices(float coord[]) {
        vertexBuffer.put(coord);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }

    public void draw(float[] mvpMatrix, int textureId) {
        // Activate the glProgram in the current OpenGL ES context
        GLES20.glUseProgram(glProgram);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // Tell the texture uniform sampler to use this texture
        // in the shader by binding to the texture unit 0
        GLES20.glUniform1i(textureUniformLocation, 0);

        // Set vertex data
        GLES20.glEnableVertexAttribArray(positionAttributeLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glVertexAttribPointer(positionAttributeLocation, VERTEX_NB_COMPONENTS, GLES20.GL_FLOAT, false, vertexStride, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Set uv data
        GLES20.glEnableVertexAttribArray(uvAttributeLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glVertexAttribPointer(uvAttributeLocation, UV_NB_COMPONENTS, GLES20.GL_FLOAT, false, UV_NB_COMPONENTS * FLOAT_SIZE, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixAttributeLocation, 1, false , mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
    }
}