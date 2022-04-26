package com.example.cameraopengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.cameraopengl.globjects.GLCameraTexture;
import com.example.cameraopengl.globjects.GLCylinder;
import com.example.cameraopengl.globjects.GLDisc;
import com.example.cameraopengl.globjects.GLLine;
import com.example.cameraopengl.globjects.GLModel;
import com.example.cameraopengl.globjects.GLRectangle;
import com.example.cameraopengl.globjects.GLRectangleTexture;

import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private GLModel glModel;

    private GLDisc glDisc;
    private GLLine glLine;
    private GLCylinder glCylinder;
    private GLRectangle glRectangle;
    private GLRectangleTexture glRectangleTexture;
    private GLCameraTexture glCameraTexture;

    private float[] modelMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f };

    private float[] viewMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f };

    private float[] mvpMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] projectionViewMatrix = new float[16];

    private GLView glView;

    private SurfaceCallback surfaceCallback;

    private int loadedTexture;

    public GLRenderer(Context context, GLView view) {
        this.context = context;
        this.glView = view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        GLES20.glClearColor ( 1.0f, 1.0f, 0.0f, 1.0f );

        loadedTexture = GLHelpers.loadTexture(context, R.drawable.wood_icon);

        glRectangleTexture = new GLRectangleTexture();

        glModel = new GLModel(context);

        glCylinder = new GLCylinder();

        glLine = new GLLine();
        glLine.setVertices(0f, 0f, 0f, 3f, 0f, 0f);
        glLine.setColor(.8f, .2f, .2f, 1.0f);

        glDisc = new GLDisc();

        glRectangle = new GLRectangle();
        glRectangle.setColor(.8f, .8f, 0f, 0.5f);

        glCameraTexture = new GLCameraTexture(glView);
        glCameraTexture.initProgram();

        surfaceCallback.onGLSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged (GL10 unused, int width, int height ) {
        GLES20.glViewport( 0, 0, width, height);

        float ratio = (float) width / height;

        // Translate and rotate objects
        Matrix.translateM(modelMatrix, 0, 0, 0, -1f);
        Matrix.rotateM(modelMatrix, 0, 5, 0, 1, 0);

        // Create the MVP matrix
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0.1f, 100f);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -1.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(projectionViewMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionViewMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void onDrawFrame ( GL10 unused ) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        glCameraTexture.draw();

        glLine.draw(mvpMatrix);
        glDisc.draw(mvpMatrix);
        glRectangle.draw(mvpMatrix);
        glRectangleTexture.draw(mvpMatrix, loadedTexture);
        glCylinder.draw(mvpMatrix);

        //glModel.draw(finalMatrix);
    }

    public SurfaceTexture getSurfaceTexture() {
        return glCameraTexture.getSurfaceTexture();
    }

    public interface SurfaceCallback {
        void onGLSurfaceCreated();
    }

    public void setSurfaceCreateCallback(SurfaceCallback surfaceCallback) {
        this.surfaceCallback = surfaceCallback;
    }
}