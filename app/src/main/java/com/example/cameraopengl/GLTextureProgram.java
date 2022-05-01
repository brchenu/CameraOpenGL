package com.example.cameraopengl;

import android.content.Context;
import android.opengl.GLES20;

import java.util.HashMap;

public class GLTextureProgram {
    int program;

    private HashMap<String, Integer> variablesLocation;

    public GLTextureProgram(Context context) {

        variablesLocation = new HashMap<>();

        program = GLHelpers.createProgram(context, "shader/vs_texture.glsl", "shader/fs_texture.glsl");

        variablesLocation.put("a_position", GLES20.glGetAttribLocation(program, "a_position"));
        variablesLocation.put("a_uv", GLES20.glGetAttribLocation(program, "a_uv"));

        variablesLocation.put("u_mvp_matrix", GLES20.glGetUniformLocation(program, "u_mvp_matrix"));
        variablesLocation.put("u_texture", GLES20.glGetUniformLocation(program, "u_texture"));
    }

    public HashMap<String, Integer> getVariablesLocation() {
        return variablesLocation;
    }

    public int getLocation(String key) {
        return variablesLocation.get(key);
    }

    public int getProgram() {
        return program;
    }
}
