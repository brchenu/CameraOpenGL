package com.example.cameraopengl.globjects;

import android.opengl.GLES20;

import com.example.cameraopengl.GLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRectangleTexture {
    private String vertexShaderCode =
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

    private float[] vertices = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    private float[] textureUV = {
         1.0f, 1.0f,
         1.0f, 0.0f,
         0.0f, 1.0f,
         0.0f, 0.0f
    };

    private int glProgram;
    private int positionAttributeLocation;
    private int uvAttributeLocation;
    private int mvpMatrixUniformLocation;
    private int textureUniformLocation;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;

    private final int VERTEX_NB_COMPONENTS = 3;
    private final int VERTEX_STRIDE = VERTEX_NB_COMPONENTS * 4;
    private final int VERTEX_COUNT = vertices.length / VERTEX_NB_COMPONENTS;

    public GLRectangleTexture() {
        glProgram = GLRenderer.loadShader(vertexShaderCode, fragmentShaderCode);

        vertexBuffer = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        uvBuffer = ByteBuffer
                .allocateDirect(textureUV.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        uvBuffer.put(textureUV);
        uvBuffer.position(0);

        positionAttributeLocation = GLES20.glGetAttribLocation(glProgram, "position");
        uvAttributeLocation = GLES20.glGetAttribLocation(glProgram, "uv");

        GLES20.glGetUniformLocation(textureUniformLocation, "texture");
        GLES20.glGetUniformLocation(mvpMatrixUniformLocation, "mvp_matrix");
    }

    public void draw(int textureId, float[] mvpMatrix) {
        GLES20.glUseProgram(glProgram);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // Tell the texture uniform sampler to use this texture
        // in the shader by binding to the texture unit 0
        GLES20.glUniform1i(textureUniformLocation, 0);

        GLES20.glEnableVertexAttribArray(positionAttributeLocation);
        GLES20.glVertexAttribPointer(positionAttributeLocation, VERTEX_NB_COMPONENTS, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        GLES20.glEnableVertexAttribArray(uvAttributeLocation);
        GLES20.glVertexAttribPointer(uvAttributeLocation, 2, GLES20.GL_FLOAT, false, 2 * 4, uvBuffer);

        GLES20.glUniformMatrix4fv(mvpMatrixUniformLocation, 1, false , mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
    }
}
