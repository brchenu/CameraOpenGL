package com.example.cameraopengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MeshQuad {
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

    public int vertexCount = vertices.length / 3;

    private final int FLOAT_SIZE = 4;

    public int[] buffers;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;

    public MeshQuad() {
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
    }
}
