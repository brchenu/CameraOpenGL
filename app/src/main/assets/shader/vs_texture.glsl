uniform mat4 mvp_matrix;
attribute vec4 position;
attribute vec2 uv;
varying vec2 vuv;

void main() {
   vuv = uv;
   gl_Position = mvp_matrix * position;
}