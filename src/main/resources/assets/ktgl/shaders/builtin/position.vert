#version 150 core

in vec3 position;

uniform mat4 ProjMat, ModelViewMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);
}
