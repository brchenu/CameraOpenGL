package com.example.cameraopengl.globjects;

import android.opengl.GLES20;

import com.example.cameraopengl.GLHelpers;
import com.example.cameraopengl.GLTextureProgram;
import com.example.cameraopengl.MeshQuad;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRectangleTexture {

    private int VERTEX_NB_COMPONENTS = 3;
    private int UV_NB_COMPONENTS = 2;

    private final int FLOAT_SIZE = 4;
    private final int vertexStride = VERTEX_NB_COMPONENTS * FLOAT_SIZE;

    private GLTextureProgram textureProgram;
    private MeshQuad meshQuad;

    public GLRectangleTexture(GLTextureProgram textureProgram, MeshQuad meshQuad) {
        this.textureProgram = textureProgram;
        this.meshQuad = meshQuad;
    }

    public void draw(float[] mvpMatrix, int textureId) {
        // Activate the glProgram in the current OpenGL ES context
        GLES20.glUseProgram(textureProgram.getProgram());

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // Tell the texture uniform sampler to use this texture
        // in the shader by binding to the texture unit 0
        GLES20.glUniform1i(textureProgram.getLocation("u_texture"), 0);

        // Set vertex data
        GLES20.glEnableVertexAttribArray(textureProgram.getLocation("a_position"));
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, meshQuad.buffers[0]);
        GLES20.glVertexAttribPointer(textureProgram.getLocation("a_position"), VERTEX_NB_COMPONENTS, GLES20.GL_FLOAT, false, vertexStride, 0);

        // Set uv data
        GLES20.glEnableVertexAttribArray(textureProgram.getLocation("a_uv"));
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, meshQuad.buffers[1]);
        GLES20.glVertexAttribPointer(textureProgram.getLocation("a_uv"), UV_NB_COMPONENTS, GLES20.GL_FLOAT, false, UV_NB_COMPONENTS * FLOAT_SIZE, 0);

        GLES20.glUniformMatrix4fv(textureProgram.getLocation("u_mvp_matrix"), 1, false , mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, meshQuad.vertexCount);
    }
}