#version 150 core

in vec3 position;

uniform mat4 ProjMat, ViewMat, ModelMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(position, 1.0);
}
