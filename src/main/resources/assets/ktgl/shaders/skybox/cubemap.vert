#version 150 core

in vec3 position;

out vec3 texCoord0;

uniform mat4 ProjMat, ModelViewMat;

void main() {
    texCoord0 = vec3(position.x, position.y, -position.z);
    vec4 pos = ProjMat * ModelViewMat * vec4(position, 1.0);
    gl_Position = pos.xyww;
}
