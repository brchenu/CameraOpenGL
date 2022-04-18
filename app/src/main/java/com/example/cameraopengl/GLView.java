package com.example.cameraopengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

public class GLView extends GLSurfaceView {
    GLRenderer glRenderer;

    public GLView(Context context) {
        super (context);

        glRenderer = new GLRenderer(context,this);
        setEGLContextClientVersion(2);
        setRenderer(glRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void surfaceCreated ( SurfaceHolder holder ) {
        super.surfaceCreated ( holder );
    }

    public void surfaceDestroyed ( SurfaceHolder holder ) {
        super.surfaceDestroyed ( holder );
    }

    public void surfaceChanged ( SurfaceHolder holder, int format, int w, int h ) {
        super.surfaceChanged ( holder, format, w, h );
    }

    public SurfaceTexture getRendererSurfaceTexture() {
        return glRenderer.getSurfaceTexture();
    }

    public void setSurfaceCallback(GLRenderer.SurfaceCallback surfaceCallback) {
        glRenderer.setSurfaceCreateCallback(surfaceCallback);
    }
}
