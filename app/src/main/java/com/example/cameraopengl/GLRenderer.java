package com.example.cameraopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.cameraopengl.globjects.GLCameraTexture;
import com.example.cameraopengl.globjects.GLDisc;
import com.example.cameraopengl.globjects.GLLine;
import com.example.cameraopengl.globjects.GLRectangle;
import com.example.cameraopengl.globjects.GLRectangleTexture;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private GLRectangleTexture glRectangleTexture;
    private GLDisc glDisc;
    private GLLine glLine;
    private GLRectangle glRectangle;
    private GLCameraTexture glCameraTexture;

    private float[] rect_matrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f };

    private float[] viewMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f };

    private float[] finalMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

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

        loadedTexture = loadTexture(context, R.drawable.wood_icon);

        glRectangleTexture = new GLRectangleTexture();

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

        // Translate and rotate the rectangle
        Matrix.translateM(rect_matrix, 0, 0, 0, -1f);
        Matrix.rotateM(rect_matrix, 0, 0, 0, 1, 0);

        // Create the MVP matrix
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0.1f, 100f);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -1.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, rect_matrix, 0);
    }

    @Override
    public void onDrawFrame ( GL10 unused ) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        glCameraTexture.draw();
        glLine.draw(finalMatrix);
        glDisc.draw(finalMatrix);
        glRectangle.draw(finalMatrix);
        glRectangleTexture.draw(loadedTexture, finalMatrix);
    }


    public static int loadShader (String vss, String fss) {
        int vshader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vshader, vss);
        GLES20.glCompileShader(vshader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(vshader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Shader", "Could not compile vshader");
            Log.v("Shader", "Could not compile vshader:"+GLES20.glGetShaderInfoLog(vshader));
            GLES20.glDeleteShader(vshader);
            vshader = 0;
        }

        int fshader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fshader, fss);
        GLES20.glCompileShader(fshader);
        GLES20.glGetShaderiv(fshader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Shader", "Could not compile fshader");
            Log.v("Shader", "Could not compile fshader:"+GLES20.glGetShaderInfoLog(fshader));
            GLES20.glDeleteShader(fshader);
            fshader = 0;
        }

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vshader);
        GLES20.glAttachShader(program, fshader);
        GLES20.glLinkProgram(program);

        return program;
    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        } else {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
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