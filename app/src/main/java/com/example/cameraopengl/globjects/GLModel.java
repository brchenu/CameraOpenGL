package com.example.cameraopengl.globjects;

import android.content.Context;
import android.opengl.GLES20;

import com.example.cameraopengl.GLHelpers;
import com.example.cameraopengl.GLRenderer;
import com.example.cameraopengl.ObjLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLModel {
    private final String vertexShaderCode =
            "uniform mat4 mvp_matrix;" +
            "attribute vec4 position;" +
            "attribute vec4 normal;" +
            "varying vec4 vnormal;" +
            "void main() {" +
            "   gl_Position = mvp_matrix * position;" +
            "   vnormal = normal;" +
            "}";

    private final String fragmentShaderCode =
            "uniform vec4 color;" +
            "void main() {" +
            "  gl_FragColor = color;" +
            "}";

    private int glProgram;
    private int positionLocation;
    private int normalsLocation;
    private int colorLocation;
    private int mvpMatrixLocation;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalsBuffer;

    float color[] = { 0.5f, 0.5f, 0.5f, 1.0f };

    private int vertex_count;

    public GLModel(Context context) {
        ObjLoader objLoader = new ObjLoader(context, "blender_cube.obj");

        vertex_count = objLoader.positions.length / 3;

        // Initialize the buffers.
        vertexBuffer = ByteBuffer.allocateDirect(objLoader.positions.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(objLoader.positions).position(0);

        normalsBuffer = ByteBuffer.allocateDirect(objLoader.normals.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(objLoader.normals).position(0);

        glProgram = GLHelpers.loadShader(vertexShaderCode, fragmentShaderCode);

        positionLocation = GLES20.glGetAttribLocation(glProgram, "position");

        normalsLocation = GLES20.glGetAttribLocation(glProgram, "normal");

        colorLocation = GLES20.glGetUniformLocation(glProgram, "color");
        mvpMatrixLocation = GLES20.glGetUniformLocation(glProgram, "mvp_matrix");
    }

    public void draw(float[] mvpMatrix) {
        // Activate the glProgram in the current OpenGL ES context
        GLES20.glUseProgram(glProgram);

        GLES20.glEnableVertexAttribArray(positionLocation);
        GLES20.glVertexAttribPointer(positionLocation, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(normalsLocation);
        GLES20.glVertexAttribPointer(normalsLocation, 3, GLES20.GL_FLOAT, false, 3 * 4, normalsBuffer);

        GLES20.glUniform4fv(colorLocation, 1, color, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixLocation, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertex_count);
    }
}
