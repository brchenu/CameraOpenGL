package com.example.cameraopengl.globjects;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.cameraopengl.GLHelpers;
import com.example.cameraopengl.GLRenderer;
import com.example.cameraopengl.GLView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLCameraTexture {
    private final String vertexShaderCode =
            "attribute vec2 vPosition;\n" +
            "attribute vec2 vTexCoord;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  texCoord = vTexCoord;\n" +
            "  gl_Position = vec4 (vPosition.x, vPosition.y, 0.0, 1.0);\n" +
            "}";

    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture,texCoord);\n" +
            "}";

    private int[] textureIds;
    private int program;

    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;

    private int locationPosition;
    private int locationTexCoord;
    private int locationTexture;

    public static SurfaceTexture mSTexture;

    private boolean mUpdateST = false;

    private GLView glSurfaceView;

    private float[] vertices = {
            -1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f  };

    private float[] texCoord = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f };

    public GLCameraTexture(GLView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;

        initTextures();

        mSTexture = new SurfaceTexture(textureIds[0]);
        mSTexture.setOnFrameAvailableListener(this::onFrameAvailable);
    }

    public void initProgram() {
        program = GLHelpers.loadShader(vertexShaderCode, fragmentShaderCode);

        vertexBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoordBuffer.put(texCoord);
        texCoordBuffer.position(0);

        locationPosition = GLES20.glGetAttribLocation(program, "vPosition");
        locationTexCoord = GLES20.glGetAttribLocation(program, "vTexCoord");
        locationTexture = GLES20.glGetUniformLocation(program, "sTexture");
    }

    private void initTextures() {
        textureIds = new int[1];
        GLES20.glGenTextures (1, textureIds, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
    }

    public synchronized void onFrameAvailable(SurfaceTexture st) {
        mUpdateST = true;
        glSurfaceView.requestRender();
    }

    public void draw() {
        synchronized(this) {
            if (mUpdateST) {
                mSTexture.updateTexImage();
                mUpdateST = false;
            }
        }

        GLES20.glUseProgram(program);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES20.glUniform1i(locationTexture, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glEnableVertexAttribArray(locationPosition);
        GLES20.glVertexAttribPointer(locationPosition, 2, GLES20.GL_FLOAT, false, 4 * 2, vertexBuffer);
        GLES20.glEnableVertexAttribArray(locationTexCoord);
        GLES20.glVertexAttribPointer(locationTexCoord, 2, GLES20.GL_FLOAT, false, 4 * 2, texCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSTexture;
    }
}

